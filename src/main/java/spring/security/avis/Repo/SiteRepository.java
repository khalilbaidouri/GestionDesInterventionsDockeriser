package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.entity.Site;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

 /*   Optional<Site> findByNom(String nom);

    List<Site> findByAdresseContainingIgnoreCase(String adresse);

    List<Site> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT s FROM Site s WHERE s.coordonnees IS NOT NULL AND s.coordonnees != ''")
    List<Site> findSitesWithCoordinates();

    @Query("SELECT s FROM Site s LEFT JOIN s.interventions i WHERE i.statut = 'EN_COURS'")
    List<Site> findSitesWithActiveInterventions();

    @Query("SELECT s FROM Site s WHERE SIZE(s.interventions) > :count")
    List<Site> findSitesWithMoreThanInterventions(@Param("count") int count);

    @Query("SELECT COUNT(i) FROM Site s JOIN s.interventions i WHERE s.id = :siteId")
    long countInterventionsBySite(@Param("siteId") Long siteId);

    boolean existsByNom(String nom);

    @Query("SELECT s FROM Site s ORDER BY SIZE(s.interventions) DESC")
    List<Site> findSitesOrderedByInterventionCount();*/
}
