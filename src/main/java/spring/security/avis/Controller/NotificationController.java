package spring.security.avis.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.RappelRequest;
import spring.security.avis.Service.NotificationService;

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
}
