package ru.bonch.szfo2024.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bonch.szfo2024.dto.RequirementDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for split regulation by sections
 * @author Daniil Petrov (GutChoice)
 */
@Service
@RequiredArgsConstructor
public class RegulationItemisationService {

    /**
     * Parses the content of a file to extract points and corresponding requirements.
     * It identifies sections based on specific patterns and stores them as RequirementDto objects.
     *
     * @param content The entire content of the file as a string
     * @return A list of RequirementDto objects extracted from the file
     */
    public List<RequirementDto> itemise(String content) {
        List<RequirementDto> RDs = new ArrayList<>(); // List to store RequirementDto objects

        // Regular expressions for different point formats
        String defaultRegex = "^(\\d+(\\.\\d+)*)(?!\\S)"; // Default regex for points without trailing dots for GOST standard
        String unRegex = "(^\\d+\\.(\\d+\\.)*\\s*)"; // Regex for points with dots (UNITED NATIONS style) for UNITED NATIONS standard
        String regex = defaultRegex; // Start with default regex (We think we have GOST standard while we are not react UNITED NATIONS in text)
        Pattern pattern = Pattern.compile(regex); // Compile the regex pattern

        String previousPoint = "0"; // Initialize previous point
        boolean unitedNationsDone = false; // Flag indicating if "UNITED NATIONS" was found
        boolean pastScope = false; // Flag indicating if we have passed the "Scope" section
        boolean inSection = false; // Flag indicating if we are inside a section

        StringBuilder textSection = new StringBuilder(); // To store the content of the current section

        String[] lines = content.split("\n"); // Split the file content into lines
        for (String line : lines) {
            line = line.trim(); // Remove leading and trailing spaces

            // Check if the line contains "UNITED NATIONS" what means that we have UNITED NATIONS standard
            if (line.contains("UNITED NATIONS")) {
                regex = unRegex; // Switch to regex for points with dots
                pattern = Pattern.compile(regex);
                System.out.println("Found UNITED NATIONS. Switching to point parsing with dots.");
                unitedNationsDone = true;
            }

            // Check for "Annex 1" to stop processing
            if (line.matches("^Annex[\\t ]+1[\\t ]*$")) {
                System.out.println("Found Annex 1. Stopping file processing.");
                break; // Stop processing and exit the loop
            }

            // Search for the "Scope" section
            if (!pastScope) {
                // If we have UNITED NATIONS standard
                if (unitedNationsDone) {
                    if (line.matches("^\\d+\\.\\s+Scope\\s*$")) {
                        System.out.println("Found Scope.");
                        pastScope = true; // Found Scope
                    }
                    // If we have GOST standard
                } else {
                    if (line.matches("^\\d+\\s+Scope\\s*$")) {
                        System.out.println("Found Scope.");
                        pastScope = true; // Found Scope
                    }
                }
            }

            if (!pastScope) {
                continue; // Skip lines until we pass "Scope"
            }

            // Search for points in the line using the regex pattern
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String currentPoint = matcher.group().replaceAll("\\.$", ""); // Remove trailing dot if present
                // If point is valid
                if (isValidNextPoint(previousPoint, currentPoint)) {
                    // Remove the point from the line and proceed
                    line = line.replaceFirst(currentPoint, "").trim();
                    System.out.println("Valid point: " + currentPoint);
                    if (!previousPoint.equals("0")) {
                        // Add the previous point and its text section to the list
                        RDs.add(new RequirementDto(previousPoint, textSection.toString()));
                        textSection = new StringBuilder(); // Reset the section text
                    }
                    inSection = true; // We are now inside a valid section
                    textSection.append(line); // Append the current line to the section
                    previousPoint = currentPoint; // Update previousPoint for the next iteration
                    continue;
                    // If point is invalid
                } else {
                    System.out.println("Invalid point: " + currentPoint + " after " + previousPoint);
                }
            }

            // If inside a section, continue appending lines to the section text
            if (inSection) {
                textSection.append(line);
            }
        }

        return RDs; // Return the list of RequirementDto objects
    }

    /**
     * Removes a trailing dot from a string, if present.
     *
     * @param input The string to process
     * @return The string without a trailing dot
     */
    private static String removeTrailingDot(String input) {
        return input.trim().replaceAll("\\.$", ""); // Remove the dot if it exists
    }

    /**
     * Checks if the current point is a valid next point in the sequence.
     *
     * This method compares the current point to the previous point, ensuring that the sequence is correct
     * based on specific rules, including nested points and top-level point transitions.
     *
     * @param previousPoint The previous valid point in the sequence
     * @param currentPoint The current point being evaluated
     * @return true if the current point is valid, otherwise false
     */
    private static boolean isValidNextPoint(String previousPoint, String currentPoint) {
        previousPoint = removeTrailingDot(previousPoint); // Clean up previous point
        currentPoint = removeTrailingDot(currentPoint); // Clean up current point

        String[] prevParts = previousPoint.split("\\.");
        String[] currParts = currentPoint.split("\\.");

        // Check for top-level point transition (e.g., going from 3.x to 4)
        if (currParts.length == 1 && prevParts.length > 1) {
            return Integer.parseInt(currParts[0]) == Integer.parseInt(prevParts[0]) + 1;
        }

        // Check if the current point is more nested than the previous
        if (currParts.length > prevParts.length) {
            int lastPrevNum = Integer.parseInt(prevParts[prevParts.length - 1]);
            int lastCurrNum = Integer.parseInt(currParts[currParts.length - 1]);

            // Allow deeper nesting (e.g., going from 3.1 to 3.1.1)
            return lastCurrNum == 1 && Integer.parseInt(currParts[currParts.length - 2]) == lastPrevNum;
        }

        // If both points have the same depth, check for valid sequence
        for (int i = 0; i < Math.min(prevParts.length, currParts.length); i++) {
            int prevNum = Integer.parseInt(prevParts[i]);
            int currNum = Integer.parseInt(currParts[i]);

            // Ensure the sequence is incrementing correctly
            if (currNum > prevNum) {
                if (i == prevParts.length - 1 && currNum - (prevNum + 1) <= 1 && currNum - (prevNum + 1) >= 0) {
                    return true; // Valid next point
                }
                if (i < prevParts.length - 1) {
                    return true; // Correct sequence, incrementing within hierarchy
                }
            } else if (currNum == prevNum) {
                // Check if the current point is more nested than the previous
                if (i == prevParts.length - 1 && currParts.length > prevParts.length) {
                    return true; // Valid deeper nesting
                }
            } else {
                return false; // Current number is less than the previous, invalid
            }
        }

        // Check for illegal transition between top-level points (e.g., skipping numbers like 4, 5, etc.)
        if (prevParts.length == 1 && currParts.length == 1 && Integer.parseInt(currentPoint) > Integer.parseInt(previousPoint) + 1) {
            return false;
        }

        return false; // Invalid sequence by default
    }
}

