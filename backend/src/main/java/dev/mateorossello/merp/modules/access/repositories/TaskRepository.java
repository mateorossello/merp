package dev.mateorossello.merp.modules.access.repositories;

import dev.mateorossello.merp.modules.access.models.Task;
import dev.mateorossello.merp.modules.access.models.TaskType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Task entities.
 */

public interface TaskRepository extends JpaRepository<Task,Long> {
    // Name related queries
    Optional<Task> findByName(TaskType name);
}
