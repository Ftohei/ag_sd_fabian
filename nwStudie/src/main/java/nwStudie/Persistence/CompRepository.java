package nwStudie.Persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fabiankaupmann on 03.06.15.
 */
@Repository
public interface CompRepository extends CrudRepository<CompEntity, Integer> {
}
