
package de.citec.util;

import java.util.List;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author swalter
 */

public class MappingResultJson implements JSONAware, Comparable{
        private final String term;
        private final String score;
        private final List<String> classTerms;
        
        public MappingResultJson(String term, String score, List<String> classTerms){
                this.term = term;
                this.score = score;
                this.classTerms = classTerms;
        }
        
        @Override
        public String toJSONString(){
                StringBuilder sb = new StringBuilder();
                
                sb.append("{");
                sb.append("\"").append(JSONObject.escape("Term")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(term)).append("\"");
                
                sb.append(",");
                
                sb.append("\"").append(JSONObject.escape("Score")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(score)).append("\"");
                String listClasses = "";
                for(String x:classTerms){
                    listClasses+=";"+x;
                }
                listClasses = listClasses.replaceFirst(";", "");
                sb.append(",");
                sb.append("\"").append(JSONObject.escape("Classes")).append("\"");
                sb.append(":");
                sb.append("\"").append(JSONObject.escape(listClasses)).append("\"");
                
                sb.append("}");
                
                return sb.toString();
        }

        @Override
        public int compareTo(Object o) {
                //sortiert abwärts
                if(Double.parseDouble(((MappingResultJson) o).score) > Double.parseDouble(this.score) ) {
                        return 1;
                } else if(Double.parseDouble(((MappingResultJson) o).score) < Double.parseDouble(this.score) ) {
                        return -1;
                }
                return 0;
        }
        

        public String getTitle() {
            return term;
        }

        public String getScore() {
            return score;
        }
}

