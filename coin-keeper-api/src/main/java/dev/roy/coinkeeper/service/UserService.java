package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.UserRequestDTO;
import dev.roy.coinkeeper.dto.UserResponseDTO;
import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Role;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.UserEmailAlreadyExistsException;
import dev.roy.coinkeeper.exception.UserNotFoundException;
import dev.roy.coinkeeper.exception.UserRoleNotFoundException;
import dev.roy.coinkeeper.repository.RoleRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserResponseDTO addUser(UserRequestDTO dto) {
        // Check if USER role exists
        Optional<Role> roleOpt = roleRepository.findByName("USER");
        if (roleOpt.isEmpty()) {
            throw new UserRoleNotFoundException("USER role not found");
        }

        // Check if email already exists
        String email = dto.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserEmailAlreadyExistsException("User email: " + email + " already exists");
        }

        // TODO: Need to encrypt password using BCrypt
        User user = new User(0,
                dto.name(), dto.email(), dto.password(), dto.picture(),
                LocalDateTime.now(), Set.of(roleOpt.get()), Set.of(new Budget()));
        User savedUser = userRepository.save(user);
        LOG.info("User: " + savedUser.getName() + " with email: " + savedUser.getEmail() + " added to database");
        return new UserResponseDTO(savedUser.getName(), savedUser.getEmail());
    }

    public UserResponseDTO findUserById(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        // Check if user does not exist
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        User user = userOpt.get();
        return new UserResponseDTO(user.getName(), user.getEmail());
    }

    public void deleteUserById(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        // Check if user does not exist
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        userRepository.delete(userOpt.get());
    }

    public UserResponseDTO updateUserById(Integer userId, UserRequestDTO dto) {
        Optional<User> userOpt = userRepository.findById(userId);

        // Check if user does not exist
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        User existingUser = userOpt.get();
        if (null != dto.name() && !dto.name().isBlank()) {
            LOG.info("Updating name");
            existingUser.setName(dto.name());
        }
        if (null != dto.email() && !dto.email().isBlank()) {
            LOG.info("Updating email");
            existingUser.setEmail(dto.email());
        }
        if (null != dto.picture() && !dto.picture().isBlank()) {
            LOG.info("Updating picture");
            existingUser.setPicture(dto.picture());
        }
        if (null != dto.password() && !dto.password().isBlank()) {
            LOG.info("Updating password");
            // TODO: Need to encrypt password using BCrypt
            existingUser.setPassword(dto.password());
        }

        User savedUser = userRepository.save(existingUser);
        return new UserResponseDTO(savedUser.getName(), savedUser.getEmail());
    }
}
