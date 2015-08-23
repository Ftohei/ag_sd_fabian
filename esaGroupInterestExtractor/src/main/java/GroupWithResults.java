import java.util.ArrayList;

/**
 * Created by Fabian on 21.08.15.
 */
public class GroupWithResults {

    int maxAge;
    int minAge;

    String taggedSearchQuery;

    private ArrayList<String> pageIds = new ArrayList();
    private ArrayList<String> wikiTitles = new ArrayList();
    private ArrayList<Float> scores = new ArrayList();

    public GroupWithResults(int maxAge, int minAge) {
        this.maxAge = maxAge;
        this.minAge = minAge;
        this.pageIds = new ArrayList();
        this.wikiTitles = new ArrayList();
        this.scores = new ArrayList();
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public String getTaggedSearchQuery() {
        return taggedSearchQuery;
    }

    public void setTaggedSearchQuery(String taggedSearchQuery) {
        this.taggedSearchQuery = taggedSearchQuery;
    }

    public ArrayList<String> getPageIds() {
        return pageIds;
    }

    public void setPageIds(ArrayList<String> pageIds) {
        this.pageIds = pageIds;
    }

    public ArrayList<String> getWikiTitles() {
        return wikiTitles;
    }

    public void setWikiTitles(ArrayList<String> wikiTitles) {
        this.wikiTitles = wikiTitles;
    }

    public ArrayList<Float> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Float> scores) {
        this.scores = scores;
    }
}
