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

        Optional<Calendrier> findFirstByOrderByIdAsc();
//        @Query("SELECT DISTINCT c FROM Calendrier c " +
//                "JOIN c.evenements e " +
//                "WHERE e.utilisateur.id = :userId")
//        List<Calendrier> findCalendriersByEvenementUtilisateurId(@Param("userId") Long userId);

}
