package spring.security.avis.Service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import spring.security.avis.DTO.DemandeInterventionDTO;
import spring.security.avis.Enum.*;
import spring.security.avis.Repo.*;
import spring.security.avis.entity.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
@Transactional
public class DemandeInterventionService {
    private final DemandeInterventionRepository demandeInterventionRepository;
    private final UserRepo userRepo;
    private final EvenementRepository evenementRepository;
    private final CalendrierService calendrierService;
    private final InterventionRepository interventionRepository;
    private final NotificationRepository notificationRepository;

    public DemandeInterventionService(DemandeInterventionRepository demandeInterventionRepository, View error, UserRepo userRepo, EvenementRepository evenementRepository, CalendrierService calendrierService, InterventionRepository interventionRepository, NotificationRepository notificationRepository) {
        this.demandeInterventionRepository = demandeInterventionRepository;
        this.userRepo = userRepo;
        this.evenementRepository = evenementRepository;
        this.calendrierService = calendrierService;
        this.interventionRepository = interventionRepository;
        this.notificationRepository = notificationRepository;
    }


    public void creerDemande(DemandeInterventionDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User utilisateur = userRepo.findByEmail(username);

        // Vérification d'existence
        Optional<DemandeIntervention> existe = demandeInterventionRepository
                .findDemandeInterventionExiste(
                        dto.getLocalisation(),
                        StatutDemande.EN_ATTENTE,
                        TypeIntervention.valueOf(dto.getTypeIntervention().name()),
                        dto.getPriorite()
                );

        if (existe.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une demande similaire existe déjà.");
        }

        DemandeIntervention demande = new DemandeIntervention();
        demande.setNom(dto.getNom());
        demande.setDescription(dto.getDescription());
        demande.setPriorite(dto.getPriorite());
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setTypeIntervention(dto.getTypeIntervention());
        demande.setDateAnnoncement(LocalDateTime.now());
        demande.setLocalisation(dto.getLocalisation());
        demande.setUtilisateur(utilisateur);

        demandeInterventionRepository.save(demande);
        demandeInterventionRepository.flush();

    }



//
//public void demandeIntervention(DemandeIntervention demandeIntervention) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    String username = authentication.getName();
//
//    User utilisateur = userRepo.findByEmail(username);
//    demandeIntervention.setStatut(StatutDemande.EN_ATTENTE);
//
//    Optional<DemandeIntervention> demandeInterventionExiste = demandeInterventionRepository
//            .findDemandeInterventionExiste(
//                    demandeIntervention.getLocalisation(),
//                    demandeIntervention.getStatut(),
//                    TypeIntervention.valueOf(String.valueOf(demandeIntervention.getTypeIntervention())),
//                    demandeIntervention.getPriorite()
//            );
//
//    if (!demandeInterventionExiste.isPresent()) {
//        demandeIntervention.setDateAnnoncement(LocalDateTime.now());
//        demandeIntervention.setUtilisateur(utilisateur);
//        this.demandeInterventionRepository.save(demandeIntervention);
//    } else {
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une demande similaire existe deja.");
//    }
//}


    public Intervention accepterDemande(Long idDemande, LocalDateTime dateDebut, LocalDateTime dateEcheance) {
        DemandeIntervention demande = demandeInterventionRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new RuntimeException("Demande deja traitee");
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

   /* public void modifierDemande(Long id, DemandeIntervention demandeIntervention) {
        Optional<DemandeIntervention> demandeRecuperer = demandeInterventionRepository.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User utilisateur = userRepo.findByEmail(username);

        if (demandeRecuperer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable avec id: " + id);
        }
        if(demandeRecuperer.get().getStatut().equals(StatutDemande.EN_COURS)){
            throw new RuntimeException("demande deja EN_COURS");
        }

        DemandeIntervention demande = demandeRecuperer.get();

        if (!demande.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas autorise a modifier cette demande.");
        }

        demande.setLocalisation(demandeIntervention.getLocalisation());
        demande.setDescription(demandeIntervention.getDescription());
        demande.setNom(demandeIntervention.getNom());
        demande.setTypeIntervention(demandeIntervention.getTypeIntervention());

        demandeInterventionRepository.save(demande);
    }
*/

    public DemandeIntervention modifierDemande(Long id, DemandeIntervention demandeIntervention) {
        // Récupération en une seule opération avec orElseThrow
        DemandeIntervention demande = demandeInterventionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable avec id: " + id));

        // Vérification des autorisations
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User utilisateur = userRepo.findByEmail(authentication.getName());

        if (!demande.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas autorisé à modifier cette demande.");
        }

        // Validation métier avec exception spécifique
        if (demande.getStatut().equals(StatutDemande.EN_COURS)) {
            throw new IllegalStateException("Impossible de modifier une demande déjà EN_COURS");
        }

        // Mise à jour des champs modifiables
        demande.setLocalisation(demandeIntervention.getLocalisation());
        demande.setDescription(demandeIntervention.getDescription());
        demande.setNom(demandeIntervention.getNom());
        demande.setTypeIntervention(demandeIntervention.getTypeIntervention());

        // Sauvegarde et retour de l'entité mise à jour
        return demandeInterventionRepository.save(demande);
    }
    public List<DemandeIntervention> getDemandeInterventionByPriorite(Priorite etat) throws AccessDeniedException {
        List<DemandeIntervention> demandeInterventions = demandeInterventionRepository.findByPriorite(etat);
        return demandeInterventions;
    }

    public List<DemandeIntervention> getMesDemande() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String username = authentication.getName();
        User utilisateur = userRepo.findByEmail(username);

        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        return demandeInterventionRepository.findByUtilisateur(utilisateur);
    }

    public void refuserDemande(Long idDemande) {
        DemandeIntervention demande = demandeInterventionRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvee"));
        if (demande.getStatut() == StatutDemande.EN_ATTENTE) {
            demande.setStatut(StatutDemande.REJETEE);
            demandeInterventionRepository.save(demande);
        } else {
            throw new RuntimeException("Impossible de refuser : la demande  deja traitee");
        }
    }

    public void annulerDemande(Long id) {
        Optional<DemandeIntervention> demandeInterventionOpt = demandeInterventionRepository.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);

        if (!demandeInterventionOpt.isPresent()) {
            throw new RuntimeException("La demande d'intervention n'existe pas");
        }
        if (user == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        DemandeIntervention demandeIntervention = demandeInterventionOpt.get();

        if (demandeIntervention.getUtilisateur().equals(user)) {
            demandeInterventionRepository.delete(demandeIntervention);
        } else {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette demande");
        }
    }

  /*  public void supprimerDemande(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User adminOrCreator = userRepo.findByEmail(email);
        Optional<DemandeIntervention> demande = demandeInterventionRepository.findById(id);
        if (adminOrCreator == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        DemandeIntervention demandeIntervention = demande.get();
        if(demandeIntervention.getUtilisateur().equals(adminOrCreator) || adminOrCreator.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR)) {
            if(demandeIntervention.getStatut().equals(StatutDemande.EN_ATTENTE)) {
                this.demandeInterventionRepository.delete(demandeIntervention);
            }
            if(demandeIntervention.getStatut().equals(StatutDemande.APPROUVEE)) {
                Intervention intervention = demandeIntervention.getIntervention();
                User userAssigne = intervention.getIngenieur();
                for(Intervention i : userAssigne.getInterventions()) {
                    if(i.equals(intervention)) {
                        interventionRepository.delete(i);
                        demandeInterventionRepository.delete(demandeIntervention);
                    }
                }
                Notification notification = new Notification();
                notification.setDestinataire(userAssigne);
                notification.setDateEnvoi(LocalDateTime.now());
                notification.setType(TypeNotification.INFO);
                notification.setStatut(StatutNotification.NON_LUE);
                notification.setMessage("l'intervention:"+intervention.getDescription()+"dans:"+intervention.getLocalisation()+"est annuler");
            }
        }

    }*/


    public void supprimerDemande(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User adminOrCreator = userRepo.findByEmail(email);

        if (adminOrCreator == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        Optional<DemandeIntervention> optDemande = demandeInterventionRepository.findById(id);
        if (optDemande.isEmpty()) {
            throw new RuntimeException("Demande introuvable");
        }

        DemandeIntervention demandeIntervention = optDemande.get();

        boolean isCreator = demandeIntervention.getUtilisateur().equals(adminOrCreator);
        boolean isAdmin = adminOrCreator.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR);
        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Accès refusé");
        }

        // Cas EN_ATTENTE : suppression simple
        if (demandeIntervention.getStatut().equals(StatutDemande.EN_ATTENTE)) {
            demandeInterventionRepository.delete(demandeIntervention);
            return;
        }

        // Cas APPROUVEE : suppression intervention + demande + notification si destinataire présent
        if (demandeIntervention.getStatut().equals(StatutDemande.APPROUVEE)) {
            Intervention intervention = demandeIntervention.getIntervention();
            User userAssigne = intervention.getIngenieur();

            interventionRepository.delete(intervention);
            demandeInterventionRepository.delete(demandeIntervention);

            if (userAssigne != null) {
                Notification notification = new Notification();
                notification.setDestinataire(userAssigne);
                notification.setDateEnvoi(LocalDateTime.now());
                notification.setType(TypeNotification.INFO);
                notification.setStatut(StatutNotification.NON_LUE);
                notification.setMessage("L'intervention \"" + intervention.getDescription() + "\" dans " + intervention.getLocalisation() + " est annulée");
                notificationRepository.save(notification);
            } else {
                // Optionnel : log pour savoir que l'intervention n'avait pas d'ingénieur assigné
                System.out.println("Pas de destinataire pour l'intervention id=" + intervention.getId());
            }
        }
    }



}
