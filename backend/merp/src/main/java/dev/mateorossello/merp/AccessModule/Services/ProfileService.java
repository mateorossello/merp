package dev.mateorossello.merp.AccessModule.Services;

import dev.mateorossello.merp.AccessModule.DTOs.ProfileInput;
import dev.mateorossello.merp.AccessModule.DTOs.ProfileOutput;
import dev.mateorossello.merp.AccessModule.Mappers.ProfileMapper;
import dev.mateorossello.merp.AccessModule.Models.Profile;
import dev.mateorossello.merp.AccessModule.Models.Task;
import dev.mateorossello.merp.AccessModule.Repositories.ProfileRepository;
import dev.mateorossello.merp.Exceptions.ResourceConflictException;
import dev.mateorossello.merp.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final TaskService taskService;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper, TaskService taskService) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.taskService = taskService;
    }

    public ProfileOutput createProfile(ProfileInput newProfile) {
        if(existsProfileByName(newProfile.name())) {
            throw new ResourceConflictException("Profile already exists");
        }

        Profile profile = profileRepository.save(profileMapper.toEntity(newProfile));

        return profileMapper.toOutput(profile);
    }

    public void deleteProfile(Long profileId) {
        Profile profile = getProfileByIdWithTasks(profileId);

        if(profile.getTasks().isEmpty()) {
            profileRepository.deleteById(profileId);
        } else {
            throw new ResourceConflictException("Profile not deleted, has tasks associated");
        }
    }

    protected Profile getProfileById(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public ProfileOutput getProfileDtoById(Long id) {
        return profileMapper.toOutput(getProfileById(id));
    }

    protected Profile getProfileByName(String name) {
        return profileRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public ProfileOutput getProfileDtoByName(String name) {
        return profileMapper.toOutput(getProfileByName(name));
    }

    public boolean existsProfileByName(String name) {
        return profileRepository.existsByName(name);
    }

    protected Profile getProfileByIdWithTasks(Long id) {
        return profileRepository.findFullById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public ProfileOutput getProfileDtoByIdWithTasks(Long id) {
        return profileMapper.toOutput(getProfileByIdWithTasks(id));
    }

    protected List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public List<ProfileOutput> getAllProfileDtos() {
        return profileMapper.toOutputList(getAllProfiles());
    }

    @Transactional
    public void updateProfileTasks(Long profileId, List<Long> newTasks) {
        Profile profile = getProfileByIdWithTasks(profileId);

        Set<Task> tasks = new HashSet<>(taskService.getTasksByIds(newTasks));

        profile.updateTasks(tasks);
        profileRepository.save(profile);
    }
}
