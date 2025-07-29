package spring.security.avis.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Data
@AllArgsConstructor
public class DemandeInterventionDTO {
    private Long id;
    private String nom;
    private String description;
    private Priorite priorite;
    private StatutDemande statut;
    private String typeIntervention;
    private LocalDateTime dateAnnoncement;
    private String localisation;


}
