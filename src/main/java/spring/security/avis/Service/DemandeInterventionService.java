package spring.security.avis.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Repo.DemandeInterventionRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
public class DemandeInterventionService {
    private final DemandeInterventionRepository demandeInterventionRepository;
    private final UserRepo userRepo;
    public DemandeInterventionService(DemandeInterventionRepository demandeInterventionRepository, View error, UserRepo userRepo) {
        this.demandeInterventionRepository = demandeInterventionRepository;
        this.userRepo = userRepo;
    }

public void demandeIntervention(DemandeIntervention demandeIntervention) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User utilisateur = userRepo.findByEmail(username);
    demandeIntervention.setStatut(StatutDemande.EN_ATTENTE);

    Optional<DemandeIntervention> demandeInterventionExiste = demandeInterventionRepository
            .findDemandeInterventionExiste(
                    demandeIntervention.getLocalisation(),
                    demandeIntervention.getStatut(),
                    demandeIntervention.getTypeIntervention(),
                    demandeIntervention.getPriorite()
            );

    if (!demandeInterventionExiste.isPresent()) {
        demandeIntervention.setDateCreation(LocalDateTime.now());
        demandeIntervention.setUtilisateur(utilisateur);
        this.demandeInterventionRepository.save(demandeIntervention);
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une demande similaire existe deja.");
    }
}

    public Intervention accepterDemande(Long idDemande,LocalDateTime dateDebut, LocalDateTime dateEcheance) {
        DemandeIntervention demande = demandeInterventionRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new RuntimeException("Demande déjà traitee");
        }

        demande.setStatut(StatutDemande.APPROUVEE);

        Intervention intervention = new Intervention();
        intervention.setDemandeIntervention(demande);
        intervention.setDescription(demande.getDescription());
        intervention.setDateDebut(dateDebut);
        intervention.setDateFin(dateEcheance);
        intervention.setStatut(StatutIntervention.ACCEPTE);
        intervention.setLocalisation(demande.getLocalisation());

        Evenement evenement = new Evenement();
        evenement.setTitre(demande.getNom());
        evenement.setIntervention(intervention);
        evenement.setDescription("Intervention démarree");
        evenement.setDateDebut(LocalDateTime.now());
        intervention.setEvenement(evenement);

        Rapport rapport = new Rapport();
        rapport.setIntervention(intervention);
        rapport.setContenu("");

        intervention.setRapport(rapport);

        demande.setIntervention(intervention);

        demandeInterventionRepository.save(demande);

        return intervention;
    }




}
