package nwExperiment.Persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by fabiankaupmann on 09.04.15.
 */
@Repository
@Transactional
public interface ArticleRepository extends CrudRepository<ArticleEntity, String>{

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 1", nativeQuery = true)
    Iterable<byte[]> findAllArticlesBielefeld();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 2", nativeQuery = true)
    Iterable<byte[]> findAllArticlesPolitik();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 3", nativeQuery = true)
    Iterable<byte[]> findAllArticlesSportBund();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 4", nativeQuery = true)
    Iterable<byte[]> findAllArticlesKultur();

    @Query(value = "SELECT artikelId FROM artikel WHERE ressortId = 5", nativeQuery = true)
    Iterable<byte[]> findAllArticlesSportBielefeld();

    ArticleEntity findById(byte[] artikelId);




}
