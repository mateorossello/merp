package dev.mateorossello.merp.AccessModule.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserInput(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password,
    
    @NotNull(message = "Profile ID is required")
    Long profileId
) {}
