package dev.mateorossello.merp.configuration;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.mateorossello.merp.modules.access.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final UserRepository userRepository;

    public JwtFilter(JwtManager jwtManager, UserRepository userRepository) {
        this.jwtManager = jwtManager;
        this.userRepository = userRepository;
    }

    // Filter the requests to validate the token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // If the header is valid
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // Validate the token and get the decoded token
                DecodedJWT decodedJWT = jwtManager.validateAndGetDecoded(authorizationHeader);

                // If the decoded token is valid
                if (decodedJWT != null) {
                    // Get the user information from the decoded token
                    String username = decodedJWT.getSubject();
                    Long id = decodedJWT.getClaim("id").asLong();
                    String profile = decodedJWT.getClaim("profile").asString();
                    List<String> tasks = decodedJWT.getClaim("tasks").asList(String.class);
                    Long tokenPermissionsVersion = decodedJWT.getClaim("permissionsVersion").asLong();
                    Long currentPermissionsVersion = userRepository.findPermissionsVersionByUserId(id).orElse(null);

                    // Check if the permissions version is outdated
                    if (!Objects.equals(tokenPermissionsVersion, currentPermissionsVersion)) {
                        SecurityContextHolder.clearContext();
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token permissions are outdated.");
                        return;
                    }

                    // Set the authorities and add tasks to authorities
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + profile.toUpperCase()));
                    
                    if (tasks != null) {
                        for (String task : tasks) {
                            authorities.add(new SimpleGrantedAuthority(task.toUpperCase()));
                        }
                    }

                    // Set the authentication and add to context
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        new CustomUser(id, username),
                        null,
                        authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                return;
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
