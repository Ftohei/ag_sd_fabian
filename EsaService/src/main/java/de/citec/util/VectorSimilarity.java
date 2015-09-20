/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

import de.citec.lucene.SearchIndex;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.simple.JSONArray;

/**
 *
 * @author swalter
 */
public class VectorSimilarity {
    
    private final SearchIndex index;
    private final DB_Connector connector;
    public VectorSimilarity(String path_index, Language language) throws IOException{
        this.index = new SearchIndex(path_index,language);
        this.connector = new DB_Connector();
        System.out.println("Done initialization");
    }
    
    
    public String getArtikels(List<String> interests, String date, boolean onlyPerson){
        
        List<Artikel> result_nw = null;
        Map<String,List<String>> result_interests = null;
        try{
            result_nw = getArtikelsForDate(date,onlyPerson);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            result_interests = index.runStrictSearch(interests, 100, onlyPerson);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
        Map<String,Double> overall_results = new HashMap<>();
        
        if (!result_nw.isEmpty() && !result_interests.isEmpty()){
            for(Artikel artikel : result_nw){
                Map<String,Integer> vector1 = new HashMap<String,Integer>();
                Map<String,Integer> vector2 = new HashMap<String,Integer>();
                createVector(vector1,vector2,artikel,result_interests,onlyPerson);
                getSimilarity(vector1,vector2,overall_results,artikel.getTitel());
            }
            
        }
        
        else{
            //do something else
        }
        
        
        overall_results.entrySet().stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) 
        .limit(5) 
        .forEach(System.out::println);
        int counter = 0;
        JSONArray results = new JSONArray();
        for(String key : overall_results.keySet()){
            counter+=1;
            results.add(new EsaResult(Integer.toString(counter),key,Double.toString(overall_results.get(key))));
        }
        return JSONArray.toJSONString(results);
        
    }
    
    public String getArtikelsRawInput(String rawInput, boolean onlyPerson, MaxentTagger tagger){

        List<String> searchInput = new ArrayList();

        rawInput = ImportNW.tagText(rawInput, tagger);

        if(rawInput.contains(",")){
            for(String i:rawInput.split(","))searchInput.add(i);
        } else {
            searchInput.add(rawInput);
        }


        Map<String,List<String>> result_rawInput = null;

        try {
            result_rawInput = index.runStrictSearch(searchInput, 100, onlyPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int counter = 0;
        JSONArray resultJSON = new JSONArray();
        for(String key : result_rawInput.keySet()){
            counter+=1;

            String artikelname = result_rawInput.get(key).get(0);
            String score = result_rawInput.get(key).get(1);

            resultJSON.add(new EsaResult(Integer.toString(counter), artikelname, score));
        }
        return JSONArray.toJSONString(resultJSON);

    }

    public String getArticlesListOfNwArticleIds(List<String> articleIdList, boolean onlyPersons){

        /**
         * TODO: für Liste von ArtikelIds die besten ESA-Ergebnisse zurückgeben. Dafür
         * Artikel aus DB suchen (schon vorher?) und dann Index durchsuchen
         */

        List<Artikel> artikels = this.getArtikelsById(articleIdList, onlyPersons);

        //TODO: von jedem Artikel die ESA-Ergebnisse in Map speichern, dann gesamte Liste sortieren und ausgeben lassen

        Map<Double, String> wikiArticleMap = new HashMap<>();

        for(Artikel artikel : artikels){
            if(onlyPersons) {
                SortedSet<String> keys = new TreeSet<>(artikel.getWikipedia_entries_onlyPersons().keySet());
                for (String key : keys){
                    wikiArticleMap.put(Double.parseDouble(artikel.getWikipedia_entries_onlyPersons().get(key).get(1)),
                            artikel.getWikipedia_entries_onlyPersons().get(key).get(0));
                }
            } else {
                SortedSet<String> keys = new TreeSet<>(artikel.getWikipedia_entries_noPersons().keySet());
                for (String key : keys){
                    wikiArticleMap.put(Double.parseDouble(artikel.getWikipedia_entries_noPersons().get(key).get(1)),
                            artikel.getWikipedia_entries_noPersons().get(key).get(0));
                }
            }
        }

        SortedSet<Double> keys = new TreeSet<Double>(wikiArticleMap.keySet());
        int count = 0;
        for (Double key : keys) {
            if(count < 100){
                System.out.println(wikiArticleMap.get(key) + " : " + key);
            }
            count++;
        }

        return null;
    }

    private List<Artikel> getArtikelsForDate(String date, boolean onlyPersons) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        List<Artikel> results = new ArrayList<>();
        try ( // date example: 2015-08-2
            Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            String query = "";
            if(onlyPersons){
                query = "SELECT Distinct Id, Titel, Text, Wikipedia_OnlyPerson FROM Artikel where Datum='"+date+"';";
            }
            else{
                query = "SELECT Distinct Id, Titel, Text, Wikipedia_NoPerson FROM Artikel where Datum='"+date+"';";
            }
            
            ResultSet resultSet = stmt.executeQuery(query);
            System.out.println(query);
            while (resultSet.next()) {
                
                Artikel artikel = new Artikel();
                artikel.setDatum(date);
                artikel.setTitel(resultSet.getString("Titel"));
                artikel.setText(resultSet.getString("Text"));
                String wiki = "";
                if(onlyPersons) {
                    wiki = resultSet.getString("Wikipedia_OnlyPerson");
                    artikel.setWikipedia_entries_onlyPersons(convertToHM(wiki));
                }
                else {
                    wiki = resultSet.getString("Wikipedia_NoPerson");
                    artikel.setWikipedia_entries_noPersons(convertToHM(wiki));
                }
                
                results.add(artikel);
            }
            
            
            stmt.close();
        }
        return results;
    }

    public List<Artikel> getArtikelsById(List<String> ids, boolean onlyPersons){

        List<Artikel> results = new ArrayList<>();

        try {
            Connection connect = null;
            connect = connector.connect();
            Statement stmt = connect.createStatement();

            for(String id : ids) {

                System.out.println("Durchsuche Datenbank nach:" + id);

                String query = "";
                if (onlyPersons) {
                    query = "SELECT Distinct Datum, Titel, Text, Wikipedia_OnlyPerson FROM Artikel where hex(id)='" + id + "';";
                } else {
                    query = "SELECT Distinct Datum, Titel, Text, Wikipedia_NoPerson FROM Artikel where hex(id)='" + id + "';";
                }

                ResultSet resultSet = stmt.executeQuery(query);

                while (resultSet.next()) {

                    Artikel artikel = new Artikel();
                    artikel.setDatum(resultSet.getString("Datum"));
                    artikel.setTitel(resultSet.getString("Titel"));
                    artikel.setText(resultSet.getString("Text"));
                    String wiki = "";
                    if (onlyPersons) {
                        wiki = resultSet.getString("Wikipedia_OnlyPerson");
                        artikel.setWikipedia_entries_onlyPersons(convertToHM(wiki));
                    } else {
                        wiki = resultSet.getString("Wikipedia_NoPerson");
                        artikel.setWikipedia_entries_noPersons(convertToHM(wiki));
                    }

                    results.add(artikel);
                }

            }

            stmt.close();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }

        return results;
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

    private void getSimilarity(Map<String, Integer> vector1, Map<String, Integer> vector2, Map<String, Double> overall_results, String titel) {
        Double cos = calculateCos(vector1,vector2);
        if(cos>0.0001) overall_results.put(titel, cos);
    }

    /**
     * This function creates 
     * @param vector1
     * @param vector2
     * @param artikel
     * @param result_interests
     * @param onlyPersons 
     */
    private void createVector(Map<String, Integer> vector1, Map<String, Integer> vector2, Artikel artikel, Map<String, List<String>> result_interests, boolean onlyPersons) {
        Map<String,Integer> general_vector = new HashMap<>();
        if(onlyPersons){
            artikel.getWikipedia_entries_onlyPersons().keySet().stream().forEach((key) -> {
            general_vector.put(key, 0);
            });
        }
        else{
           artikel.getWikipedia_entries_noPersons().keySet().stream().forEach((key) -> {
            general_vector.put(key, 0);
            }); 
        }
        
        result_interests.keySet().stream().forEach((key) -> {
            general_vector.put(key, 0);
        });
        
//        System.out.println("Size general_vector:"+general_vector.size());
//        System.out.println("Size interests: "+result_interests.size());
        vector1.putAll(general_vector);
        vector2.putAll(general_vector);
        if(onlyPersons){
            artikel.getWikipedia_entries_onlyPersons().keySet().stream().filter((key) -> (vector2.containsKey(key))).forEach((key) -> {
            vector2.put(key, 1);
            });
        }
        else{
            artikel.getWikipedia_entries_noPersons().keySet().stream().filter((key) -> (vector2.containsKey(key))).forEach((key) -> {
            vector2.put(key, 1);
            });
        }
        
        result_interests.keySet().stream().filter((key) -> (vector1.containsKey(key))).forEach((key) -> {
            vector1.put(key, 1);
        });
        
    }

    /**
     * Calculates the cosinus between two given vectors. A vector in this case is a hashMap<String,Integer> with a value between 0 and 1
     * @param vector1
     * @param vector2
     * @return 
     */
    private Double calculateCos(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        double cos = 0.0;
        double len_vector1 = calculateLen(vector1);
        double len_vector2 =calculateLen(vector2);
        
        double scalar_product = calculateScalar(vector1,vector2);
        
        cos = scalar_product/(len_vector1*len_vector2);
        return cos;
    }

    /**
     * Calculates the lenght of a vector. A vector is defined in the same way as in the function calculateScalar(v1,v2)
     * @param vector1
     * @return 
     */
    private double calculateLen(Map<String, Integer> vector1) {
        int len = 0;
        len = vector1.values().stream().map((value) -> value*value).reduce(len, Integer::sum);
        return Math.sqrt(len);
        
    }

    /**
     * Calculates the scalar product of two "vectors", in this case a vector is a hashMap with a WikipediaArtikelID as key and a integer as value (in the moment 0 or 1)
     * @param vector1
     * @param vector2
     * @return 
     */
    private double calculateScalar(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        if(vector1.size()!=vector2.size()) return 0.0;
        double scalar = 0.0;
        for(String key:vector1.keySet()){
            scalar+=vector1.get(key)*vector2.get(key);
        }
        
        return scalar;
    }

}
