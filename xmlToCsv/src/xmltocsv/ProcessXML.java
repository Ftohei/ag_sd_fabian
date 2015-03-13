package xmltocsv;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ProcessXML {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, SQLException {
		
                /**
                 * Todo: Fehler mit dem DocumentBuilder beheben
                 */
            
            
		File fXmlFile = new File(args[0]);
//                File fXmlFile = new File("./8586833-Kogni-13012015.xml");                
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                
                dBuilder.setEntityResolver(new EntityResolver() {
                    @Override
                    public InputSource resolveEntity(String publicId, String systemId)
                            throws SAXException, IOException {
                        if (systemId.contains("pmg-artikel-liste.dtd")) {
                            return new InputSource(new StringReader(""));
                        } else {
                            return null;
                        }
                    }
                });
                
                //Hier liegt das Problem!
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
                articlesToCSV(artikles);
	}

	private static void serialize(List<Artikel> artikles) throws IOException{


		FileWriter writer;
		
		for (Artikel artikle: artikles)
		{
                    writer = new FileWriter("/Users/fabiankaupmann/Desktop/NW-Datenbank/NW-Artikel/"+artikle.getArtikelID()+".txt");
                    writer.write(artikle.getText());
                    writer.close();
		}
		

	}
        
        private static void articlesToCSV(List<Artikel> artikles) throws SQLException{
            

            MaxentTagger tagger = new MaxentTagger("/home/fabian/xmlToCsv/taggers/german-fast.tagger");
            
            try {
                Searcher searcher = new Searcher("/home/fabian/indexFullWiki/");
//                Searcher searcher = new Searcher("/Users/fabiankaupmann/Desktop/testIndexStandard/");

                FileAnalyzer fileAnalyzer = new FileAnalyzer();
                ArrayList<CSVPerson> persons = fileAnalyzer.getPersons("/home/fabian/xmlToCsv/relevance_predictions_13012015_top10.txt");
                

                for(CSVPerson person : persons){
                
//                    System.out.println("------------");
//                    System.out.println("Person: " + person.getName());

                    for(ArticleWithResults article : person.getArticleList()){
                        String articleId = article.getArticleId();
                        for(Artikel artikle: artikles){
                            if(articleId.equals(artikle.getArtikelID())){
//                                System.out.println("Artikel " + articleId + " wird untersucht");
//                                System.out.println(artikle.getText());
                                
                                String textGetaggt = tagger.tagString(artikle.getText());
                                
                                
                                //Sucht für eine Person für einen bestimmten Artikel die 100 besten ESA-Ergebnisse
                                article.setTitle(artikle.getTitel());
                                
                                searcher.addResultsToArticle(article, textGetaggt);
//                                System.out.println("-----------");
//                                System.out.println("Titel: " + article.getTitle());
//                                System.out.println(article.getPageIds());
//                                System.out.println(article.getWikiTitles());
//                                System.out.println(article.getScores());
//                                System.out.println("------------");

                                //TODO: ausgabe als CSV
                                // searcher abändern, so dass er auf dem vollen Index arbeitet
                                //kommandozeilenargumente
                                
                                
                                
                            }
                        }
                    }
                }
                
                CSVWriter writer = new CSVWriter();
                writer.writeCsvFile("/home/fabian/csvOut.csv", persons);
                 
            } catch (IOException ex) {
                Logger.getLogger(ProcessXML.class.getName()).log(Level.SEVERE, null, ex);
            }
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
