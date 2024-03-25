package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.*;
import dev.roy.coinkeeper.security.service.AuthenticationService;
import dev.roy.coinkeeper.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequestDTO dto) {
        LOG.info("Registering of new user started");
        UserResponseDTO userResponseDTO = userService.addUser(dto);
        LOG.info("New user successfully registered");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, 201, "User created", userResponseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletResponse response) {
        LOG.info("Logging in user: " + dto.email());
        LoginResponseDTO loginResponseDTO = authenticationService.login(dto);
        Cookie cookie = new Cookie("refreshToken", loginResponseDTO.refreshToken().getToken());
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
//        cookie.setMaxAge(loginResponseDTO.refreshToken().getExpiry().getSecond());
        response.addCookie(cookie);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User logged in", loginResponseDTO.jwt()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse> getNewJwt(HttpServletRequest request) {
        LOG.info("Processing refresh token started");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, 400, "No cookies found", null));
        }

        Optional<Cookie> refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();
        if (refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, 400, "No refresh token in cookie", null));
        }

        String jwt = authenticationService.getNewJwt(refreshToken.get());
        LOG.info("Processing refresh token completed");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "success", jwt));
    }
}
