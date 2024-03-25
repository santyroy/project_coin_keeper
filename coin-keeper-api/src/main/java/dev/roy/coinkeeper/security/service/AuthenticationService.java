package dev.roy.coinkeeper.security.service;

import dev.roy.coinkeeper.dto.LoginRequestDTO;
import dev.roy.coinkeeper.dto.LoginResponseDTO;
import dev.roy.coinkeeper.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
            String jwt = tokenService.generateJWT(auth);
            return new LoginResponseDTO(jwt);
        } catch (AuthenticationException ex) {
            LOG.error("Authentication failed {}", ex.getMessage());
            throw new InvalidCredentialsException(ex.getMessage());
        }
    }
}
