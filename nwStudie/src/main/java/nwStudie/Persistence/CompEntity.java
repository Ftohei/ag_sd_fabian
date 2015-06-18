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
@Table(name="auswahlPart2")
public class CompEntity {

    @Id
    @Column(name = "auswahlId")
    private Integer compId;

    @Column(name = "verständlichkeit")
    private int comprehensibility;

    @Column(name = "komplexitaet")
    private int complexity;

    @Column(name = "interesse")
    private int interest;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentInstantAsTimestamp")
    @Column(name = "timestamp")
    private Instant timestamp;

    public CompEntity() {
    }

    public CompEntity(Integer praeferenzId, int comprehensibility, int complexity, int interest) {
        this.compId = praeferenzId;
        this.comprehensibility = comprehensibility;
        this.complexity = complexity;
        this.interest = interest;
        this.timestamp = Instant.now();
    }

    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    public int getComprehensibility() {
        return comprehensibility;
    }

    public void setComprehensibility(int comprehensibility) {
        this.comprehensibility = comprehensibility;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
