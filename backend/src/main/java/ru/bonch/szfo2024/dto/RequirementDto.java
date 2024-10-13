package ru.bonch.szfo2024.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for individual requirements.
 * Stores details about a specific section and its requirements.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequirementDto {
    String section;      // Section related to the requirement
    String requirement;  // Details of the requirement
}
