package ru.bonch.szfo2024.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service class for determining if two names are related.
 * The class provides methods for checking if two names share common words,
 * have related abbreviations, or exhibit similarity based on string analysis.
 *
 * @author Andrey Kurnosov (GutChoice)
 */
@Service
public class NameRelationService {

    /**
     * Checks if two names are related.
     * @param name1 the first name to be compared
     * @param name2 the second name to be compared
     * @return true if the names are related; false otherwise
     */
    public boolean areRelated(String name1, String name2) {
        // Split the names into individual words
        Set<String> words1 = new HashSet<>(Arrays.asList(name1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(name2.toLowerCase().split("\\s+")));

        // Check for common words between the names
        words1.retainAll(words2);
        if (!words1.isEmpty()) {
            return true; // Return true if there are matching words
        }

        // Check for relation through abbreviations
        if (areAbbreviationsRelated(name1, name2)) {
            return true;
        }

        // Analyze similarity between the two names
        return analyzeSimilarity(name1.toLowerCase(), name2.toLowerCase());
    }

    /**
     * Private method for checking if abbreviations relate the two names.
     * @param str1 the first name to check
     * @param str2 the second name to check
     * @return true if there is an abbreviation-based relationship; false otherwise
     */
    private static boolean areAbbreviationsRelated(String str1, String str2) {
        // Remove spaces and special characters
        str1 = str1.replaceAll("[^a-zA-Z]", "");
        str2 = str2.replaceAll("[^a-zA-Z]", "");

        // Check if str1 is an abbreviation (all uppercase letters)
        boolean isStr1Abbreviation = str1.equals(str1.toUpperCase()) && str1.length() > 1;

        // Check if str2 is an abbreviation
        boolean isStr2Abbreviation = str2.equals(str2.toUpperCase()) && str2.length() > 1;

        // If str1 is an abbreviation, check for matches in str2
        if (isStr1Abbreviation) {
            return checkInitialsInAbbreviation(str1, str2);
        }

        // If str2 is an abbreviation, check for matches in str1
        if (isStr2Abbreviation) {
            return checkInitialsInAbbreviation(str2, str1);
        }

        // If neither condition is met, return false
        return false;
    }

    /**
     * Helper method to check if the initials of words in a phrase are found in an abbreviation.
     * @param abbreviation the abbreviation to check
     * @param phrase the phrase whose initials are to be checked
     * @return true if the initials are found in the abbreviation; false otherwise
     */
    private static boolean checkInitialsInAbbreviation(String abbreviation, String phrase) {
        // Extract the initials from the phrase
        StringBuilder initials = new StringBuilder();
        String[] words = phrase.split("\\s+"); // Split the phrase into individual words

        for (String word : words) {
            if (word.length() > 0) {
                initials.append(word.charAt(0)); // Append the first letter of each word
            }
        }

        // Check if the initials are contained in the abbreviation
        for (char initial : initials.toString().toUpperCase().toCharArray()) {
            if (abbreviation.contains(String.valueOf(initial))) {
                return true; // Return true if a matching letter is found
            }
        }

        return false; // No relation found through abbreviation
    }

    /**
     * Private method to analyze similarity between two strings.
     * @param str1 the first string to compare
     * @param str2 the second string to compare
     * @return true if the strings are similar based on the analysis criteria; false otherwise
     */
    private static boolean analyzeSimilarity(String str1, String str2) {
        // Remove spaces and special characters from both strings
        str1 = str1.replaceAll("[^a-zA-Z]", "");
        str2 = str2.replaceAll("[^a-zA-Z]", "");

        // Compare the lengths of the strings
        if (Math.abs(str1.length() - str2.length()) > 3) {
            return false; // Return false if string lengths differ significantly
        }

        // Check if the beginning of the strings match (for partial similarity)
        String shorter = str1.length() < str2.length() ? str1 : str2;
        String longer = str1.length() < str2.length() ? str2 : str1;

        for (int i = 0; i < shorter.length(); i++) {
            if (shorter.charAt(i) != longer.charAt(i)) {
                return false; // Return false if characters differ
            }
        }

        // Return true if there is similarity based on the first character
        return true;
    }
}
