package dev.mateorossello.merp.AccessModule.DTOs;

public record UserOutput(
    Long id,

    String username,
    
    ProfileOutput profile
) {}
