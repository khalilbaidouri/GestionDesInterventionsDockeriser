package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import spring.security.avis.entity.Token;

import java.util.Optional;
import java.util.stream.Stream;

public interface TokenRepo extends CrudRepository<Token, Long> {

    Optional<Token> findByContenu(String token);

    Optional<Token> findByContenuAndIsDesactiveAndIsExpire(String contenu, boolean isDesactive, boolean isExpire);

    @Query("FROM Token t WHERE t.isExpire = :isExpire AND t.isDesactive = :isDesactive AND t.user.email = :email")
    Optional<Token> findUserValidToken(
            @Param("email") String email,
            @Param("isDesactive") boolean isDesactive,
            @Param("isExpire") boolean isExpire
    );

    @Query("FROM Token t WHERE t.user.email = :email")
    Stream<Token> findUserByUsername(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.isExpire = :isExpire AND t.isDesactive = :isDesactive")
    void removeTokenByExpireAndDesactive(@Param("isExpire") boolean isExpire, @Param("isDesactive") boolean isDesactive);

    @Query("FROM Token t WHERE t.refreshToken.token = :token")
    Optional<Token> findByRefreshToken(@Param("token") String token);


}
