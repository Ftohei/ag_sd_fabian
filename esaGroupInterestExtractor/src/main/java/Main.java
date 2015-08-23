import java.io.*;
import java.util.ArrayList;

/**
 * Created by Fabian on 21.08.15.
 */
public class Main{

        public static void main(String[] args) throws IOException {

            /**
             * String aus allen Artikeln aufbauen
             * auf Index abfeuern
             * ergebnisse in File abspeichern
             */
            
            String pathToTaggedArticles = args[0];

            String pathToIndex = args[1];

            int numberOfEsaResults = Integer.parseInt(args[2]);

            String language = args[3];

            boolean includePersonArticles = false;
            if (args[4].equals("1")) {
                includePersonArticles = true;
            }

            String outputDir = args[5];

            Searcher seacher = new Searcher(pathToIndex, numberOfEsaResults, language, includePersonArticles);

            int[] minAges = {30, 40, 50, 60, 70};

            int minAge;
            int maxAge;

            ArrayList<GroupWithResults> groupList = new ArrayList<>();

            for (int i = 0; i < minAges.length; i++) {
                minAge = minAges[i];
                maxAge = minAge + 9;

                GroupWithResults groupWithResults = new GroupWithResults(minAge, maxAge);

                groupList.add(groupWithResults);

                Reader reader = null;
                BufferedReader br = null;

                try {
                    reader = new FileReader(pathToTaggedArticles + "/" + minAge + "-" + maxAge + ".txt");
                    br = new BufferedReader(reader);

                    String line = br.readLine();

                    groupWithResults.setTaggedSearchQuery(line);

                    seacher.addResultsToGroup(groupWithResults, line);

                    br.close();
                    reader.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
                FileWriter fileWriter = new FileWriter(outputDir + "/resultsPerAgeGroup.txt");
                for (GroupWithResults groupWithResults : groupList) {
                    fileWriter.write("Gruppe " + groupWithResults.getMinAge() + " - " + groupWithResults.getMaxAge());
                    fileWriter.append("\n");
                    for (int i = 0; i < groupWithResults.getPageIds().size(); i++) {
                        fileWriter.write(groupWithResults.getPageIds().get(i) + " " +
                                groupWithResults.getWikiTitles().get(i) + " " + groupWithResults.getScores().get(i));
                        fileWriter.append("\n");
                    }
                }
                fileWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
}
