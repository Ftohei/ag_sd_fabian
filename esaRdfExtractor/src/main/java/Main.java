import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        //args: tagger index numberOfEsaResults includePersonArticles

        int numberOfESAResults = 100;
        if(args[2] != null){
            numberOfESAResults = Integer.parseInt(args[2]);
        }

        boolean includePersonArticles = false;
        if(args[3] != null && Integer.parseInt(args[3]) == 1){
            includePersonArticles = true;
        }

        boolean onlyPersonArticles = false;
        if(args[4] != null && Integer.parseInt(args[4]) == 1){
            onlyPersonArticles = true;
        }

        RdfExtractor rdfExtractor = new RdfExtractor();
        ArrayList<PersonWithInterests> personsWithInterests = rdfExtractor.extractInterests();
//        MaxentTagger tagger = new MaxentTagger("/Users/Fabian/Documents/Dev/intellijProjects/esaXmlToDatabase/taggers/german-fast.tagger");
        MaxentTagger tagger = new MaxentTagger(args[0]);

        try {
            Searcher searcher = new Searcher(args[1],
                    numberOfESAResults, "german", includePersonArticles, onlyPersonArticles);


            for(PersonWithInterests personWithInterests : personsWithInterests){
                System.out.print(personWithInterests.getFullName() + ": ");

                String taggedInterests = "";

                for(String interest : personWithInterests.getInterests()){
                    System.out.print(interest + ", ");

                    taggedInterests = taggedInterests + " " + tagger.tagString(interest);
                }

                System.out.print("\n");

                personWithInterests = searcher.addResultsToPerson(personWithInterests, taggedInterests);

                for(int i = 0; i<personWithInterests.getWikiTitles().size(); i++){
                    System.out.println("\t" + personWithInterests.getWikiTitles().get(i) + " --- " + personWithInterests.getScores().get(i));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
