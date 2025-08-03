package spring.security.avis.Controller;

import org.springframework.web.bind.annotation.*;
import spring.security.avis.DTO.EvenementDTO;
import spring.security.avis.Repo.EvenementRepository;
import spring.security.avis.entity.Evenement;

import java.util.ArrayList;
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
    public List<EvenementDTO> getEvenements() {
        List<Evenement> events = evenementRepository.findAll();
        List<EvenementDTO> dtos = new ArrayList<>();
        for (Evenement event : events) {
            dtos.add(new EvenementDTO(event));
        }
        return dtos;
    }

}
