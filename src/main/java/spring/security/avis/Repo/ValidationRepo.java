package spring.security.avis.Repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import spring.security.avis.entity.Validation;

import java.time.Instant;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Transactional
public interface ValidationRepo extends CrudRepository<Validation, Long> {
    Optional<Validation> findByCode(String code);
    @Modifying
    void deleteByDateExpirationBefore(Instant now);
}
