import java.util.ArrayList;

/**
 * Created by fabiankaupmann on 06.04.15.
 */
public class PersonWithInterests {

    private String givenName;

    private String familyName;

    private String fullName;

    private ArrayList<String> interests;

    private ArrayList<String> pageIds = new ArrayList();
    private ArrayList<String> wikiTitles = new ArrayList();
    private ArrayList<Float> scores = new ArrayList();

    public PersonWithInterests(String givenName, String familyName){
        this.givenName = givenName;
        this.familyName = familyName;
        this.fullName = givenName + " " + familyName;
        this.interests = new ArrayList<>();

    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
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
