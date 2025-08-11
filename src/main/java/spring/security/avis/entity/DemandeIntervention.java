package spring.security.avis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.Priorite;
import spring.security.avis.Enum.StatutDemande;
import spring.security.avis.Enum.TypeIntervention;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_intervention")
@Data
public class DemandeIntervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_intervention")
    private TypeIntervention typeIntervention;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @Column(name = "date_creation")
//    private LocalDateTime dateCreation;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @Column(name = "date_echeance")
//    private LocalDate dateEcheance;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_annoncement")
    private LocalDateTime dateAnnoncement;

    private String localisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    @OneToOne(mappedBy = "demandeIntervention", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Intervention intervention;




}