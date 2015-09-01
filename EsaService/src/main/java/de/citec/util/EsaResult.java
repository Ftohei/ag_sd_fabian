
package de.citec.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class EsaResult implements JSONAware{
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
}

