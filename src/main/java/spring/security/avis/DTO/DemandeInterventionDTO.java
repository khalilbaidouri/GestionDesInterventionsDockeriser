package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.Enum.TypeIntervention;
import spring.security.avis.entity.DemandeIntervention;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandeInterventionDTO {
    private Long id;
    private String nom;
    private String description;
    private Priorite priorite;
    private StatutDemande statut;
    private TypeIntervention typeIntervention;
    private LocalDateTime dateAnnoncement;
    private String localisation;
    private UserResponseDTO utilisateur;

    public DemandeInterventionDTO(DemandeIntervention demande) {
        this.id =   demande.getId();
        this.nom =     demande.getNom();
        this.description =  demande.getDescription();
        this.priorite =   demande.getPriorite();
        this.statut =   demande.getStatut();
        this.typeIntervention =   demande.getTypeIntervention();
        this.dateAnnoncement =   demande.getDateAnnoncement();
        this.localisation =   demande.getLocalisation();
        if (demande.getUtilisateur() != null) {
            this.utilisateur = new UserResponseDTO(demande.getUtilisateur());
        }
    }


}
