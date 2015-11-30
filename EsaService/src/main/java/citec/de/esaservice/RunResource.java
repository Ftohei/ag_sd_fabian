/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import static de.citec.util.Language.DE;
import de.citec.util.VectorSimilarity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;


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
    
    private Config config;

    public final VectorSimilarity vec;

    /**
     * Creates a new instance of RunResource2
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
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("date") String date, @QueryParam("interests") String interests,  @QueryParam("onlyPersons") String onlyPersons,@QueryParam("personid") String personid, @QueryParam("json") String json_input) {
        List<String> terms = new ArrayList<String>();
        if(date!=null && interests!=null && onlyPersons!=null){
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
           return vec.getArtikels(terms, date,persons,json);
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
            return vec.getArtikels(terms, date,persons,json);
        }
        
        else if(date!=null && personid!=null && onlyPersons!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            boolean json = true;
            if(json_input!=null){
               if(json_input.toLowerCase().equals("false")) json=false;
            }
            return vec.getArtikels(getInterests(personid), date,persons,json);
        }
        
        else return "ERROR";
        
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
}
