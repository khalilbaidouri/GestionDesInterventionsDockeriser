package spring.security.avis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contenu", length = 1000)
    private String contenu;
    @Column(name = "is_desactive")
    private boolean isDesactive=false;
    @Column(name = "is_expire")
    private boolean isExpire=false;
    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    private RefreshToken refreshToken;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;
}
