package nwStudie.Persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by fabiankaupmann on 19.04.15.
 */
@Repository
public interface ProbandArticleListRepository extends CrudRepository<ProbandArticleListEntity, Integer> {

    @Modifying
    @Query(value = "UPDATE Praeferenzen SET praeferenz = ?1 where praeferenzId = ?2", nativeQuery = true)
    void updatePraeferenzByPraeferenzId(String praeferenz, int praeferenzId);

    @Query(value = "select min(praeferenzId) from ProbandArtikelListe where probandId = ?1", nativeQuery = true)
    Integer findMinIndexOfArticleListForProband(int probandId);

    @Query(value = "select max(praeferenzId) from ProbandArtikelListe where probandId = ?1", nativeQuery = true)
    Integer findMaxIndexOfArticleListForProband(int probandId);

    @Query(value = "select ArtikelId from ProbandArtikelListe where praeferenzId = ?1", nativeQuery = true)
    byte[] findArticleIdByPraeferenzId(int praeferenzId);
}
