/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.util.Language;
import de.citec.util.VectorSimilarity;

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

    /**
     * Creates a new instance of RawInputResource
     */
    public RawInputResource() throws IOException {
        this.vec = new VectorSimilarity("/Users/Fabian/Documents/Arbeit/AG_SD/Index", Language.DE);
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
        return vec.getArtikelsRawInput(rawInput, persons);
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
