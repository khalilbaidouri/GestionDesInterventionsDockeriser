package spring.security.avis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AccepterDemandeRequest;
import spring.security.avis.DTO.DemandeInterventionDTO;
import spring.security.avis.DTO.InterventionDTO;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Service.DemandeInterventionService;
import spring.security.avis.entity.DemandeIntervention;
import spring.security.avis.entity.Intervention;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<InterventionDTO> accepterDemande(
            @PathVariable Long idDemande,
            @RequestBody AccepterDemandeRequest request) {
        Intervention intervention = demandeInterventionService.accepterDemande(idDemande, request.dateDebut(), request.dateEcheance());

            InterventionDTO interventionDTO = new InterventionDTO(
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
        return ResponseEntity.ok(interventionDTO);
    }

    @PostMapping("/{idDemande}/refuser")
    public ResponseEntity<String> refuserDemande(@PathVariable Long idDemande) {
        demandeInterventionService.refuserDemande(idDemande);
        return ResponseEntity.ok(" Demande refuse");
    }

    @GetMapping("/afficherLesInterventionsParEtat")
    public ResponseEntity<List<DemandeInterventionDTO>> afficherLesInterventionsParPriorite(
            @RequestParam Priorite priorite) throws AccessDeniedException {

        List<DemandeIntervention> demandeInterventions = demandeInterventionService.getDemandeInterventionByPriorite(priorite);

        List<DemandeInterventionDTO> listDemande = new ArrayList<>();

        for (DemandeIntervention demande : demandeInterventions) {
            DemandeInterventionDTO dto = new DemandeInterventionDTO(
                    demande.getId(),
                    demande.getNom(),
                    demande.getDescription(),
                    demande.getPriorite(),
                    demande.getStatut(),
                    demande.getTypeIntervention(),
                    demande.getDateAnnoncement(),
                    demande.getLocalisation()
            );
            listDemande.add(dto);
        }

        return ResponseEntity.ok(listDemande);
    }


}
