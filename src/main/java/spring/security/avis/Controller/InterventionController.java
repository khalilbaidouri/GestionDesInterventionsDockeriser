package spring.security.avis.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AssignerInterventionRequest;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
    public ResponseEntity<Intervention> terminer(@PathVariable Long id, @RequestBody String rapport) throws AccessDeniedException {
        return ResponseEntity.ok(interventionService.terminerIntervention(id,rapport));
    }

    @PutMapping("/{id}/echouer")
    public ResponseEntity<Intervention> echouer(@PathVariable Long id,@RequestBody String rapport) throws AccessDeniedException {
        return ResponseEntity.ok(interventionService.echouerIntervention(id,rapport));
    }
    @GetMapping("/afficherLesInterventionsParPriotite")
    public ResponseEntity<List<Intervention>> afficherLesInterventionsParPriorite(
            @RequestParam Priorite priorite) throws AccessDeniedException {

        List<Intervention> interventions = interventionService.getInterventionPriorite(priorite);
        return ResponseEntity.ok(interventions);
    }
    @GetMapping("/afficherLesInterventionsParStatut")
    public ResponseEntity<List<Intervention>> afficherLesInterventionsParPriorite(
            @RequestParam StatutIntervention statut) throws AccessDeniedException {

        List<Intervention> interventions = interventionService.getInterventionByStatut(statut);
        return ResponseEntity.ok(interventions);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Intervention> modifierIntervention(
            @PathVariable Long id,
            @RequestBody Intervention nouvelleIntervention) {

        Intervention interventionModifiee = interventionService.modifierIntervention(id, nouvelleIntervention);
        return ResponseEntity.ok(interventionModifiee);
    }

    @GetMapping("/mesInterventions")
    public ResponseEntity<List<Intervention>> getMesInterventions() {
        List<Intervention> interventions = interventionService.getMesInterventions();
        return ResponseEntity.ok(interventions);
    }

}
