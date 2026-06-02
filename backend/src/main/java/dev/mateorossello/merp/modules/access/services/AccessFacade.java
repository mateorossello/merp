package dev.mateorossello.merp.modules.access.services;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing access related operations that involve multiple services.
 */

@Service
@AllArgsConstructor
public class AccessFacade {
    private final UserService userService;
    private final ProfileService profileService;

    // Safe delete methods

    @Transactional
    public void deleteProfileSafe(Long profileId) {
        if (userService.existsUserByProfileId(profileId)) {
            throw new ResourceConflictException("Profile not deleted, has users associated");
        }
        
        profileService.deleteProfile(profileId);
    }
}
