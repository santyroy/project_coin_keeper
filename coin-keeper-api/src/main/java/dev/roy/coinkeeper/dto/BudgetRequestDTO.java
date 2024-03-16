package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.NotBlank;

public record BudgetRequestDTO(
        @NotBlank(message = "Budget name is mandatory")
        String name,
        String type,
        Float goal,
        @NotBlank(message = "userId is mandatory")
        String userId ) {
}
