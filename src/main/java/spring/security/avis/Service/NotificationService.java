package spring.security.avis.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import spring.security.avis.Enum.StatutNotification;
import spring.security.avis.Enum.TypeNotification;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.InterventionRepository;
import spring.security.avis.Repo.NotificationRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.Notification;
import spring.security.avis.entity.User;
import spring.security.avis.entity.Validation;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final UserRepo userRepo;
    private final InterventionRepository interventionRepository;
    private final NotificationRepository notificationRepository;


    public NotificationService(JavaMailSender mailSender, UserRepo userRepo, InterventionRepository interventionRepository, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.userRepo = userRepo;
        this.interventionRepository = interventionRepository;
        this.notificationRepository = notificationRepository;
    }

    public void envoyer(Validation validation){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("khalilbaidouri2020@gmail.com");
        message.setTo(validation.getUser().getUsername());
        message.setSubject("votre code de validation");
        String texte=String.format("Bonjours %s , \nvotre code d'activation est : %s . \nA bientôt.", validation.getUser().getUsername(), validation.getCode());
        message.setText(texte);
        mailSender.send(message);
    }


    public void envoyerMessageRappel(long idIntervention, String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User admin = userRepo.findByEmail(username);
        if (admin == null || admin.getRole().getLibelle() != TypeRole.ADMINISTRATEUR) {
            throw new RuntimeException("Vous etes pas autorise.");
        }

        Intervention intervention = interventionRepository.findById(idIntervention)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvee."));

        User ingenieur = intervention.getIngenieur();
        if (ingenieur == null) {
            throw new RuntimeException("Aucun ingenieur assigne a cette intervention.");
        }

        Notification notification = new Notification();
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setType(TypeNotification.RAPPEL);
        notification.setStatut(StatutNotification.NON_LUE);
        notification.setMessage(message);
        notification.setDestinataire(ingenieur);

        notificationRepository.save(notification);
    }

}
