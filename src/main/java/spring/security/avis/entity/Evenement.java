package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.TypeEvenement;

import java.time.LocalDateTime;

@Entity
@Table(name = "evenement")
@Data
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private TypeEvenement type;

    private String lieu;

    @OneToOne
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    @ManyToOne
    @JoinColumn(name = "calendrier_id")
    private Calendrier calendrier;


}