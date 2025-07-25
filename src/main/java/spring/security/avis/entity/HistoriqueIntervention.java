package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.StatutIntervention;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_intervention")
@Data
public class HistoriqueIntervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TypeAction action;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;


//    @Enumerated(EnumType.STRING)
//    @Column(name = "ancien_statut")
//    private StatutIntervention ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutIntervention statut;


    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "duree_action")
    private Long dureeAction;

    @Column(name = "localisation")
    private String localisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;


}
