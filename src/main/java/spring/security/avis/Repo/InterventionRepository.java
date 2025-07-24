package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    Optional<Intervention> findByDemandeIntervention(DemandeIntervention demandeIntervention);

    List<Intervention> findByStatut(StatutIntervention statut);

   // List<Intervention> findBySite(Site site);

   // List<Intervention> findBySiteId(Long siteId);

    @Query("SELECT i FROM Intervention i WHERE i.dateDebut BETWEEN :debut AND :fin")
    List<Intervention> findByDateDebutBetween(@Param("debut") LocalDateTime debut,
                                              @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Intervention i WHERE i.dateFin BETWEEN :debut AND :fin")
    List<Intervention> findByDateFinBetween(@Param("debut") LocalDateTime debut,
                                            @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Intervention i WHERE i.statut = 'EN_COURS'")
    List<Intervention> findActiveInterventions();

    @Query("SELECT i FROM Intervention i WHERE i.statut = 'PLANIFIEE' AND i.dateDebut <= :date")
    List<Intervention> findScheduledInterventionsToStart(@Param("date") LocalDateTime date);



    @Query("SELECT AVG(i.dureeReelle) FROM Intervention i WHERE i.statut = 'TERMINEE' AND i.dureeReelle IS NOT NULL")
    Double getAverageDuration();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.statut = :statut")
    long countByStatut(@Param("statut") StatutIntervention statut);

    @Query("SELECT i FROM Intervention i WHERE YEAR(i.dateDebut) = :year AND MONTH(i.dateDebut) = :month")
    List<Intervention> findByYearAndMonth(@Param("year") int year, @Param("month") int month);



    List<Intervention> findByIngenieurAndStatutInAndDateFinAfterAndDateDebutBefore(
            User ingenieur,
            List<StatutIntervention> statuts,  // Assurez-vous que c'est bien List<StatutIntervention>
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    );

}