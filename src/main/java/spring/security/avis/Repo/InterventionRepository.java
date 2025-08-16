package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.Priorite;
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
    List<Intervention> findByPriorite(Priorite priorite);



    @Query("SELECT i FROM Intervention i WHERE i.dateDebut BETWEEN :debut AND :fin")
    List<Intervention> findByDateDebutBetween(@Param("debut") LocalDateTime debut,
                                              @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Intervention i WHERE i.dateFin BETWEEN :debut AND :fin")
    List<Intervention> findByDateFinBetween(@Param("debut") LocalDateTime debut,
                                            @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Intervention i WHERE i.statut = 'EN_COURS'")
    List<Intervention> findInterventionEnCours();
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'ACCEPTE'")
    List<Intervention> findInterventionEnAccepte();
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'TERMINEE'")
    List<Intervention> findInterventionTerminer();
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'ECHEC'")
    List<Intervention> findInterventionEchouer();

    @Query("SELECT i FROM Intervention i WHERE i.priorite = 'CRITIQUE'")
    List<Intervention> findInterventionCritique();
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'NORMALE'")
    List<Intervention> findInterventionNormale();

    @Query("SELECT i FROM Intervention i WHERE i.statut = 'HAUTE'")
    List<Intervention> findInterventionHaute();

    @Query("SELECT i FROM Intervention i WHERE i.statut = 'BASSE'")
    List<Intervention> findInterventionBasse();




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
            List<StatutIntervention> statuts,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    );

    List<Intervention> findByIngenieurId(Long id);

    List<Intervention> findByPrioriteAndIngenieurEmail(Priorite priorite, String email);

    @Query("SELECT i FROM Intervention i " +
            "WHERE i.priorite = :priority " +
            "AND i.ingenieur.email = :email")
    List<Intervention> findByPriorityAndUserEmail(
            @Param("priority") Priorite priority,
            @Param("email") String email);



    List<Intervention> findAllByOrderByIdDesc();


}