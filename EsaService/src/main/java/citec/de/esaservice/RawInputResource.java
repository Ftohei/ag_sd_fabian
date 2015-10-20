/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.io.ReadDatabase;
import de.citec.util.Language;
import static de.citec.util.Language.DE;
import static de.citec.util.Language.EN;
import de.citec.util.VectorSimilarity;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * REST Web Service
 *
 * @author swalter
 */
@Path("rawInput")
public class RawInputResource {

    private Config config;
    @Context
    private UriInfo context;

    private VectorSimilarity vec;

    private MaxentTagger tagger;

    private ReadDatabase rd;
    /**
     * Creates a new instance of RawInputResource
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.ClassNotFoundException
     */
    public RawInputResource() throws IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException, DOMException, Exception {
        
        this.config = new Config();
        this.tagger = new MaxentTagger(config.getPathTagger());
        this.rd = new ReadDatabase(config);
    }


    /*
     curl "http://localhost:8080/EsaService-1.0-SNAPSHOT/webresources/rawInput?rawInput="informatik"&onlyPersons="true"&language="de""
    */

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RawInputResource
     * @param rawInput
     * @param onlyPersons
     * @param language_input
     * @return an instance of java.lang.String
     * @throws java.lang.Exception
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("rawInput") String rawInput, @QueryParam("onlyPersons") String onlyPersons,
                          @QueryParam("language") String language_input) throws Exception {
        Language language = getLanguage(language_input);
        
        try {
            switch (language){
                case DE:
                    this.vec = new VectorSimilarity(config.getPathIndexGerman(), DE,config);
                    break;
                case EN:
                    this.vec = new VectorSimilarity(config.getPathIndexEnglish(), EN,config);
                    break;
                default:
                    this.vec = new VectorSimilarity(config.getPathIndexGerman(), DE,config);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean persons = true;
        if(onlyPersons.contains("false")) persons=false;
        return vec.getArtikelsRawInput(rawInput, persons, tagger);
    }

    /**
     * PUT method for updating or creating an instance of RawInputResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/plain")
    public void putText(String content) {
    }

    private Language getLanguage(String s) throws Exception{       
            if      (s.toLowerCase().equals("en") || s.toLowerCase().equals("eng")) return EN;
            else if (s.toLowerCase().equals("de") || s.toLowerCase().equals("ger")) return DE;
            else throw new Exception("Language '" + s + "' unknown.");
    }
    
    
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("artikelid") String artikelid) {

        return rd.getInformations(artikelid);
    }
}
