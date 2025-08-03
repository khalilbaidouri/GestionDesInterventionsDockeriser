package spring.security.avis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AccepterDemandeRequest;
import spring.security.avis.DTO.DemandeInterventionDTO;
import spring.security.avis.DTO.InterventionDTO;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Repo.DemandeInterventionRepository;
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
    private final DemandeInterventionRepository demandeInterventionRepository;

    public DemandeController(DemandeInterventionService demandeInterventionService, DemandeInterventionRepository demandeInterventionRepository) {
        this.demandeInterventionService = demandeInterventionService;
        this.demandeInterventionRepository = demandeInterventionRepository;
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

            InterventionDTO interventionDTO = new InterventionDTO(intervention);
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
            DemandeInterventionDTO dto = new DemandeInterventionDTO(demande);
            listDemande.add(dto);
        }

        return ResponseEntity.ok(listDemande);
    }
    @GetMapping("/getAllDemande")
    public ResponseEntity<List<DemandeInterventionDTO>> getAllDemande(){
        List<DemandeIntervention> alldemande = demandeInterventionRepository.findAll();
        List<DemandeInterventionDTO> dtos = new ArrayList<>();
        for (DemandeIntervention demande : alldemande) {
            DemandeInterventionDTO dto = new DemandeInterventionDTO(demande);
            dtos.add(dto);
        }
        return ResponseEntity.ok(dtos);
    }

}
