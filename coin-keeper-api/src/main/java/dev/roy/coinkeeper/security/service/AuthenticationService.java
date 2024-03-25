package dev.roy.coinkeeper.security.service;

import dev.roy.coinkeeper.dto.LoginRequestDTO;
import dev.roy.coinkeeper.dto.LoginResponseDTO;
import dev.roy.coinkeeper.entity.RefreshToken;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.InvalidCredentialsException;
import dev.roy.coinkeeper.exception.InvalidRefreshTokenException;
import dev.roy.coinkeeper.exception.UserNotFoundException;
import dev.roy.coinkeeper.repository.RefreshTokenRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
            String jwt = tokenService.generateJWT(auth);

            RefreshToken savedRefreshToken = null;
            Optional<User> userOpt = userRepository.findByEmail(dto.email());
            if (userOpt.isPresent()) {
                // Check if refresh token exists for the user
                Optional<RefreshToken> rtOpt = tokenRepository.findByUser(userOpt.get());
                RefreshToken refreshToken;
                if(rtOpt.isPresent()) {
                    refreshToken = rtOpt.get();
                    refreshToken.setToken(UUID.randomUUID().toString());
                    refreshToken.setExpiry(LocalDateTime.now().plusMinutes(2));
                } else {
                    refreshToken = new RefreshToken();
                    refreshToken.setToken(UUID.randomUUID().toString());
                    refreshToken.setExpiry(LocalDateTime.now().plusMinutes(2));
                    refreshToken.setUser(userOpt.get());
                }
                savedRefreshToken = tokenRepository.save(refreshToken);
            }
            return new LoginResponseDTO(jwt, savedRefreshToken);
        } catch (AuthenticationException ex) {
            LOG.error("Authentication failed {}", ex.getMessage());
            throw new InvalidCredentialsException(ex.getMessage());
        }
    }

    public String getNewJwt(Cookie cookie) {
        String token = cookie.getValue();
        Optional<RefreshToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new InvalidRefreshTokenException("Invalid Refresh token");
        }

        if (tokenOpt.get().getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Expired Refresh token");
        }

        Optional<User> userOpt = userRepository.findById(tokenOpt.get().getUser().getId());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found/registered");
        }

        return tokenService.generateJWT(userOpt.get());
    }
}
