package dev.mateorossello.merp.AccessModule.DTOs;

import dev.mateorossello.merp.AccessModule.Models.TaskType;

public record TaskOutput(
    Long id,
    
    TaskType name
) {}
