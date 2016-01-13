/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.io.DatabaseAction;
import de.citec.util.Language;
import static de.citec.util.Language.DE;
import static de.citec.util.Language.EN;
import de.citec.util.VectorSimilarity;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author swalter
 */
@Path("rawInput")
public class RawInputResource {

    @Context
    private UriInfo context;
    
    private final Config config;
    
    private VectorSimilarity vec;

    private final MaxentTagger tagger;

    private final DatabaseAction rd;

    /**
     * Creates a new instance of RawInputResource2
     * @throws java.io.IOException
     */
    public RawInputResource() throws IOException {
        this.config = new Config();
        this.tagger = new MaxentTagger(config.getPathTagger());
        this.rd = new DatabaseAction(config);
    }

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RawInputResource
     * @param interests
     * @param onlyPersons
     * @param language_input
     * @param artikelid
     * @return an instance of java.lang.String
     * @throws java.lang.Exception
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("interests") String interests, @QueryParam("onlyPersons") String onlyPersons,
                          @QueryParam("language") String language_input, @QueryParam("artikelid") String artikelid) throws Exception {
        Language language = DE;
        if(language_input!=null){
            language = getLanguage(language_input);
        }
        
        
        
        if(interests!=null && onlyPersons!=null){
            try {
            switch (language){
                case DE:
                    this.vec = new VectorSimilarity( DE,config);
                    break;
                case EN:
                    this.vec = new VectorSimilarity( EN,config);
                    break;
                default:
                    this.vec = new VectorSimilarity( DE,config);
                    break;
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean persons = true;
            if(onlyPersons.contains("false")) persons=false;
            return vec.getArtikelsRawInput(interests, persons, tagger);
        }
        else if(artikelid!=null){
            return rd.getInformations(artikelid);
        }
        else return "ERROR";
        
        
    }
    
    

    /**
     * PUT method for updating or creating an instance of RawInputResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    
    
    private Language getLanguage(String s) throws Exception{       
            if      (s.toLowerCase().equals("en") || s.toLowerCase().equals("eng")) return EN;
            else if (s.toLowerCase().equals("de") || s.toLowerCase().equals("ger")) return DE;
            else throw new Exception("Language '" + s + "' unknown.");
    }
}
