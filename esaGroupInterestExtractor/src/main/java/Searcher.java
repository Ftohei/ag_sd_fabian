/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sucht in einem Lucene-Index nach relevanten Ergebnissen.
 */
public class Searcher {

    IndexReader reader;
    IndexSearcher searcher;

    /**
     * Wie viele ESA-Ergebnisse pro Query?
     */
    int precision;

    /**
     * Eine Liste mit den Scores aller Ergebnisse der letzten Suche.
     */
    ArrayList<Float> scores;

    /**
     * Eine Liste mit den Wikipedia-Titeln der letzten Suche.
     */
    ArrayList<String> titles;

    /**
     * Eine Liste mit den Page-Ids der letzten Suche.
     */
    ArrayList<String> pageIds;

    /**
     * Die Sprache, in der gesucht werden soll (beeinflusst die Adjektiv-Nomen-Suche)
     */
    String language;

    /**
     * Mit oder ohne Personen suchen?
     */
    boolean includePersonArticles;
    
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private StandardAnalyzer analyzer = new StandardAnalyzer();

    /**
     * Konstruktor.
     * @param indexDir Das Verzeichnis mit dem Index, der durchsucht werden soll.
     * @param numberOfEsaResults Anzahl der Resultate pro Query.
     * @param language Sprache, in der gesucht werden soll (muss mit Index übereinstimmen).
     * @param includePersonArticles Mit oder ohne Personen suchen.
     * @throws IOException
     */
    public Searcher(String indexDir, int numberOfEsaResults, String language, boolean includePersonArticles) throws IOException{
        Path indexPath = Paths.get(indexDir) ;
        reader = DirectoryReader.open(FSDirectory.open(indexPath));
        searcher = new IndexSearcher(reader);
        this.language = language;
        this.includePersonArticles = includePersonArticles;
        this.precision = numberOfEsaResults;
        this.scores = new ArrayList();
        this.titles = new ArrayList();
        this.pageIds = new ArrayList();
              
    }

    /**
     * Durchsucht den Index mit der search()-Methode dieser Klasse und fügt die Resultate in ein GroupWithResults-Objekt ein.
     * @param group Ein GroupWithResults-Objekt.
     * @param inputGetaggt Ein input-Text, der mit einem passenden POS-Tagger getaggt wurde.
     * @return GroupWithResults mit Resultaten der Lucene-Suche.
     */
    public GroupWithResults addResultsToGroup(GroupWithResults group, String inputGetaggt){
        GroupWithResults resultGroup = group;
        String nounAdjectiveQuery = this.extractQueryString(inputGetaggt);
        
        this.scores.clear();
        this.titles.clear();
        this.pageIds.clear();


        //Index wird durchsucht.
        this.search(nounAdjectiveQuery);

        //Resultate zu dem Result-Objekt hinzufügen.
        resultGroup.getPageIds().addAll(this.pageIds);
        resultGroup.getWikiTitles().addAll(this.titles);
        resultGroup.getScores().addAll(this.scores);
        
        return resultGroup;
    }
    
    public void search(String query){

        try {
            String escapedQuery = QueryParser.escape(query);
            if(!escapedQuery.isEmpty()){

                BooleanQuery.setMaxClauseCount(100000);
                TopDocs docs = null;
                if (!this.includePersonArticles) {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    Query qText = new QueryParser("text", analyzer).parse(escapedQuery);
                    Query qPerson = new TermQuery(new Term("person", "0"));
                    booleanQuery.add(qText, BooleanClause.Occur.MUST);
                    booleanQuery.add(qPerson, BooleanClause.Occur.MUST);
                    docs = searcher.search(booleanQuery, this.precision);
                } else {
                    Query qText = new QueryParser("text", analyzer).parse(escapedQuery);
                    docs = searcher.search(qText, this.precision);
                }

                ScoreDoc[] hits = docs.scoreDocs;

                for (int i = 0; i < hits.length; i++) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    this.pageIds.add(d.get("id"));
                    this.scores.add(hits[i].score);
                    this.titles.add(d.get("title"));

                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String extractQueryString(String taggedString){
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

        if(this.language.equals("german")) {
            adjectiveTags.add("ADJA");
            adjectiveTags.add("ADJD");
            nounTags.add("NN");
            nounTags.add("NE");
        } else if (this.language.equals("english")){
            adjectiveTags.add("JJ");
            adjectiveTags.add("JJR");
            adjectiveTags.add("JJS");
            nounTags.add("NN");
            nounTags.add("NNS");
            nounTags.add("NNP");
            nounTags.add("NNPS");
        }

        for(String element : taggedElements){
            scanner = new Scanner(element);
            scanner.useDelimiter("_");
            while(scanner.hasNext()){
                words.add(scanner.next());
                if(scanner.hasNext()) {
                    postags.add(scanner.next());
                } else {
                    words.remove(words.size() - 1);
                }
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

    public void normalizeResultsWithMaxResult(){
        ArrayList<Float> normalizedScores = this.scores;
        float maxResult = Collections.max(normalizedScores);
        for(int i = 0; i<this.scores.size(); i++){
         //   this.scores.get(i) = this.scores.get(i) / maxResult;
        }
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isIncludePersonArticles() {
        return includePersonArticles;
    }

    public void setIncludePersonArticles(boolean includePersonArticles) {
        this.includePersonArticles = includePersonArticles;
    }
}

