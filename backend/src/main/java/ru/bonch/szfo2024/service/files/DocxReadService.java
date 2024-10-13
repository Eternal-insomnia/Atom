package ru.bonch.szfo2024.service.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bonch.szfo2024.dto.UseCaseDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for reading and processing DOCX files containing Use Cases.
 * @author Andrey Kurnosov (GutChoice)
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class DocxReadService {

    /**
     * Extracts Use Cases from a list of DOCX files.
     * @param files List of MultipartFile objects representing DOCX files
     * @return Map of filenames and use case texts with their system name in UseCaseDto object
     */
    public Map<String, UseCaseDto> getUseCases(List<MultipartFile> files) {
        Map<String, UseCaseDto> filesText = new HashMap<>();

        for (MultipartFile file : files) {
            // Extract regulations and full text from the DOCX file
            UseCaseDto textAndRegulations = extractTextAndRegulationsFromUC(file);
            // Add the filename and corresponding UseCaseDto to the map
            filesText.put(file.getOriginalFilename(), textAndRegulations);
        }

        return filesText;
    }


    /**
     * Extracts regulations and full text from a single Use Case DOCX file.
     * @param file MultipartFile object representing a DOCX file
     * @return List containing the list of regulations and the full text of the Use Case
     */
    private UseCaseDto extractTextAndRegulationsFromUC(MultipartFile file) {
        StringBuilder ucText = new StringBuilder();
        UseCaseDto response = new UseCaseDto();

        try (InputStream inputStream = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(inputStream)) {

            // Read paragraphs from .docx
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                ucText.append(paragraph.getText()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error while reading file: " + file.getOriginalFilename());
            e.printStackTrace();
        }

        // Extract regulations from the text
        List<String> regulations = extractRegulationsUntilDescription(ucText.toString());

        // Add both the list of regulations and the full text to the response
        response.setSystemNames(regulations);      // First item: list of regulations
        response.setText(ucText.toString()); // Second item: full text

        return response; // Return the list containing both regulations and full text
    }

    /**
     * Extracts a list of texts after each slash ('/') in the input string, up until "Description" is encountered.
     * The function returns a list of regulations (strings) found after each slash.
     * @param input The input string to process
     * @return A list of regulations (strings) found after slashes
     */
    private List<String> extractRegulationsUntilDescription(String input) {
        List<String> regulations = new ArrayList<>();
        String[] lines = input.split("\\n");

        for (String line : lines) {
            // Check if the line contains "Description"
            if (line.contains("Description")) {
                break; // Stop processing if "Description" is found
            }

            int slashIndex = line.indexOf('/');
            if (slashIndex != -1) {
                // Extract text after the slash and add it to the list of regulations
                String textAfterSlash = line.substring(slashIndex + 1).trim();
                regulations.add(textAfterSlash);
            }
        }

        return regulations; // Return the list of regulations
    }

}
