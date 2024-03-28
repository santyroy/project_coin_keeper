package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgetPasswordRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(regexp = "^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$")
        String email) {
}
