package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
