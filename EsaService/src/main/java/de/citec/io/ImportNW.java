/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import de.citec.util.Artikel;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author swalter
 */
public class ImportNW {
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, IllegalAccessException, ClassNotFoundException, DOMException, Exception {
        Config config = new Config();
//        config.loadFromFile("config.xml");
        //File fXmlFile = new File(config.getPathXML());
        File fXmlFile = new File(args[0]);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        DatabaseAction da = new DatabaseAction(config);
        Set<String> artikel_titel = new HashSet<>();

        dBuilder.setEntityResolver(new EntityResolver() {
//            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                if (systemId.contains("pmg-artikel-liste.dtd")) {
                    return new InputSource(new StringReader(""));
                } else {
                    return null;
                }
            }
        });
        System.out.println(fXmlFile.toString());

        Document doc = dBuilder.parse(fXmlFile);


        Set<Artikel> artikelliste = new HashSet<Artikel>();

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());


        NodeList nList = doc.getElementsByTagName("artikel");
        
        MaxentTagger tagger = new MaxentTagger("./taggers/german-fast.tagger");

        System.out.println("----------------------------");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            Artikel artikel = new Artikel();



            NodeList nodes = nNode.getChildNodes();

            for (int j = 0; j < nodes.getLength(); j++) {

                Node node = nodes.item(j);

                if (node.getNodeName().equals("metadaten"));
                {
                    if (getArtikelID(node) != null) artikel.setArtikelID(getArtikelID(node));
                    if (getDatum(node) != null) artikel.setDatum(getDatum(node));
                    if (getArtikelPDF(node) != null) artikel.setArtikelPDF(getArtikelPDF(node));
                }

                
                if (node.getNodeName().equals("inhalt"))
                {
                    artikel.setTitel(getTitel(node));
                    if(artikel.getTitel()!=null && artikel.getTitel().length()>2){
                        String text = getText(node);
                        artikel.setText(text);
                        artikel.setTaggedText(tagText(text,tagger));
                    }
                    
                }
            }
            if(!artikel_titel.contains(artikel.getTitel().trim().toLowerCase()) && artikel.getArtikelPDF()!=null){
                artikel_titel.add(artikel.getTitel().trim().toLowerCase());
                artikelliste.add(artikel);

                
            }
            
            //System.out.print(artikel);
        }
        System.out.println("Alle Artikel aus XML extrahiert");

         /*
        Run search on index - but on cleaned entries_onlyPersons
        */
        

        for(Artikel artikel:artikelliste){
            if(artikel.getTaggedText()!=null){
                String[] terms = artikel.getTaggedText().split(" ");
                List<String> term_list = new ArrayList();
                term_list.addAll(Arrays.asList(terms));
                Map<Integer,Float> entries_onlyPersons = da.getWikipediaArtikels(term_list, true,10);
                artikel.setWikipedia_entries_onlyPersons(entries_onlyPersons);
                
                Map<Integer,Float> entries_all = da.getWikipediaArtikels(term_list, false,10);
                artikel.setWikipedia_entries_all(entries_all);
                
            }
            
        }
        System.out.println("Für alle Artifkel Index durchsucht");
        
        try {
            da.articlesToDatabase(artikelliste);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        da.close();
    }



    


     private static String getArtikelPDF(Node nNode) {

        NodeList nodes = nNode.getChildNodes();

        Node node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("artikel-pdf"))
            {

                return node.getTextContent();
            }
        }
        return null;
    }
        

    private static String getText(Node nNode) {

        String body = "";

        NodeList nodes = nNode.getChildNodes();

        NodeList subnodes;

        Node subnode,node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("text"))
            {

                subnodes = node.getChildNodes();

                for (int j = 0; j < subnodes.getLength(); j++) {

                    subnode = subnodes.item(j);

                    if (subnode.getNodeName().equals("absatz"))
                    {
                        if (body.equals(""))

                            body =  subnode.getTextContent();

                        else body =  body +" "+subnode.getTextContent();

                    }

                }

            }
        }
        return body;
    }

 

    private static String getTitel(Node nNode) {
        NodeList nodes = nNode.getChildNodes();

        NodeList subnodes;

        Node subnode,node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("titel-liste"))
            {

                subnodes = node.getChildNodes();

                for (int j = 0; j < subnodes.getLength(); j++) {

                    subnode = subnodes.item(j);

                    if (subnode.getNodeName().equals("titel"))
                    {
                        return subnode.getTextContent();
                    }

                }

            }
        }
        return null;
    }




  



    private static String getDatum(Node nNode) {
        NodeList nodes = nNode.getChildNodes();

        NodeList subnodes;

        Node subnode,node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("quelle"))
            {

                subnodes = node.getChildNodes();

                for (int j = 0; j < subnodes.getLength(); j++) {

                    subnode = subnodes.item(j);

                    if (subnode.getNodeName().equals("datum"))
                    {
                        return subnode.getTextContent();
                    }

                }

            }
        }
        return null;
    }


    private static String getArtikelID(Node nNode) {

        NodeList nodes = nNode.getChildNodes();

        Node node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("artikel-id"))
            {

                return node.getTextContent();
            }
        }
        return null;
    }

    public static String tagText(String body,MaxentTagger tagger) {
        String line = tagger.tagString(body);
        String new_artikel= "";
        String term = "";
        for(String word : line.split(" ")){
            if (word.contains("NN") || word.contains("ADJA")|| word.contains("ADJD") || word.contains( "NE")){
                term+=" "+word.split("_")[0];
            }
            else{
                term = term.replace(" ","_");
                if(term.length()>0){
                    new_artikel+=" "+term.toLowerCase().substring(1);
                    term = ""; 
                }

            }
        }

        if(term.length() > 0){
            new_artikel+=" "+term.toLowerCase().substring(1);
        }
        new_artikel = new_artikel.replace("'", "");
        new_artikel = new_artikel.replace(" ._$.", "");
        new_artikel = new_artikel.replace("_$.", "");
        new_artikel = new_artikel.replace("_card", "");
        new_artikel = new_artikel.replace("_xy ", "");
        new_artikel = new_artikel.replace("/_$[", "");
        new_artikel = new_artikel.replace("-_$[", "");
        new_artikel = new_artikel.replace("_$[", "");
        new_artikel = new_artikel.replace(("_pper"), "");
        new_artikel = new_artikel.replace(("_appr"), "");
        new_artikel = new_artikel.replace(" 's", "s");
        new_artikel = new_artikel.replace("_piat", "");
        new_artikel = new_artikel.replace("_adv", "");
        new_artikel = new_artikel.replace("' ", "");
        new_artikel = new_artikel.replace("'", " ");
        new_artikel = new_artikel.replace("  ", " ");
        new_artikel = new_artikel.trim();
        if(new_artikel.length() == 0) {
            return new_artikel;
        }
        return new_artikel.substring(1);
    }

//    private static String convertEntriesToString(Map<String, List<String>> wikipedia_entries) {
//        String output = "";
//        for(String key : wikipedia_entries.keySet()){
//            List<String> info = wikipedia_entries.get(key);
//            output+=key+","+info.get(0)+","+info.get(1)+"##";
//        }
//        
//        output = output.replace(" ._$.", "");
//        output = output.replace("_$.", "");
//        output = output.replace("_card", "");
//        output = output.replace("_xy ", "");
//        output = output.replace("/_$[", "");
//        output = output.replace("-_$[", "");
//        output = output.replace("_$[", "");
//        output = output.replace(("_pper"), "");
//        output = output.replace(("_appr"), "");
//        output = output.replace(" 's", "s");
//        output = output.replace("_piat", "");
//        output = output.replace("_adv", "");
//        output = output.replace("' ", "");
//        output = output.replace("'", " ");
//        output = output.replace("  ", " ");
//        output = output.trim();
//        //System.out.println(output);
//        return output;
//    }



}
