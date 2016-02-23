/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.util.EsaResultJson;
import static de.citec.util.Language.DE;
import de.citec.util.TagCloud;
import de.citec.util.VectorSimilarity;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import org.json.simple.JSONArray;


/*
 curl -H "Accept: application/json" "http://localhost:8080/EsaService/webresources/run?date=2015-09-01&interests=Kunst,Kultur&onlyPersons=true"
*/
/**
 * REST Web Service
 *
 * @author swalter
 */
@Path("run")
public class RunResource {

    @Context
    private UriInfo context;
    
    private final Config config;

    public final VectorSimilarity vec;

    /**
     * Creates a new instance of RunResource2
     * @throws java.io.IOException
     */
    public RunResource() throws IOException {
                this.config = new Config();

        this.vec = new VectorSimilarity(DE,config);
    }

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RunResource
     * @param date
     * @param interests
     * @param onlyPersons
     * @param personid
     * @param json_input
     * @param tagCloud
     * @param numberArticles
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("date") String date, @QueryParam("interests") String interests,  @QueryParam("onlyPersons") String onlyPersons,
            @QueryParam("personid") String personid, @QueryParam("json") String json_input, @QueryParam("wordcloud") String word_cloud, 
            @QueryParam("numberArticles") String numberArticles, @Context HttpServletResponse response) throws SQLException, IOException {
        List<String> terms = new ArrayList<String>();
        int value = 500;
        try{
            value = Integer.valueOf(numberArticles);
        }
        catch(Exception e){}
        

        boolean wordcloud = false; 
        
        try{
            if(word_cloud.toLowerCase().contains("true")) wordcloud=true;
        }
        catch(Exception e){}
        //http://localhost:8080/EsaService/webresources/run?date=2016-02-22&wordcloud=true
        if(wordcloud){
            boolean persons = true;
            terms.addAll(getInterests("1"));
            Map<String,String> titel_id = new HashMap<>();
            Map<String, Double> result = vec.getArtikels(terms, date,persons, value,titel_id);
            Map<String, Integer> input = new HashMap<>();
            for(String w:result.keySet()){
                int input_value = (int) Math.rint(result.get(w)*10);
                input.put(w, input_value);
            }
            java.util.Date creation_date = new java.util.Date();
            //Long.toString(creation_date.getTime())+".png"
            String path = Long.toString(creation_date.getTime())+".png";
            TagCloud.createCloud(input,path); 

            return path;
        }
        else if(date!=null && personid!=null && onlyPersons!=null && interests!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            if(interests.contains(",")){
                terms.addAll(Arrays.asList(interests.split(",")));
            }
            else terms.add(interests);
            terms.addAll(getInterests(personid));
            boolean json = true;
            if(json_input!=null){
               if(json_input.toLowerCase().equals("false")) json=false;
            }
            Map<String,String> titel_id = new HashMap<>();
            Map<String, Double> result = vec.getArtikels(terms, date,persons, value,titel_id);
            vec.close();
            return createOutput(result,json,titel_id);
        }
        
        else if(date!=null && interests!=null && onlyPersons!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            if(interests.contains(",")){
                terms.addAll(Arrays.asList(interests.split(",")));
            }
            else terms.add(interests);
           boolean json = true;
           if(json_input!=null){
               if(json_input.toLowerCase().equals("false")) json=false;
           }
           Map<String,String> titel_id = new HashMap<>();
           Map<String, Double> result =vec.getArtikels(terms, date,persons, value, titel_id);
           vec.close();
           return createOutput(result,json,titel_id);
        }
        
        else if(date!=null && personid!=null && onlyPersons!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            boolean json = true;
            if(json_input!=null){
               if(json_input.toLowerCase().equals("false")) json=false;
            }
            Map<String,String> titel_id = new HashMap<>();
            Map<String, Double> result =  vec.getArtikels(getInterests(personid), date,persons, value, titel_id);
            vec.close();
            return createOutput(result,json,titel_id);
        }
        
        else{
            vec.close();
            return "ERROR";
        }
        
    }

    /**
     * PUT method for updating or creating an instance of RunResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    

//    /*
//   1 http://info.uni-bielefeld.de/kognihome/PaulBecker
//   2 http://info.uni-bielefeld.de/kognihome/AlexanderBecker
//   3 http://info.uni-bielefeld.de/kognihome/ChristinaBecker
//   4 http://info.uni-bielefeld.de/kognihome/KatharinaBecker
//   5 http://info.uni-bielefeld.de/kognihome/HeinrichBecker
//   6 http://info.uni-bielefeld.de/kognihome/LottaBecker
//   7 http://info.uni-bielefeld.de/kognihome/NinaBecker    
//                
//    */
    private List<String> getInterests(String id) {
        String personURI = "";
        List<String> interests = new ArrayList<>();
        
        switch(id){
            case "1":
                personURI="http://info.uni-bielefeld.de/kognihome/PaulBecker";
                interests.add("BMX");
                interests.add("Fussball");
                interests.add("Fußball");
                break;
                
            case "2":
                personURI="http://info.uni-bielefeld.de/kognihome/AlexanderBecker";
                interests.add("Fitness");
                break;
                
            case "3":
                personURI="http://info.uni-bielefeld.de/kognihome/ChristinaBecker";
                interests.add("Pilates");
                interests.add("Rennrad");
                interests.add("Gesunde_Ernährung");
                break;
                
            case "4":
                personURI="http://info.uni-bielefeld.de/kognihome/KatharinaBecker";
                interests.add("Gartenarbeit");
                interests.add("Sauna");
                interests.add("Kaffekranz");
                interests.add("Walking");
                interests.add("Aquajogging");
                interests.add("klassische_musik");
                break;
                
            case "5":
                personURI="http://info.uni-bielefeld.de/kognihome/HeinrichBecker";
                interests.add("Musik_68_und_70er");
                break;
           
            case "6":
                personURI="http://info.uni-bielefeld.de/kognihome/LottaBecker";
                interests.add("mit_grossem_bruder_spielen");
                break;
            
            case "7":
                personURI="http://info.uni-bielefeld.de/kognihome/NinaBecker";
                interests.add("Social_Media");
                interests.add("Flohmarkt");
                interests.add("Schwimmen");
                break;
                
            default:
                personURI = "http://info.uni-bielefeld.de/kognihome/PaulBecker";            
            
        }
//        System.out.println("personURI:"+personURI);
//        Query query = QueryFactory.create("select distinct ?interest where {<"+personURI+"> <http://xmlns.com/foaf/0.1/interest> ?interest} LIMIT 100");
//        System.out.println("query:"+query);
//        QueryExecution qExec = QueryExecutionFactory.sparqlService("http://129.70.129.138:8892/sparql", query);
//        ResultSet rs = qExec.execSelect() ;
//        try {
//         while ( rs.hasNext() ) {
//                 QuerySolution qs = rs.next();
//                 try{
//                         interests.add(qs.get("?interest").toString().replace("http://info.uni-bielefeld.de/kognihome/", ""));	
//                  }
//                 catch(Exception e){
//                }
//             }
//        }
//        catch(Exception e){
//        }
//        qExec.close() ;  
        
        for(String i: interests){
            System.out.println(i);
        }
        return interests;
    }

    private String createOutput(Map<String, Double> result, boolean json, Map<String,String> titel_id) {
        ArrayList<EsaResultJson> esaResults = new ArrayList<>();
        JSONArray results = new JSONArray();
//        System.out.println(overall_results.size());
        for(String key : result.keySet()){
            esaResults.add(new EsaResultJson(titel_id.get(key),key,Double.toString(result.get(key))));
        }
        Collections.sort(esaResults);
        results.addAll(esaResults);
        if(json)
            return JSONArray.toJSONString(results);
        else
            return getPlainTitle(esaResults);
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
}
