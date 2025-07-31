package spring.security.avis.Service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Repo.DemandeInterventionRepository;
import spring.security.avis.Repo.EvenementRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/

@Service
public class DemandeInterventionService {
    private final DemandeInterventionRepository demandeInterventionRepository;
    private final UserRepo userRepo;
    private final EvenementRepository evenementRepository;
    private final CalendrierService calendrierService;
    public DemandeInterventionService(DemandeInterventionRepository demandeInterventionRepository, View error, UserRepo userRepo, EvenementRepository evenementRepository, CalendrierService calendrierService) {
        this.demandeInterventionRepository = demandeInterventionRepository;
        this.userRepo = userRepo;
        this.evenementRepository = evenementRepository;
        this.calendrierService = calendrierService;
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
        demandeIntervention.setDateAnnoncement(LocalDateTime.now());
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
        intervention.setPriorite(demande.getPriorite());
        intervention.setStatut(StatutIntervention.ACCEPTE);
        intervention.setLocalisation(demande.getLocalisation());

        Calendrier calendrier = calendrierService.getCalendrierUnique();
        intervention.setCalendrier(calendrier);

        Evenement evenement = new Evenement();
        evenement.setTitre(demande.getNom());
        evenement.setIntervention(intervention);
        evenement.setLieu(intervention.getLocalisation());
        evenement.setDescription("Intervention demarree");
        evenement.setDateDebut(LocalDateTime.now());
        evenement.setCalendrier(calendrier);

        intervention.setEvenement(evenement);

        Rapport rapport = new Rapport();
        rapport.setIntervention(intervention);
        rapport.setContenu("");

        intervention.setRapport(rapport);

        demande.setIntervention(intervention);

        demandeInterventionRepository.save(demande);

        return intervention;
    }

    public void modifierDemande(Long id, DemandeIntervention demandeIntervention) {
        Optional<DemandeIntervention> demandeRecuperer = demandeInterventionRepository.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User utilisateur = userRepo.findByEmail(username);

        if (demandeRecuperer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable avec id: " + id);
        }

        DemandeIntervention demande = demandeRecuperer.get();

        if (!demande.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas autorise à modifier cette demande.");
        }

        demande.setLocalisation(demandeIntervention.getLocalisation());
        demande.setDescription(demandeIntervention.getDescription());
        demande.setNom(demandeIntervention.getNom());
        demande.setTypeIntervention(demandeIntervention.getTypeIntervention());

        demandeInterventionRepository.save(demande);
    }

    public List<DemandeIntervention> getDemandeInterventionByPriorite(Priorite etat) throws AccessDeniedException {
        List<DemandeIntervention> demandeInterventions = demandeInterventionRepository.findByPriorite(etat);
        return demandeInterventions;
    }

}
