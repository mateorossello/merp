package dev.mateorossello.merp.modules.access.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserInput(
    @NotBlank(message = "Username is required.")
    String username,

    @NotBlank(message = "Password is required.")
    @Size(min = 10, max = 64, message = "Password must be between 10 and 64 characters long.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
        message = "Password must include uppercase, lowercase, number and special character"
    )
    String password,
    
    @NotNull(message = "Profile ID is required.")
    Long profileId
) {}
