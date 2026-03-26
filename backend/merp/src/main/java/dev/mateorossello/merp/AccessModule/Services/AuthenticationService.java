package dev.mateorossello.merp.AccessModule.Services;

import dev.mateorossello.merp.AccessModule.Models.Profile;
import dev.mateorossello.merp.AccessModule.Models.User;
import dev.mateorossello.merp.Configuration.JwtManager;
import dev.mateorossello.merp.Exceptions.ResourceNotFoundException;
import dev.mateorossello.merp.Exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JwtManager jwtManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserService userService, JwtManager jwtManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtManager = jwtManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        User userStored;

        try {
            userStored = userService.getFullUserByUsername(username);
        } catch (ResourceNotFoundException exception) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if(!passwordEncoder.matches(password, userStored.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        Profile profile = userStored.getProfile();
        List<String> tasks = profile.getTasks().stream().map(task -> task.getName().name()).toList();

        return jwtManager.generateToken(userStored.getUsername(), profile.getName(), tasks);
    }
}
