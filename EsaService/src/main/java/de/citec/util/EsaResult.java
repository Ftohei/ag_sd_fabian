
package de.citec.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class EsaResult implements JSONAware, Comparable{
        private final String id;
        private final String title;
        private final String score;
        
        public EsaResult(String id, String title, String score){
                this.id = id;
                this.title = title;
                this.score = score;
        }
        
        @Override
        public String toJSONString(){
                StringBuilder sb = new StringBuilder();
                
                sb.append("{");
                
                sb.append(JSONObject.escape("ID"));
                sb.append(":");
                sb.append(id);
                
                sb.append(",");
                
                sb.append(JSONObject.escape("Title"));
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(title)).append("\"");
                
                sb.append(",");
                
                sb.append(JSONObject.escape("Score"));
                sb.append(":");
                sb.append(score);
                
                sb.append("}");
                
                return sb.toString();
        }

        @Override
        public int compareTo(Object o) {
                //sortiert abwärts
                if(Double.parseDouble(((EsaResult) o).score) > Double.parseDouble(this.score) ) {
                        return 1;
                } else if(Double.parseDouble(((EsaResult) o).score) < Double.parseDouble(this.score) ) {
                        return -1;
                }
                return 0;
        }
        
        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getScore() {
            return score;
        }
}

