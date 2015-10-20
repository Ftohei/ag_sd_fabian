/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.util.Language;
import static de.citec.util.Language.DE;
import de.citec.util.VectorSimilarity;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * REST Web Service
 *
 * @author fkaupmann
 */
@Path("idList")
public class IdListResource
{

    /**
     * Work in Progress, not working
     */

    private Config config;
    
    @Context
    private UriInfo context;

    private final VectorSimilarity vec;

    private MaxentTagger tagger;

    /**
     * Creates a new instance of RawInputResource
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.ClassNotFoundException
     */
    public IdListResource() throws IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException, DOMException, Exception {
//        this.config.loadFromFile("config.xml");
        this.vec = new VectorSimilarity(config.getPathIndexGerman(), DE,config);
//        this.tagger = new MaxentTagger("/Users/Fabian/Documents/Arbeit/AG_SD/ag_sd_fabian/EsaService/taggers/german-fast.tagger");
    }
//
//
//    /*
//    curl "http://localhost:8080/EsaService-1.0-SNAPSHOT/webresources/idList?idListPath="/Users/Fabian/Documents/Arbeit/AG_SD/getaggteArtikelNachAlter/50-59.txt"&onlyPersons=true"
//    */
//
    /**
     * Retrieves representation of an instance of citec.de.esaservice.RawInputResource
     * @param idListPath
     * @param onlyPersons
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText(@QueryParam("idListPath") String idListPath, @QueryParam("onlyPersons") String onlyPersons) {
//        Reader reader = null;
//        List<String> ids = new ArrayList<>();
//
//        try {
//            reader = new FileReader(idListPath);
//            String id = "";
//            int c;
//            int count = 0;
//            while ( (c = reader.read()) != -1){
//                if( ((char) c) == '\n') {
//                    if ( count == 0 ) {
//                        System.out.println("Erste ZEILE: " + id);
//                        count++;
//                    }
//                    ids.add(id);
//                    id = "";
//                } else {
//                    id = id + Character.toString((char) c);
//                }
//            }
//            ids.add(id);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        boolean persons = true;
//        if(onlyPersons.contains("false")) persons=false;
//        return vec.getArticlesListOfNwArticleIds(ids, persons);
        return null;
    }
//
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
