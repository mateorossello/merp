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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                DecodedJWT decodedJWT = jwtManager.validateAndGetDecoded(authorizationHeader);

                if (decodedJWT != null) {
                    String username = decodedJWT.getSubject();
                    Long id = decodedJWT.getClaim("id").asLong();
                    String profile = decodedJWT.getClaim("profile").asString();
                    List<String> tasks = decodedJWT.getClaim("tasks").asList(String.class);
                    Long tokenPermissionsVersion = decodedJWT.getClaim("permissionsVersion").asLong();
                    Long currentPermissionsVersion = userRepository.findPermissionsVersionByUserId(id).orElse(null);

                    if (!Objects.equals(tokenPermissionsVersion, currentPermissionsVersion)) {
                        SecurityContextHolder.clearContext();
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token permissions are outdated");
                        return;
                    }

                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + profile.toUpperCase()));
                    
                    if (tasks != null) {
                        for (String task : tasks) {
                            authorities.add(new SimpleGrantedAuthority(task.toUpperCase()));
                        }
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        new CustomUser(id, username),
                        null,
                        authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
