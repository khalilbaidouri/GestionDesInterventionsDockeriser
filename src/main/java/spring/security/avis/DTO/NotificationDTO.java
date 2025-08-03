package spring.security.avis.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.StatutNotification;
import spring.security.avis.Enum.TypeNotification;
import spring.security.avis.entity.Notification;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Data
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    private TypeNotification type;
    private StatutNotification statut;

    public NotificationDTO (Notification notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.dateEnvoi = notification.getDateEnvoi();
        this.dateLecture = notification.getDateLecture();
        this.type = notification.getType();
        this.statut = notification.getStatut();
    }
}
