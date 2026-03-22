package dev.mateorossello.merp.AccessModule.Controllers;

import dev.mateorossello.merp.AccessModule.DTOs.ProfileInput;
import dev.mateorossello.merp.AccessModule.DTOs.ProfileOutput;
import dev.mateorossello.merp.AccessModule.Services.AccessFacade;
import dev.mateorossello.merp.AccessModule.Services.ProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final AccessFacade accessFacade;

    public ProfileController(ProfileService profileService, AccessFacade accessFacade) {
        this.profileService = profileService;
        this.accessFacade = accessFacade;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<ProfileOutput> createProfile(@Valid @RequestBody ProfileInput newProfile) {
        return ResponseEntity.ok().body(profileService.createProfile(newProfile));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        accessFacade.deleteProfileSafe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<ProfileOutput> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileDtoById(id));
    }

    @GetMapping("/exists")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<Boolean> existsProfileByName(@RequestParam(required = true) String name) {
        return ResponseEntity.ok(profileService.existsProfileByName(name));
    }
    
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<ProfileOutput> getProfileByIdWithTasks(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileDtoByIdWithTasks(id));
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<List<ProfileOutput>> getProfiles(@RequestParam(required = false) String name) {
        if(name != null) {
            return ResponseEntity.ok(List.of(profileService.getProfileDtoByName(name)));
        }

        return ResponseEntity.ok(profileService.getAllProfileDtos());
    }

    @PutMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<Void> updateProfileTasks(@PathVariable("id") Long profileId, @RequestBody List<Long> newTasks) {
        profileService.updateProfileTasks(profileId, newTasks);
        return ResponseEntity.noContent().build();
    }
}
