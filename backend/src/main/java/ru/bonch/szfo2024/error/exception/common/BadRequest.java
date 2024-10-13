package ru.bonch.szfo2024.error.exception.common;

/**
 * @author Andrey Kurnosov (GutChoice)
 */
public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}