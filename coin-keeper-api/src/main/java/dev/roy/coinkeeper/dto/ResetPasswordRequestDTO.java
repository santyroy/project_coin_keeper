package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.*;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(regexp = "^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$")
        String email,
        @Positive
        @NotNull
        Integer otp,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password length: min 8 characters")
        String password) {
}
