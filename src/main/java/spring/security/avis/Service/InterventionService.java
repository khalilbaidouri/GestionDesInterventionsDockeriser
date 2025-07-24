package spring.security.avis.Service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import spring.security.avis.Enum.*;
import spring.security.avis.Repo.DemandeInterventionRepository;
import spring.security.avis.Repo.InterventionRepository;
import spring.security.avis.Repo.NotificationRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
@Transactional
public class InterventionService {
    private final InterventionRepository interventionRepository;
    private final UserRepo userRepository;
    private final NotificationRepository notificationRepository;
    private final DemandeInterventionRepository demandeInterventionRepository;
    private final UserService userService;

    public InterventionService(InterventionRepository interventionRepository, UserRepo userRepository, NotificationRepository notificationRepository, DemandeInterventionRepository demandeInterventionRepository, UserService userService) {
        this.interventionRepository = interventionRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.demandeInterventionRepository = demandeInterventionRepository;
        this.userService = userService;
    }
/*

    public Intervention assignerIntervention(Long idIntervention, Long idIngenieur) {
        if (idIntervention == null) {
            throw new IllegalArgumentException("ID d'intervention ne doit pas être nul.");
        }

        Intervention interventionAssigner = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

        User ingenieur = (idIngenieur != null)
                ? userRepository.findById(idIngenieur)
                .orElseThrow(() -> new RuntimeException("Ingénieur non trouvé"))
                : userService.ingenieurDispo(interventionAssigner.getDateDebut(), interventionAssigner.getDateFin())
                .orElseThrow(() -> new RuntimeException("Aucun ingénieur disponible pour cette période"));

        for (Intervention i : ingenieur.getInterventions()) {
            boolean chevauchement =
                    !(interventionAssigner.getDateFin().isBefore(i.getDateDebut()) ||
                            interventionAssigner.getDateDebut().isAfter(i.getDateFin()));
            if (chevauchement) {
                throw new RuntimeException("L'ingénieur sélectionné n'est pas disponible pour cette période.");
            }
        }

        interventionAssigner.setStatut(StatutIntervention.EN_COURS);
        interventionAssigner.setIngenieur(ingenieur);

        envoyerNotificationNouvelleIntervention(interventionAssigner, ingenieur);

        return interventionRepository.save(interventionAssigner);
    }*/

public Intervention assignerIntervention(Long idIntervention, Long idIngenieur) {
    if (idIntervention == null) {
        throw new IllegalArgumentException("ID d'intervention ne doit pas être nul.");
    }

    Intervention interventionAssigner = interventionRepository.findById(idIntervention)
            .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

    // Trouver l'ingénieur manuellement ou automatiquement
    User ingenieur = (idIngenieur != null)
            ? userRepository.findById(idIngenieur)
            .orElseThrow(() -> new RuntimeException("Ingénieur non trouvé"))
            : userService.ingenieurDispo(interventionAssigner.getDateDebut(), interventionAssigner.getDateFin())
            .orElseThrow(() -> new RuntimeException("Aucun ingénieur disponible pour cette période"));

    // DEBUG : afficher les dates de l'intervention à assigner
    System.out.println("Intervention à assigner : " + interventionAssigner.getDateDebut() + " -> " + interventionAssigner.getDateFin());

    // Vérifier disponibilité de l'ingénieur
    for (Intervention i : ingenieur.getInterventions()) {
        System.out.println("Comparaison avec intervention existante : " + i.getId() + " | " + i.getDateDebut() + " -> " + i.getDateFin());

        boolean chevauchement =
                !(interventionAssigner.getDateFin().isBefore(i.getDateDebut()) ||
                        interventionAssigner.getDateDebut().isAfter(i.getDateFin()));

        if (chevauchement) {
            throw new RuntimeException("L'ingénieur sélectionné a déjà une intervention (ID " + i.getId() + ") qui chevauche cette période.");
        }
    }

    // Mise à jour de l'intervention
    interventionAssigner.setStatut(StatutIntervention.EN_COURS);
    interventionAssigner.setIngenieur(ingenieur);

    // Notification
    envoyerNotificationNouvelleIntervention(interventionAssigner, ingenieur);

    // Sauvegarde
    return interventionRepository.save(interventionAssigner);
}


    private void envoyerNotificationNouvelleIntervention(Intervention intervention, User ingenieur) {
        Notification notification = new Notification();
        notification.setStatut(StatutNotification.NON_LUE);
        notification.setDestinataire(ingenieur);
        notification.setType(TypeNotification.URGENCE);
        notification.setMessage("Tu as une nouvelle mission assignée : Intervention #" + intervention.getId() + " " + intervention.getDescription());
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

            intervention.setDateDebut(LocalDateTime.now());
            intervention.setStatut(StatutIntervention.EN_COURS);
            return interventionRepository.save(intervention);
        }

        public Intervention terminerIntervention(Long idIntervention) throws AccessDeniedException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User utilisateur = userRepository.findByEmail(username);

            Intervention intervention = interventionRepository.findById(idIntervention)
                    .orElseThrow(() -> new RuntimeException("Intervention non trouvee"));

            if (intervention.getIngenieur() == null || !intervention.getIngenieur().getId().equals(utilisateur.getId())) {
                throw new AccessDeniedException("Vous n'etes pas autorise à commencer cette intervention");
            }


            intervention.setDateFin(LocalDateTime.now());
            intervention.setStatut(StatutIntervention.TERMINEE);
            return interventionRepository.save(intervention);
        }

        public Intervention echouerIntervention(Long idIntervention) throws AccessDeniedException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User utilisateur = userRepository.findByEmail(username);

            Intervention intervention = interventionRepository.findById(idIntervention)
                    .orElseThrow(() -> new RuntimeException("Intervention non trouvee"));

            if (intervention.getIngenieur() == null || !intervention.getIngenieur().getId().equals(utilisateur.getId())) {
                throw new AccessDeniedException("Vous n'etes pas autorise à commencer cette intervention");
            }

            intervention.setStatut(StatutIntervention.ECHEC);
            return interventionRepository.save(intervention);
        }



}
