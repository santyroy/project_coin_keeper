package dev.roy.coinkeeper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BudgetRequestDTO(
        @NotBlank(message = "Budget name is mandatory")
        String name,
        String type,
        Float goal,
        @NotNull(message = "userId is mandatory")
        @Positive
        Integer userId) {
}
