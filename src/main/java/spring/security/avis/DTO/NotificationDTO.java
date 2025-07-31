package spring.security.avis.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.StatutNotification;
import spring.security.avis.Enum.TypeNotification;
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
}
