/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import de.citec.util.Artikel;
import de.citec.util.ArtikelJson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author swalter
 */
public class ReadDatabase {
    private final DB_Connector connector;
    
    public ReadDatabase(Config config) throws IOException{
        this.connector = new DB_Connector(config);
        System.out.println("Done initialization");
    }
    
    
    public String getInformations(String id){
        try {
            Artikel artikel = getArtikelForId(id);
            return convertoToJson(artikel);
            
        } catch (SQLException ex) {
            Logger.getLogger(ReadDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ReadDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ReadDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReadDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return "Artikel with ID "+id+" was not found";
    }
    
    
    
    private Artikel getArtikelForId(String id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        Artikel artikel = new Artikel();
        try ( // date example: 2015-08-2
            Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            String query = "SELECT Distinct ArtikelId, Datum, Titel, Text, TaggedText, Wikipedia_OnlyPerson, Wikipedia_NoPerson FROM Artikel where ArtikelId='"+id+"';";
            
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                
                artikel.setDatum(resultSet.getString("Datum"));
                artikel.setArtikelID(resultSet.getString("ArtikelId"));
                artikel.setTitel(resultSet.getString("Titel"));
                artikel.setText(resultSet.getString("Text"));
                artikel.setTaggedText(resultSet.getString("TaggedText"));
                String wiki = resultSet.getString("Wikipedia_OnlyPerson");
                artikel.setWikipedia_entries_onlyPersons(convertToHM(wiki));
                wiki = resultSet.getString("Wikipedia_NoPerson");
                artikel.setWikipedia_entries_noPersons(convertToHM(wiki));
            }
            
            
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
    
    
    private String convertoToJson(Artikel artikel) {
        ArtikelJson output = new ArtikelJson(artikel);
        return output.toJSONString();
    }
         
    
    //    public String getArticlesListOfNwArticleIds(List<String> articleIdList, boolean onlyPersons){
//
//        /**
//         * TODO: für Liste von ArtikelIds die besten ESA-Ergebnisse zurückgeben. Dafür
//         * Artikel aus DB suchen (schon vorher?) und dann Index durchsuchen
//         */
//
//        List<Artikel> artikels = this.getArtikelsById(articleIdList, onlyPersons);
//
//        //TODO: von jedem Artikel die ESA-Ergebnisse in Map speichern, dann gesamte Liste sortieren und ausgeben lassen
//
//        Map<Double, String> wikiArticleMap = new HashMap<>();
//
//        for(Artikel artikel : artikels){
//            if(onlyPersons) {
//                SortedSet<String> keys = new TreeSet<>(artikel.getWikipedia_entries_onlyPersons().keySet());
//                for (String key : keys){
//                    wikiArticleMap.put(Double.parseDouble(artikel.getWikipedia_entries_onlyPersons().get(key).get(1)),
//                            artikel.getWikipedia_entries_onlyPersons().get(key).get(0));
//                }
//            } else {
//                SortedSet<String> keys = new TreeSet<>(artikel.getWikipedia_entries_noPersons().keySet());
//                for (String key : keys){
//                    wikiArticleMap.put(Double.parseDouble(artikel.getWikipedia_entries_noPersons().get(key).get(1)),
//                            artikel.getWikipedia_entries_noPersons().get(key).get(0));
//                }
//            }
//        }
//
//        SortedSet<Double> keys = new TreeSet<Double>(wikiArticleMap.keySet());
//        int count = 0;
//        for (Double key : keys) {
//            if(count < 100){
//                System.out.println(wikiArticleMap.get(key) + " : " + key);
//            }
//            count++;
//        }
//
//        return null;
//    }
    
    
    
    //    public List<Artikel> getArtikelsById(List<String> ids, boolean onlyPersons){
//
//        List<Artikel> results = new ArrayList<>();
//
//        try {
//            Connection connect = null;
//            connect = connector.connect();
//            Statement stmt = connect.createStatement();
//
//            for(String id : ids) {
//
//                System.out.println("Durchsuche Datenbank nach:" + id);
//
//                String query = "";
//                if (onlyPersons) {
//                    query = "SELECT Distinct Datum, Titel, Text, Wikipedia_OnlyPerson FROM Artikel where hex(id)='" + id + "';";
//                } else {
//                    query = "SELECT Distinct Datum, Titel, Text, Wikipedia_NoPerson FROM Artikel where hex(id)='" + id + "';";
//                }
//
//                ResultSet resultSet = stmt.executeQuery(query);
//
//                while (resultSet.next()) {
//
//                    Artikel artikel = new Artikel();
//                    artikel.setDatum(resultSet.getString("Datum"));
//                    artikel.setTitel(resultSet.getString("Titel"));
//                    artikel.setText(resultSet.getString("Text"));
//                    String wiki = "";
//                    if (onlyPersons) {
//                        wiki = resultSet.getString("Wikipedia_OnlyPerson");
//                        artikel.setWikipedia_entries_onlyPersons(convertToHM(wiki));
//                    } else {
//                        wiki = resultSet.getString("Wikipedia_NoPerson");
//                        artikel.setWikipedia_entries_noPersons(convertToHM(wiki));
//                    }
//
//                    results.add(artikel);
//                }
//
//            }
//
//            stmt.close();
//        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e1) {
//            e1.printStackTrace();
//        }
//
//        return results;
//    }


    
    
}
