package nwStudie.Persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fabiankaupmann on 16.04.15.
 */
@Repository
public interface ProbandRepository extends CrudRepository<ProbandEntity, Integer>{

    @Query(value = "SELECT max(probandId) FROM proband", nativeQuery = true)
    Integer findNewestId();

}
