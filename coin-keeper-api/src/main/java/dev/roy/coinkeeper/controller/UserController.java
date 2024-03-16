package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.UserRequestDTO;
import dev.roy.coinkeeper.dto.UserResponseDTO;
import dev.roy.coinkeeper.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addUser(@Valid @RequestBody UserRequestDTO dto) {
        LOG.info("Adding of new user started");
        UserResponseDTO userResponseDTO = userService.addUser(dto);
        LOG.info("New user successfully added");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, 201, "User created", userResponseDTO));
    }

    @GetMapping("{userId}")
    public ResponseEntity<ApiResponse> findUserById(@PathVariable Integer userId) {
        LOG.info("Searching user started");
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        LOG.info("Searching user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User found", userResponseDTO));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable Integer userId) {
        LOG.info("Deletion of user started");
        userService.deleteUserById(userId);
        LOG.info("Deletion of user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User deleted", null));
    }

    @PutMapping("{userId}")
    public ResponseEntity<ApiResponse> updateUserById(@PathVariable Integer userId, @RequestBody UserRequestDTO dto) {
        LOG.info("Updating user started");
        UserResponseDTO userResponseDTO = userService.updateUserById(userId, dto);
        LOG.info("Updating user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User updated", userResponseDTO));
    }
}
