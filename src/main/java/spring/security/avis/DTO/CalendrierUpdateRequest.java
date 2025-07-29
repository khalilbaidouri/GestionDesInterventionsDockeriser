package spring.security.avis.DTO;

import spring.security.avis.Enum.StatutCalendrier;

/**
 * @author $ {USERS}
 **/
public record CalendrierUpdateRequest( String nom,
         String description,
        StatutCalendrier statut) {
}
