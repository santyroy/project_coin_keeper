package dev.roy.coinkeeper.dto;

import dev.roy.coinkeeper.entity.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        TransactionType type,
        Float amount,
        String category,
        LocalDateTime date,
        Integer budgetId) {
}
