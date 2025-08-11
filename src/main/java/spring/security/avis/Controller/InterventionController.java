package spring.security.avis.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.InterventionDTO;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Repo.InterventionRepository;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/interventions")
public class InterventionController {

    private static final Logger log = LoggerFactory.getLogger(InterventionController.class);
    private final InterventionService interventionService;
    private final InterventionRepository interventionRepository;

    public InterventionController(InterventionService interventionService, InterventionRepository interventionRepository) {
        this.interventionService = interventionService;
        this.interventionRepository = interventionRepository;
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
    public ResponseEntity<InterventionDTO> commencer(@PathVariable Long id) throws AccessDeniedException {
        Intervention intervention = interventionService.commencerIntervention(id);
        return ResponseEntity.ok(new InterventionDTO(intervention));
    }


    @PutMapping("/{id}/terminer")
    public ResponseEntity<InterventionDTO> terminer(@PathVariable Long id, @RequestBody String rapport) throws AccessDeniedException {
        Intervention intervention = interventionService.terminerIntervention(id, rapport);
        return ResponseEntity.ok(new InterventionDTO(intervention));
    }

    @PutMapping("/{id}/echouer")
    public ResponseEntity<InterventionDTO> echouer(@PathVariable Long id,@RequestBody String rapport) throws AccessDeniedException {
        Intervention intervention = interventionService.echouerIntervention(id, rapport);
        return ResponseEntity.ok(new InterventionDTO(intervention));
    }
//    @GetMapping("/afficherLesInterventionsParPriorite") // Correction de "Priotite" à "Priorite"
//    public ResponseEntity<List<InterventionDTO>> afficherLesInterventionsParPriorite(
//            @RequestParam Priorite priorite) throws AccessDeniedException {
//
//        // Log pour débogage
//        log.info("Récupération des interventions avec priorité: {}", priorite);
//
//        try {
//            List<Intervention> interventions = interventionService.getInterventionPriorite(priorite);
//            List<InterventionDTO> interventionDTOS = interventions.stream()
//                    .map(InterventionDTO::new)
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(interventionDTOS);
//        } catch (Exception e) {
//            log.error("Erreur lors de la récupération des interventions", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @GetMapping("/afficherLesInterventionsParStatut")
    public ResponseEntity<List<InterventionDTO>> afficherLesInterventionsParStatut(
            @RequestParam StatutIntervention statut) throws AccessDeniedException {

        List<Intervention> interventions = interventionService.getInterventionByStatut(statut);
        List<InterventionDTO> interventionDTOS = new ArrayList<>();

        for (Intervention intervention : interventions) {
            interventionDTOS.add(new InterventionDTO(intervention));
        }
        return ResponseEntity.ok(interventionDTOS);
    }
    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> modifierIntervention(
            @PathVariable Long id,
            @RequestBody Intervention nouvelleIntervention) {

        Intervention interventionModifiee = interventionService.modifierIntervention(id, nouvelleIntervention);
        return ResponseEntity.ok(new InterventionDTO(interventionModifiee));
    }

    @GetMapping("/mesInterventions")
    public ResponseEntity<List<InterventionDTO>> getMesInterventions() {
        List<Intervention> interventions = interventionService.getMesInterventions();

        List<InterventionDTO> interventionDTOS = new ArrayList<>();

        for (Intervention intervention : interventions) {
            interventionDTOS.add(new InterventionDTO(intervention));
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

    @GetMapping("/stats")
    public Map<String, Long> getInterventionStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", interventionRepository.count());
        stats.put("terminees", interventionRepository.countByStatut(StatutIntervention.TERMINEE));
        stats.put("enCours", interventionRepository.countByStatut(StatutIntervention.EN_COURS));
        stats.put("planifier", interventionRepository.countByStatut(StatutIntervention.PLANIFIEE));
        return stats;
    }

    @GetMapping("/allIntervention")
    public ResponseEntity<List<InterventionDTO>> getAllInterventions() {
        List<InterventionDTO> interventions = interventionService.getAllInterventions();
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/afficherLesInterventionsParPriorite")
    public ResponseEntity<List<InterventionDTO>> getByPriorite(
            @RequestParam Priorite priorite,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        List<Intervention> interventions = interventionService.findByPriorite(priorite, username);

        return ResponseEntity.ok(
                interventions.stream()
                        .map(InterventionDTO::new)
                        .collect(Collectors.toList())
        );

    }

    @GetMapping("/my-by-priority")
    public ResponseEntity<List<InterventionDTO>> getMyInterventionsByPriority(
            @RequestParam Priorite priority,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername(); // L'email est le username dans ce cas

        List<Intervention> interventions = interventionService.findByPriorityAndUserEmail(priority, email);

        return ResponseEntity.ok(interventions.stream()
                .map(InterventionDTO::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/by-priority")
    public ResponseEntity<List<InterventionDTO>> getInterventionsByPriority(
            @RequestParam Priorite priority) {

        List<Intervention> interventions = interventionService.findByPriority(priority);
        return ResponseEntity.ok(interventions.stream()
                .map(InterventionDTO::new)
                .collect(Collectors.toList()));
    }


}


