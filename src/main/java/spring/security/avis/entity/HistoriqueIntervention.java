package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Enum.TypeAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_intervention")
@Data
public class HistoriqueIntervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction action;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;


    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut")
    private StatutIntervention ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut")
    private StatutIntervention nouveauStatut;


    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "duree_action")
    private Integer dureeAction; // en minutes

    @Column(name = "ip_address")
    private String ipAddress;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;


}
