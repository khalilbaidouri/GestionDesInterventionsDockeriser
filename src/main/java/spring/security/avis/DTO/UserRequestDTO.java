package spring.security.avis.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.security.avis.Enum.TypeRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private TypeRole role;
    //private boolean disponibilite;
    //private String specialite;
    private String matricule;
    private String password;
}
