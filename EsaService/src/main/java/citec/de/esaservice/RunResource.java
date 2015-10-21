/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.util.Language;
import de.citec.util.VectorSimilarity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;


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

        this.vec = new VectorSimilarity(config.getPathIndexGerman(),Language.DE,config);
    }

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RunResource
     * @param date
     * @param interests
     * @param onlyPersons
     * @param personid
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("date") String date, @QueryParam("interests") String interests,  @QueryParam("onlyPersons") String onlyPersons,@QueryParam("personid") String personid) {
        List<String> terms = new ArrayList<String>();
        if(date!=null && interests!=null && onlyPersons!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            if(interests.contains(",")){
                for(String i:interests.split(","))terms.add(i);
            }
            else terms.add(interests);
           return vec.getArtikels(terms, date,persons);
        }
        
        else if(date!=null && personid!=null && onlyPersons!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            return vec.getArtikels(getInterests(personid), date,persons);
        }
        
        else if(date!=null && personid!=null && onlyPersons!=null && interests!=null){
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            if(interests.contains(",")){
                for(String i:interests.split(","))terms.add(i);
            }
            else terms.add(interests);
            terms.addAll(getInterests(personid));
            return vec.getArtikels(terms, date,persons);
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
        
        switch(id){
            case "1":
                personURI="http://info.uni-bielefeld.de/kognihome/PaulBecker";
                break;
                
            case "2":
                personURI="http://info.uni-bielefeld.de/kognihome/AlexanderBecker";
                break;
                
            case "3":
                personURI="http://info.uni-bielefeld.de/kognihome/ChristinaBecker";
                break;
                
            case "4":
                personURI="http://info.uni-bielefeld.de/kognihome/KatharinaBecker";
                break;
                
            case "5":
                personURI="http://info.uni-bielefeld.de/kognihome/HeinrichBecker";
                break;
           
            case "6":
                personURI="http://info.uni-bielefeld.de/kognihome/LottaBecker";
                break;
            
            case "7":
                personURI="http://info.uni-bielefeld.de/kognihome/NinaBecker";
                break;
                
            default:
                personURI = "http://info.uni-bielefeld.de/kognihome/PaulBecker";            
            
        }
        System.out.println("personURI:"+personURI);
        Query query = QueryFactory.create("select distinct ?interest where {<"+personURI+"> <http://xmlns.com/foaf/0.1/interest> ?interest} LIMIT 100");
        System.out.println("query:"+query);
        List<String> interests = new ArrayList<>();
        QueryExecution qExec = QueryExecutionFactory.sparqlService("http://129.70.129.138:8892/sparql", query);
        ResultSet rs = qExec.execSelect() ;
        try {
         while ( rs.hasNext() ) {
                 QuerySolution qs = rs.next();
                 try{
                         interests.add(qs.get("?interest").toString().replace("http://info.uni-bielefeld.de/kognihome/", ""));	
                  }
                 catch(Exception e){
                }
             }
        }
        catch(Exception e){
        }
        qExec.close() ;  
        
        
        return interests;
    }
}
