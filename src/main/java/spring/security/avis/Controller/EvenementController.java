package spring.security.avis.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.avis.Repo.EvenementRepository;
import spring.security.avis.entity.Evenement;

import java.util.List;

/**
 * @author $ {USERS}
 **/
@RestController
@RequestMapping("/evenements")
public class EvenementController {

    private final EvenementRepository evenementRepository;

    public EvenementController(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @GetMapping("/evenements")
    public List<Evenement> getEvenements() {
        return evenementRepository.findAll();
    }

}
