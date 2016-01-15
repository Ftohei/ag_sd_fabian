
package de.citec.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class EsaResultJson implements JSONAware, Comparable{
        private final String id;
        private final String titel;
        private final String score;
        
        public EsaResultJson(String id, String title, String score){
                this.id = id;
                this.titel = title;
                this.score = score;
        }
        
        @Override
        public String toJSONString(){
                StringBuilder sb = new StringBuilder();
                
                sb.append("{");
                
                sb.append("\"").append(JSONObject.escape("ID")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(id)).append("\"");
                
                sb.append(",");
                
                sb.append("\"").append(JSONObject.escape("Titel")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(titel)).append("\"");
                
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
                if(Double.parseDouble(((EsaResultJson) o).score) > Double.parseDouble(this.score) ) {
                        return 1;
                } else if(Double.parseDouble(((EsaResultJson) o).score) < Double.parseDouble(this.score) ) {
                        return -1;
                }
                return 0;
        }
        
        public String getId() {
            return id;
        }

        public String getTitle() {
            return titel;
        }

        public String getScore() {
            return score;
        }
}

