
package de.citec.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class EsaResultWikipedia implements JSONAware, Comparable{
        private final String id;
        private final String score;
        
        public EsaResultWikipedia(String title, String score){
                this.id = title;
                this.score = score;
        }
        
        @Override
        public String toJSONString(){
                StringBuilder sb = new StringBuilder();
                
                sb.append("{");
                sb.append("\"").append(JSONObject.escape("WikipediaId")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(id)).append("\"");
                
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
                if(Double.parseDouble(((EsaResultWikipedia) o).score) > Double.parseDouble(this.score) ) {
                        return 1;
                } else if(Double.parseDouble(((EsaResultWikipedia) o).score) < Double.parseDouble(this.score) ) {
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

