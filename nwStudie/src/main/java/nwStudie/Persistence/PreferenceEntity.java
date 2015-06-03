package nwStudie.Persistence;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by fabiankaupmann on 06.05.15.
 */
@Entity
@Table(name = "AuswahlPart1")
public class PreferenceEntity {

    @Id
    @Column(name = "auswahlId")
    private Integer preferenceId;

    @Column(name = "praeferenz")
    private int preference;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentInstantAsTimestamp")
    @Column(name = "timestamp")
    private Instant timestamp;

    public PreferenceEntity() {
    }

    public PreferenceEntity(Integer praeferenzId, int preference) {
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

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
