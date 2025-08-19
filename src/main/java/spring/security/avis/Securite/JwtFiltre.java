package spring.security.avis.Securite;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.security.avis.Service.UserService;
import spring.security.avis.entity.Token;

import java.io.IOException;

/**
 * @author $ {USERS}
 **/
@Service
public class JwtFiltre extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    public JwtFiltre(UserDetailsService userDetailsService, JwtService jwtService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String username = null;
        String token=null;
        boolean isExpairedToken = true;
        Token tokenDansBDD = null;
        final String authorization = request.getHeader("Authorization");
        //Bearer eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IiQyYSQxMCRyVHFPWnlCTTNsNTIwejhBc0M0MEtPQmxhMVNpbHdmbTJKS0l2THp3czk2NUhqZTJKSjZPaSIsInVzZXJuYW1lIjoia2hhbGlsYmFpZG91cmk5N0BnbWFpbC5jb20ifQ.XVFR5-KtS1FXWYAt07xKGMTIRGfpQAFdAFAKbNX-5tk
        if(authorization != null && authorization.startsWith("Bearer ")) {
            token=authorization.substring(7);
            isExpairedToken = jwtService.isTokenExpaired(token);
            username = jwtService.lireUsername(token);
            tokenDansBDD=jwtService.lireTokenBDD(token);
        }
        if(!isExpairedToken && tokenDansBDD.getUser().getUsername().equals(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
