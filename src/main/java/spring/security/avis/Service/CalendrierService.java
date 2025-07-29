package spring.security.avis.Service;

import org.springframework.stereotype.Service;
import spring.security.avis.Enum.StatutCalendrier;
import spring.security.avis.Repo.CalendrierRepository;
import spring.security.avis.entity.Calendrier;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
public class CalendrierService {

    private final CalendrierRepository calendrierRepository;

    public CalendrierService(CalendrierRepository calendrierRepository) {
        this.calendrierRepository = calendrierRepository;
    }

    /**
     * Récupère ou crée le calendrier unique de l'application
     */
    public Calendrier getCalendrierUnique() {
        Optional<Calendrier> calendrier = calendrierRepository.findFirstByOrderByIdAsc();

        if (calendrier.isPresent()) {
            return calendrier.get();
        } else {
            // Créer le calendrier unique s'il n'existe pas
            Calendrier nouveauCalendrier = new Calendrier();
            nouveauCalendrier.setNom("Calendrier Principal");
            nouveauCalendrier.setDescription("Calendrier unique de l'application");
            nouveauCalendrier.setStatut(StatutCalendrier.ACTIF);
            nouveauCalendrier.setDateCreation(LocalDateTime.now());

            return calendrierRepository.save(nouveauCalendrier);
        }
    }

    /**
     * Met à jour le calendrier unique
     */
    public Calendrier updateCalendrier(String nom, String description, StatutCalendrier statut) {
        Calendrier calendrier = getCalendrierUnique();
        calendrier.setNom(nom);
        calendrier.setDescription(description);
        calendrier.setStatut(statut);

        return calendrierRepository.save(calendrier);
    }
}
