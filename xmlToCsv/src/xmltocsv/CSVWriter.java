/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltocsv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fabiankaupmann
 */
public class CSVWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPERATOR = "\n";
    
    private static final String HEADER = "ArtikelId,Titel,Konfidenzen,WikiPageID,WikiTitel,Score,URL";
    
       
    public void writeCsvFile(String filename, ArrayList<CSVPerson> persons){
        
        FileWriter fileWriter = null;
        
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.append(HEADER);
            fileWriter.append(NEW_LINE_SEPERATOR);
            
            for(CSVPerson person : persons){
                fileWriter.append(String.valueOf(person.getName()));
                fileWriter.append(NEW_LINE_SEPERATOR);
                for(ArticleWithResults article : person.getArticleList()){
                    for(int i = 0; i<article.getPageIds().size(); i++){
                        fileWriter.append(String.valueOf(article.getArticleId()));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf(article.getTitle()));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf(article.getConfidence()));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf(article.getPageIds().get(i)));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf(article.getWikiTitles().get(i)));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf(article.getScores().get(i)));
                        fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(String.valueOf("http://de.wikipedia.org/wiki?curid=" + article.getPageIds().get(i)));
                        fileWriter.append(NEW_LINE_SEPERATOR);

                        
                        
                    }
                    
                }
                fileWriter.append(NEW_LINE_SEPERATOR);
                fileWriter.append(NEW_LINE_SEPERATOR);
                
                System.out.println("CSV für " + person.getName() + " erfolgreich!");

            }
        } catch (IOException ex) {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    
}
