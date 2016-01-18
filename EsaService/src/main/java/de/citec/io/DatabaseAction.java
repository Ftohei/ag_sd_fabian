/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import de.citec.util.Artikel;
import de.citec.util.ArtikelJson;
import de.citec.util.EsaResultJson;
import de.citec.util.Levenshtein;
import de.citec.util.MappingResultJson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;

/**
 *
 * @author swalter
 */
public class DatabaseAction {
    private final DB_Connector connector;
    
    public DatabaseAction(Config config) throws IOException{
        this.connector = new DB_Connector(config);
        System.out.println("Done initialization");
    }
    
    
    public String getInformations(String id){
        try {
            Artikel artikel = getArtikelForId(id);
            return convertoToJson(artikel);
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DatabaseAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DatabaseAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return "Artikel with ID "+id+" was not found";
    }
    
    
    public void close() throws SQLException{
        connector.closeConnection();
    }
    
    private Artikel getArtikelForId(String id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        Artikel artikel = new Artikel();
        try ( // date example: 2015-08-2
            Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            String query = "SELECT Distinct artikelId, id, artikelPDF, datum, titel, text, taggedText FROM artikel WHERE artikelId='"+id+"';";
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                
                artikel.setDatum(resultSet.getString("Datum"));
                artikel.setArtikelID(resultSet.getString("ArtikelId"));
                artikel.setArtikelPDF(resultSet.getString("ArtikelPDF"));
                artikel.setTitel(resultSet.getString("Titel"));
                artikel.setText(resultSet.getString("Text"));
                artikel.setTaggedText(resultSet.getString("TaggedText"));
                artikel.setSqlID(resultSet.getInt("id"));
            }
            
            String query_all = "SELECT Distinct wikipediaId, score FROM relationen_Alle WHERE artikelId='"+artikel.getSqlID()+"';";
            String query_onlyPerson = "SELECT Distinct wikipediaId, score FROM relationen_NurPersonen WHERE artikelId='"+artikel.getSqlID()+"';";

            Map<Integer, Float> wikipedia_entries_AllPersons = new HashMap<>();
            Map<Integer, Float> wikipedia_entries_onlyPersons = new HashMap<>();
            ResultSet resultSet_all = stmt.executeQuery(query_all);
            while (resultSet_all.next()) {
                int wikipediaID = resultSet_all.getInt("wikipediaId");
                float score = resultSet_all.getFloat("score");
                wikipedia_entries_AllPersons.put(wikipediaID, score);
            }
            ResultSet resultSet_onlyPerson = stmt.executeQuery(query_all);
            while (resultSet_onlyPerson.next()) {
                int wikipediaID = resultSet_onlyPerson.getInt("wikipediaId");
                float score = resultSet_onlyPerson.getFloat("score");
                wikipedia_entries_onlyPersons.put(wikipediaID, score);
            }
            artikel.setWikipedia_entries_all(wikipedia_entries_AllPersons);
            artikel.setWikipedia_entries_onlyPersons(wikipedia_entries_onlyPersons);
            
            stmt.close();
        }
        
        return artikel;
    }
        
     private Map<String, List<String>> convertToHM(String wiki) {
        Map<String, List<String>> wikipedia_entries = new HashMap<>();
        String[] tmp = wiki.split("##");
        for(String s : tmp){
            s = s.replace("(","");
            String[] s_temp = s.split(",");
            String id = s_temp[0];
            String name = s_temp[1];
            String value = s_temp[2];
            List<String> l = new ArrayList<>();
            l.add(name);
            l.add(value);
            wikipedia_entries.put(id, l);
        }
        
        
        return wikipedia_entries;
    }
     
     public String getWikipediaTitle(int id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        String title = "";
        try ( // date example: 2015-08-2
            Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            String query_all = "SELECT Distinct title FROM wikipedia WHERE id='"+Integer.toString(id)+"';";
//            System.out.println(query_all);
            ResultSet resultSet_all = stmt.executeQuery(query_all);
            while (resultSet_all.next()) {
                title= resultSet_all.getString("title");
            }
        }
//         System.out.println(title);
         return title;
     }
    
    
    private String convertoToJson(Artikel artikel) {
        ArtikelJson output = new ArtikelJson(artikel);
        return output.toJSONString();
    }
         
    /**
     *
     * @param term_list
     * @param onlyPersons
     * @param limit
     * @return
     */
    public  Map<Integer, Float> getWikipediaArtikels(List<String> term_list, boolean onlyPersons, int limit) {
       Connection connect;
       Set<String> terms = new HashSet<>();
       term_list.stream().forEach((t) -> {
           terms.add(t);
        });
        Map<Integer,Float> results = new  HashMap<>();
        try {
            
            connect = connector.connect();
            for(String term : terms){
                term = term.replace("_", " ");
                Statement stmt = connect.createStatement();
                String query = "";
                try{
                    query = "SELECT id, MATCH (body) AGAINST\n" +
                        "    ('"+term+"'\n" +
                        "    IN NATURAL LANGUAGE MODE) AS score\n" +
                        "    FROM wikipedia WHERE MATCH (body) AGAINST\n" +
                        "    ('"+term+"'\n" +
                        "    IN NATURAL LANGUAGE MODE)";
                    if(onlyPersons) {
                        query+=" and person='1' LIMIT "+limit+";";
                    }
                    else query+=" LIMIT "+limit+";";

                    ResultSet resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        int key = resultSet.getInt("id");
                        float value = resultSet.getFloat("score");
                        if(results.containsKey(key)){
                            float tmp_value = results.get(key);
                            results.put(key, value+tmp_value);
                        }
                        else results.put(key, value);
                    }
                }
                catch(Exception e){
                    System.out.println("Error in: "+query);
                }
                stmt.close();
            }
            
            connect.close();
        } catch (Exception e) {
           
        }
       
       return results;
    
    }



    public void articlesToDatabase(Set<Artikel> artikelliste) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        System.out.println("articlesToDatabase gestartet");

        try (Connection connect = connector.connect()) {
            
            ResultSet rs;
            
            
            System.out.println("------------");
            
            
            //            Searcher searcher = new Searcher("");
            
            for (Artikel artikel: artikelliste){
                
                Statement stmt;
                stmt = connect.createStatement();                
                
                System.out.println("Artikel: " + artikel.getArtikelID() + " wird in die Datenbank gespeichert");

                String artikelDatum = artikel.convertDate(artikel.getDatum());

//                    Daten des Artikels in Tabelle Artikel schreiben
                try {
                    if(artikel.getTitel()!=null && artikel.getTitel().length()>2){
                        int anzahl_woerter = artikel.getText().split(" ").length;
                        stmt.executeUpdate("INSERT INTO artikel SET "+
                                " artikelId = '" + artikel.getArtikelID() + "'" +
                                ", artikelPDF = '" + artikel.getArtikelPDF() + "'" +
                                ", datum = '" + artikel.convertDate(artikel.getDatum()) + "'" +
                                ", titel = '" + artikel.getTitel() + "'" +
                                ", text = '" + artikel.getText() + "'" +
                                ", taggedText = '" + artikel.getTaggedText() + "';"
                        );
                        
                        String query = "SELECT Distinct id FROM artikel where artikelId='"+artikel.getArtikelID()+"';";

                        ResultSet resultSet = stmt.executeQuery(query);
                        int id = 0;
                        while (resultSet.next()) {
                            id = resultSet.getInt("id");
                        }
//                        System.out.println(id);
                        stmt.clearBatch();
                        for(int key : artikel.getWikipedia_entries_all().keySet()){
                            stmt.addBatch("INSERT INTO relationen_Alle SET "+
                                    " artikelId = '" + id + "' , "
                                    + "wikipediaId = '" + key + "' , "
                                    + "score = '" + artikel.getWikipedia_entries_all().get(key)+"';");
                        }
                        
                        for(int key : artikel.getWikipedia_entries_onlyPersons().keySet()){
                            stmt.addBatch("INSERT INTO relationen_NurPersonen SET "+
                                    " artikelId = '" + id + "' , "
                                    + "wikipediaId = '" + key + "' , "
                                    + "score = '" + artikel.getWikipedia_entries_onlyPersons().get(key)+"';");
                        }
                        stmt.executeBatch();
                                            
                    }
                    else{
                        System.out.println("No title given for "+artikel.getArtikelPDF());
                    }
                    
                    
                stmt.close();
                System.out.println("DONE");
                } catch (SQLException e) {
                    e.printStackTrace();
                    stmt.close();
                }
                
                
            }
            
            
            
            
            
        }

    }
    
    
    
    
    public List<Artikel> getArtikelsForDate(String date, boolean onlyPersons) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        List<Artikel> results = new ArrayList<>();
        // date example: 2015-08-2
        try (Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            String query = "SELECT Distinct id, artikelId, titel FROM artikel where datum='"+date+"';";
            
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                
                Artikel artikel = new Artikel();
                artikel.setSqlID(resultSet.getInt("id"));
                artikel.setDatum(date);
                artikel.setArtikelID(resultSet.getString("ArtikelId"));
                artikel.setTitel(resultSet.getString("Titel"));
                //artikel.setText(resultSet.getString("Text"));
                results.add(artikel);
            }
            
            
            for(Artikel artikel : results){
                if(onlyPersons)
                    query = "SELECT Distinct wikipediaId,score FROM relationen_NurPersonen where artikelId='"+artikel.getSqlID()+"';";
                else
                    query = "SELECT Distinct wikipediaId,score FROM relationen_Alle where artikelId='"+artikel.getSqlID()+"';";
                resultSet = stmt.executeQuery(query);
                 Map<Integer,Float> wikipedia_entries = new HashMap();
                while (resultSet.next()) {
                    int wikipediaId = resultSet.getInt("wikipediaId");
                    float score = resultSet.getFloat("score");
                    if(wikipedia_entries.containsKey(wikipediaId)){
                        float tmp_score = wikipedia_entries.get(wikipediaId);
                        wikipedia_entries.put(wikipediaId, score+tmp_score);
                    }
                    else wikipedia_entries.put(wikipediaId, score);
               }
               if(onlyPersons) artikel.setWikipedia_entries_onlyPersons(wikipedia_entries);
               else artikel.setWikipedia_entries_all(wikipedia_entries);
                
            }
            stmt.close();
        }
//        System.out.println("results.size():"+results.size());
        return results;
    }
    
    
    public String getMapping(String term) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Map<String,List<String>> mapping = new HashMap<>();
        try (Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            Map<Integer,String> ids = new HashMap<>();
            
            String query = "SELECT Distinct resId, res FROM resources WHERE res LIKE '%"+term+"%';";
            
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                ids.put(resultSet.getInt("resId"), resultSet.getString("res"));
            }
            System.out.println(ids.size());
            for(int id : ids.keySet()){
                query = "SELECT Distinct Classes FROM resource_class_mapping WHERE Id='"+Integer.toString(id)+"';";
                List<String> tmp_results = new ArrayList<>();
                
                ResultSet resultSet_new = stmt.executeQuery(query);
                    while (resultSet_new.next()) {
                        tmp_results.add(resultSet_new.getString("Classes"));
                    }
                
                if(!tmp_results.isEmpty()){
                    mapping.put(ids.get(id), tmp_results);
                }
            }
            
            stmt.close();    
        }
            
        ArrayList<MappingResultJson> mappingResults = new ArrayList<>();
        JSONArray results = new JSONArray();
        
        for(String key : mapping.keySet()){
            double nld = Levenshtein.normalizedLevenshteinDistance(term, key);
            mappingResults.add(new MappingResultJson(key,Double.toString(nld),mapping.get(key)));
        }
        Collections.sort(mappingResults);
        results.addAll(mappingResults);
        return JSONArray.toJSONString(results);
        

    }
        
    
    
}
