package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.StatutRapport;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.Rapport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    Optional<Rapport> findByIntervention(Intervention intervention);

    Optional<Rapport> findByInterventionId(Long interventionId);

    List<Rapport> findByStatut(StatutRapport statut);

    @Query("SELECT r FROM Rapport r WHERE r.dateCreation BETWEEN :debut AND :fin")
    List<Rapport> findByDateCreationBetween(@Param("debut") LocalDateTime debut,
                                            @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM Rapport r WHERE r.statut = 'BROUILLON'")
    List<Rapport> findDraftReports();

    @Query("SELECT r FROM Rapport r WHERE r.statut = 'FINALISE' AND r.dateCreation >= :dateDebut")
    List<Rapport> findRecentFinalizedReports(@Param("dateDebut") LocalDateTime dateDebut);


    @Query("SELECT COUNT(r) FROM Rapport r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") StatutRapport statut);

    @Query("SELECT r FROM Rapport r WHERE r.contenu LIKE %:keyword%")
    List<Rapport> findByContenuContaining(@Param("keyword") String keyword);

    boolean existsByIntervention(Intervention intervention);
}