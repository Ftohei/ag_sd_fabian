/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikiindexercomplete;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fabiankaupmann
 */
public class WikiIndexerGermanComplete {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            WikiIndexer indexer = new WikiIndexer(args[0]);
//            WikiIndexer indexer = new WikiIndexer("/Users/fabiankaupmann/Desktop/testFullWiki");
            
            indexer.createIndex(args[1]);
//            indexer.createIndex("/Users/fabiankaupmann/Desktop/text.txt");

        } catch (IOException ex) {
            Logger.getLogger(WikiIndexerGermanComplete.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
