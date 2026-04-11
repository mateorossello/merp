package dev.mateorossello.merp.AccessModule.Controllers;

import dev.mateorossello.merp.AccessModule.DTOs.ProfileInput;
import dev.mateorossello.merp.AccessModule.DTOs.ProfileOutput;
import dev.mateorossello.merp.AccessModule.Services.AccessFacade;
import dev.mateorossello.merp.AccessModule.Services.ProfileService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing profiles. Provides methods for creating, deleting, updating and retrieving profiles.
 */

@RestController
@RequestMapping("/profiles")
@AllArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final AccessFacade accessFacade;

    // Create methods

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<ProfileOutput> createProfile(@Valid @RequestBody ProfileInput newProfile) {
        return ResponseEntity.ok().body(profileService.createProfile(newProfile));
    }

    // Delete methods

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        accessFacade.deleteProfileSafe(id);
        return ResponseEntity.noContent().build();
    }

    // Update methods

    @PutMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<Void> updateProfileTasks(@PathVariable("id") Long profileId, @RequestBody Long[] newTasks) {
        profileService.updateProfileTasks(profileId, List.of(newTasks));
        return ResponseEntity.noContent().build();
    }

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<ProfileOutput> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileDtoById(id));
    }

    // Name related methods

    @GetMapping("/exists")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<Boolean> existsProfileByName(@RequestParam(required = true) String name) {
        return ResponseEntity.ok(profileService.existsProfileByName(name));
    }

    // ID related methods
    
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<ProfileOutput> getProfileByIdWithTasks(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileDtoByIdWithTasks(id));
    }

    // Other methods
    
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<List<ProfileOutput>> getProfiles(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(
            name != null
                ? List.of(profileService.getProfileDtoByName(name))
                : profileService.getAllProfileDtos()
        );
    }
}
