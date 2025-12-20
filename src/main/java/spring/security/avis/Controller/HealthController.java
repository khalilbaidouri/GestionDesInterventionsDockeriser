package spring.security.avis.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author $ {USERS}
 **/

@RestController
public class HealthController {
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}

