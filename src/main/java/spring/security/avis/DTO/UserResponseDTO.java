package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.entity.User;

/**
 * @author $ {USERS}
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String matricule;
    private TypeRole role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.nom = user.getNom();
        this.prenom = user.getPrenom();
        this.email = user.getEmail();
        this.matricule = user.getMatricule();
        this.role=user.getRole().getLibelle();
    }

}
