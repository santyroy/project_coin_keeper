package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.TransactionRequestDTO;
import dev.roy.coinkeeper.dto.TransactionResponseDTO;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Transaction;
import dev.roy.coinkeeper.entity.TransactionType;
import dev.roy.coinkeeper.exception.TransactionNotFoundException;
import dev.roy.coinkeeper.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;

    public TransactionService(TransactionRepository transactionRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
    }

    public TransactionResponseDTO addTransaction(TransactionRequestDTO dto) {
        Integer budgetId = Integer.parseInt(dto.budgetId());
        Budget budget = budgetService.getBudget(budgetId);

        Transaction transaction = new Transaction();
        transaction.setBudget(budget);
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

    public TransactionResponseDTO findTransactionById(Integer transactionId) {
        Transaction transaction = getTransaction(transactionId);
        return new TransactionResponseDTO(transaction.getType(), transaction.getAmount(), transaction.getCategory(),
                transaction.getDate(), transaction.getBudget().getId());
    }

    public void deleteTransactionById(Integer transactionId) {
        Transaction transaction = getTransaction(transactionId);
        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO updateTransactionById(Integer transactionId, TransactionRequestDTO dto) {
        Transaction transaction = getTransaction(transactionId);
        if (dto.type() != null) {
            transaction.setType(TransactionType.CREDIT.name().equals(dto.type().toUpperCase()) ? TransactionType.CREDIT : TransactionType.DEBIT);
        }
        if (dto.amount() != null && dto.amount() > 0) {
            transaction.setAmount(dto.amount());
        }
        if (dto.category() != null) {
            transaction.setCategory(dto.category());
        }
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return new TransactionResponseDTO(updatedTransaction.getType(), updatedTransaction.getAmount(),
                updatedTransaction.getCategory(), updatedTransaction.getDate(), updatedTransaction.getBudget().getId());
    }

    public Page<TransactionResponseDTO> findAllTransactions(Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactions = transactionRepository.findAll(page);
        return transactions
                .map(transaction -> new TransactionResponseDTO(transaction.getType(), transaction.getAmount(),
                        transaction.getCategory(), transaction.getDate(), transaction.getBudget().getId()));
    }

    private Transaction getTransaction(Integer transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with id: " + transactionId + " not found");
        }
        return transactionOpt.get();
    }

    public Page<TransactionResponseDTO> findAllTransactionsByBudget(Integer budgetId, int pageNo, int pageSize) {
        Budget budget = budgetService.getBudget(budgetId);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactions = transactionRepository.findTransactionByBudget(budget, pageRequest);
        return transactions
                .map(transaction -> new TransactionResponseDTO(transaction.getType(), transaction.getAmount(),
                        transaction.getCategory(), transaction.getDate(), transaction.getBudget().getId()));
    }
}