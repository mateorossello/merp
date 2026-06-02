package dev.mateorossello.merp.modules.access.mappers;

import dev.mateorossello.merp.modules.access.dtos.TaskOutput;
import dev.mateorossello.merp.modules.access.models.Task;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskOutput toOutput(Task task);
    List<TaskOutput> toOutputList(List<Task> tasks);
}
