package spring.security.avis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AccepterDemandeRequest;
import spring.security.avis.Service.DemandeInterventionService;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.Intervention;

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
        return ResponseEntity.ok().build(); // HTTP 200 OK sans corps
    }


    @PostMapping("/{idDemande}/accepter")
    public ResponseEntity<Intervention> accepterDemande(
            @PathVariable Long idDemande,
            @RequestBody AccepterDemandeRequest request) {
        Intervention intervention = demandeInterventionService.accepterDemande(idDemande, request.dateDebut(), request.dateEcheance());
        return ResponseEntity.ok(intervention);
    }
}
