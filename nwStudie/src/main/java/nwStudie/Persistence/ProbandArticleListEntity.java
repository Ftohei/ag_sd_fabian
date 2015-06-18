package nwStudie.Persistence;

import javax.persistence.*;

/**
 * Created by fabiankaupmann on 19.04.15.
 */
@Entity
@Table(name = "probandArtikelListe")
public class ProbandArticleListEntity {

    @Id
    @GeneratedValue
    @Column(name = "auswahlId")
    private Integer preferenceId;

    @Column(name = "artikelId")
    private byte[] articleId;

    @Column(name = "probandId")
    private int probandId;

//    @Column(name = "praeferenz")
//    private String preference;

//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentInstantAsTimestamp")
//    @Column(name = "timestamp")
//    private Instant timestamp;

    public ProbandArticleListEntity() {
    }

    public ProbandArticleListEntity(byte[] articleId, int probandId) {
        this.articleId = articleId;
        this.probandId = probandId;
//        this.timestamp = Instant.now();
    }

    public byte[] getArticleId() {
        return articleId;
    }

    public void setArticleId(byte[] articleId) {
        this.articleId = articleId;
    }

    public Integer getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Integer preferenceId) {
        this.preferenceId = preferenceId;
    }

    public int getProbandId() {
        return probandId;
    }

    public void setProbandId(int probandId) {
        this.probandId = probandId;
    }

//    public String getPreference() {
//        return preference;
//    }
//
//    public void setPreference(String preference) {
//        this.preference = preference;
//    }
}
