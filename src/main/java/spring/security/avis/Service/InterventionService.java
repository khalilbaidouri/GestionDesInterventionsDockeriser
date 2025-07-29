package spring.security.avis.Service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import spring.security.avis.Enum.*;
import spring.security.avis.Repo.*;
import spring.security.avis.entity.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static spring.security.avis.Service.UserService.calculerDureeEnMinutes;

/**
 * @author $ {USERS}
 **/
@Service
@Transactional
public class InterventionService {
    private final InterventionRepository interventionRepository;
    private final HistoriqueInterventionRepository historiqueInterventionRepository;
    private final UserRepo userRepository;
    private final NotificationRepository notificationRepository;
    private final DemandeInterventionRepository demandeInterventionRepository;
    private final UserService userService;

    public InterventionService(InterventionRepository interventionRepository, HistoriqueInterventionRepository historiqueInterventionRepository, UserRepo userRepository, NotificationRepository notificationRepository, DemandeInterventionRepository demandeInterventionRepository, UserService userService) {
        this.interventionRepository = interventionRepository;
        this.historiqueInterventionRepository = historiqueInterventionRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.demandeInterventionRepository = demandeInterventionRepository;
        this.userService = userService;
    }


    public Intervention assignerIntervention(Long idIntervention, Long idIngenieur) {
        if (idIntervention == null) {
            throw new IllegalArgumentException("ID d'intervention ne doit pas être nul.");
        }

        Intervention interventionAssigner = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

        User ingenieur = (idIngenieur != null)
                ? userRepository.findById(idIngenieur)
                .orElseThrow(() -> new RuntimeException("Ingénieur non trouve"))
                : userService.ingenieurDispo(interventionAssigner.getDateDebut(), interventionAssigner.getDateFin())
                .orElseThrow(() -> new RuntimeException("Aucun ingénieur disponible pour cette periode"));

        System.out.println("Intervention à assigner : " + interventionAssigner.getDateDebut() + " -> " + interventionAssigner.getDateFin());

        for (Intervention i : ingenieur.getInterventions()) {
            System.out.println("Comparaison avec intervention existante : " + i.getId() + " | " + i.getDateDebut() + " -> " + i.getDateFin());

            boolean chevauchement =
                    !(interventionAssigner.getDateFin().isBefore(i.getDateDebut()) ||
                            interventionAssigner.getDateDebut().isAfter(i.getDateFin()));

            if (chevauchement) {
                throw new RuntimeException("L'ingenieur selectionne a deja une intervention (ID " + i.getId() + ") qui chevauche cette periode.");
            }
        }

        interventionAssigner.setStatut(StatutIntervention.EN_COURS);
        interventionAssigner.setIngenieur(ingenieur);

        envoyerNotificationNouvelleIntervention(interventionAssigner, ingenieur);

        return interventionRepository.save(interventionAssigner);
    }


    private void envoyerNotificationNouvelleIntervention(Intervention intervention, User ingenieur) {
        String etatIntervention = (String)intervention.getPriorite().name();
        Notification notification = new Notification();
        notification.setStatut(StatutNotification.NON_LUE);
        notification.setDestinataire(ingenieur);
        if(etatIntervention.equals(Priorite.HAUTE)){
            notification.setType(TypeNotification.URGENCE);
        }
        else {
            notification.setType(TypeNotification.ALERTE);
        }
        notification.setMessage("Tu as une nouvelle mission assignee : Intervention #" + intervention.getId() + " " + intervention.getDescription());
        notification.setDateEnvoi(LocalDateTime.now());
        notificationRepository.save(notification);
    }



    public Intervention commencerIntervention(Long idIntervention) throws AccessDeniedException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User utilisateur = userRepository.findByEmail(username);

            Intervention intervention = interventionRepository.findById(idIntervention)
                    .orElseThrow(() -> new RuntimeException("Intervention non trouvee"));

            if (intervention.getIngenieur() == null || !intervention.getIngenieur().getId().equals(utilisateur.getId())) {
                throw new AccessDeniedException("Vous n'etes pas autorise à commencer cette intervention");
            }

            intervention.setDateDebutReelle(LocalDateTime.now());
            intervention.setStatut(StatutIntervention.EN_COURS);
            return interventionRepository.save(intervention);
        }

    public Intervention terminerIntervention(Long idIntervention , String rapport) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User utilisateur = userRepository.findByEmail(username);

        Intervention intervention = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvee"));

        if (intervention.getIngenieur() == null || !intervention.getIngenieur().getId().equals(utilisateur.getId())) {
            throw new AccessDeniedException("Vous n'etes pas autorise à commencer cette intervention");
        }


        intervention.setDateFinalReelle(LocalDateTime.now());
        intervention.setStatut(StatutIntervention.TERMINEE);
        intervention.getEvenement().setDateFin(LocalDateTime.now());
        intervention.getEvenement().setType(TypeEvenement.MAINTENANCE);
        long duree = calculerDureeEnMinutes(intervention.getDateDebutReelle(),intervention.getDateFinalReelle());
        intervention.setDureeReelle(duree);
        intervention.getRapport().setContenu(rapport);
        intervention.getRapport().setDateCreation(LocalDateTime.now());
        HistoriqueIntervention historiqueIntervention = new HistoriqueIntervention();
        historiqueIntervention.setDureeAction(intervention.getDureeReelle());
        historiqueIntervention.setIntervention(intervention);
        historiqueIntervention.setDateDebut(intervention.getDateDebutReelle());
        historiqueIntervention.setDateFin(intervention.getDateFinalReelle());
        historiqueIntervention.setUtilisateur(intervention.getIngenieur());
        historiqueIntervention.setLocalisation(intervention.getLocalisation());
        historiqueIntervention.setStatut(StatutIntervention.TERMINEE);
        historiqueInterventionRepository.save(historiqueIntervention);
        return interventionRepository.save(intervention);
    }

    public Intervention echouerIntervention(Long idIntervention, String rapport) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User utilisateur = userRepository.findByEmail(username);

        Intervention intervention = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvee"));

        if (intervention.getIngenieur() == null || !intervention.getIngenieur().getId().equals(utilisateur.getId())) {
            throw new AccessDeniedException("Vous n'etes pas autorise à commencer cette intervention");
        }

        intervention.setStatut(StatutIntervention.ECHEC);
        long duree = calculerDureeEnMinutes(intervention.getDateDebutReelle(),intervention.getDateFinalReelle());
        intervention.setDureeReelle(duree);
        intervention.getRapport().setContenu(rapport);
        intervention.getRapport().setDateCreation(LocalDateTime.now());
        intervention.getEvenement().setDateFin(LocalDateTime.now());
        intervention.getEvenement().setType(TypeEvenement.MAINTENANCE);

        HistoriqueIntervention historiqueIntervention = new HistoriqueIntervention();
        historiqueIntervention.setDureeAction(intervention.getDureeReelle());
        historiqueIntervention.setIntervention(intervention);
        historiqueIntervention.setDateDebut(intervention.getDateDebutReelle());
        historiqueIntervention.setDateFin(intervention.getDateFinalReelle());
        historiqueIntervention.setUtilisateur(intervention.getIngenieur());
        historiqueIntervention.setLocalisation(intervention.getLocalisation());
        historiqueIntervention.setStatut(StatutIntervention.ECHEC);

        historiqueInterventionRepository.save(historiqueIntervention);
        return interventionRepository.save(intervention);
    }

public List<Intervention> getInterventionPriorite(Priorite etat) throws AccessDeniedException {
    List<Intervention> interventions = interventionRepository.findByPriorite(etat);
    return interventions;

}
public List<Intervention> getInterventionByStatut(StatutIntervention statut) throws AccessDeniedException {
    List<Intervention> interventions = interventionRepository.findByStatut(statut);
    return interventions;

}



    public Intervention modifierIntervention(Long idIntervention, Intervention nouvelleIntervention) {
        Intervention interventionExistante = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

        if (interventionExistante.getStatut() != StatutIntervention.ACCEPTE) {
            throw new IllegalStateException("Seules les interventions avec le statut ACCEPTEE peuvent être modifiées.");
        }

        interventionExistante.setDateDebut(nouvelleIntervention.getDateDebut());
        interventionExistante.setDateFin(nouvelleIntervention.getDateFin());
        interventionExistante.setIngenieur(nouvelleIntervention.getIngenieur());
        interventionExistante.setPriorite(nouvelleIntervention.getPriorite());
        interventionExistante.setDescription(nouvelleIntervention.getDescription());

        return interventionRepository.save(interventionExistante);
    }

    public List<Intervention> getMesInterventions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User utilisateur = userRepository.findByEmail(username);
        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur non trouve");
        }

        List<Intervention> interventions = utilisateur.getInterventions();

        if (interventions == null) {
            return new ArrayList<>();
        }

        return interventions;
    }


}
