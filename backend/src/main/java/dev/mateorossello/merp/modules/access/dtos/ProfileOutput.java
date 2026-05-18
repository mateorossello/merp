package dev.mateorossello.merp.modules.access.dtos;

import java.util.Set;

public record ProfileOutput(
    Long id,

    String name,
    
    Set<TaskOutput> tasks
) {}
