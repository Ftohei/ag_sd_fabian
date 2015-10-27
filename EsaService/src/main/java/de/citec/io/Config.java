package de.citec.io;


import de.citec.util.Language;
import static de.citec.util.Language.DE;
import static de.citec.util.Language.EN;


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
            this.Language=DE;
            this.pathIndexGerman="/Users/swalter/Documents/EsaDeutsch/Index/";
           this.pathIndexEnglish="/Users/swalter/Documents/EsaEnglish/Index/";
           this.portNumber="3306";
           this.serverName="localhost";
           this.dbms="mysql";
           this.password="1234567";
           this.database="esa";
           this.userName="esa";
           this.pathTagger="/Users/swalter/Git/ag_sd_fabian/EsaService/taggers/german-fast.tagger";

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
