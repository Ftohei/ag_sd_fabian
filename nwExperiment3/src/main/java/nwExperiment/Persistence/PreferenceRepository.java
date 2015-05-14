package nwExperiment.Persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fabiankaupmann on 06.05.15.
 */
@Repository
public interface PreferenceRepository extends CrudRepository<PreferenceEntity, Integer>{
}
