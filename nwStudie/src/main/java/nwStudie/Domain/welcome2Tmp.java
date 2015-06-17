package nwStudie.Domain;

/**
 * Created by fabiankaupmann on 14.06.15.
 */
public class Welcome2Tmp {

    private int currentIndex;
    private int maxIndex;

    public Welcome2Tmp() {
    }

    public Welcome2Tmp(int currentIndex, int maxIndex) {
        this.currentIndex = currentIndex;
        this.maxIndex = maxIndex;
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
}
