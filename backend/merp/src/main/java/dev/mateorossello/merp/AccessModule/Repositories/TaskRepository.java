package dev.mateorossello.merp.AccessModule.Repositories;

import dev.mateorossello.merp.AccessModule.Models.Task;
import dev.mateorossello.merp.AccessModule.Models.TaskType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByName(TaskType name);
}
