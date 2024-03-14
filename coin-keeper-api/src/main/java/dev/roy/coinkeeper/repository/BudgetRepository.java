package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
}
