package dev.mateorossello.merp.configuration;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration class for Spring Data JPA Auditing.
 * Automatically provides the current authenticated user's ID for @CreatedBy audit fields.
 */

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
    @Bean
    AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            
            if (authentication.getPrincipal() instanceof CustomUser customUser) {
                return Optional.ofNullable(customUser.getId());
            }

            return Optional.empty();
        };
    }
}
