package dev.roy.coinkeeper.security.service;

import dev.roy.coinkeeper.dto.*;
import dev.roy.coinkeeper.entity.RefreshToken;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.entity.UserOTP;
import dev.roy.coinkeeper.exception.InvalidCredentialsException;
import dev.roy.coinkeeper.exception.InvalidOTPException;
import dev.roy.coinkeeper.exception.InvalidRefreshTokenException;
import dev.roy.coinkeeper.repository.RefreshTokenRepository;
import dev.roy.coinkeeper.repository.UserOTPRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import dev.roy.coinkeeper.service.MailService;
import dev.roy.coinkeeper.service.UserService;
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
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenRepository tokenRepository;
    private final UserOTPRepository otpRepository;
    private final MailService mailService;

    public UserResponseDTO register(UserRequestDTO dto) {
        UserResponseDTO userResponseDTO = userService.addUser(dto);
        Optional<User> savedUser = userRepository.findByEmail(userResponseDTO.email());
        savedUser.ifPresent(this::sendOTPViaEmail);
        return userResponseDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
            String jwt = tokenService.generateJWT(auth);

            RefreshToken savedRefreshToken = null;
            User user = userService.getUser(dto.email());
            // Check if refresh token exists for the user
            Optional<RefreshToken> rtOpt = tokenRepository.findByUser(user);
            RefreshToken refreshToken;
            if (rtOpt.isPresent()) {
                refreshToken = rtOpt.get();
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setExpiry(LocalDateTime.now().plusMinutes(2));
            } else {
                refreshToken = new RefreshToken();
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setExpiry(LocalDateTime.now().plusMinutes(2));
                refreshToken.setUser(user);
            }
            savedRefreshToken = tokenRepository.save(refreshToken);
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

        User user = userService.getUser(tokenOpt.get().getUser().getId());
        return tokenService.generateJWT(user);
    }

    public void deleteRefreshToken(Cookie cookie) {
        String token = cookie.getValue();
        Optional<RefreshToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new InvalidRefreshTokenException("Invalid Refresh token");
        }
        tokenRepository.delete(tokenOpt.get());
    }

    public void sendOTPViaEmail(User user) {
        LOG.info("Generating OTP for user: " + user.getEmail());
        Optional<UserOTP> userOTP = otpRepository.findByUser(user);
        Random random = new Random();
        final int OTP = random.nextInt(1000, 9999);
        if (userOTP.isEmpty()) {
            otpRepository.save(new UserOTP(0, OTP, LocalDateTime.now().plusMinutes(2), user));
        } else {
            UserOTP tempOTP = userOTP.get();
            tempOTP.setOtp(OTP);
            tempOTP.setExpiry(LocalDateTime.now().plusMinutes(2));
            otpRepository.save(tempOTP);
        }

        String sub = "Welcome to coin keeper";
        String body = "Hi " + user.getName() + ", \n\nPlease use the below OTP (valid for 15 minutes):  " + OTP + " for completing the registration";
        mailService.sendSimpleMessage(user.getEmail(), sub, body);
        LOG.info("Sending OTP via email for user: " + user.getEmail());
    }

    public void verifyOTP(VerifyOTPRequestDTO dto) {
        User user = userService.getUser(dto.userId());
        Optional<UserOTP> userOTP = otpRepository.findByUser(user);
        if (userOTP.isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (!dto.otp().equals(userOTP.get().getOtp())) {
            throw new InvalidOTPException("OTP mismatch");
        }
        if (userOTP.get().getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidOTPException("OTP expired");
        }
        // activate the user
        user.setActive(true);
        userRepository.save(user);
        // remove existing OTP entry
        otpRepository.delete(userOTP.get());
    }

    public void resendOTP(ResendOTPRequestDTO dto) {
        User user = userService.getUser(dto.userId());
        sendOTPViaEmail(user);
    }
}
