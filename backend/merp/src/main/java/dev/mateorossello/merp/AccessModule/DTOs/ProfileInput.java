package dev.mateorossello.merp.AccessModule.DTOs;

import jakarta.validation.constraints.NotBlank;

public record ProfileInput(
    @NotBlank(message = "Profile name is required")
    String name
) {}
