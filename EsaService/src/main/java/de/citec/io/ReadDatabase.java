/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import java.io.IOException;

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
        String query = "SELECT Distinct Titel, Text, Wikipedia_OnlyPerson FROM Artikel where hex(Id)='"+id+"';";
        
        
        return "DoJson";
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
