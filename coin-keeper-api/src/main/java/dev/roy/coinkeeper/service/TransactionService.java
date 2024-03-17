package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.TransactionRequestDTO;
import dev.roy.coinkeeper.dto.TransactionResponseDTO;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Transaction;
import dev.roy.coinkeeper.entity.TransactionType;
import dev.roy.coinkeeper.exception.BudgetNotFoundException;
import dev.roy.coinkeeper.repository.BudgetRepository;
import dev.roy.coinkeeper.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public TransactionService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public TransactionResponseDTO addTransaction(TransactionRequestDTO dto) {
        Integer budgetId = Integer.parseInt(dto.budgetId());
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new BudgetNotFoundException("Budget with id: " + budgetId + " not found");
        }

        Transaction transaction = new Transaction();
        transaction.setBudget(budgetOpt.get());
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(dto.amount());
        transaction.setType(TransactionType.CREDIT.name().equals(dto.type().toUpperCase()) ? TransactionType.CREDIT : TransactionType.DEBIT);
        if (dto.category() != null) {
            transaction.setCategory(dto.category());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponseDTO(savedTransaction.getType(), savedTransaction.getAmount(),
                savedTransaction.getCategory(), savedTransaction.getDate(), savedTransaction.getBudget().getId());
    }
}