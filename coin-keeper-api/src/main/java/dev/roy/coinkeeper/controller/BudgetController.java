package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.dto.UserResponseDTO;
import dev.roy.coinkeeper.service.BudgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{budgetId}")
    public ResponseEntity<ApiResponse> findBudgetById(@PathVariable Integer budgetId) {
        LOG.info("Searching budget for user started");
        BudgetResponseDTO budgetResponseDTO = budgetService.findBudgetById(budgetId);
        LOG.info("Searching budget for user completed");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, 200, "Budget found", budgetResponseDTO));
    }

    @DeleteMapping("{budgetId}")
    public ResponseEntity<ApiResponse> deleteBudgetById(@PathVariable Integer budgetId) {
        LOG.info("Deletion of budget started");
        budgetService.deleteBudgetById(budgetId);
        LOG.info("Deletion of budget completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budget deleted", null));
    }

    @PutMapping("{budgetId}")
    public ResponseEntity<ApiResponse> updateBudgetById(@PathVariable Integer budgetId,
                                                        @RequestBody BudgetRequestDTO dto) {
        LOG.info("Updating budget started");
        BudgetResponseDTO budgetResponseDTO = budgetService.updateBudgetById(budgetId, dto);
        LOG.info("Updating budget completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Budget updated", budgetResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllBudgets(@RequestParam(required = false, defaultValue = "0") int page,
                                                    @RequestParam(required = false, defaultValue = "5") int size) {
        LOG.info("Fetching all budgets started");
        Page<BudgetResponseDTO> allBudgets = budgetService.findAllBudgets(page, size);
        LOG.info("Fetching all budgets completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Users fetched", allBudgets));
    }
}
