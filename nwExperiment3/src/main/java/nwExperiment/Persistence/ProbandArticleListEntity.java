package nwExperiment.Persistence;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Reference;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by fabiankaupmann on 19.04.15.
 */
@Entity
@Table(name = "ProbandArtikelListe")
public class ProbandArticleListEntity {

    @Id
    @GeneratedValue
    @Column(name = "PraeferenzId")
    private Integer preferenceId;

    @Column(name = "ArtikelId")
    private byte[] articleId;

    @Column(name = "ProbandId")
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
