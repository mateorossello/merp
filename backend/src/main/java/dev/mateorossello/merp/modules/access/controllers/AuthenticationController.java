package dev.mateorossello.merp.modules.access.controllers;

import dev.mateorossello.merp.modules.access.dtos.LoginInput;
import dev.mateorossello.merp.modules.access.services.AuthenticationService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing authentication. Provides methods for user login.
 */

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    // Login methods

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginInput loginInput) {
        String token = authenticationService.authenticate(loginInput.username(), loginInput.password());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
