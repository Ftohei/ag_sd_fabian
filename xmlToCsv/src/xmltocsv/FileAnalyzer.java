/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltocsv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author fabiankaupmann
 */
public class FileAnalyzer {
    
    private boolean isName = true;
    private String name = "";
    private String articleId = "";
    private String confidence = "";
    
    
    public FileAnalyzer(){
        
    }
    
    public ArrayList<CSVPerson> getPersons(String filePath) throws FileNotFoundException{
            
            ArrayList<CSVPerson> persons = new ArrayList();
             
            Scanner s = new Scanner(new BufferedReader(new FileReader(filePath)));
            
            ArrayList<String> articleIds = new ArrayList();
            ArrayList<String> confidences = new ArrayList();
            
            while (s.hasNextLine()) {
                Scanner l = new Scanner(s.nextLine());
                l.useDelimiter("\\s");
                if(this.isName == true && l.hasNext()){
                    //wenn neuer Name kommt, den alten erst speichern
//                    System.out.println("--------");
//                    System.out.println(this.name);
//                    System.out.println(articleIds);
//                    System.out.println(confidences);
                    persons.add(new CSVPerson(this.name,articleIds,confidences));
                    articleIds.clear();
                    confidences.clear();
                    this.name = "";
                    while(l.hasNext()){
                        this.name = this.name + " " + l.next();
                    }
                    this.isName = false;
                } else if(!l.hasNext()){
                    this.isName = true;
                } else {
                    while(l.hasNext()){
                        this.articleId = l.next();
                        this.confidence = l.next();
                        articleIds.add(this.articleId);
                        confidences.add(this.confidence);
                   } 
                }
                
                
                
//                System.out.println(this.name + " " + this.articleId + " " + this.confidence);    
                
            }
            persons.add(new CSVPerson(this.name,articleIds,confidences));
            
            return persons;
        }
    
}
