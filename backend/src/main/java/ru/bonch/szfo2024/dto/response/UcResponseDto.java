package ru.bonch.szfo2024.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for use case response data.
 * Represents a response containing regulations requirements for uc file.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UcResponseDto {
    String fileName; // Name of the use case file
    List<RegulationResponseDto> regulations = new ArrayList<>(); // List of regulations associated with the uc
}