package nwStudie.Domain;

/**
 * Created by fabiankaupmann on 05.05.15.
 */

/**
 * Speichert alle Daten, die während des Experiments gehalten werden müssen: artikelId, artikelListe, aktueller Artikelindex aus Liste
 */
public class TmpData {

    public int currentIndex;

    public int maxIndex;

    public String preference;

    public TmpData() {
    }

    public TmpData(int minIndex, int maxIndex) {
        this.maxIndex = maxIndex;
        this.currentIndex = minIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}
