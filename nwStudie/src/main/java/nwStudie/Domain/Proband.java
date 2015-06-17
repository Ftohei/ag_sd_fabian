package nwStudie.Domain;

/**
 * Created by fabiankaupmann on 13.04.15.
 */
public class Proband {


    public String sex;
    public int age;
    public String interests;
    public int postalCode;
    public String graduation;
    public String rubrik;
    public int interestInPolitics;
    public int interestInCulture;
    public int interestInLocalArticles;
    public int interestInSports;
    public int interestInLocalSports;

    //used for iterating over articles
    public int iterator = 0;

    public int probandId;

    public String origin;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getProbandId() {
        return probandId;
    }

    public void setProbandId(int probandId) {
        this.probandId = probandId;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getRubrik() {
        return rubrik;
    }

    public void setRubrik(String rubrik) {
        this.rubrik = rubrik;
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getInterestInPolitics() {
        return interestInPolitics;
    }

    public void setInterestInPolitics(int interestInPolitics) {
        this.interestInPolitics = interestInPolitics;
    }

    public int getInterestInCulture() {
        return interestInCulture;
    }

    public void setInterestInCulture(int interestInCulture) {
        this.interestInCulture = interestInCulture;
    }

    public int getInterestInLocalArticles() {
        return interestInLocalArticles;
    }

    public void setInterestInLocalArticles(int interestInLocalArticles) {
        this.interestInLocalArticles = interestInLocalArticles;
    }

    public int getInterestInSports() {
        return interestInSports;
    }

    public void setInterestInSports(int interestInSports) {
        this.interestInSports = interestInSports;
    }

    public int getInterestInLocalSports() {
        return interestInLocalSports;
    }

    public void setInterestInLocalSports(int interestInLocalSports) {
        this.interestInLocalSports = interestInLocalSports;
    }

    public String getGraduation() {
        return graduation;
    }

    public void setGraduation(String graduation) {
        this.graduation = graduation;
    }
}
