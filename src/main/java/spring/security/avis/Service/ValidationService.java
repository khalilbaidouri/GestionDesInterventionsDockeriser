package spring.security.avis.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spring.security.avis.Repo.ValidationRepo;
import spring.security.avis.entity.User;
import spring.security.avis.entity.Validation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;

/**
 * @author $ {USERS}
 **/
@Transactional
@Slf4j
@Service
public class ValidationService {
    private final ValidationRepo validationRepo;
    private final NotificationService notificationService;
    public ValidationService(ValidationRepo validationRepo, NotificationService notificationService) {
        this.validationRepo = validationRepo;
        this.notificationService = notificationService;
    }

    public void enregitrer(User user) {
        Validation validation = new Validation();
        validation.setUser(user);
        Instant creation = Instant.now();
        validation.setDateCreation(creation);
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);
        validation.setDateExpiration(expiration);
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        String code = String.format("%06d", randomInt);
        validation.setCode(code);
        this.validationRepo.save(validation);
        this.notificationService.envoyer(validation);
    }

    public Validation getValidationByCode(String code) {
       return this.validationRepo.findByCode(code).orElseThrow(() ->
               new RuntimeException("votre code est invalid !"));
    }
    @Scheduled(cron = "0 * * * * *")
    public void netoyerTable(){
        log.info("suppression des validation a {}",Instant.now());
        this.validationRepo.deleteByDateExpirationBefore(Instant.now());
    }
}
