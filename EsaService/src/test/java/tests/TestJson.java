/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import de.citec.lucene.Searcher;
import de.citec.util.Language;
import java.io.IOException;

/**
 *
 * @author swalter
 */
public class TestJson {
    public static void main(String[] args) throws IOException{
        Searcher searcher = new Searcher("/Users/swalter/Downloads/IndizesEsa/esaIndex_german",50,Language.DE);
        System.out.println(searcher.searchAndReturnJSON("Literatur", true));
    }
}
