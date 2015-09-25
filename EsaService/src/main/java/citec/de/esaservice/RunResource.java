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
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

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
     * Creates a new instance of RunResource
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.ClassNotFoundException
     */
    public RunResource() throws IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException, DOMException, Exception {
        this.config.loadFromFile("config.xml");
        this.vec = new VectorSimilarity(config.getPathIndexGerman(),Language.DE,config);
    }

    /*
     curl "http://localhost:8080/EsaService/webresources/run?date=2015-08-28&input=Kunst,Kultur&onlyPersons=true"
     curl "http://localhost:8080/EsaService-1.0-SNAPSHOT/webresources/run?date=2015-08-28&input=Kunst,Kultur&onlyPersons=true"
    */

    /**
     * Retrieves representation of an instance of citec.de.esaservice.RunResource
     * @param date
     * @param input
     * @param onlyPersons
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/html")
    public String getHtml(@QueryParam("date") String date, @QueryParam("input") String input,  @QueryParam("onlyPersons") String onlyPersons) {
        List<String> terms = new ArrayList<String>();
        boolean persons = true;
        if(onlyPersons.contains("false")) persons=false;
        if(input.contains(",")){
            for(String i:input.split(","))terms.add(i);
        }
        else terms.add(input);
        
       return vec.getArtikels(terms, date,persons);
    }

    /**
     * PUT method for updating or creating an instance of RunResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/html")
    public void putHtml(String content) {
    }
}
