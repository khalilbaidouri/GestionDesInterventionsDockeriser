package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.StatutCalendrier;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "calendrier")
@Data
public class Calendrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatutCalendrier statut;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;


    @OneToMany(mappedBy = "calendrier")
    private List<Intervention> interventions;

    @OneToMany(mappedBy = "calendrier", cascade = CascadeType.ALL)
    private List<Evenement> evenements;

}
