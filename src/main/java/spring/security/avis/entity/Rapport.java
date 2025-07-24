package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.Data;
import spring.security.avis.Enum.StatutRapport;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapport")
@Data
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    private StatutRapport statut;

    // Relations
    @OneToOne
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;


}