package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.entity.*;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Data
@AllArgsConstructor
public class InterventionDTO {

        private Long id;
        private String Description;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private LocalDateTime dateFinalReelle;
        private LocalDateTime DateDebutReelle;
        private StatutIntervention statut;
        private Priorite priorite;
        private String observation;
        private Long dureeReelle;
        private String localisation;



}
