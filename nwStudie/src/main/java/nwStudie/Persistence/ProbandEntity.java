package nwStudie.Persistence;

import javax.persistence.*;

/**
 * Created by fabiankaupmann on 16.04.15.
 */
@Entity
@Table(name = "proband")
public class ProbandEntity {

    @Id
    @GeneratedValue
    @Column(name = "probandId")
    private Integer id;

    @Column(name = "probandGeschlecht")
    private String sex;

    @Column(name = "probandAlter")
    private int age;

    @Column(name = "probandPostleitzahl")
    private int postalCode;

    @Column(name = "interessantesteRubrik")
    private String mostInterestingRubrik;

    @Column(name = "hobbies")
    private String interests;

    @Column(name = "herkunft")
    private String origin;

    @Column(name = "abschluss")
    private String graduation;

    @Column(name = "probandInteressePolitik")
    private int interestInPolitics;

    @Column(name = "probandInteresseKultur")
    private int interestInCulture;

    @Column(name = "probandInteresseLokales")
    private int interestInLocalArticles;

    @Column(name = "probandInteresseSport")
    private int interestInSports;

    @Column(name = "probandInteresseLokalsport")
    private int interestInLocalSports;



    public ProbandEntity() {
    }

    public ProbandEntity(String sex, int age, int postalCode, String mostInterestingRubrik, String interests, String origin, String graduation, int interestInPolitics, int interestInCulture, int interestInLocalArticles, int interestInSports, int interestInLocalSports) {
        this.sex = sex;
        this.age = age;
        this.postalCode = postalCode;
        this.mostInterestingRubrik = mostInterestingRubrik;
        this.interests = interests;
        this.origin = origin;
        this.graduation = graduation;
        this.interestInPolitics = interestInPolitics;
        this.interestInCulture = interestInCulture;
        this.interestInLocalArticles = interestInLocalArticles;
        this.interestInSports = interestInSports;
        this.interestInLocalSports = interestInLocalSports;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getMostInterestingRubrik() {
        return mostInterestingRubrik;
    }

    public void setMostInterestingRubrik(String mostInterestingRubrik) {
        this.mostInterestingRubrik = mostInterestingRubrik;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getGraduation() {
        return graduation;
    }

    public void setGraduation(String graduation) {
        this.graduation = graduation;
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
}
