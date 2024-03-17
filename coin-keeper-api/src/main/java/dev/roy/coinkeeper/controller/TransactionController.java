package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.TransactionRequestDTO;
import dev.roy.coinkeeper.dto.TransactionResponseDTO;
import dev.roy.coinkeeper.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        LOG.info("Adding new transaction for budget started");
        TransactionResponseDTO transactionResponseDTO = transactionService.addTransaction(dto);
        LOG.info("Adding new transaction for budget completed");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, 201, "Transaction added", transactionResponseDTO));
    }

    @GetMapping("{transactionId}")
    public ResponseEntity<ApiResponse> findTransactionById(@PathVariable Integer transactionId) {
        LOG.info("Searching for transaction started");
        TransactionResponseDTO transactionResponseDTO = transactionService.findTransactionById(transactionId);
        LOG.info("Searching for transaction completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Transaction found", transactionResponseDTO));
    }

    @DeleteMapping("{transactionId}")
    public ResponseEntity<ApiResponse> deleteTransactionById(@PathVariable Integer transactionId) {
        LOG.info("Deletion of transaction started");
        transactionService.deleteTransactionById(transactionId);
        LOG.info("Deletion of transaction completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Transaction deleted", null));
    }
}
