package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.BudgetRequestDTO;
import dev.roy.coinkeeper.dto.BudgetResponseDTO;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.BudgetNotFoundException;
import dev.roy.coinkeeper.exception.UserNotFoundException;
import dev.roy.coinkeeper.repository.BudgetRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetService.class);

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public BudgetService(UserRepository userRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public BudgetResponseDTO addBudget(BudgetRequestDTO dto) {
        Integer userId = Integer.parseInt(dto.userId());
        LOG.info("Adding budget for userId: " + userId);
        // Check if user exist
        Optional<User> userOpt = userRepository.findById(userId);

        // Check if user does not exist
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        Budget budget = new Budget();
        budget.setName(dto.name());
        budget.setUser(userOpt.get());
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
        return new BudgetResponseDTO(budget.getId() ,budget.getName(), budget.getType(), budget.getGoal(),
                budget.getOpenDate(), budget.getUser().getId());
    }

    public void deleteBudgetById(Integer budgetId) {
        Budget budget = getBudget(budgetId);
        budgetRepository.delete(budget);
    }

    private Budget getBudget(Integer budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new BudgetNotFoundException("Budget with id: " + budgetId + " not found");
        }
        return budgetOpt.get();
    }
}
