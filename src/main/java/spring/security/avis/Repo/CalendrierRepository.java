package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.StatutCalendrier;
import spring.security.avis.entity.Calendrier;
import spring.security.avis.entity.Role;
import spring.security.avis.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendrierRepository extends JpaRepository<Calendrier, Long> {

//    Optional<Calendrier> findByUtilisateur(User utilisateur);
//
//    Optional<Calendrier> findByUtilisateurId(Long utilisateurId);
//
//    List<Calendrier> findByStatut(StatutCalendrier statut);
//
//    @Query("SELECT c FROM Calendrier c WHERE c.statut = 'ACTIF'")
//    List<Calendrier> findActiveCalendriers();
//
//    @Query("SELECT c FROM Calendrier c WHERE c.utilisateur.role = :role AND c.statut = 'ACTIF'")
//    List<Calendrier> findActiveCalendriersByRole(@Param("role") Role role);
//
//    boolean existsByUtilisateur(User utilisateur);
//
//    @Query("SELECT COUNT(c) FROM Calendrier c WHERE c.statut = :statut")
//    long countByStatut(@Param("statut") StatutCalendrier statut);

        Optional<Calendrier> findFirstByOrderByIdAsc();
}
