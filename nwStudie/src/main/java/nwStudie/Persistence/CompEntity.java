package nwStudie.Persistence;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Created by fabiankaupmann on 03.06.15.
 */
@Entity
@Table(name="AuswahlPart2")
public class CompEntity {

    @Id
    @Column(name = "auswahlId")
    private Integer compId;

    @Column(name = "verständlichkeit")
    private String comprehensibility;

    @Column(name = "komplexitaet")
    private String complexity;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentInstantAsTimestamp")
    @Column(name = "timestamp")
    private Instant timestamp;

    public CompEntity() {
    }

    public CompEntity(Integer praeferenzId, String comprehensibility, String complexity) {
        this.compId = praeferenzId;
        this.comprehensibility = comprehensibility;
        this.complexity = complexity;
        this.timestamp = Instant.now();
    }

    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
