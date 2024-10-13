package ru.bonch.szfo2024.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bonch.szfo2024.error.exception.common.BadRequest;
import ru.bonch.szfo2024.error.exception.common.InternalServerError;
import ru.bonch.szfo2024.error.exception.common.NotFound;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global error handler for the application.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Handles unexpected exceptions, responding with HTTP 500 Internal Server Error.
     *
     * @param e the exception caught by this handler
     * @return an ErrorResponse containing the error type and message
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unexpectedErrorHandle(final Exception e) {
        log.error("Error: {} / Description: {}", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
    }

    /**
     * Handles validation exceptions for invalid method arguments, responding with HTTP 400 Bad Request.
     *
     * @param e the MethodArgumentNotValidException caught by this handler
     * @return an ErrorResponse with detailed information about the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notValidArgumentHandle(final MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        String errorMessage = errors.stream()
                .map(error -> String.format("[Field: %s, error: %s, value: %s]",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.joining(" "));
        log.warn("Error: {} / Description: {}", HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage);
    }

    /**
     * Handles custom BadRequest exceptions, responding with HTTP 400 Bad Request.
     *
     * @param e the BadRequest exception caught by this handler
     * @return an ErrorResponse containing the error type and message
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestHandler(final BadRequest e) {
        log.warn("Error: {} / Description: {}", HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * Handles custom NotFound exceptions, responding with HTTP 404 Not Found.
     *
     * @param e the NotFound exception caught by this handler
     * @return an ErrorResponse containing the error type and message
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundHandler(final NotFound e) {
        log.warn("Error: {} / Description: {}", HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    /**
     * Handles custom InternalServerError exceptions, responding with HTTP 500 Internal Server Error.
     *
     * @param e the InternalServerError exception caught by this handler
     * @return an ErrorResponse containing the error type and message
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerError(final InternalServerError e) {
        log.error("Error: {} / Description: {}", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
    }
}