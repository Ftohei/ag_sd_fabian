package de.citec.util;

import java.util.HashMap;
import java.util.Map;

public class Artikel {
    
        int SqlID = 0;

        public int getSqlID() {
            return SqlID;
        }

        public void setSqlID(int SqlID) {
            this.SqlID = SqlID;
        }

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
        
        String TaggedText;

        
	String ArtikelPDF;
        
        String Autor;
        
        Map<Integer,Float> wikipedia_entries_onlyPersons = new HashMap();
        Map<Integer, Float> wikipedia_entries_all = new HashMap();

    
	public Artikel()
	{
		
	}
        
        public String getTaggedText() {
            return TaggedText;
        }

        public void setTaggedText(String TaggedText) {
            this.TaggedText = TaggedText;
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
        
        public Map<Integer, Float> getWikipedia_entries_onlyPersons() {
            return wikipedia_entries_onlyPersons;
        }

        public void setWikipedia_entries_onlyPersons(Map<Integer, Float> wikipedia_entries_onlyPersons) {
            this.wikipedia_entries_onlyPersons = wikipedia_entries_onlyPersons;
        }

        public Map<Integer, Float> getWikipedia_entries_all() {
            return wikipedia_entries_all;
        }

        public void setWikipedia_entries_all(Map<Integer, Float> wikipedia_entries_all) {
            this.wikipedia_entries_all = wikipedia_entries_all;
        }
	

}

