/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.util.Language;
import de.citec.util.VectorSimilarity;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Web Service
 *
 * @author swalter
 */
@Path("rawInput")
public class RawInputResource {

    @Context
    private UriInfo context;

    private final VectorSimilarity vec;

    private MaxentTagger tagger;

    /**
     * Creates a new instance of RawInputResource
     */
    public RawInputResource() throws IOException {
        this.vec = new VectorSimilarity("/Users/Fabian/Documents/Arbeit/AG_SD/Index", Language.DE);
        this.tagger = new MaxentTagger("/Users/Fabian/Documents/Arbeit/AG_SD/ag_sd_fabian/EsaService/taggers/german-fast.tagger");
    }


    /*
     curl "http://localhost:8080/EsaService-1.0-SNAPSHOT/webresources/rawInput?rawInput="Morihei_Ueshiba"&onlyPersons=true"
    */

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RawInputResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("rawInput") String rawInput, @QueryParam("onlyPersons") String onlyPersons) {
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
}
