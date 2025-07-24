package spring.security.avis.Securite;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author $ {USERS}
 **/
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class ConfigSecurite {
    private final JwtFiltre jwtFiltre;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ConfigSecurite(JwtFiltre jwtFiltre, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtFiltre = jwtFiltre;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests.requestMatchers(HttpMethod.POST,"/inscription").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/activation").permitAll()
                                        //.requestMatchers(HttpMethod.POST,"/demande").hasAuthority("ROLEADMINISTRATEUR")
                                        .requestMatchers(HttpMethod.POST,"/connexion").permitAll()
                                        .requestMatchers("/interventions/*/rapport").permitAll()
                                        .requestMatchers(HttpMethod.POST    ,"/newPassword").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/alterPassword").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/refreshToken").permitAll()
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**"
                                        ).permitAll()
                                        .requestMatchers(HttpMethod.GET, "/avis").hasAuthority("ROLEADMINISTRATEUR")
                                        .requestMatchers(HttpMethod.GET, "/mesInfo").hasAuthority("ROLEADMINISTRATEUR")
                                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFiltre, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean

    public AuthenticationProvider authenticationProvider( UserDetailsService userDetailsService){
        DaoAuthenticationProvider  daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return daoAuthenticationProvider;
    }

}
