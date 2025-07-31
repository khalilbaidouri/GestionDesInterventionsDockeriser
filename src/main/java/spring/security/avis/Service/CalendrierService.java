package spring.security.avis.Service;

import org.springframework.stereotype.Service;
import spring.security.avis.Enum.StatutCalendrier;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.CalendrierRepository;
import spring.security.avis.Repo.EvenementRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.Calendrier;
import spring.security.avis.entity.Evenement;
import spring.security.avis.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
public class CalendrierService {

    private final CalendrierRepository calendrierRepository;
    private final UserRepo userRepo;
    private final EvenementRepository evenementRepo;

    public CalendrierService(CalendrierRepository calendrierRepository, UserRepo userRepo, EvenementRepository evenementRepo) {
        this.calendrierRepository = calendrierRepository;
        this.userRepo = userRepo;
        this.evenementRepo = evenementRepo;
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



    public List<Evenement> getEvenements(User user) {
        if (user.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR)) {
            return evenementRepo.findAll();
        } else {
            return evenementRepo.findByUserId(user.getId());
        }
    }


    public List<Evenement> getEvenementsParDate(User user, LocalDate date) {
        if (user.getRole().getLibelle() == TypeRole.ADMINISTRATEUR) {
            return evenementRepo.findEvenementsByDate(date);
        } else {
            return evenementRepo.findEvenementsByDateAndUser(date, user.getId());
        }
    }
}
