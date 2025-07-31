package spring.security.avis.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.NotificationDTO;
import spring.security.avis.DTO.RappelRequest;
import spring.security.avis.Service.NotificationService;
import spring.security.avis.entity.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $ {USERS}
 **/
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000") // adapte si nécessaire
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/{idIntervention}/rappel")
    public ResponseEntity<?> envoyerRappel( @PathVariable Long idIntervention,@RequestBody RappelRequest request) {
        try {
            notificationService.envoyerMessageRappel(idIntervention, request.message());
            return ResponseEntity.ok("Notification envoyee avec succes.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/mesNotification")
    public ResponseEntity<List<NotificationDTO>> getMesNotif() {
        List<Notification> notifications = notificationService.getMesNotif();
        List<NotificationDTO> dtos = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getDateEnvoi(),
                    notification.getDateLecture(),
                    notification.getType(),
                    notification.getStatut()
            );
            dtos.add(notificationDTO);
        }
        return ResponseEntity.ok(dtos);
    }

}
