/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.de.esaservice;

import de.citec.io.Config;
import de.citec.io.DatabaseAction;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author swalter
 */
@Path("mapping")
public class MappingResource {

    @Context
    private UriInfo context;
    private final Config config;
    private final DatabaseAction rd;
    

    /**
     * Creates a new instance of MappingResource
     * @throws java.io.IOException
     */
    public MappingResource() throws IOException {
        this.config = new Config();
        this.rd = new DatabaseAction(config);
    }

    /**
     * Retrieves representation of an instance of citec.de.esaservice.MappingResource
     * @param term
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("term") String term) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String result =  rd.getMapping(term.toLowerCase());
        rd.close();
        return result;
    }

    /**
     * PUT method for updating or creating an instance of MappingResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
