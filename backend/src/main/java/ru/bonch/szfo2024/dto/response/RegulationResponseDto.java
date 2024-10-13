package ru.bonch.szfo2024.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for a single regulation response.
 * Contains the name of the regulation and any associated comments.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegulationResponseDto {
    String name;    // Name of the regulation
    String comment; // Comment for uc from regulation
}
