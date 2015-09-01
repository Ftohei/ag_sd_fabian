/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import de.citec.util.Language;
import de.citec.util.VectorSimilarity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author swalter
 */
public class TestIndex {
    
    public static void main(String[] args) throws IOException  {
//    SearchIndex index = new SearchIndex("/Users/swalter/Documents/EsaDeutsch/Index",Language.DE);
    List<String> terms = new ArrayList<String>();
    terms.add("europäische Geschichte");
    terms.add("Kunst");
//    Map<String,List<String>> result = index.runStrictSearch(terms, 50, true);
//    for (List<String> r : result.values()){
//        System.out.println(r.get(0));
//        System.out.println(r.get(1));
//        System.out.println();
//    }
    
    VectorSimilarity vec = new VectorSimilarity("/Users/swalter/Documents/EsaDeutsch/Index",Language.DE);
    System.out.println(vec.getArtikels(terms, "2015-08-28",true));
    
    }
}
