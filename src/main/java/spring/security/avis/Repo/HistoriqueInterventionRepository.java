package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.TypeAction;
import spring.security.avis.entity.HistoriqueIntervention;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoriqueInterventionRepository extends JpaRepository<HistoriqueIntervention, Long> {

    List<HistoriqueIntervention> findByIntervention(Intervention intervention);

    List<HistoriqueIntervention> findByInterventionId(Long interventionId);

    List<HistoriqueIntervention> findByUtilisateur(User utilisateur);

    List<HistoriqueIntervention> findByUtilisateurId(Long utilisateurId);

    //List<HistoriqueIntervention> findByAction(TypeAction action);
/*
    @Query("SELECT h FROM HistoriqueIntervention h WHERE h.dateAction BETWEEN :debut AND :fin")
    List<HistoriqueIntervention> findByDateActionBetween(@Param("debut") LocalDateTime debut,
                                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT h FROM HistoriqueIntervention h WHERE h.intervention.id = :interventionId ORDER BY h.dateAction DESC")
    List<HistoriqueIntervention> findByInterventionIdOrderByDateActionDesc(@Param("interventionId") Long interventionId);


    @Query("SELECT h FROM HistoriqueIntervention h WHERE h.action = :action AND h.dateAction >= :dateDebut")
    List<HistoriqueIntervention> findRecentActionsByType(@Param("action") TypeAction action,
                                                         @Param("dateDebut") LocalDateTime dateDebut);

    @Query("SELECT COUNT(h) FROM HistoriqueIntervention h WHERE h.utilisateur.id = :userId AND h.action = :action")
    long countByUtilisateurIdAndAction(@Param("userId") Long userId, @Param("action") TypeAction action);

    @Query("SELECT h FROM HistoriqueIntervention h WHERE h.ipAddress = :ipAddress ORDER BY h.dateAction DESC")
    List<HistoriqueIntervention> findByIpAddressOrderByDateActionDesc(@Param("ipAddress") String ipAddress);*/
}
