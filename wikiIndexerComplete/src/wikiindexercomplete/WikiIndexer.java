/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikiindexercomplete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author fabiankaupmann
 */
public class WikiIndexer {
    
    

    private String indexDir;
    
    private IndexWriter writer;
    
    private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LATEST);
    
    private GermanAnalyzer germanAnalyzer = new GermanAnalyzer(Version.LATEST);
  
    private Directory directory;
    
    private HashSet<String> adjectiveTags = new HashSet<String>();
        
    private HashSet<String> nounTags = new HashSet<String>();
    
    private ArrayList<File> queue = new ArrayList<File>();
    
    private String articleId;
    
    private String lastId = "0";
    
    private String articleNounsAndAdjectives = "";
    
    public WikiIndexer(String indexDir) throws IOException{
        this.indexDir = indexDir;
        
        this.directory = FSDirectory.open(new File(indexDir));
        
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        
        this.writer = new IndexWriter(directory, config);
        
        
        this.adjectiveTags.add("ADJA");
        this.adjectiveTags.add("ADJD");
        
        this.nounTags.add("NN");
        this.nounTags.add("NE");
    }
    
    public void createIndex(String filePath) throws IOException{
        this.indexInput(filePath);
        
        this.closeIndex();
        
        System.out.println("Index erfolgreich erstellt");
    }
    
    private void indexInput(String filePath) throws FileNotFoundException{
        File file = new File(filePath);
        this.processFileOrDirectory(file);
        
    }
    
    private void index(String id, String title, String abstr, String text) {
        try {
            Document doc = new Document();
            
            doc.add(new Field("id", id, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("abstract", abstr, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
            
            this.writer.addDocument(doc);
            
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            
            out.println("---------");
            out.println("Artikel wird indexiert:");
            out.println("id: "  + id);
            out.println("Titel: " + title);
            out.println(abstr);
            out.println(text);
            out.println("---------");
            
        } catch (IOException ex) {
            Logger.getLogger(WikiIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeIndex() throws IOException {
        writer.close();
    }
       
    private void processFileOrDirectory(File file){
        if (!file.exists()) {
            System.out.println(file + " existiert nicht.");
          }
        if (file.isDirectory()) {
          for (File f : file.listFiles()) {
            processFileOrDirectory(f);
          }
        } else {
          String filename = file.getName().toLowerCase();
          if (filename.endsWith(".htm") || filename.endsWith(".html") || 
                  filename.endsWith(".xml") || filename.endsWith(".txt") || filename.endsWith(".tagged")) {
              try {
                  processFile(file.getPath());
              } catch (FileNotFoundException ex) {
                  Logger.getLogger(WikiIndexer.class.getName()).log(Level.SEVERE, null, ex);
              }
          } else {
            System.out.println("Skipped " + filename);
          }
        }
    }
        
    public String processFile(String filePath) throws FileNotFoundException{
        Scanner s = null;
        String result = "";

        try {
            s = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8")));

            while (s.hasNextLine()) {
                processLine(s.nextLine());
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WikiIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return result;
    }

    public void processLine(String line){
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter("######");
        
        String id = "";
        if(lineScanner.hasNext()){
            id = lineScanner.next();
        }

        String title = "";
        if(lineScanner.hasNext()){
            title = lineScanner.next();
        }

        
        String abstr = "";
        if(lineScanner.hasNext()){
            abstr = lineScanner.next();
        }
        
        String text = "";
        if(lineScanner.hasNext()){
            text = lineScanner.next();
        }

        
        String titleProcessed = this.processTitle(title);
        String abstrProcessed = this.processPart(abstr);
        String textProcessed = this.processPart(text);
        
        this.index(id, titleProcessed, abstrProcessed, textProcessed);
        
    }

    public String processTitle(String title){
        Scanner preciseScanner = new Scanner(title);
        preciseScanner.useDelimiter("\\s+|\\_");
        
        ArrayList<String> words = new ArrayList();
        ArrayList<String> postags = new ArrayList();
        
        while(preciseScanner.hasNext()){
            words.add(preciseScanner.next());
            if(preciseScanner.hasNext()){
                postags.add(preciseScanner.next());
            }
        }
        
        String titleToIndex = "";
        for(String word : words){
            titleToIndex = titleToIndex + " " + word;
        }
        return titleToIndex;
    }
    
    public String processPart(String part){
        Scanner preciseScanner = new Scanner(part);
        preciseScanner.useDelimiter("\\s+|\\_");
        
        ArrayList<String> words = new ArrayList();
        ArrayList<String> postags = new ArrayList();
        
        while(preciseScanner.hasNext()){
            words.add(preciseScanner.next());
            if(preciseScanner.hasNext()){
                postags.add(preciseScanner.next());
            }
        }
        
        String wordsToIndex = "";
        
        for(int j = 0; j<(postags.size()); j++){
            
            if(j<(postags.size() - 1)){
                
                
                //checken: Adjektiv * Nomen +, dh. 0 oder mehr adjektive, 1 oder mehr Nomen
                if(nounTags.contains(postags.get(j))){
                    wordsToIndex = wordsToIndex + " " + words.get(j);
                    
                } else if(adjectiveTags.contains(postags.get(j))){
                    //Zähle die Adjektive                    
                    int k = 0;
                    while(adjectiveTags.contains(postags.get(j+k))){
                        if(j+k<(postags.size()-1)){
                            k++;
                        } else {
                            break;
                        }                    }
                    //Sobald ein Nicht-Adjektiv kommt, prüfe ob es ein Nomen ist. Wenn nein, Wörter verwerfen
                    if(nounTags.contains(postags.get(j+k))){
                        for(int l=j; l<=(j+k); l++){
                            wordsToIndex = wordsToIndex + " " + words.get(l);
                        } 
                    }
                    //Springe an nächste zu prüfende Stelle
                    j = j + k + 1;
                    
                }
                
                
            } else {

                if(nounTags.contains(postags.get(j))){
                    wordsToIndex = wordsToIndex + " " + words.get(j);
                } else if(adjectiveTags.contains(postags.get(j))){
                    //Zähle die Adjektive
                    int k = 0;
                    while(adjectiveTags.contains(postags.get(j+k))){
                        if(j+k<(postags.size()-1)){
                            k++;
                        } else {
                            break;
                        }
                    }
                    //Sobald ein Nicht-Adjektiv kommt, prüfe ob es ein Nomen ist. Wenn nein, Wörter verwerfen
                    if(nounTags.contains(postags.get(j+k))){
                        for(int l=j; l<=(j+k); l++){
                            wordsToIndex = wordsToIndex + " " + words.get(l);
                        } 
                    }
                    //Springe an nächste zu prüfende Stelle
                    j = j + k + 1;
                    
                }
                
            }  
        }
        
        return wordsToIndex;
        
    }
}
