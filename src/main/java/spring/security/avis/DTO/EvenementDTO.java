package spring.security.avis.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import spring.security.avis.Enum.TypeEvenement;
import spring.security.avis.entity.Calendrier;
import spring.security.avis.entity.Evenement;
import spring.security.avis.entity.Intervention;

import java.time.LocalDateTime;

/**
 * @author $ {USERS}
 **/
@Data
@AllArgsConstructor
public class EvenementDTO {

    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private TypeEvenement type;
    private String lieu;

    public EvenementDTO (Evenement evenement){
        this.id= evenement.getId();
        this.titre = evenement.getTitre();
        this.description = evenement.getDescription();
        this.dateDebut = evenement.getDateDebut();
        this.dateFin = evenement.getDateFin();
        this.type = evenement.getType();
        this.lieu = evenement.getLieu();
    }

}
