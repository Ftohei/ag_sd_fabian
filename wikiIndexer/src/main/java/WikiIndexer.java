import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fabian on 08.08.15.
 */
public class WikiIndexer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /**
         * TODO: Einstellung, ob englisch oder deutsch indexiert werden soll
         */

        if(args[0] == null || args[1] == null || args[2] == null){
            System.out.println("Falsche argumente übergeben! (index-Verzeichnis Datensatz Sprache");
            System.exit(0);
        }

        try {
            Indexer indexer = new Indexer(args[0], args[2]);
//            WikiIndexer indexer = new WikiIndexer("/Users/Fabian/Documents/Arbeit/AG SD/testIndex");

            indexer.createIndex(args[1]);
//            indexer.createIndex("/Users/Fabian/Documents/Arbeit/AG SD/testIndex/wikiTest50.txt");

        } catch (IOException ex) {
            Logger.getLogger(WikiIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

}
