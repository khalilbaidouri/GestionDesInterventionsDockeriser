package spring.security.avis.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.AuthDto;
import spring.security.avis.Securite.JwtService;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.Service.UserService;
import spring.security.avis.entity.Intervention;
import spring.security.avis.entity.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final InterventionService interventionService;
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, InterventionService interventionService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.interventionService = interventionService;
    }

    @PostMapping(path = "/inscription")
    public void inscription(@RequestBody User user) {
        this.userService.inscription(user);
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

    //@PreAuthorize("hasAuthority('ROLE_ADMINISTRATEUR')")
    @GetMapping("/mesInfo")
    public ResponseEntity<User> getMesInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User userConnecter = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userConnecter);
    }

    @PostMapping("/alterPassword")
    public void alterPassword(@RequestBody Map<String,String> parametre) {
        this.userService.alterPassword(parametre);
    }
    @PostMapping("/newPassword")
    public void newPassword(@RequestBody Map<String,String> parametre) {
        this.userService.newPassword(parametre);
    }


}
