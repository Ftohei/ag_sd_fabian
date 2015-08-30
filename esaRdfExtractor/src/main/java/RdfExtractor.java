import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

import java.util.ArrayList;


/**
 * Created by fabiankaupmann on 31.03.15.
 */
public class RdfExtractor {

    private String rdfFilePath;

    public RdfExtractor( String rdfFilePath){
        this.rdfFilePath = rdfFilePath;
    }

    public  ArrayList<PersonWithInterests> extractInterests() {
        // create an empty model
        Model model = RDFDataMgr.loadModel(this.rdfFilePath);

        Resource interest = model.getResource("http://info.uni-bielefeld.de/kognihome/Rennrad");

        // Create a new query
        String queryString =
                "PREFIX  foaf: <http://xmlns.com/foaf/0.1/> " +
                    "SELECT ?givenName ?familyName ?interestLabel " +
                    "WHERE {" +
                    "      ?x <http://www.w3.org/2000/01/rdf-schema#label> ?z  . " +
                    "      ?x  foaf:givenName ?givenName  . " +
                    "      ?x  foaf:familyName ?familyName    . " +
                    "      ?x  foaf:interest  ?interest ." +
                    "      ?interest <http://www.w3.org/2000/01/rdf-schema#label> ?interestLabel " +
                    "      }";

        Query query = QueryFactory.create(queryString);

// Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

// Output query results

        ArrayList<PersonWithInterests> personsWithInterests = new ArrayList<>();
        int i = 0;

        String person = "start";


        while(results.hasNext()) {
            QuerySolution row = results.nextSolution();
            String currentPerson = row.getLiteral("givenName").getString() + " " + row.getLiteral("familyName").getString();
            if(person.equals(currentPerson)){
                personsWithInterests.get(i).getInterests().add(row.getLiteral("interestLabel").getString());
            } else {
                person = currentPerson;
                personsWithInterests.add(new PersonWithInterests(row.getLiteral("givenName").getString(), row.getLiteral("familyName").getString()));
                i = personsWithInterests.size() - 1;
                personsWithInterests.get(i).getInterests().add(row.getLiteral("interestLabel").getString());
            }
        }

        qe.close();

        return personsWithInterests;

    }
}


