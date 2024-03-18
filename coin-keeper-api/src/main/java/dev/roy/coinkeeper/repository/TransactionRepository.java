package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findTransactionByBudget(Budget budget, Pageable pageable);
}
