package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * @author $ {USERS}
 **/

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String token;
    private Instant dateCreation;
    private Instant dateExpire;
    private boolean expired;

}
