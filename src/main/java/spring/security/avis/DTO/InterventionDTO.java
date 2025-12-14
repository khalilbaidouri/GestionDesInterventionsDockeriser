package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Enum.TypeIntervention;
import spring.security.avis.entity.*;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        private TypeIntervention typeIntervention;
        private UserResponseDTO ingenieur;

        public InterventionDTO(Intervention intervention) {
                this.id = intervention.getId();
                this.Description = intervention.getDescription();
                this.dateDebut = intervention.getDateDebut();
                this.dateFin = intervention.getDateFin();
                this.dateFinalReelle = intervention.getDateFinalReelle();
                this.DateDebutReelle = intervention.getDateDebutReelle();
                this.statut = intervention.getStatut();
                this.priorite = intervention.getPriorite();
                this.observation = intervention.getObservation();
                this.dureeReelle = intervention.getDureeReelle();
                this.localisation = intervention.getLocalisation();
                this.typeIntervention = intervention.getTypeIntervention();
                if (intervention.getIngenieur() != null) {
                        this.ingenieur = new UserResponseDTO(intervention.getIngenieur());
                }

        }




}
