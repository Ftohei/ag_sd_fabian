package de.citec.io;


import de.citec.util.Language;
import static de.citec.util.Language.DE;
import static de.citec.util.Language.EN;
import static de.citec.util.Language.ES;
import static de.citec.util.Language.JA;
import java.io.File;
import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Config {
	Language Language = EN;
        String userName;    
        String password;
        String dbms;
        String serverName;
        String portNumber;
        String database;
        String pathIndexEnglish;
        String pathIndexGerman;
        String pathTagger;
        String pathXML;

    
	public Config()
	{
	}
	
	public void loadFromFile(String configFile) throws ParserConfigurationException, SAXException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, DOMException, Exception {
	
		// add logger here...
		System.out.print("Reading configuration from: "+configFile+"\n");
		
		File fXmlFile = new File(configFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
	 
		NodeList nList = doc.getDocumentElement().getChildNodes();
		
	
		System.out.println("----------------------------");
		
		for (int i = 0; i < nList.getLength(); i++) {
			 
			Node node = nList.item(i);
									
			if (node.getNodeName().equals("Language"))
			{
				this.Language = mapToLanguage(node.getTextContent());
				
			}
                        
                        if (node.getNodeName().equals("pathIndexGerman"))
			{
				this.pathIndexGerman = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("pathIndexEnglish"))
			{
				this.pathIndexEnglish = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("database"))
			{
				this.database = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("portNumber"))
			{
				this.portNumber = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("serverName"))
			{
				this.serverName = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("dbms"))
			{
				this.dbms = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("password"))
			{
				this.password = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("userName"))
			{
				this.userName = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("pathTagger"))
			{
				this.pathTagger = node.getTextContent();
			}
                        
                        if (node.getNodeName().equals("pathXML"))
			{
				this.pathXML = node.getTextContent();
			}
			
                }
		
	}

        
        private Language mapToLanguage(String s) throws Exception {
            
            if      (s.toLowerCase().equals("en") || s.toLowerCase().equals("eng")) return EN;
            else if (s.toLowerCase().equals("de") || s.toLowerCase().equals("ger")) return DE;
            else if (s.toLowerCase().equals("es") || s.toLowerCase().equals("spa")) return ES;
            else if (s.toLowerCase().equals("ja") || s.toLowerCase().equals("jpn")) return JA;
            else throw new Exception("Language '" + s + "' unknown.");
        }

	public Language getLanguage() {
		return Language;
	}



	public void setLanguage(Language language) {
		Language = language;
	}
        
        public String getPathTagger() {
            return pathTagger;
        }

        public void setPathTagger(String pathTagger) {
            this.pathTagger = pathTagger;
        }

    
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getPathXML() {
            return pathXML;
        }

        public void setPathXML(String pathXML) {
            this.pathXML = pathXML;
        }


        public String getDbms() {
            return dbms;
        }

        public void setDbms(String dbms) {
            this.dbms = dbms;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getPortNumber() {
            return portNumber;
        }

        public void setPortNumber(String portNumber) {
            this.portNumber = portNumber;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getPathIndexEnglish() {
            return pathIndexEnglish;
        }

        public void setPathIndexEnglish(String pathIndexEnglish) {
            this.pathIndexEnglish = pathIndexEnglish;
        }

        public String getPathIndexGerman() {
            return pathIndexGerman;
        }

        public void setPathIndexGerman(String pathIndexGerman) {
            this.pathIndexGerman = pathIndexGerman;
        }



}
