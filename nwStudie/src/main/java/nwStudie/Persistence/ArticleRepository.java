package nwStudie.Persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by fabiankaupmann on 09.04.15.
 */
@Repository
@Transactional
public interface ArticleRepository extends CrudRepository<ArticleEntity, String>{
    

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 1  AND anzahl_woerter > 50 AND datum = (SELECT max(datum) FROM ausgabe)", nativeQuery = true)
    Iterable<byte[]> findAllArticlesBielefeld();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 2  AND anzahl_woerter > 50 AND datum = (SELECT max(datum) FROM ausgabe)", nativeQuery = true)
    Iterable<byte[]> findAllArticlesPolitik();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 3  AND anzahl_woerter > 50 AND datum = (SELECT max(datum) FROM ausgabe)", nativeQuery = true)
    Iterable<byte[]> findAllArticlesSportBund();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 4  AND anzahl_woerter > 50 AND datum = (SELECT max(datum) FROM ausgabe)", nativeQuery = true)
    Iterable<byte[]> findAllArticlesKultur();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 5  AND anzahl_woerter > 50 AND datum = (SELECT max(datum) FROM ausgabe)", nativeQuery = true)
    Iterable<byte[]> findAllArticlesSportBielefeld();


    //TODO: nur kultur, politik und lokales
    @Query(value = "SELECT artikelId FROM artikel WHERE  anzahl_woerter > 70 AND datum = (SELECT max(datum) FROM ausgabe) AND (ressortId = 1 OR ressortId = 2 OR ressortId = 4)", nativeQuery = true)
    Iterable<byte[]> findAllArticles();

    ArticleEntity findById(byte[] artikelId);




}
