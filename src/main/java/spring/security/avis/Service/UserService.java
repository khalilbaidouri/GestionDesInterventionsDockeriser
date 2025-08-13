package spring.security.avis.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.avis.DTO.UserRequestDTO;
import spring.security.avis.DTO.UserResponseDTO;
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

    public UserResponseDTO inscription(UserRequestDTO userRequestDTO) {

        if (userRequestDTO.getEmail() == null
                || !userRequestDTO.getEmail().contains("@")
                || !userRequestDTO.getEmail().contains(".")) {
            throw new RuntimeException("Email invalide");
        }

        Optional<User> userOpt = userRepo.findEmail(userRequestDTO.getEmail());
        if (userOpt.isPresent()) {
            throw new RuntimeException("Email déjà existant");
        }

        User user = new User();
        user.setUsername(userRequestDTO.getEmail());
        user.setNom(userRequestDTO.getNom());
        user.setPrenom(userRequestDTO.getPrenom());
        user.setMatricule(userRequestDTO.getMatricule());

        String encodedPassword = bCryptPasswordEncoder.encode(userRequestDTO.getPassword());
        user.setPassword(encodedPassword);

        Role role = new Role();
        role.setLibelle(TypeRole.OPERATEUR);
        user.setRole(role);

        User savedUser = userRepo.save(user);

        validationService.enregitrer(savedUser);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getNom(),
                savedUser.getPrenom(),
                savedUser.getUsername(),
                savedUser.getMatricule(),
                savedUser.getRole().getLibelle()
        );
    }


    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.getValidationByCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getDateExpiration())) {
            System.out.println("Date expiration : " + validation.getDateExpiration());
            System.out.println("Date actuelle : " + Instant.now());
            throw new RuntimeException("votre code est expire");
        }
        User userActiver = this.userRepo.findById(validation.getUser().getId()).orElseThrow(()
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
        Validation validation = validationService.getValidationByCode(parametre.get("code"));
        if (validation.getUser().getUsername().equals(user.getUsername())) {
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

    public List<Notification> getMesNotifications() {
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

    public boolean isAdminOrChefDepartement(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if (user.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR) || user.getRole().getLibelle().equals(TypeRole.CHEF_DE_DEPARTEMENT)) {
                return true;
            }
        }
        return false;
    }

    public UserResponseDTO supprimerUser(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User admin = userRepo.findByEmail(username);
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User n'existe pas.");
        }

        if (admin.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR)) {
            userRepo.delete(user);
            return new UserResponseDTO(user);
        }

        throw new RuntimeException("Accès refusé : vous n'êtes pas administrateur.");
    }


    public List<UserResponseDTO> listerUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User admin = userRepo.findByEmail(username);

        if (admin == null || !admin.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR)) {
            throw new RuntimeException("Accès refusé : vous n'êtes pas administrateur.");
        }

        List<User> users = userRepo.findAll();
        List<UserResponseDTO> usersDto = new ArrayList<>();

        for (User user : users) {
            usersDto.add(new UserResponseDTO(user));
        }

        return usersDto;
    }



}
