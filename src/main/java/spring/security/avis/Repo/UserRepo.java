package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.entity.Role;
import spring.security.avis.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    User findById(long id);
    @Query("FROM User user  WHERE user.email = :email")
    Optional<User> findEmail(
            @Param("email") String email
    );
    @Query("SELECT u FROM User u WHERE u.role.libelle = :role")
    List<User> findByRole(@Param("role") TypeRole role);




}
