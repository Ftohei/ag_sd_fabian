/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

import de.citec.io.Config;
import de.citec.io.DatabaseAction;
import java.io.IOException;
import java.util.*;

import java.sql.SQLException;

import org.json.simple.JSONArray;

/**
 *
 * @author swalter
 */
public class VectorSimilarity {
    
    private final DatabaseAction da;
    public VectorSimilarity(Language language, Config config) throws IOException{
        this.da = new DatabaseAction(config);
//        System.out.println("Done initialization");
    }
    
    
    public void close() throws SQLException{
        da.close();
    }
    
    public String getArtikels(List<String> interests, String date, boolean onlyPerson, boolean json){
        
        List<Artikel> result_nw = null;
        Map<Integer, Float> result_interests = new HashMap();
        try{
            result_nw = da.getArtikelsForDate(date,onlyPerson);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            int value = result_nw.size();
            if(value>0){
                if(onlyPerson)
                    result_interests = da.getWikipediaArtikels(interests, onlyPerson,500);
                else result_interests = da.getWikipediaArtikels(interests, onlyPerson,500);
            }
            
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println("result_interests.size():"+result_interests.size());
        Map<String,Double> overall_results = new HashMap<>();
        
        Map<String,String> titel_id = new HashMap<>();
        if (!result_nw.isEmpty() && !result_interests.isEmpty()){
            for(Artikel artikel : result_nw){
                String id = artikel.getArtikelID();
                String titel = artikel.getTitel();
                if(!titel_id.containsKey(titel)){
                    Map<Integer,Float> vector1 = new HashMap<>();
                    Map<Integer,Float> vector2 = new HashMap<>();
                    createVector(vector1,vector2,artikel,result_interests,onlyPerson);
                    getSimilarity(vector1,vector2,overall_results,titel);
                    titel_id.put(titel, id);
                    
                    
                }
                
            }
            
        }
//        
//        else{
//            //do something else
//        }
//        
//        
//        overall_results.entrySet().stream()
//        .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) 
//        .limit(10) 
//        .forEach(System.out::println);
        ArrayList<EsaResultJson> esaResults = new ArrayList<>();
        JSONArray results = new JSONArray();
//        System.out.println(overall_results.size());
        for(String key : overall_results.keySet()){
            esaResults.add(new EsaResultJson(titel_id.get(key),key,Double.toString(overall_results.get(key))));
        }
        Collections.sort(esaResults);
        results.addAll(esaResults);
        if(json)
            return JSONArray.toJSONString(results);
        else
            return getPlainTitle(esaResults);
    }
    
    
    
    public String getArtikelsRawInput(String rawInput, boolean onlyPerson, int value){

        List<String> searchInput = new ArrayList();

//        rawInput = ImportNW.tagText(rawInput, tagger);

        if(rawInput.contains(",")){
            for(String i:rawInput.split(","))searchInput.add(i);
        } else {
            searchInput.add(rawInput);
        }

        Map<Integer, Float> result_rawInput = da.getWikipediaArtikels(searchInput,onlyPerson,value);


        JSONArray resultJSON = new JSONArray();
        ArrayList<EsaResultWikipediaJson> esaResultsWikipedia = new ArrayList<>();
        Map<Integer,String> wikipedia_titles = getWikipediaTitles(result_rawInput);
        for(int key : result_rawInput.keySet()){
            String artikelid = Integer.toString(key);
            String score = Float.toString(result_rawInput.get(key));
            if(wikipedia_titles.containsKey(key))
                esaResultsWikipedia.add(new EsaResultWikipediaJson(artikelid, score,wikipedia_titles.get(key)));
        }
        Collections.sort(esaResultsWikipedia);
        resultJSON.addAll(esaResultsWikipedia);
        return JSONArray.toJSONString(resultJSON);

    }
    
    


    private void getSimilarity(Map<Integer, Float> vector1, Map<Integer, Float> vector2, Map<String, Double> overall_results, String titel) {
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
    private void createVector(Map<Integer, Float> vector1, Map<Integer, Float> vector2, Artikel artikel, Map<Integer, Float> result_interests, boolean onlyPersons) {
        
        Map<Integer, Float> input = new HashMap<>();
        Map<Integer, Float> tmp = new HashMap<>();
        if(onlyPersons) {
//            input = artikel.getWikipedia_entries_onlyPersons();
            tmp = artikel.getWikipedia_entries_onlyPersons();
        }
        else {
//            input = artikel.getWikipedia_entries_all();
            tmp = artikel.getWikipedia_entries_all();

        }
        for(int key:result_interests.keySet())input.put(key, 0f);
        for(int key:tmp.keySet())input.put(key, 0f);
        
//        input.keySet().retainAll(result_interests.keySet());
        
        for(int key : input.keySet()){
           float value1 = 0f;
           float value2 = 0f;
//           if(result_interests.containsKey(key)) value1 = 1/(1+result_interests.get(key));
//           if(tmp.containsKey(key)) value2 = 1/(1+tmp.get(key));
           if(result_interests.containsKey(key)) value1 = result_interests.get(key);
           if(tmp.containsKey(key)) value2 = tmp.get(key);
           vector1.put(key, value1);
           vector2.put(key, value2);
        }
        
    }

    /**
     * Calculates the cosinus between two given vectors. A vector in this case is a hashMap<String,Integer> with a value between 0 and 1
     * @param vector1
     * @param vector2
     * @return 
     */
    private Double calculateCos(Map<Integer, Float> vector1, Map<Integer, Float> vector2) {
        if(vector1.size()!=vector2.size()) return 0.0;
        double cos = 0.0;
        float scalar_product = calculateScalar(vector1,vector2);
        if(scalar_product==0.0) return 0.0;
        float norm_vector1 = calculateNorm(vector1);
        float norm_vector2 = calculateNorm(vector2);
        cos = scalar_product/(Math.sqrt(norm_vector1)*Math.sqrt(norm_vector2));
        return cos;
     }

    /**
     * Calculates the lenght of a vector. A vector is defined in the same way as in the function calculateScalar(v1,v2)
     * @param inputvector
     * @return 
     */
    private float calculateNorm(Map<Integer, Float> inputvector) {
        float norm = 0f;
        for(Float value : inputvector.values()){
            norm+= Math.pow(value, 2);
        }
        return norm;
        
    }

    /**
     * Calculates the scalar product of two "vectors", in this case a vector is a hashMap with a WikipediaArtikelID as key and a integer as value (in the moment 0 or 1)
     * @param vector1
     * @param vector2
     * @return 
     */
    private float calculateScalar(Map<Integer, Float> vector1, Map<Integer, Float> vector2) {
        float scalar = 0f;
        for(Integer key:vector1.keySet()){
            scalar+=vector1.get(key)*vector2.get(key);
        }
        
        return scalar;
    }

    private String getPlainTitle(ArrayList<EsaResultJson> esaResults) {
        String output="";
        int counter = 0;
        for(EsaResultJson result : esaResults){
            if (counter <30) output+=result.getTitle()+"\n";
            counter+=1;
        }
        
        return output;
    }

    private Map<Integer, String> getWikipediaTitles(Map<Integer, Float> result_rawInput) {
        Map<Integer,String> titles = new HashMap<>();
        for(int key : result_rawInput.keySet()){
            try{
                titles.put(key, da.getWikipediaTitle(key));
            }
            catch(Exception e){
                //pass
            }
        }
        
        
        return titles;
    }
    

    
    

}
