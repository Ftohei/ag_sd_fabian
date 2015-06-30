import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fabiankaupmann on 15.05.15.
 */
public class ProcessXML {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

//		File fXmlFile = new File(args[0]);
        File fXmlFile = new File("/Users/fabiankaupmann/Desktop/8586833-Kogni-30052015.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

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


        List<Artikel> artikles = new ArrayList<Artikel>();

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("artikel");

        Artikel artikel = null;

        System.out.println("----------------------------");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            artikel = new Artikel();

            artikles.add(artikel);

            NodeList nodes = nNode.getChildNodes();

            for (int j = 0; j < nodes.getLength(); j++) {

                Node node = nodes.item(j);

                if (node.getNodeName().equals("metadaten"));
                {
                    if (getArtikelID(node) != null) artikel.setArtikelID(getArtikelID(node));
                    if (getDatum(node) != null) artikel.setDatum(getDatum(node));
                    if (getLieferantId(node) != null) artikel.setLieferantId(getLieferantId(node));
                    if (getQuelleId(node) != null) artikel.setQuelleId(getQuelleId(node));
                    if (getName(node) != null) artikel.setName(getName(node));
                    if (getStartSeite(node) != null) artikel.setStartSeite(getStartSeite(node));
                    if (getArtikelPDF(node) != null) artikel.setArtikelPDF(getArtikelPDF(node));
                    if (getAutor(node) != null) artikel.setAutor(getAutor(node));
                }

                if (node.getNodeName().equals("inhalt"))
                {
                    artikel.setTitel(getTitel(node));
                    artikel.setRessort(getRessort(node));
                    artikel.setRubrik(getRubrik(node));
                    artikel.setText(getText(node));
                }
            }
            System.out.print(artikel);
        }
        try {
            articlesToDatabase(artikles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void serialize(List<Artikel> artikles) throws IOException{


        FileWriter writer;

        for (Artikel artikle: artikles)
        {
            writer = new FileWriter("./output/NW-Artikel/"+artikle.getArtikelID()+".txt");
            writer.write(artikle.getText());
            writer.close();
        }


    }

    private static void articlesToDatabase(List<Artikel> artikles) throws SQLException {

        Statement stmt;
        ResultSet rs;

        Connector connector = new Connector();

        try {
            connector.connect();
        } catch (SQLException ex) {
            Logger.getLogger(ProcessXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProcessXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ProcessXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ProcessXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        stmt = connector.getConn().createStatement();

        //Neue Ausgabe in Tabelle Ausgabe erstellen
        String datumNeueAusgabe = artikles.get(0).convertDate(artikles.get(0).getDatum());
        stmt.executeUpdate("INSERT INTO ausgabe SET datum = '" + datumNeueAusgabe + "';");

        //Liste der bereits vorhandenen Rubriken erstellen, um zu überprüfen, falls neue hinzukommen
        ResultSet rubrikSet;

        rubrikSet = stmt.executeQuery("SELECT name FROM rubrik");

        ArrayList<String> existingRubriken = new ArrayList<String>();


        System.out.println("Bisherige Rubriken:");

        while(rubrikSet.next()){
            String rubrik = rubrikSet.getString("name");
            System.out.println(rubrik);
            existingRubriken.add(rubrik);

        }

        System.out.println("------------");

        MaxentTagger tagger = new MaxentTagger("./taggers/german-fast.tagger");

        //            Searcher searcher = new Searcher("");

        for (Artikel artikel: artikles){

            boolean rubrikContained = false;

            for(String rubrik : existingRubriken){
                if(artikel.getRubrik().equals(rubrik)){
                    rubrikContained = true;
                }
            }

            if(!rubrikContained){
                System.out.println("Neue Rubrik: " + artikel.getRubrik());
                stmt.executeUpdate("INSERT INTO rubrik SET name = '" + artikel.getRubrik() + "'");
                existingRubriken.add(artikel.getRubrik());
            }

            ResultSet resultSet;

//          searcher.setCurrentArticleId(artikel.getArtikelID());
            resultSet = stmt.executeQuery("SELECT hex(artikelId) FROM artikel WHERE ausgabeId = (SELECT ausgabeId FROM ausgabe WHERE datum = '" + datumNeueAusgabe + "') AND text LIKE '%" + artikel.getText() + "'");

            if(resultSet.next()) {

                String artikelId = resultSet.getString("hex(artikelId)");

                System.out.println("Artikel mit id:" + artikel.getArtikelID() +  " ist doppelung; speichere " + artikelId + " mit Rubrik " + artikel.getRubrik());

                stmt.executeUpdate("INSERT INTO inrubrik SET artikelId = 0x" + artikelId +
                        ", rubrikId = (SELECT rubrikId FROM rubrik WHERE Name like '" + artikel.getRubrik() + "');");

            } else {

                System.out.println("Artikel: " + artikel.getArtikelID() + " wird in die Datenbank gespeichert");

                String artikelDatum = artikel.convertDate(artikel.getDatum());

//                    Daten des Artikels in Tabelle Artikel schreiben
                try {
                    if(artikel.getTitel()!=null){
                        int anzahl_woerter = artikel.getText().split(" ").length;
                        stmt.executeUpdate("INSERT INTO artikel SET "
                                    + "artikelId = 0x" + artikel.getArtikelID() +
                                    ", lieferantId = '" + artikel.getLieferantId() + "'" +
                                    ", quelleId = '" + artikel.getQuelleId() + "'" +
                                    ", artikelPDF = '" + artikel.getArtikelPDF() + "'" +
                                    ", herausgeber = '" + artikel.getName() + "'" +
                                    ", datum = '" + artikel.convertDate(artikel.getDatum()) + "'" +
                                    ", titel = '" + artikel.getTitel() + "'" +
                                    ", text = '" + artikel.getText() + "'" +
                                    ", anzahl_woerter = '"+Integer.toString(anzahl_woerter)+"'"+
                                    ", ausgabeId = (SELECT ausgabeId FROM ausgabe WHERE datum = '" + artikelDatum + "')" +
                                    ", ressortId = (SELECT ressortId FROM ressort WHERE Name like '" + artikel.getRessort() + "')" +
                                    ", autor = '" + artikel.getAutor() + "'" +
                                    ", seite = '" + artikel.getStartSeite() + "';"
                        );
                        //                    Artikel mit Rubrik verbinden
                        stmt.executeUpdate("INSERT INTO inrubrik SET artikelId = 0x" + artikel.getArtikelID() +
                                ", rubrikId = (SELECT rubrikId FROM rubrik WHERE Name like '" + artikel.getRubrik() + "');");

                        String textGetaggt = tagger.tagString(artikel.getText());
                        //Single Quotes aus dem String entfernen
                        String textGetaggtOhneSingleQuotes = textGetaggt.replace("'", "\\'");
                        stmt.executeUpdate("INSERT INTO artikelGetaggt SET "
                                + "artikelId = 0x" + artikel.getArtikelID() +
                                ", textGetaggt = '" + textGetaggtOhneSingleQuotes + "';");

                    
                    }
                    else{
                        System.out.println("No title given for "+artikel.getArtikelPDF());
                    }
                    


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //Für jeden Artikel die 100 besten ESA-Ergebnisse (pageId von Wikipedia) speichern
//                    ArrayList<Integer> resultsPerArticle = searcher.searchIndexWithQueryString(searcher.extractQueryString(textGetaggt));
//                    if(!resultsPerArticle.isEmpty()){
//
//
//                        //                    String esaIntoDatabaseQuery = "INSERT INTO EsaResultate(ArtikelId,pageId) VALUES ";
//                        for(Integer result : resultsPerArticle){
//
//                            //                        esaIntoDatabaseQuery = esaIntoDatabaseQuery + "(0x"+ artikel.getArtikelID() +",'"+result+"'),";
//                        }
//                        //                    esaIntoDatabaseQuery = esaIntoDatabaseQuery.substring(0, esaIntoDatabaseQuery.length()-1);
//                        //                    esaIntoDatabaseQuery = esaIntoDatabaseQuery + ";";
//                        //                    stmt.executeUpdate(esaIntoDatabaseQuery);
//                    }

            }

            resultSet.close();

        }

        stmt.close();
        connector.closeConnection();

    }

    private static String getStartSeite(Node nNode) {

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

                    if (subnode.getNodeName().equals("seite-start"))
                    {

                        return subnode.getTextContent();
                    }

                }

            }
        }
        return null;

    }


    private static String getName(Node nNode) {

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

                    if (subnode.getNodeName().equals("name"))
                    {

                        return subnode.getTextContent();
                    }

                }

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

    private static String getRubrik(Node nNode) {
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

                    if (subnode.getNodeName().equals("rubrik"))
                    {
                        return subnode.getTextContent();
                    }

                }

            }
        }
        return null;
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

    private static String getRessort(Node nNode) {
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

                    if (subnode.getNodeName().equals("ressort"))
                    {
                        return subnode.getTextContent();
                    }

                }

            }
        }
        return null;
    }

    private static String getQuelleId(Node nNode) {

        String quelle = null;

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

                    if (subnode.getNodeName().equals("quelle-id"))
                    {
                        quelle = subnode.getTextContent();
                    }

                }

            }
        }
        return quelle;
    }

    private static String getLieferantId(Node nNode) {
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

                    if (subnode.getNodeName().equals("lieferant-id"))
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

    private static String getAutor(Node nNode) {
        NodeList nodes = nNode.getChildNodes();

        NodeList subnodes;

        Node subnode,node;

        for (int i = 0; i < nodes.getLength(); i++) {

            node = nodes.item(i);

            if (node.getNodeName().equals("autor"))
            {

                subnodes = node.getChildNodes();

                for (int j = 0; j < subnodes.getLength(); j++) {

                    subnode = subnodes.item(j);

                    if (subnode.getNodeName().equals("autor-name"))
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

}
