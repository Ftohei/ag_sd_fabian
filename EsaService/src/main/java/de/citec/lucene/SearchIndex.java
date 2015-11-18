/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.lucene;

import de.citec.util.Language;
import static de.citec.util.Language.DE;
import static de.citec.util.Language.EN;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author swalter
 */
public class SearchIndex {
    
    private Analyzer analyzer;
        private final  IndexReader reader ;
        private final  IndexSearcher searcher;
        private final  Language language;
	
	public SearchIndex(String pathToIndex, Language input_language) throws IOException{
		if(input_language.equals(DE)) this.analyzer = new GermanAnalyzer();
                if(input_language.equals(EN)) this.analyzer = new EnglishAnalyzer();
                this.language=input_language;
                this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(pathToIndex)));
                this.searcher = new IndexSearcher(reader);
                
	}
	
	
	
        
        public Map<String,List<String>> runStrictSearch(List<String> terms, int top_k, boolean onlypersons)
			throws IOException {
            Set<String> cache = new HashSet<>();
            Map<String,List<String>> wikipedia_entries = new HashMap();
            try {

                //Generate Boolean query out of term
                BooleanQuery booleanQuery = new BooleanQuery();

                for(String term:terms){
                    booleanQuery.add(new QueryParser("text", analyzer).parse("\""+term.toLowerCase().replace(" ", "_")+"\""), BooleanClause.Occur.SHOULD);
                }
                if(onlypersons){
                     booleanQuery.add(new QueryParser("persons", analyzer).parse("1"), BooleanClause.Occur.MUST);
                     wikipedia_entries.putAll(runSearch(top_k,booleanQuery));
                }
                else{
                    wikipedia_entries.putAll(runSearch(top_k,booleanQuery));
                    booleanQuery.add(new QueryParser("persons", analyzer).parse("1"), BooleanClause.Occur.MUST);
                    wikipedia_entries.putAll(runSearch(top_k,booleanQuery));
                }

		}
		catch(Exception e){
			
		}
		
		return wikipedia_entries;
	}
        
    public Map<String,List<String>> runSearch(int top_k,BooleanQuery booleanQuery) throws IOException{
        int hitsPerPage = top_k;	    
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(booleanQuery, collector);
        Map<String,List<String>> wikipedia_entries = new HashMap();
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        for(int i=0;i<hits.length;++i) {
              int docId = hits[i].doc;
              Document d = searcher.doc(docId);
              String artikelname = d.get("name");
              List<String> result = new ArrayList<>();
              result.add(artikelname);
              result.add(Float.toString(hits[i].score));
              wikipedia_entries.put(d.get("id"),result);
        }
       return wikipedia_entries;
    }
	
    
}
