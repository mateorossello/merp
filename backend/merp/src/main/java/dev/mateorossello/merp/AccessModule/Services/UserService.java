package dev.mateorossello.merp.AccessModule.Services;

import dev.mateorossello.merp.AccessModule.DTOs.UserInput;
import dev.mateorossello.merp.AccessModule.DTOs.UserOutput;
import dev.mateorossello.merp.AccessModule.Mappers.UserMapper;
import dev.mateorossello.merp.AccessModule.Models.User;
import dev.mateorossello.merp.AccessModule.Repositories.UserRepository;
import dev.mateorossello.merp.Exceptions.ResourceConflictException;
import dev.mateorossello.merp.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserOutput createUser(UserInput newUser) {
        if(existsUserByUsername(newUser.username())) {
            throw new ResourceConflictException("User already exists");
        }

        User user = userMapper.toEntity(newUser);
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setProfile(profileService.getProfileById(newUser.profileId()));
        user = userRepository.save(user);

        return userMapper.toOutput(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);

        userRepository.delete(user);
    }

    protected User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserOutput getUserDtoById(Long id) {
        return userMapper.toOutput(getUserById(id));
    }

    protected User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserOutput getUserDtoByUsername(String username) {
        return userMapper.toOutput(getUserByUsername(username));
    }

    public boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    protected User getUserByUsernameWithProfileAndTasks(String username) {
        return userRepository.findFullByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserOutput getUserDtoByUsernameWithProfileAndTasks(String username) {
        return userMapper.toOutput(getUserByUsernameWithProfileAndTasks(username));
    }

    protected List<User> getAllUsersByProfileId(Long profileId) {
        return userRepository.findAllByProfileId(profileId);
    }

    public List<UserOutput> getAllUserDtosByProfileId(Long profileId) {
        return userMapper.toOutputList(userRepository.findAllByProfileId(profileId));
    }

    public boolean existsUserByProfileId(Long id) {
        return userRepository.existsByProfileId(id);
    }

    protected List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserOutput> getAllUserDtos() {
        return userMapper.toOutputList(getAllUsers());
    }
}
