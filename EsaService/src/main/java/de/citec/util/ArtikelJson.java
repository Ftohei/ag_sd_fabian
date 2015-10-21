/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

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

        sb.append(JSONObject.escape("Datum"));
        sb.append(":");
        sb.append(artikel.getDatum());
        
        sb.append(",");
        
        sb.append(JSONObject.escape("Title"));
        sb.append(":");
        sb.append("\"").append(JSONObject.escape(artikel.getTitel())).append("\"");
        
        sb.append(",");

        sb.append(JSONObject.escape("Text"));
        sb.append(":");
        sb.append("\"").append(JSONObject.escape(artikel.getText())).append("\"");
        
        sb.append(",");

//        sb.append(JSONObject.escape("Title"));
//        sb.append(":");
//        sb.append("\"").append(JSONObject.escape(artikel.getWikipedia_entries_onlyPersons())).append("\"");
        

        sb.append("}");

        return sb.toString();
    }


}
