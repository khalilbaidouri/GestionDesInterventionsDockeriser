package spring.security.avis.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.security.avis.Repo.InterventionRepository;
import spring.security.avis.Service.RapportService;
import spring.security.avis.entity.Intervention;

@RestController
@RequestMapping("/interventions")
public class RapportController {
    private final InterventionRepository interventionRepository;
    private final RapportService rapportService;

    public RapportController(InterventionRepository interventionRepository, RapportService rapportService) {
        this.interventionRepository = interventionRepository;
        this.rapportService = rapportService;
    }

    @GetMapping("/{id}/rapport")
    public ResponseEntity<byte[]> getRapportPdf(@PathVariable Long id) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

        byte[] pdfBytes = rapportService.generateRapportPdfFromIntervention(intervention);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "rapport_intervention_" + id + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
