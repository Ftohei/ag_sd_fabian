package nwStudie.Domain;

/**
 * Created by fabiankaupmann on 02.06.15.
 */

/**
 * Speichert alle Daten, die während des zweiten Teils des Experiments gehalten werden müssen: artikelId, artikelListe, aktueller Artikelindex aus Liste
 */
public class TmpData2 {

    public int currentIndex;

    public int maxIndex;

    public String comprehensibility;

    public String complexity;

    public TmpData2() {
    }

    public TmpData2(int minIndex, int maxIndex) {
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

    public String getComprehensibility() {
        return comprehensibility;
    }

    public void setComprehensibility(String comprehensibility) {
        this.comprehensibility = comprehensibility;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }
}
