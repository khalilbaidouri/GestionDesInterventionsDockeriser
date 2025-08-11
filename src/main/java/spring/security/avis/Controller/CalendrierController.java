package spring.security.avis.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.CalendrierUpdateRequest;
import spring.security.avis.DTO.InterventionDTO;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.Service.CalendrierService;
import spring.security.avis.Service.InterventionService;
import spring.security.avis.entity.Calendrier;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import spring.security.avis.entity.Evenement;
import spring.security.avis.entity.Intervention;
import org.springframework.security.core.Authentication;
import spring.security.avis.entity.User;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author $ {USERS}
 **/
@RestController
@RequestMapping("/calendrier")
@RequiredArgsConstructor
public class CalendrierController {

    private final CalendrierService calendrierService;
    private final UserRepo userRepo;
    private final InterventionService interventionService;



    @GetMapping
    public ResponseEntity<Calendrier> getCalendrier() {
        return ResponseEntity.ok(calendrierService.getCalendrierUnique());
    }

    @PutMapping
    public ResponseEntity<Calendrier> updateCalendrier(@RequestBody CalendrierUpdateRequest request) {
        Calendrier calendrier = calendrierService.updateCalendrier(
                request.nom(),
                request.description(),
                request.statut()
        );
        return ResponseEntity.ok(calendrier);
    }

    @GetMapping("/interventions")
    public ResponseEntity<List<Intervention>> getInterventionsCalendrier() {
        Calendrier calendrier = calendrierService.getCalendrierUnique();
        return ResponseEntity.ok(calendrier.getInterventions());
    }


    @GetMapping("/evenements")
    public ResponseEntity<List<Map<String, Object>>> getEvenements(Authentication authentication) {
        Authentication connecter = SecurityContextHolder.getContext().getAuthentication();
        String username = connecter.getName();
        User user = userRepo.findByEmail(username);
        List<Evenement> evenements = calendrierService.getEvenements(user);

        List<Map<String, Object>> response = evenements.stream().map(e -> {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("id", e.getId());
            eventMap.put("title", e.getTitre());
            eventMap.put("start", e.getDateDebut());
            //eventMap.put("end", e.getDateFin());
            eventMap.put("description", e.getDescription());
            eventMap.put("lieu", e.getLieu());
            return eventMap;
        }).toList();

        return ResponseEntity.ok(response);
    }




    @GetMapping("/evenements/{date}")
    public ResponseEntity<List<Map<String, Object>>> getEvenementsParDate(
            @PathVariable String date,
            Authentication authentication) {

        User user = userRepo.findByEmail(authentication.getName());

        LocalDate localDate = LocalDate.parse(date);

        List<Evenement> evenements = calendrierService.getEvenementsParDate(user, localDate);

        List<Map<String, Object>> response = evenements.stream().map(e -> {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("id", e.getId());
            eventMap.put("title", e.getTitre());
            eventMap.put("start", e.getDateDebut());
            eventMap.put("end", e.getDateFin());
            eventMap.put("description", e.getDescription());
            eventMap.put("lieu", e.getLieu());
            return eventMap;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar")
    public List<InterventionDTO> getInterventionsForCalendar(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());

        if(user.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR) || user.getRole().getLibelle().equals(TypeRole.CHEF_DE_DEPARTEMENT)) {
            return interventionService.getAllInterventions();
        } else {
            return interventionService.getInterventionsByIngenieur(user.getId());
        }
    }

}

