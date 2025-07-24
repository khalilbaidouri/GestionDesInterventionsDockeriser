package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.TypeEvenement;
import spring.security.avis.entity.Evenement;
import spring.security.avis.entity.Intervention;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    Optional<Evenement> findByIntervention(Intervention intervention);

    Optional<Evenement> findByInterventionId(Long interventionId);

    List<Evenement> findByType(TypeEvenement type);

    List<Evenement> findByLieu(String lieu);

    @Query("SELECT e FROM Evenement e WHERE e.dateDebut BETWEEN :debut AND :fin")
    List<Evenement> findByDateDebutBetween(@Param("debut") LocalDateTime debut,
                                           @Param("fin") LocalDateTime fin);

    @Query("SELECT e FROM Evenement e WHERE e.dateFin BETWEEN :debut AND :fin")
    List<Evenement> findByDateFinBetween(@Param("debut") LocalDateTime debut,
                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT e FROM Evenement e WHERE e.dateDebut <= :maintenant AND e.dateFin >= :maintenant")
    List<Evenement> findCurrentEvents(@Param("maintenant") LocalDateTime maintenant);

    @Query("SELECT e FROM Evenement e WHERE e.dateDebut > :maintenant ORDER BY e.dateDebut ASC")
    List<Evenement> findUpcomingEvents(@Param("maintenant") LocalDateTime maintenant);


    @Query("SELECT COUNT(e) FROM Evenement e WHERE e.type = :type")
    long countByType(@Param("type") TypeEvenement type);

    @Query("SELECT e FROM Evenement e WHERE e.titre LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Evenement> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByIntervention(Intervention intervention);
}