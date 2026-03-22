package dev.mateorossello.merp.AccessModule.Controllers;

import dev.mateorossello.merp.AccessModule.DTOs.LoginInput;
import dev.mateorossello.merp.AccessModule.Services.AuthenticationService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginInput loginInput) {
        String token = authenticationService.authenticate(loginInput.username(), loginInput.password());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
