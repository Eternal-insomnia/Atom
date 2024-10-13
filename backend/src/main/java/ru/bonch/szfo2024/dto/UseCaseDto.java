package ru.bonch.szfo2024.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Data Transfer Object for representing a use case.
 * Contains the system name and UC text .
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UseCaseDto {
    List<String> systemNames; // Names of the system
    String text;       // UC text
}
