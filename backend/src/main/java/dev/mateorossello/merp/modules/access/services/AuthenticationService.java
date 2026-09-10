package dev.mateorossello.merp.modules.access.services;

import dev.mateorossello.merp.configuration.JwtManager;
import dev.mateorossello.merp.exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.exceptions.UnauthorizedException;
import dev.mateorossello.merp.modules.access.models.Profile;
import dev.mateorossello.merp.modules.access.models.User;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing authentication operations. Provides methods for authenticating users.
 */

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtManager jwtManager;
    private final PasswordEncoder passwordEncoder;

    // Authentication methods

    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        User userStored;

        try {
            userStored = userService.getUserByUsernameWithProfileAndTasks(username);
        } catch (ResourceNotFoundException exception) {
            throw new UnauthorizedException("Invalid credentials.");
        }

        if(!passwordEncoder.matches(password, userStored.getPassword())) {
            throw new UnauthorizedException("Invalid credentials.");
        }

        Profile profile = userStored.getProfile();
        List<String> tasks = profile.getTasks().stream().map(task -> task.getName().name()).toList();

        return jwtManager.generateToken(
            userStored.getUsername(),
            userStored.getId(),
            profile.getName(),
            tasks,
            profile.getPermissionsVersion()
        );
    }
}
