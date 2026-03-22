package dev.mateorossello.merp.AccessModule.Controllers;

import dev.mateorossello.merp.AccessModule.DTOs.UserInput;
import dev.mateorossello.merp.AccessModule.DTOs.UserOutput;
import dev.mateorossello.merp.AccessModule.Services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserOutput> createUser(@Valid @RequestBody UserInput newUser) {
        return ResponseEntity.ok(userService.createUser(newUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<UserOutput> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @GetMapping("/exists/username")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<Boolean> existeUsuarioNombreUsuario(@RequestParam String username) {
        return ResponseEntity.ok(userService.existsUserByUsername(username));
    }

    @GetMapping("/{username}/profile-tasks")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<UserOutput> getUserByUsernameWithProfileAndTasks(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserDtoByUsernameWithProfileAndTasks(username));
    }

    @GetMapping("/by-profile")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<List<UserOutput>> getUserByProfileId(@RequestParam Long profileId) {
        return ResponseEntity.ok(userService.getAllUserDtosByProfileId(profileId));
    }

    @GetMapping("/exists/profile")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<Boolean> existsUserByProfileId(@RequestParam(required = true) Long profileId) {
        return ResponseEntity.ok(userService.existsUserByProfileId(profileId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<List<UserOutput>> getUsers(@RequestParam(required = false) String name) {
        if(name != null) {
            return ResponseEntity.ok(List.of(userService.getUserDtoByUsername(name)));
        }

        return ResponseEntity.ok(userService.getAllUserDtos());
    }
}
