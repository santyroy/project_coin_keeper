package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

        @NotBlank(message = "Name is mandatory")
        String name,
        @NotBlank(message = "Email is mandatory")
        @Email(regexp = "^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password length: min 8 characters")
        String password,
        String picture) {
}
