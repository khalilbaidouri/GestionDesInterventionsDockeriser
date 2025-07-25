package spring.security.avis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AccepterDemandeRequest;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Service.DemandeInterventionService;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

/**
 * @author $ {USERS}
 **/
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DemandeController {
    private final DemandeInterventionService demandeInterventionService;

    public DemandeController(DemandeInterventionService demandeInterventionService) {
        this.demandeInterventionService = demandeInterventionService;
    }

    @PostMapping(path = "/demande")
    public ResponseEntity<Void> creerDemande(@RequestBody DemandeIntervention demandeIntervention) {
        demandeInterventionService.demandeIntervention(demandeIntervention);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "/{idIntervention}/modifier")
    public ResponseEntity<Void> modifierDemande(@PathVariable("idIntervention") Long idDemande,
                                                @RequestBody DemandeIntervention demandeIntervention) {
        demandeInterventionService.modifierDemande(idDemande, demandeIntervention);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{idDemande}/accepter")
    public ResponseEntity<Intervention> accepterDemande(
            @PathVariable Long idDemande,
            @RequestBody AccepterDemandeRequest request) {
        Intervention intervention = demandeInterventionService.accepterDemande(idDemande, request.dateDebut(), request.dateEcheance());
        return ResponseEntity.ok(intervention);
    }

    @GetMapping("/afficherLesInterventionsParEtat")
    public ResponseEntity<List<DemandeIntervention>> afficherLesInterventionsParPriorite(
            @RequestParam Priorite priorite) throws AccessDeniedException {

        List<DemandeIntervention> demandeInterventions = demandeInterventionService.getDemandeInterventionByPriorite(priorite);
        return ResponseEntity.ok(demandeInterventions);
    }

}
