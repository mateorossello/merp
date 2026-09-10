package dev.mateorossello.merp.modules.access.dtos;

import jakarta.validation.constraints.NotBlank;

public record ProfileInput(
    @NotBlank(message = "Profile name is required.")
    String name
) {}
