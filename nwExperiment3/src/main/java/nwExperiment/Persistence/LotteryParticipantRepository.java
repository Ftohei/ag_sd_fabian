package nwExperiment.Persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fabiankaupmann on 12.05.15.
 */
@Repository
public interface LotteryParticipantRepository extends CrudRepository<LotteryParticipantEntity, Integer> {
}
