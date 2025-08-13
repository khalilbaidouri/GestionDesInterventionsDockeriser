package spring.security.avis.Controller;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DemandeController {
    private final DemandeInterventionService demandeInterventionService;
    private final DemandeInterventionRepository demandeInterventionRepository;



    @PostMapping(path = "/demande")
    public ResponseEntity<String> creerDemande(@RequestBody DemandeInterventionDTO dto) {
        demandeInterventionService.creerDemande(dto);
        return ResponseEntity.ok("Demande enregistrée avec succès");
    }



    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerDemande(@PathVariable Long id) {
        try {
            demandeInterventionService.annulerDemande(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            if (message.contains("n'existe pas")) {
                return ResponseEntity.notFound().build(); // 404 si demande non trouvée
            } else if (message.contains("autorise")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 si pas autorisé
            } else if (message.contains("authentifié")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 si non authentifié
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PostMapping(path = "/demande")
//    public ResponseEntity<Void> creerDemande(@RequestBody DemandeIntervention demandeIntervention) {
//        demandeInterventionService.demandeIntervention(demandeIntervention);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }


/*    @PutMapping(path = "/{idIntervention}/modifier")
    public ResponseEntity<Void> modifierDemande(@PathVariable("idIntervention") Long idDemande,
                                                @RequestBody DemandeIntervention demandeIntervention) {
        demandeInterventionService.modifierDemande(idDemande, demandeIntervention);
        return ResponseEntity.ok().build();
    }*/

    @PutMapping(path = "/{idIntervention}/modifier")
    public ResponseEntity<DemandeInterventionDTO> modifierDemande(@PathVariable("idIntervention") Long idDemande,
                                                               @RequestBody DemandeIntervention demandeIntervention) {
        DemandeIntervention updated = demandeInterventionService.modifierDemande(idDemande,demandeIntervention);
        DemandeInterventionDTO dto = new DemandeInterventionDTO(updated);
        return ResponseEntity.ok(dto);
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

    @GetMapping("/mesDemandes")
    public ResponseEntity<List<DemandeIntervention>> getMesDemandes() {
        try {
            List<DemandeIntervention> demandes = demandeInterventionService.getMesDemande();
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<String> supprimerDemande(@PathVariable Long id) {
        try {
            demandeInterventionService.supprimerDemande(id);
            return ResponseEntity.ok("Demande supprimée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
