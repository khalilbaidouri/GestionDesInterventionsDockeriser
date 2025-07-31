package spring.security.avis.DTO;

/**
 * @author $ {USERS}
 **/
public record ProfilUpdateRequest(
        String nom,
        String prenom,
        String email,
        String password
) {}