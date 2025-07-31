package spring.security.avis.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.avis.Enum.StatutIntervention;
import spring.security.avis.Enum.StatutNotification;
import spring.security.avis.Enum.TypeNotification;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.*;
import spring.security.avis.entity.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author $ {USERS}
 **/
@Service
public class UserService implements UserDetailsService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ValidationService validationService;
    private final ValidationRepo validationRepo;
    private final TokenRepo tokenRepo;
    private final InterventionRepository interventionRepository;
    private final NotificationRepository notificationRepository;



    public UserService(UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder, ValidationService validationService, ValidationRepo validationRepo, TokenRepo tokenRepo, InterventionRepository interventionRepository, NotificationRepository notificationRepository) {
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.validationService = validationService;
        this.validationRepo = validationRepo;
        this.tokenRepo = tokenRepo;
        this.interventionRepository = interventionRepository;
        this.notificationRepository = notificationRepository;
    }

    public void inscription(User user) {

        if (user.getUsername() == null || !user.getUsername().contains("@") || !user.getUsername().contains(".")) {
            throw new RuntimeException("Email invalide");
        }

        Optional<User> userOpt = userRepo.findEmail(user.getUsername());
        if (userOpt.isPresent()) {
            throw new RuntimeException("Email deja existant");
        }

        String newPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(newPassword);

        Role role = new Role();
        role.setLibelle(TypeRole.UTILISATEUR);
        user.setRole(role);

        user = this.userRepo.save(user);

        this.validationService.enregitrer(user);
    }

    public void activation(Map<String,String> activation){
        Validation validation= this.validationService.getValidationByCode(activation.get("code"));
        if(Instant.now().isAfter(validation.getDateExpiration())){
            System.out.println("Date expiration : " + validation.getDateExpiration());
            System.out.println("Date actuelle : " + Instant.now());
            throw new RuntimeException("votre code est expire");
        }
        User userActiver=this.userRepo.findById(validation.getUser().getId()).orElseThrow(()
                -> new RuntimeException("user not found"));

         userActiver.setActive(true);
         validation.setDateActviation(Instant.now());
         this.validationRepo.save(validation);
         this.userRepo.save(userActiver);
    }

        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User utilisateur = userRepo.findByEmail(email);
        if (utilisateur == null) {
            throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
        }

        return utilisateur;
    }


    public void alterPassword(Map<String, String> parametre) {
        User user = (User) this.loadUserByUsername(parametre.get("email"));
        this.validationService.enregitrer(user);
    }

    public void newPassword(Map<String, String> parametre) {
        User user = (User) this.loadUserByUsername(parametre.get("email"));
        Validation validation  = validationService.getValidationByCode(parametre.get("code"));
        if(validation.getUser().getUsername().equals(user.getUsername())){
            String newPassword = bCryptPasswordEncoder.encode(parametre.get("password"));
            user.setPassword(newPassword);
            validation.setDateActviation(Instant.now());
            this.userRepo.save(user);
        }
    }



  public Optional<User> ingenieurDispo(LocalDateTime dateDebut, LocalDateTime dateEcheance) {
        List<User> ingenieurs = userRepo.findByRole(TypeRole.INGENIEUR);

        for (User ingenieur : ingenieurs) {
            List<Intervention> interventionsConflit = interventionRepository
                    .findByIngenieurAndStatutInAndDateFinAfterAndDateDebutBefore(
                            ingenieur,
                            Arrays.asList(StatutIntervention.ACCEPTE, StatutIntervention.PLANIFIEE),
                            dateDebut,
                            dateEcheance);

            if (interventionsConflit.isEmpty()) {
                return Optional.of(ingenieur);
            }
        }

        return Optional.empty();
    }

    public static long calculerDureeEnMinutes(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut != null && dateFin != null && !dateFin.isBefore(dateDebut)) {
            return Duration.between(dateDebut, dateFin).toMinutes();
        } else {
            throw new IllegalArgumentException("Les dates sont nulles ou la date de fin est avant la date de début.");
        }
    }

    public List<Notification> getMesNotifications(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Notification> notifications = new ArrayList<>();
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User n'exist pas.");
        }
        for (Notification notification : user.getNotifications()) {
            notifications.add(notification);
        }
        return notifications;
    }

    public void modifierProfil(String nom, String prenom, String email, String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepo.findByEmail(username);

        if (user != null) {
            if (nom != null && !nom.trim().isEmpty()) {
                user.setNom(nom);
            }

            if (prenom != null && !prenom.trim().isEmpty()) {
                user.setPrenom(prenom);
            }

            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email);
            }

            if (password != null && !password.trim().isEmpty()) {
                String newPassword = bCryptPasswordEncoder.encode(password);
                user.setPassword(newPassword);
            }

            userRepo.save(user);
        }
    }

}

