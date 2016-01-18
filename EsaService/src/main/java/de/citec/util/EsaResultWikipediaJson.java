
package de.citec.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class EsaResultWikipediaJson implements JSONAware, Comparable{
        private final String id;
        private final String score;
        private final String title;
        
        public EsaResultWikipediaJson(String artikelID, String score, String title){
                this.id = artikelID;
                this.score = score;
                this.title = title;
        }
        
        @Override
        public String toJSONString(){
                StringBuilder sb = new StringBuilder();
                
                sb.append("{");
                sb.append("\"").append(JSONObject.escape("WikipediaId")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(id)).append("\"");
                
                sb.append(",");
                
                sb.append("\"").append(JSONObject.escape("Titel")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(title)).append("\"");
                
                sb.append(",");
                
                sb.append("\"").append(JSONObject.escape("Score")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(score)).append("\"");
                
                sb.append("}");
                
                return sb.toString();
        }

        @Override
        public int compareTo(Object o) {
                //sortiert abwärts
                if(Double.parseDouble(((EsaResultWikipediaJson) o).score) > Double.parseDouble(this.score) ) {
                        return 1;
                } else if(Double.parseDouble(((EsaResultWikipediaJson) o).score) < Double.parseDouble(this.score) ) {
                        return -1;
                }
                return 0;
        }
        

        public String getTitle() {
            return id;
        }

        public String getScore() {
            return score;
        }
}

