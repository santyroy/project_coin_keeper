package dev.roy.coinkeeper.exception;

import dev.roy.coinkeeper.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(BindingResult result,
                                                                             MethodArgumentNotValidException ex) {
        LOG.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, "error", errors));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        LOG.error(ex.getMessage());
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                violations.put(violation.getRootBeanClass().getName(), violation.getMessageTemplate()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, "error", violations));
    }

    @ExceptionHandler(value = UserRoleNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserRoleNotFoundException(UserRoleNotFoundException ex) {
        LOG.error(ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, "error", detail));
    }

    @ExceptionHandler(value = UserEmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex) {
        LOG.error(ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, "error", detail));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
        LOG.error(ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, "error", detail));
    }
}
