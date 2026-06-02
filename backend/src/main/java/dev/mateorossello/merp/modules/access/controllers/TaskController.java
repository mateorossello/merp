package dev.mateorossello.merp.modules.access.controllers;

import dev.mateorossello.merp.modules.access.dtos.TaskOutput;
import dev.mateorossello.merp.modules.access.models.TaskType;
import dev.mateorossello.merp.modules.access.services.TaskService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing tasks. Provides methods for creating, deleting, updating and retrieving tasks.
 */

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    //
    // Get methods
    //

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_TASKS')")
    public ResponseEntity<TaskOutput> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskDtoById(id));
    }

    // Other methods

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_TASKS')")
    public ResponseEntity<List<TaskOutput>> getTasks(@RequestParam(required = false) TaskType name) {
        return ResponseEntity.ok(
            name != null
                ? List.of(taskService.getTaskDtoByName(name))
                : taskService.getAllTaskDtos()
        );
    }
}
