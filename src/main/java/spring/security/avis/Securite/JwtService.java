package spring.security.avis.Securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import spring.security.avis.Repo.TokenRepo;
import spring.security.avis.Service.UserService;
import spring.security.avis.entity.RefreshToken;
import spring.security.avis.entity.Token;
import spring.security.avis.entity.User;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author $ {USERS}
 **/
@Slf4j
@Transactional
@Configuration
public class JwtService {
    public static final String BEARER = "bearer";
    private static String key = "sCnvJShN6frTEHDNF9n5ifKXLJs55FOFASXUmTnd41hW30FTcUxRRAZOzxihkKXp\n";
    private final UserService userService;
    private final TokenRepo tokenRepo;

    public JwtService(UserService userService, TokenRepo jwtRepo) {
        this.userService = userService;
        this.tokenRepo = jwtRepo;
    }

    public Map<String,String> generate(String username){
        User user = (User)this.userService.loadUserByUsername(username);
        Map<String, String> jwtMap = new HashMap<>(this.generateJwt(user));
        RefreshToken refreshToken =RefreshToken.builder()
                .expired(false)
                .token(UUID.randomUUID().toString())
                .dateCreation(Instant.now())
                .dateExpire(Instant.now().plusMillis(30*60*1000))
                .build();

        final Token token = Token
                .builder()
                .contenu(jwtMap.get(BEARER))
                .isDesactive(false)
                .isExpire(false)
                .refreshToken(refreshToken)
                .user(user)
                .build();
        this.tokenRepo.save(token);
        jwtMap.put("refreshToken",refreshToken.getToken());
        return jwtMap;
    }

    public void deconexion() {
        User userConnecter = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Token token = this.tokenRepo.findUserValidToken(userConnecter.getUsername(), false, false)
                .orElseThrow(() -> new RuntimeException("token not found"));
        token.setExpire(true);
        token.setDesactive(true);
        this.tokenRepo.save(token);
    }

    public void desactiveTokens(User user) {
        List<Token> listTokenExiste = this.tokenRepo.findUserByUsername(user.getUsername())
                .peek(token -> {
                    token.setDesactive(true);
                    token.setExpire(true);
                })
                .collect(Collectors.toList());

        this.tokenRepo.saveAll(listTokenExiste);
    }


    public Key getKey() {
        byte[] decode = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(decode);
    }

    public String lireUsername(String token) {
        return this.getClams(token,Claims::getSubject);
    }

    public boolean isTokenExpaired(String token) {
        Date experationDate = getExperationDateToken(token);
        return experationDate.before(new Date());
    }

    public Token lireTokenBDD(String token) {
        return tokenRepo.findByContenu(token).orElseThrow(() -> new RuntimeException("Token not found"));
    }

    private Date getExperationDateToken(String token) {
        return this.getClams(token, Claims::getExpiration);
    }

    private <T> T getClams(String token, Function<Claims, T> function) {
       Claims claims = getAllClaims(token);
       return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .parseClaimsJws(token)
                .getBody();
    }



    private Map<String, String> generateJwt(User user) {
        long currentTime = System.currentTimeMillis();
        long experationTime = System.currentTimeMillis() + 30* 60 * 1000;

        Map<String, Object> claims = Map.of(
                "username", user.getUsername(),
                "password", user.getPassword(),
                Claims.EXPIRATION,new Date(experationTime),
                Claims.SUBJECT , user.getUsername()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(experationTime))
                .setSubject(user.getUsername())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer",bearer);
    }

    @Scheduled(cron = "0 * * * * *")
    public void removeLessTokenExpireAndDesactive(){
        log.info("suppression des token a {}", Instant.now());
        this.tokenRepo.removeTokenByExpireAndDesactive(true,true);
    }

    public  Map<String, String> refreshToken(Map<String, String> refreshToken) {
        Token token = this.tokenRepo.findByRefreshToken(refreshToken.get("refreshToken"))
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if(token.getRefreshToken().isExpired() || token.getRefreshToken().getDateExpire().isBefore(Instant.now())){
            throw new RuntimeException("token expired");
        }
       return this.generate(token.getUser().getUsername());
    }
}
