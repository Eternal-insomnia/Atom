package ru.bonch.szfo2024.error.exception.common;

/**
 * @author Andrey Kurnosov (GutChoice)
 */
public class InternalServerError extends RuntimeException {
    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }
}