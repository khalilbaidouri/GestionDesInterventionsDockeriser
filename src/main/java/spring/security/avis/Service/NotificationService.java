package spring.security.avis.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import spring.security.avis.entity.Validation;

/**
 * @author $ {USERS}
 **/
@Service
public class NotificationService {
    final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

}
