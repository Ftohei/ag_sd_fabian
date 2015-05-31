package nwStudie.Persistence;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by fabiankaupmann on 06.05.15.
 */
@Entity
@Table(name = "Praeferenzen")
public class PreferenceEntity {

    @Id
    @Column(name = "praeferenzId")
    private Integer preferenceId;

    @Column(name = "praeferenz")
    private String preference;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentInstantAsTimestamp")
    @Column(name = "timestamp")
    private Instant timestamp;

    public PreferenceEntity() {
    }

    public PreferenceEntity(Integer praeferenzId, String preference) {
        this.preferenceId = praeferenzId;
        this.preference = preference;
        this.timestamp = Instant.now();
    }

    public Integer getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Integer preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
