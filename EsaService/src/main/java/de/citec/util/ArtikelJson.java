/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

import java.util.Map;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */
public class ArtikelJson implements JSONAware{
    
    private final Artikel artikel;
    
    public ArtikelJson(Artikel input){
        this.artikel=input;
    }
    

    @Override
    public String toJSONString() {
        StringBuilder sb = new StringBuilder();
                
        sb.append("{");

        sb.append(JSONObject.escape("ID"));
        sb.append(":");
        sb.append(artikel.getArtikelID());

        sb.append(",");

        sb.append(JSONObject.escape("Title"));
        sb.append(":");
        sb.append("\"").append(JSONObject.escape(artikel.getTitel())).append("\"");

        sb.append(",");
        
        sb.append(JSONObject.escape("ArtikelPDF"));
        sb.append(":");
        sb.append("\"").append(JSONObject.escape(artikel.getArtikelPDF())).append("\"");

        sb.append(",");

        sb.append(JSONObject.escape("Datum"));
        sb.append(":");
        sb.append(artikel.getDatum());
        
        sb.append(",");

        sb.append(JSONObject.escape("Text"));
        sb.append(":");
        sb.append("\"").append(JSONObject.escape(artikel.getText())).append("\"");
        
        sb.append(",");

        sb.append(JSONObject.escape("WikipediaOnlyPersons"));
        sb.append("{");
        StringBuilder sb_onlyPersons = new StringBuilder();
        Map<Integer, Float> wikipedia_onlyPersons = artikel.getWikipedia_entries_onlyPersons();
        for(Integer id:wikipedia_onlyPersons.keySet()){
            sb_onlyPersons.append(JSONObject.escape(Integer.toString(id)));
            sb_onlyPersons.append(":");
            sb_onlyPersons.append(Float.toString(wikipedia_onlyPersons.get(id)));
            sb_onlyPersons.append(",");
        }
        //sb_onlyPersons.deleteCharAt(sb_onlyPersons.lastIndexOf(","));
        sb.append("\"").append(JSONObject.escape(sb_onlyPersons.toString())).append("\"");
        sb.append("},");
        
        sb.append(JSONObject.escape("WikipediaAllPersons"));
        sb.append("{");
        StringBuilder sb_allPersons = new StringBuilder();
        Map<Integer, Float> wikipedia_allPersons = artikel.getWikipedia_entries_all();
        for(Integer id:wikipedia_allPersons.keySet()){
            sb_allPersons.append(JSONObject.escape(Integer.toString(id)));
            sb_allPersons.append(":");
            sb_allPersons.append(Float.toString(wikipedia_allPersons.get(id)));
            sb_allPersons.append(",");
        }
        //sb_allPersons.deleteCharAt(sb_onlyPersons.lastIndexOf(","));
        sb.append("\"").append(JSONObject.escape(sb_allPersons.toString())).append("\"");
        sb.append("}");
        
//        sb.append("\"").append(JSONObject.escape(artikel.getWikipedia_entries_onlyPersons())).append("\"");
        

        sb.append("}");

        return sb.toString();
    }


}
