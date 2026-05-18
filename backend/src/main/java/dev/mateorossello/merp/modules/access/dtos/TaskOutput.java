package dev.mateorossello.merp.modules.access.dtos;

import dev.mateorossello.merp.modules.access.models.TaskType;

public record TaskOutput(
    Long id,
    
    TaskType name
) {}
