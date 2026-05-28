package nl.inholland.recipekeeper.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.model.dto.response.ErrorResponse;
import nl.inholland.recipekeeper.exception.external.AlreadyImportedRecipeException;
import nl.inholland.recipekeeper.exception.domain.BusinessRuleViolationException;
import nl.inholland.recipekeeper.exception.domain.RecipeNotFoundException;
import nl.inholland.recipekeeper.exception.external.ExternalServiceException;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------
    // DOMAIN ERRORS
    // -------------------------

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            RecipeNotFoundException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ex,
                req,
                ErrorCode.RECIPE_NOT_FOUND
        );
    }

    @ExceptionHandler(AlreadyImportedRecipeException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            AlreadyImportedRecipeException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                ex,
                req,
                ErrorCode.CONFLICT
        );
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleViolationException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ex,
                req,
                ErrorCode.BUSINESS_RULE_VIOLATION
        );
    }

    // -------------------------
    // VALIDATION
    // -------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                msg,
                req,
                ErrorCode.VALIDATION_ERROR
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest req
    ) {
        String msg = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        return error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                msg,
                req,
                ErrorCode.VALIDATION_ERROR
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest req) {
        return error(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                ex.getMessage(),
                req,
                ErrorCode.VALIDATION_ERROR
        );
    }

    // -------------------------
    // EXTERNAL / INFRA
    // -------------------------

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<String> handleRateLimit(Exception ex) {
        return ResponseEntity.status(429)
                .body("External API rate limit reached. Try again later.");
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternal(
            ExternalServiceException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.BAD_GATEWAY,
                "External service failure",
                ex,
                req,
                ErrorCode.EXTERNAL_SERVICE_ERROR
        );
    }

    @ExceptionHandler(ImageDownloadException.class)
    public ResponseEntity<ErrorResponse> handleImageFailure(
            ImageDownloadException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Image pipeline failed",
                ex,
                req,
                ErrorCode.EXTERNAL_SERVICE_ERROR
        );
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleParsing(
            JsonProcessingException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.BAD_GATEWAY,
                "Invalid external data format",
                ex.getMessage(),
                req,
                ErrorCode.EXTERNAL_SERVICE_ERROR
        );
    }

    // -------------------------
    // DATABASE
    // -------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDB(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.CONFLICT,
                "Database constraint violation",
                ex,
                req,
                ErrorCode.CONFLICT
        );
    }

    // -------------------------
    // EDGE CASE
    // -------------------------

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle404(
            NoHandlerFoundException ex,
            HttpServletRequest req
    ) {
        return error(
                HttpStatus.NOT_FOUND,
                "Endpoint not found",
                "Route does not exist",
                req,
                ErrorCode.INTERNAL_ERROR
        );
    }

    // -------------------------
    // FALLBACK
    // -------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(
            Exception ex,
            HttpServletRequest req
    ) {
        log.error("[{}] UNEXPECTED_ERROR", req.getRequestURI(), ex);

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
//                "Unexpected error",
                ex.getMessage(),
                ex,
//                "Internal failure",
                req,
                ErrorCode.INTERNAL_ERROR
        );
    }

    // -------------------------
    // CORE BUILDER
    // -------------------------

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status,
            String message,
            Exception ex,
            HttpServletRequest req,
            ErrorCode code
    ) {
        log.warn("[{}] {}: {}", req.getRequestURI(), code, ex.getMessage(), ex);

        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        code,
                        message,
                        req.getRequestURI()
                ));
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status,
            String message,
            String internalMessage,
            HttpServletRequest req,
            ErrorCode code
    ) {
        log.warn("[{}] {}: {}", req.getRequestURI(), code, internalMessage);

        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        code,
                        message,
                        req.getRequestURI()
                ));
    }
}