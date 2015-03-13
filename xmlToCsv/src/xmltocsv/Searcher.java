/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltocsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author fabiankaupmann
 */
public class Searcher {
    
    /*
    * Werte normalsieren: Lucene index Score aufsummieren und dann durch die anzahl teilen.
    */
    
    /**
     * normalizedScore summiert über ALLE Ergebnisse und teilt dufch die Anzahl ALLER Ergebnisse
     */
    
    
    IndexReader reader;
    IndexSearcher searcher;
    String indexDir;
    int precision;
    ArrayList<Float> scores;
    ArrayList<String> titles;
    ArrayList<String> pageIds;
    
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LATEST);    
    
    public Searcher(String indexDir) throws IOException{
        
        this.indexDir = indexDir;
        reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
        searcher = new IndexSearcher(reader);    
        this.precision = 100;
        this.scores = new ArrayList();
        this.titles = new ArrayList();
        this.pageIds = new ArrayList();
              
    }
    
    public String extractQueryString(String taggedString){
        Scanner scanner = new Scanner(taggedString);
        scanner.useDelimiter("\\s");
        
        ArrayList<String> taggedElements = new ArrayList<>();
        
        while(scanner.hasNext()){
            taggedElements.add(scanner.next());
        }
        
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> postags = new ArrayList<>();
        
        ArrayList<String> adjectiveTags = new ArrayList<>();
        ArrayList<String> nounTags = new ArrayList<>();
        
        adjectiveTags.add("ADJA");
        adjectiveTags.add("ADJD");
        
        nounTags.add("NN");
        nounTags.add("NE");
        
        for(String element : taggedElements){
            scanner = new Scanner(element);
            scanner.useDelimiter("_");
            while(scanner.hasNext()){
                words.add(scanner.next());
                postags.add(scanner.next());
            }
        }
        
        String queryString = "";
        for(int j = 0; j<(postags.size()); j++){
            if(j<(postags.size() - 1)){
                
                //checken: Adjektiv * Nomen +, dh. 0 oder mehr adjektive, 1 oder mehr Nomen
                if(nounTags.contains(postags.get(j))){
                    queryString = queryString + " " + words.get(j);
                    
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
                            queryString = queryString + " " + words.get(l);
                        } 
                    }
                    //Springe an nächste zu prüfende Stelle
                    j = j + k + 1;
                    
                }
            } else {
                if(nounTags.contains(postags.get(j))){
                    queryString = queryString + " " + words.get(j);
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
                            queryString = queryString + " " + words.get(l);
                        } 
                    }
                    //Springe an nächste zu prüfende Stelle
                    j = j + k + 1;
                    
                }
            }  
        }
        
        return queryString;   
    }
    
    public ArticleWithResults addResultsToArticle(ArticleWithResults article, String inputGetaggt){
        ArticleWithResults resultArticle = article;
        String nounAdjectiveQuery = this.extractQueryString(inputGetaggt);
//        System.out.println(nounAdjectiveQuery);
        
        this.scores.clear();
        this.titles.clear();
        this.pageIds.clear();
        
        this.search(nounAdjectiveQuery);
//        System.out.println("-----------");
//        System.out.println(this.scores);
//        System.out.println(this.titles);
//        System.out.println(this.pageIds);
//        System.out.println("-----------");
        
        
        resultArticle.getPageIds().addAll(this.pageIds);
        resultArticle.getWikiTitles().addAll(this.titles);
        resultArticle.getScores().addAll(this.scores);
        
        return resultArticle;
    }
    
    public void search(String query){
        
        

        try {
            String escapedQuery = QueryParser.escape(query);
            if(!escapedQuery.isEmpty()){
                Query q = new QueryParser(Version.LATEST, "text", analyzer).parse(escapedQuery);
                TopDocs docs = searcher.search(q, this.precision);
                ScoreDoc[] hits = docs.scoreDocs;

        
                for(int i=0; i<hits.length; i++){
//                    Explanation explanation = searcher.explain(q, hits[i].doc);
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    this.pageIds.add(d.get("id"));
                    this.scores.add(hits[i].score);
                    this.titles.add(d.get("title"));
                    
                    
//                    result.add(d.get("pageId"));
//                    this.scores.add(hits[i].score);
//                    this.normalizedScore = this.normalizedScore + hits[i].score;
                }
                
            }
            
            
        } catch (ParseException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }               
    
}
