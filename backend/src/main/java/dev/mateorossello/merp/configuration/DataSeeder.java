package dev.mateorossello.merp.configuration;

import dev.mateorossello.merp.modules.access.models.Profile;
import dev.mateorossello.merp.modules.access.models.Task;
import dev.mateorossello.merp.modules.access.models.TaskType;
import dev.mateorossello.merp.modules.access.models.User;
import dev.mateorossello.merp.modules.access.repositories.ProfileRepository;
import dev.mateorossello.merp.modules.access.repositories.TaskRepository;
import dev.mateorossello.merp.modules.access.repositories.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProfileRepository profileRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProfileRepository profileRepository, TaskRepository taskRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // TODO: Limpiar generación de datos automáticos para producción.

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (taskRepository.count() == 0) {
            List<Task> defaultTasks = Arrays.stream(TaskType.values()).map(type -> {
                Task task = new Task();
                task.setName(type);
                return task;
            }).toList();
            taskRepository.saveAll(defaultTasks);
        }

        if (userRepository.findByUsername("ADMINISTRATOR").isEmpty()) {
            Profile adminProfile = profileRepository.findFullByName("ADMINISTRATOR").orElseGet(() -> {
                Profile newAdminProfile = new Profile();
                newAdminProfile.setName("ADMINISTRATOR");
                return profileRepository.save(newAdminProfile);
            });

            adminProfile.getTasks().addAll(taskRepository.findAll());
            profileRepository.save(adminProfile);

            User admin = new User();
            admin.setUsername("ADMINISTRATOR");
            admin.setPassword(passwordEncoder.encode("Administrator@1"));
            admin.setProfile(adminProfile);

            userRepository.save(admin);
        }
    }
}
