/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

import de.citec.io.Config;
import de.citec.io.ImportNW;
import de.citec.io.DB_Connector;
import de.citec.lucene.SearchIndex;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.nio.charset.Charset;
import org.json.simple.JSONArray;

/**
 *
 * @author swalter
 */
public class VectorSimilarity {
    
    private final SearchIndex index;
    private final DB_Connector connector;
    public VectorSimilarity(String path_index, Language language, Config config) throws IOException{
        this.index = new SearchIndex(path_index,language);
        this.connector = new DB_Connector(config);
        System.out.println("Done initialization");
    }
    
    
    public String getArtikels(List<String> interests, String date, boolean onlyPerson){
        
        List<Artikel> result_nw = null;
        Map<String,List<String>> result_interests = new HashMap();
        try{
            result_nw = getArtikelsForDate(date,onlyPerson);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            int value = result_nw.size();
            if(value>0){
                if(onlyPerson)
                    result_interests = index.runStrictSearch(interests, value, onlyPerson);
                else result_interests = index.runStrictSearch(interests, value, onlyPerson);
            }
            
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("result_interests.size():"+result_interests.size());
        Map<String,Double> overall_results = new HashMap<>();
        
        Map<String,String> titel_id = new HashMap<>();
        if (!result_nw.isEmpty() && !result_interests.isEmpty()){
            for(Artikel artikel : result_nw){
                String id = artikel.getArtikelID();
                String titel = artikel.getTitel();
                if(!titel_id.containsKey(titel)){
                    Map<String,Float> vector1 = new HashMap<>();
                    Map<String,Float> vector2 = new HashMap<>();
                    createVector(vector1,vector2,artikel,result_interests,onlyPerson);
                    getSimilarity(vector1,vector2,overall_results,titel);
                    titel_id.put(titel, id);
                }
                
            }
            
        }
        
        else{
            //do something else
        }
        
        
//        overall_results.entrySet().stream()
//        .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) 
//        .limit(5) 
//        .forEach(System.out::println);
        ArrayList<EsaResult> esaResults = new ArrayList<>();
        JSONArray results = new JSONArray();
        System.out.println(overall_results.size());
        for(String key : overall_results.keySet()){
            esaResults.add(new EsaResult(titel_id.get(key),key,Double.toString(overall_results.get(key))));
        }
        Collections.sort(esaResults);
        results.addAll(esaResults);
        return JSONArray.toJSONString(results);
    }
    
    public String getArtikelsRawInput(String rawInput, boolean onlyPerson, MaxentTagger tagger){

        List<String> searchInput = new ArrayList();

        rawInput = ImportNW.tagText(rawInput, tagger);

        if(rawInput.contains(",")){
            for(String i:rawInput.split(","))searchInput.add(i);
        } else {
            searchInput.add(rawInput);
            System.out.println("RawInput: " + rawInput);
        }


        Map<String,List<String>> result_rawInput = null;

        try {
            result_rawInput = index.runStrictSearch(searchInput, 100, onlyPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int counter = 0;
        JSONArray resultJSON = new JSONArray();
        ArrayList<EsaResult> esaResults = new ArrayList<>();
        for(String key : result_rawInput.keySet()){
            counter+=1;

            String artikelname = result_rawInput.get(key).get(0);
            String score = result_rawInput.get(key).get(1);

            esaResults.add(new EsaResult(Integer.toString(counter), artikelname, score));
        }
        Collections.sort(esaResults);
        resultJSON.addAll(esaResults);
        return JSONArray.toJSONString(resultJSON);

    }


    private List<Artikel> getArtikelsForDate(String date, boolean onlyPersons) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        List<Artikel> results = new ArrayList<>();
        try ( // date example: 2015-08-2
            Connection connect = connector.connect()) {
            Statement stmt = connect.createStatement();
            
            String query = "";
            if(onlyPersons){
                query = "SELECT Distinct ArtikelId, Titel, Text, Wikipedia_OnlyPerson FROM Artikel where Datum='"+date+"';";
            }
            else{
                query = "SELECT Distinct ArtikelId, Titel, Text, Wikipedia_NoPerson FROM Artikel where Datum='"+date+"';";
            }
            
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                
                Artikel artikel = new Artikel();
                artikel.setDatum(date);
                artikel.setArtikelID(resultSet.getString("ArtikelId"));
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
        System.out.println("results.size():"+results.size());
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

    private void getSimilarity(Map<String, Float> vector1, Map<String, Float> vector2, Map<String, Double> overall_results, String titel) {
        Double cos = calculateCos(vector1,vector2);
        if(cos>0.002) overall_results.put(titel, cos);
    }

    /**
     * This function creates 
     * @param vector1
     * @param vector2
     * @param artikel
     * @param result_interests
     * @param onlyPersons 
     */
    private void createVector(Map<String, Float> vector1, Map<String, Float> vector2, Artikel artikel, Map<String, List<String>> result_interests, boolean onlyPersons) {
        Map<String,Float> general_vector = new HashMap<>();
        if(onlyPersons){
            artikel.getWikipedia_entries_onlyPersons().keySet().stream().forEach((key) -> {
                try{
                    //make sure artikle contains convertable float
                    //TODO: Find out, why we have _$ as id; if found, remove try catch and the Float.valueOf
                    if(!artikel.getWikipedia_entries_onlyPersons().get(key).get(1).contains("_$")){
                        general_vector.put(key, 0f);
                    }
                    
                }
                catch(Exception e){e.printStackTrace();}
            });
        }
        else{
           artikel.getWikipedia_entries_noPersons().keySet().stream().forEach((key) -> {
                try{
                    //make sure artikle contains convertable float
                    //TODO: Find out, why we have _$ as id; if found, remove try catch and the Float.valueOf
                    if(!artikel.getWikipedia_entries_noPersons().get(key).get(1).contains("_$")&&!artikel.getWikipedia_entries_noPersons().get(key).get(1).contains(" ")){
                        general_vector.put(key, 0f);
                    }
                }
                catch(Exception e){e.printStackTrace();}
            }); 
        }
        
        result_interests.keySet().stream().forEach((key) -> {
            try{
                general_vector.put(key, 0f);
            }
            catch(Exception e){e.printStackTrace();}
        });
        
//        System.out.println("Size general_vector:"+general_vector.size());
//        System.out.println("Size interests: "+result_interests.size());
        vector1.putAll(general_vector);
        vector2.putAll(general_vector);
        if(onlyPersons){
            artikel.getWikipedia_entries_onlyPersons().keySet().stream().filter((key) -> (vector2.containsKey(key))).forEach((key) -> {
                 if(!artikel.getWikipedia_entries_onlyPersons().get(key).get(1).contains("_$")){
                     vector2.put(key, Float.valueOf(artikel.getWikipedia_entries_onlyPersons().get(key).get(1)));
                 }
            });
        }
        else{
            artikel.getWikipedia_entries_noPersons().keySet().stream().filter((key) -> (vector2.containsKey(key))).forEach((key) -> {
                if(!artikel.getWikipedia_entries_noPersons().get(key).get(1).contains("_$")&&!artikel.getWikipedia_entries_noPersons().get(key).get(1).contains(" ")){
                    vector2.put(key, Float.valueOf(artikel.getWikipedia_entries_noPersons().get(key).get(1)));
                }
            });
        }
        
        result_interests.keySet().stream().filter((key) -> (vector1.containsKey(key))).forEach((key) -> {
            vector1.put(key, Float.valueOf(result_interests.get(key).get(1)));
        });
        
    }

    /**
     * Calculates the cosinus between two given vectors. A vector in this case is a hashMap<String,Integer> with a value between 0 and 1
     * @param vector1
     * @param vector2
     * @return 
     */
    private Double calculateCos(Map<String, Float> vector1, Map<String, Float> vector2) {
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
    private double calculateLen(Map<String, Float> vector1) {
        float len = 0f;
        len = vector1.values().stream().map((value) -> value*value).reduce(len, Float::sum);
        return Math.sqrt(len);
        
    }

    /**
     * Calculates the scalar product of two "vectors", in this case a vector is a hashMap with a WikipediaArtikelID as key and a integer as value (in the moment 0 or 1)
     * @param vector1
     * @param vector2
     * @return 
     */
    private double calculateScalar(Map<String, Float> vector1, Map<String, Float> vector2) {
        if(vector1.size()!=vector2.size()) return 0.0;
        double scalar = 0.0;
        for(String key:vector1.keySet()){
            scalar+=vector1.get(key)*vector2.get(key);
        }
        
        return scalar;
    }

}
