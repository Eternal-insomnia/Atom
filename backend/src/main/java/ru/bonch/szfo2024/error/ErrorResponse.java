package ru.bonch.szfo2024.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Represents a standardized error response.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    String error; // The type or name of the error
    String description; // A detailed description of the error
}