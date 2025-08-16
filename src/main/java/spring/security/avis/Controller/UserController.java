package spring.security.avis.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AuthDto;
import spring.security.avis.DTO.ProfilUpdateRequest;
import spring.security.avis.DTO.UserRequestDTO;
import spring.security.avis.DTO.UserResponseDTO;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.InterventionRepository;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.Securite.JwtService;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.Service.UserService;
import spring.security.avis.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final InterventionService interventionService;
    private final InterventionRepository interventionRepository;
    private final UserRepo userRepo;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, InterventionService interventionService, InterventionRepository interventionRepository, UserRepo userRepo) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.interventionService = interventionService;
        this.interventionRepository = interventionRepository;
        this.userRepo = userRepo;
    }

    @PostMapping(path = "/inscription")
    public ResponseEntity<UserResponseDTO> inscription(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO response = userService.inscription(userRequestDTO);
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/connexion")
    public Map<String,String> connexion(@RequestBody AuthDto authDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.email(), authDto.password()
                ));
        if (authenticate.isAuthenticated()) {
            User user = (User)authenticate.getPrincipal();
            jwtService.desactiveTokens(user);
            return this.jwtService.generate(authDto.email());
        }
        return null;
    }


    @PostMapping(path = "/activation")
    public void activation(@RequestBody Map<String,String> activation) {
        this.userService.activation(activation);
    }
    @PostMapping(path = "/refreshToken")
    public @ResponseBody Map<String, String> refreshToken(@RequestBody Map<String,String> refreshToken) {
        return this.jwtService.refreshToken(refreshToken);
    }
    @PostMapping(path = "/deconexion")
    public void deconexion() {
        this.jwtService.deconexion();
    }

    @GetMapping("/mesInfo")
    public ResponseEntity<UserRequestDTO> getMesInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String email = auth.getName();
        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole().getLibelle(),
                user.getMatricule(),
                null
        );

        return ResponseEntity.ok(userRequestDTO);
    }

 /*   @PostMapping("/alterPassword")
    public void alterPassword(@RequestBody Map<String,String> parametre) {
        this.userService.alterPassword(parametre);
    }
    @PostMapping("/newPassword")
    public void newPassword(@RequestBody Map<String,String> parametre) {
        this.userService.newPassword(parametre);
    }*/

    @PostMapping("/alterPassword")
    public ResponseEntity<Map<String, String>> alterPassword(@RequestBody Map<String,String> parametre) {
        try {
            this.userService.alterPassword(parametre);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reset code sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to send reset code");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/newPassword")
    public ResponseEntity<Map<String, String>> newPassword(@RequestBody Map<String,String> parametre) {
        try {
            this.userService.newPassword(parametre);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to reset password");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/modifierProfil")
    public ResponseEntity<?> modifierProfil(@RequestBody ProfilUpdateRequest request) {
        try {
            userService.modifierProfil(
                    request.nom(),
                    request.prenom(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.ok("Profil modifie avec succes.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/ingenieurs")
    public ResponseEntity<List<UserResponseDTO>> getAllIngenieurs() {
        List<User> ingenieurs = userRepo.findAllByRoleLibelle(TypeRole.INGENIEUR);

        List<UserResponseDTO> dtos = ingenieurs.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    @DeleteMapping("/{email}/supprimerUser")
    public ResponseEntity<UserResponseDTO> supprimerUser(@PathVariable String email) {
        try {
            UserResponseDTO deletedUser = userService.supprimerUser(email);
            return ResponseEntity.ok(deletedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/listeUesrs")
    public ResponseEntity<List<UserResponseDTO>> listerUsers() {
        return ResponseEntity.ok(userService.listerUsers());
    }

}
