package spring.security.avis.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AssignerInterventionRequest;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/interventions")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }


    @PostMapping("/{idIntervention}/assigner")
    public ResponseEntity<Intervention> assignerIntervention(
            @PathVariable Long idIntervention,
            @RequestParam(required = false) Long idIngenieur) {
        try {
            Intervention intervention = interventionService.assignerIntervention(idIntervention, idIngenieur);
            return ResponseEntity.ok(intervention);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/{id}/commencer")
    public ResponseEntity<Intervention> commencer(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(interventionService.commencerIntervention(id));
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<Intervention> terminer(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(interventionService.terminerIntervention(id));
    }

    @PutMapping("/{id}/echouer")
    public ResponseEntity<Intervention> echouer(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(interventionService.echouerIntervention(id));
    }

}
