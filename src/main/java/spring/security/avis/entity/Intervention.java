package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.StatutIntervention;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "intervention")
@Data
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Description;
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "date_fin_reelle")
    private LocalDateTime dateFinalReelle;

    @Column(name = "date_debut_reelle")
    private LocalDateTime DateDebutReelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private User ingenieur;

    @Enumerated(EnumType.STRING)
    private StatutIntervention statut;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(name = "duree_reelle")
    private Integer dureeReelle;


    @OneToOne
    @JoinColumn(name = "demande_intervention_id")
    private DemandeIntervention demandeIntervention;

    private String localisation;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rapport rapport;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Evenement evenement;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueIntervention> historiques;


}