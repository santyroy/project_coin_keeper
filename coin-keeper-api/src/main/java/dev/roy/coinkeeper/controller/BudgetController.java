package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.service.BudgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetController.class);

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addBudget(@Valid @RequestBody BudgetRequestDTO dto) {
        LOG.info("Adding new budget for user started");
        BudgetResponseDTO budgetResponseDTO = budgetService.addBudget(dto);
        LOG.info("Adding new budget for user completed");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, 201, "Budget created", budgetResponseDTO));
    }
}
