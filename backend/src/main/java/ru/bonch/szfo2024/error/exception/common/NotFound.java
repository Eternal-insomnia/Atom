package ru.bonch.szfo2024.error.exception.common;

/**
 * @author Andrey Kurnosov (GutChoice)
 */
public class NotFound extends RuntimeException {
    public NotFound(String message) {
        super(message);
    }
}