package ru.bonch.szfo2024.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for sending a use case and related regulations.
 * Contains the use case and a list of associated requirements.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UcAndRegulationDto {
    String useCase;                       // Description or name of the use case
    List<RequirementDto> regulation = new ArrayList<>(); // List of requirements for the use case
}
