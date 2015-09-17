/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.util;

import de.citec.lucene.SearchIndex;
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
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        File fXmlFile = new File("/Users/Fabian/Documents/Arbeit/AG_SD/8586833-Kogni-28082015.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        
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

        Document doc = dBuilder.parse(fXmlFile);


        Set<Artikel> artikelliste = new HashSet<Artikel>();

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        SearchIndex index = new SearchIndex("/Users/Fabian/Documents/Arbeit/AG_SD/Index",Language.DE);

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
                }

                if (node.getNodeName().equals("inhalt"))
                {
                    artikel.setTitel(getTitel(node));
                    if(artikel.getTitel()!=null && artikel.getTitel().length()>2){
                        artikel.setText(getText(node,tagger));
                    }
                    
                }
            }
            if(!artikel_titel.contains(artikel.getTitel().trim().toLowerCase())){
                artikel_titel.add(artikel.getTitel().trim().toLowerCase());
                artikelliste.add(artikel);
            }
            
            //System.out.print(artikel);
        }


         /*
        Run search on index - but on cleaned entries_onlyPersons
        */
        for(Artikel artikel:artikelliste){
            if(artikel.getText()!=null){
                String[] terms = artikel.getText().split(" ");
                List<String> term_list = new ArrayList();
                term_list.addAll(Arrays.asList(terms));
                Map<String,List<String>> entries_onlyPersons = index.runStrictSearch(term_list, 99, true);
                artikel.setWikipedia_entries_onlyPersons(entries_onlyPersons);
                
                Map<String,List<String>> entries_noPersons = index.runStrictSearch(term_list, 99, false);
                artikel.setWikipedia_entries_noPersons(entries_noPersons);
            }
            
        }
       
        
        try {
            articlesToDatabase(artikelliste);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void articlesToDatabase(Set<Artikel> artikelliste) throws SQLException {


        System.out.println("articlesToDatabase gestartet");

        Statement stmt;
        ResultSet rs;

        DB_Connector connector = new DB_Connector();

        try {
            connector.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        stmt = connector.getConn().createStatement();






        System.out.println("------------");


        //            Searcher searcher = new Searcher("");

        for (Artikel artikel: artikelliste){


            ResultSet resultSet;


                System.out.println("Artikel: " + artikel.getArtikelID() + " wird in die Datenbank gespeichert");

                String artikelDatum = artikel.convertDate(artikel.getDatum());

//                    Daten des Artikels in Tabelle Artikel schreiben
                try {
                    if(artikel.getTitel()!=null && artikel.getTitel().length()>2){
                        int anzahl_woerter = artikel.getText().split(" ").length;
                        stmt.executeUpdate("INSERT INTO Artikel SET "
                                    + "Id = 0x" + artikel.getArtikelID() +
                                    ", Datum = '" + artikel.convertDate(artikel.getDatum()) + "'" +
                                    ", Titel = '" + artikel.getTitel() + "'" +
                                    ", Text = '" + artikel.getText() + "'" +
                                    ", Wikipedia_OnlyPerson = '" + convertEntriesToString(artikel.getWikipedia_entries_onlyPersons())+ "'"+
                                    ", Wikipedia_NoPerson = '" + convertEntriesToString(artikel.getWikipedia_entries_noPersons())+ "'"
                                
                                   
                        );
                    
                    }
                    else{
                        System.out.println("No title given for "+artikel.getArtikelPDF());
                    }
                    


                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }


        

        stmt.close();
        connector.closeConnection();

    }

    




    private static String getText(Node nNode,MaxentTagger tagger) {

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
        return tagText(body,tagger);
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

//        System.out.println(new_artikel);
        if(new_artikel.length() == 0) {
            return new_artikel;
        }
        return new_artikel.substring(1);
    }

    private static String convertEntriesToString(Map<String, List<String>> wikipedia_entries) {
        String output = "";
        for(String key : wikipedia_entries.keySet()){
            List<String> info = wikipedia_entries.get(key);
            output+=key+","+info.get(0)+","+info.get(1)+"##";
        }
        
        output = output.replace(" ._$.", "");
        output = output.replace("_$.", "");
        output = output.replace("_card", "");
        output = output.replace("_xy ", "");
        output = output.replace("/_$[", "");
        output = output.replace("-_$[", "");
        output = output.replace("_$[", "");
        output = output.replace(("_pper"), "");
        output = output.replace(("_appr"), "");
        output = output.replace(" 's", "s");
        output = output.replace("_piat", "");
        output = output.replace("' ", "");
        output = output.replace("'", " ");
        //System.out.println(output);
        return output;
    }

}
