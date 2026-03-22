package dev.mateorossello.merp.AccessModule.Services;

import dev.mateorossello.merp.Exceptions.ResourceConflictException;
import org.springframework.stereotype.Service;

@Service
public class AccessFacade {
    private final UserService userService;
    private final ProfileService profileService;

    public AccessFacade(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    public void deleteProfileSafe(Long profileId) {
        if (userService.existsUserByProfileId(profileId)) {
            throw new ResourceConflictException("Profile not deleted, has users associated");
        }
        
        profileService.deleteProfile(profileId);
    }
}
