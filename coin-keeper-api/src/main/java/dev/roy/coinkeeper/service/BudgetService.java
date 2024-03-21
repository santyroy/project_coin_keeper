package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.BudgetNotFoundException;
import dev.roy.coinkeeper.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetService.class);

    private final UserService userService;
    private final BudgetRepository budgetRepository;

    public BudgetResponseDTO addBudget(BudgetRequestDTO dto) {
        Integer userId = dto.userId();
        LOG.info("Adding budget for userId: " + userId);
        User user = userService.getUser(userId);

        Budget budget = new Budget();
        budget.setName(dto.name());
        budget.setUser(user);
        budget.setOpenDate(LocalDateTime.now());
        budget.setTransactions(null);

        // Check if payload has non-mandatory fields
        if (dto.type() != null) {
            LOG.info("Budget type added");
            budget.setType(dto.type());
        }
        if (dto.goal() != null) {
            LOG.info("Budget goal added");
            budget.setGoal(dto.goal());
        }

        Budget savedBudget = budgetRepository.save(budget);
        return new BudgetResponseDTO(savedBudget.getId(), savedBudget.getName(), savedBudget.getType(), savedBudget.getGoal(),
                savedBudget.getOpenDate(), userId);
    }

    public BudgetResponseDTO findBudgetById(Integer budgetId) {
        Budget budget = getBudget(budgetId);
        return new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(), budget.getGoal(),
                budget.getOpenDate(), budget.getUser().getId());
    }

    public void deleteBudgetById(Integer budgetId) {
        Budget budget = getBudget(budgetId);
        budgetRepository.delete(budget);
    }

    public BudgetResponseDTO updateBudgetById(Integer budgetId, BudgetRequestDTO dto) {
        Budget existingBudget = getBudget(budgetId);
        if (dto.name() != null) {
            existingBudget.setName(dto.name());
        }
        if (dto.type() != null) {
            existingBudget.setType(dto.type());
        }
        if (dto.goal() != null) {
            existingBudget.setGoal(dto.goal());
        }

        Budget updatedBudget = budgetRepository.save(existingBudget);
        return new BudgetResponseDTO(updatedBudget.getId(), updatedBudget.getName(), updatedBudget.getType(),
                updatedBudget.getGoal(), updatedBudget.getOpenDate(), updatedBudget.getUser().getId());
    }

    public Page<BudgetResponseDTO> findAllBudgets(int pageNo, int pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Page<Budget> budgets = budgetRepository.findAll(page);
        return budgets
                .map(budget -> new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(),
                        budget.getGoal(), budget.getOpenDate(), budget.getUser().getId()));
    }

    public Page<BudgetResponseDTO> findAllBudgetsByUser(Integer userId, int pageNo, int pageSize) {
        User user = userService.getUser(userId);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "openDate"));
        Page<Budget> budgets = budgetRepository.findByUser(user, pageRequest);
        return budgets
                .map(budget -> new BudgetResponseDTO(budget.getId(), budget.getName(), budget.getType(),
                        budget.getGoal(), budget.getOpenDate(), budget.getUser().getId()));
    }

    protected Budget getBudget(Integer budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new BudgetNotFoundException("Budget with id: " + budgetId + " not found");
        }
        return budgetOpt.get();
    }
}
