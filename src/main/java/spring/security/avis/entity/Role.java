    package spring.security.avis.entity;

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import lombok.*;
    import spring.security.avis.Enum.TypeRole;

    /**
     * @author $ {USERS}
     **/
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Builder
    public class Role {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private TypeRole libelle;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public TypeRole getLibelle() {
            return libelle;
        }

        public void setLibelle(TypeRole libelle) {
            this.libelle = libelle;
        }
    }
