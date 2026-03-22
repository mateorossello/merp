package dev.mateorossello.merp.AccessModule.Mappers;

import dev.mateorossello.merp.AccessModule.DTOs.TaskOutput;
import dev.mateorossello.merp.AccessModule.Models.Task;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskOutput toOutput(Task task);
    List<TaskOutput> toOutputList(List<Task> tasks);
}
