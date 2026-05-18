package dev.mateorossello.merp.modules.access.dtos;

public record UserOutput(
    Long id,

    String username,
    
    ProfileOutput profile
) {}
