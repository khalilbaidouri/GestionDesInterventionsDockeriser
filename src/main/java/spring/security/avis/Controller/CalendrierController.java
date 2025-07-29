package spring.security.avis.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.CalendrierUpdateRequest;
import spring.security.avis.Service.CalendrierService;
import spring.security.avis.entity.Calendrier;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import spring.security.avis.entity.Intervention;

import java.util.List;


/**
 * @author $ {USERS}
 **/
@RestController
@RequestMapping("/calendrier")
public class CalendrierController {

    private final CalendrierService calendrierService;

    public CalendrierController(CalendrierService calendrierService) {
        this.calendrierService = calendrierService;
    }

    @GetMapping
    public ResponseEntity<Calendrier> getCalendrier() {
        return ResponseEntity.ok(calendrierService.getCalendrierUnique());
    }

    @PutMapping
    public ResponseEntity<Calendrier> updateCalendrier(@RequestBody CalendrierUpdateRequest request) {
        Calendrier calendrier = calendrierService.updateCalendrier(
                request.nom(),
                request.description(),
                request.statut()
        );
        return ResponseEntity.ok(calendrier);
    }

    @GetMapping("/interventions")
    public ResponseEntity<List<Intervention>> getInterventionsCalendrier() {
        Calendrier calendrier = calendrierService.getCalendrierUnique();
        return ResponseEntity.ok(calendrier.getInterventions());
    }
}

