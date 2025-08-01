package spring.security.avis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.InterventionDTO;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interventions")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }


    @PostMapping("/{idIntervention}/assigner")
    public ResponseEntity<InterventionDTO> assignerIntervention(
            @PathVariable Long idIntervention,
            @RequestParam(required = false) Long idIngenieur) {
        try {
            Intervention intervention = interventionService.assignerIntervention(idIntervention, idIngenieur);
            InterventionDTO  interventionDTO= new InterventionDTO(intervention);
            return ResponseEntity.ok(interventionDTO);
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
    public ResponseEntity<List<InterventionDTO>> afficherLesInterventionsParPriorite(
            @RequestParam StatutIntervention statut) throws AccessDeniedException {

        List<Intervention> interventions = interventionService.getInterventionByStatut(statut);
        List<InterventionDTO> interventionDTOS = new ArrayList<>();

        for (Intervention intervention : interventions) {
            InterventionDTO dto = new InterventionDTO(
                    intervention.getId(),
                    intervention.getDescription(),
                    intervention.getDateDebut(),
                    intervention.getDateFin(),
                    intervention.getDateFinalReelle(),
                    intervention.getDateDebutReelle(),
                    //intervention.getIngenieur(),
                    intervention.getStatut(),
                    intervention.getPriorite(),
                    intervention.getObservation(),
                    intervention.getDureeReelle(),
                    //intervention.getDemandeIntervention(),
                    intervention.getLocalisation()
            );
            interventionDTOS.add(dto);
        }
        return ResponseEntity.ok(interventionDTOS);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Intervention> modifierIntervention(
            @PathVariable Long id,
            @RequestBody Intervention nouvelleIntervention) {

        Intervention interventionModifiee = interventionService.modifierIntervention(id, nouvelleIntervention);
        return ResponseEntity.ok(interventionModifiee);
    }

    @GetMapping("/mesInterventions")
    public ResponseEntity<List<InterventionDTO>> getMesInterventions() {
        List<Intervention> interventions = interventionService.getMesInterventions();

        List<InterventionDTO> interventionDTOS = new ArrayList<>();

        for (Intervention intervention : interventions) {
            InterventionDTO dto = new InterventionDTO(
                    intervention.getId(),
                    intervention.getDescription(),
                    intervention.getDateDebut(),
                    intervention.getDateFin(),
                    intervention.getDateFinalReelle(),
                    intervention.getDateDebutReelle(),
                    //intervention.getIngenieur(),
                    intervention.getStatut(),
                    intervention.getPriorite(),
                    intervention.getObservation(),
                    intervention.getDureeReelle(),
                    //intervention.getDemandeIntervention(),
                    intervention.getLocalisation()
            );
            interventionDTOS.add(dto);
        }

        return ResponseEntity.ok(interventionDTOS);
    }

    @PutMapping("/interventions/{id}/refaire")
    public ResponseEntity<String> tacheARefaire(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String message = body.get("message");
        String dateDebutStr = body.get("dateDebut");
        String dateFinStr = body.get("dateFin");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body("Le message est obligatoire.");
        }
        if (dateDebutStr == null || dateFinStr == null) {
            return ResponseEntity.badRequest().body("Les dates de début et fin sont obligatoires.");
        }

        LocalDateTime dateDebut;
        LocalDateTime dateFin;
        try {
            dateDebut = LocalDateTime.parse(dateDebutStr);
            dateFin = LocalDateTime.parse(dateFinStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format de date invalide. Utilisez le format ISO-8601 (ex: 2025-08-05T09:00:00).");
        }

        if (!dateDebut.isBefore(dateFin)) {
            return ResponseEntity.badRequest().body("La date de début doit être avant la date de fin.");
        }

        try {
            interventionService.tacheARefaire(id, message, dateDebut, dateFin);
            return ResponseEntity.ok("Intervention mise à refaire et planifiée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne.");
        }
    }

    @PutMapping("/interventions/{id}/valider")
    public ResponseEntity<?> validerTache(@PathVariable Long id) {
        try {
            interventionService.validerTache(id);
            return ResponseEntity.ok("Intervention validee avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}
