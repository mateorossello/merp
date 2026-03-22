package dev.mateorossello.merp.AccessModule.DTOs;

import java.util.Set;

public record ProfileOutput(
    Long id,

    String name,
    
    Set<TaskOutput> tasks
) {}
