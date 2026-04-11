package dev.mateorossello.merp.AccessModule.Services;

import dev.mateorossello.merp.AccessModule.DTOs.TaskOutput;
import dev.mateorossello.merp.AccessModule.Mappers.TaskMapper;
import dev.mateorossello.merp.AccessModule.Models.Task;
import dev.mateorossello.merp.AccessModule.Models.TaskType;
import dev.mateorossello.merp.AccessModule.Repositories.TaskRepository;
import dev.mateorossello.merp.Exceptions.ResourceNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing tasks. Provides methods for creating, deleting, updating and retrieving tasks.
 */

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    //
    // Get methods
    //

    protected Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public TaskOutput getTaskDtoById(Long id) {
        return taskMapper.toOutput(getTaskById(id));
    }

    // Name related methods

    protected Task getTaskByName(TaskType name) {
        return taskRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public TaskOutput getTaskDtoByName(TaskType name) {
        return taskMapper.toOutput(getTaskByName(name));
    }

    // Other methods

    protected List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<TaskOutput> getAllTaskDtos() {
        return taskMapper.toOutputList(getAllTasks());
    }

    protected List<Task> getTasksByIds(List<Long> ids) {
        return taskRepository.findAllById(ids);
    }

    public List<TaskOutput> getTaskDtosByIds(List<Long> ids) {
        return taskMapper.toOutputList(getTasksByIds(ids));
    }
}
