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
    @Query(value = "UPDATE praeferenzen SET praeferenz = ?1 where auswahlId = ?2", nativeQuery = true)
    void updatePraeferenzByPraeferenzId(String praeferenz, int praeferenzId);

    @Query(value = "select min(auswahlId) from probandArtikelListe where probandId = ?1", nativeQuery = true)
    Integer findMinIndexOfArticleListForProband(int probandId);

    @Query(value = "select max(auswahlId) from probandArtikelListe where probandId = ?1", nativeQuery = true)
    Integer findMaxIndexOfArticleListForProband(int probandId);

    @Query(value = "select artikelId from probandArtikelListe where auswahlId = ?1", nativeQuery = true)
    byte[] findArticleIdByPraeferenzId(int praeferenzId);
}
