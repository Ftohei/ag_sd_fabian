package de.citec.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Artikel {

	String ArtikelID;
	
	String LieferantId;
	
	String QuelleId;
	
	String Name;
	
	String Datum;
	
	String StartSeite;
	
	String Rubrik;
	
	String Ressort;
	
        String Titel;
        
	String Text;
        
	String ArtikelPDF;
        
        String Autor;
        
        Map<String,List<String>> wikipedia_entries_onlyPersons = new HashMap();
        public Map<String, List<String>> getWikipedia_entries_onlyPersons() {
            return wikipedia_entries_onlyPersons;
        }
        public void setWikipedia_entries_onlyPersons(Map<String, List<String>> wikipedia_entries) {
            this.wikipedia_entries_onlyPersons = wikipedia_entries;
        }
        
        Map<String,List<String>> wikipedia_entries_noPersons = new HashMap();
        public Map<String, List<String>> getWikipedia_entries_noPersons() {
            return wikipedia_entries_noPersons;
        }
        public void setWikipedia_entries_noPersons(Map<String, List<String>> wikipedia_entries) {
            this.wikipedia_entries_noPersons = wikipedia_entries;
        }
    
	public Artikel()
	{
		
	}

        public String convertDate(String date){
            String convertedDate = "";
            convertedDate = date.substring(4) + "-" + date.substring(2,4) + "-" + date.substring(0,2);
            return convertedDate;
        }
        
	public String getArtikelID() {
		return ArtikelID;
	}

	public void setArtikelID(String artikelID) {
		ArtikelID = artikelID;
	}

	public String getLieferantId() {
		return LieferantId;
	}

	public void setLieferantId(String lieferantId) {
		LieferantId = lieferantId;
	}

	public String getQuelleId() {
		return QuelleId;
	}

	public void setQuelleId(String quelleId) {
		
		System.out.println("Setting Quelle to:"+quelleId);
		QuelleId = quelleId;
	}

	public String getName() {
		return Name;
	}
         @Override
	public boolean equals(Object obj) {
            Artikel other = (Artikel) obj;
            if(this.getArtikelID().equals(other.getArtikelID())
                    || this.getTitel().toLowerCase().equals(other.getTitel().toLowerCase())
                    ||this.getText().equals(other.getText())) return false;
            else return true;
            
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDatum() {
		return Datum;
	}

	public void setDatum(String datum) {
		Datum = datum;
	}

	public String getStartSeite() {
		return StartSeite;
	}

	public void setStartSeite(String startSeite) {
		StartSeite = startSeite;
	}

	public String getRubrik() {
		return Rubrik;
	}

	public void setRubrik(String rubrik) {
		Rubrik = rubrik;
	}

	public String getRessort() {
		return Ressort;
	}

	public void setRessort(String ressort) {
		Ressort = ressort;
	}

        public String getTitel() {
            return Titel;
        }

        public void setTitel(String Titel) {
            this.Titel = Titel;
        }

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

        public String getArtikelPDF() {
            return ArtikelPDF;
        }

        public void setArtikelPDF(String ArtikelPDF) {
            this.ArtikelPDF = ArtikelPDF;
        }

        public String getAutor() {
            return Autor;
        }

        public void setAutor(String Autor) {
            this.Autor = Autor;
        }

	@Override
	public String toString() {
		return "Artikel [ArtikelID=" + ArtikelID + ", LieferantId="
				+ LieferantId + ", QuelleId=" + QuelleId + ", Name=" + Name
				+ ", Datum=" + Datum + ", StartSeite=" + StartSeite
				+ ", Rubrik=" + Rubrik + ", Ressort=" + Ressort + ", Text="
				+ Text + "]\n";
	}
	

}

