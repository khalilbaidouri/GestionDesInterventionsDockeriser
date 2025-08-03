package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.security.avis.Enum.TypeRole;

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
}
