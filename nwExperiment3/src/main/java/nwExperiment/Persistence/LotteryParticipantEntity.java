package nwExperiment.Persistence;

import javax.persistence.*;

/**
 * Created by fabiankaupmann on 12.05.15.
 */
@Entity
@Table(name = "TeilnehmerVerlosung")
public class LotteryParticipantEntity {

    @Id
    @GeneratedValue
    @Column(name = "teilnehmerId")
    private int id;

    @Column(name = "email")
    private String email;

    public LotteryParticipantEntity() {
    }

    public LotteryParticipantEntity(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
