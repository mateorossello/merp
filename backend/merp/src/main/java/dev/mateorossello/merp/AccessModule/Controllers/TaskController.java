package dev.mateorossello.merp.AccessModule.Controllers;

import dev.mateorossello.merp.AccessModule.DTOs.TaskOutput;
import dev.mateorossello.merp.AccessModule.Models.TaskType;
import dev.mateorossello.merp.AccessModule.Services.TaskService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_TASKS')")
    public ResponseEntity<TaskOutput> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskDtoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_TASKS')")
    public ResponseEntity<List<TaskOutput>> getTasks(@RequestParam(required = false) TaskType name) {
        if(name != null) {
            return ResponseEntity.ok(List.of(taskService.getTaskDtoByName(name)));
        }

        return ResponseEntity.ok(taskService.getAllTaskDtos());
    }
}
