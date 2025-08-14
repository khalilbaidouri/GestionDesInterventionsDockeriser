package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.Enum.TypeIntervention;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeInterventionRepository extends JpaRepository<DemandeIntervention, Long> {

    List<DemandeIntervention> findByUtilisateur(User utilisateur);

    List<DemandeIntervention> findByUtilisateurId(Long utilisateurId);

    List<DemandeIntervention> findByStatut(StatutDemande statut);

    List<DemandeIntervention> findByPriorite(Priorite priorite);

    List<DemandeIntervention> findByTypeIntervention(String typeIntervention);

    List<DemandeIntervention> findByLocalisation(String localisation);


    @Query("SELECT COUNT(d) FROM DemandeIntervention d WHERE d.statut = :statut")
    long countByStatut(@Param("statut") StatutDemande statut);

    @Query("SELECT d FROM DemandeIntervention d WHERE d.utilisateur.id = :userId AND d.statut = :statut")
    List<DemandeIntervention> findByUserIdAndStatut(@Param("userId") Long userId,
                                                    @Param("statut") StatutDemande statut);


    @Query("SELECT d FROM DemandeIntervention d WHERE d.localisation = :localisation  AND d.statut = :statut AND d.typeIntervention = :typeIntervention AND d.priorite = :priorite")
    Optional<DemandeIntervention> findDemandeInterventionExiste(@Param("localisation") String localisation,
                                                                @Param("statut") StatutDemande statut,
                                                                @Param("typeIntervention") TypeIntervention typeIntervention,
                                                                @Param("priorite") Priorite priorite
    );


    @Query("SELECT d FROM DemandeIntervention d JOIN FETCH d.utilisateur u")
    List<DemandeIntervention> findAllWithUtilisateur();

    @Query("SELECT d FROM DemandeIntervention d JOIN FETCH d.utilisateur WHERE d.utilisateur.email = :email")
    List<DemandeIntervention> findByUtilisateurEmailWithUser(@Param("email") String email);

    // Ou avec @EntityGraph :
    @EntityGraph(attributePaths = {"utilisateur"})
    List<DemandeIntervention> findAll();


}