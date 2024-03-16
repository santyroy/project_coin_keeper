package dev.roy.coinkeeper.dto;

import java.time.LocalDateTime;

public record BudgetResponseDTO(String name, String type, Float goal, LocalDateTime openDate, Integer userId ) {
}
