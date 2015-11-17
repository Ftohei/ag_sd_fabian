/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.lucene;

import de.citec.util.Language;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


/**
 *
 * @author swalter
 */
public class CreateIndex {
    
    private static int counter = 0;
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = null;
        
        List<String> files = new ArrayList<>();
        files.add("/Users/swalter/Documents/EsaDeutsch/new_copus_german.txt");
        String indexPath = "/Users/swalter/Documents/EsaDeutsch/Index/";
        Language language = Language.DE;
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        
        if(language.equals(Language.DE)) analyzer = new GermanAnalyzer();
        if(language.equals(Language.ES)) analyzer = new SpanishAnalyzer();
        if(language.equals(Language.EN)) analyzer = new EnglishAnalyzer();
        
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(12000);
        try (IndexWriter writer = new IndexWriter(dir, iwc)) {
            files.forEach(f->{
                try {
                    indexDocs(writer,Paths.get(f));
                } catch (IOException ex) {
                    Logger.getLogger(CreateIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
        }
        System.out.println(counter);
        
        
        
    }
    
    
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        Stream<String> lines = Files.lines(path);
        lines.forEach(s->indexDoc(writer,s));
    }
    
    
//            name = name.replace("_piat", "");
//            name = name.replace("_adv", "");
//            name = name.replace(("_pper"), "");
//            name = name.replace(("_appr"), "");
//            name = name.replace("_card", "");
//            name = name.replace("_xy ", "");
//            name = name.replace("_card .", "");
//            name = name.replace("_NN","");
//            name = name.replace("_ADJA","");
//            name = name.replace("_ADJD","");
//            name = name.replace("_NE","");
//            name = name.replace("_appr", "");
    
    private static void indexDoc(final IndexWriter writer,String input) {
        
        try{
            input = input.replace("\n","");
            String[] tmp = input.split("######");
            String persons = tmp[0];
            String id = tmp[1];
            String name = tmp[2];
            name = name.replace("-RRB-_TRUNC","");
            name = name.replace("-LRB-_TRUNC","");
            name = name.replace("._$.","");
            name = name.replace(" ._$.", "");
            name = name.replace("_$.", "");
            name = name.replace("/_$[", "");
            name = name.replace("-_$[", "");
            name = name.replace("_$[", "");
            name = name.replace(" 's", "s");
            name = name.replace("' ", "");
            name = name.replace("'", " ");
            name = name.replace("  ", " ");
            if(name.contains(" ")){
                String[] tmp_string = name.split(" ");
                name = "";
                for(String s : tmp_string){
                    if(s.contains("_")){
                        name+=" "+s.split("_")[0];
                    }
                    else
                        name+=" "+s;
                }
            }
            name = name.trim();
            if(tmp.length<4){
                //System.out.println(input);
                counter +=1;
            }
            else{
                //System.out.println(name);
                String text = tmp[3];
                Document doc = new Document();
                Field field_persons = new StringField("persons",persons,Field.Store.NO);
                Field field_id = new StringField("id",id,Field.Store.YES);
                Field field_name = new TextField("name",name,Field.Store.YES);
                Field field_text = new TextField("text",text.toLowerCase(),Field.Store.NO);
                doc.add(field_persons);
                doc.add(field_id);
                doc.add(field_name);
                doc.add(field_text);
                writer.addDocument(doc);
            }
            
            
        }
        catch(Exception e){
            e.printStackTrace();
            //System.out.println("Problem with:"+input);
        }
        
        
    }
      

    
  
    
}
