package ru.bonch.szfo2024.service.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for reading and processing PDF files containing regulations.
 * @author Andrey Kurnosov (GutChoice)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PdfReadService {
    private final RestTemplate restTemplate;


    /**
     * Processes a list of PDF files and extracts their content.
     * @param files List of MultipartFile objects representing PDF files
     * @return Map of regulation names to their extracted content
     */
    public Map<String, String> uploadFiles(List<MultipartFile> files) {
        Map<String, String> filesText = new HashMap<String, String>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            // Remove suffix "_ENG.pdf" or "_EN.pdf" using regex
            String baseName = fileName.replaceAll("_[A-Z]{2,3}\\.pdf$", "");
            String regulationName = baseName.replace("_", " ");

            filesText.put(regulationName, uploadFile(file));
        }
        return filesText;
    }

    /**
     * Processes a single PDF file and extracts its content with specific formatting rules.
     * @param file MultipartFile object representing a PDF file
     * @return Extracted and formatted content of the PDF file
     */
    private String uploadFile(MultipartFile file) {

        StringBuilder text = new StringBuilder(); // Для хранения итогового текста

        /// Regular expressions for removing specific lines
        String[] patternsToRemove = {
                ".*This document has been prepared.*",  // Remove line containing "This document has been prepared"
                ".*Internal Use Only.*",                // Remove line containing "Internal Use Only"
                ".*UNECE.*",                            // Remove line containing "UNECE"
                ".*ГОСТ.*"                              // Remove line containing "ГОСТ"
        };

        // Regular expression to find the first Roman numeral
        String romanNumerals = "\\b[IVXLCDM]+\\b";  // Roman numerals I, V, X, L, C, D, M

        // Regular expression to find the first Arabic number
        String arabicNumbers = "\\b\\d+\\b";        // Sequence of one or more digits

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            int numberOfPages = document.getNumberOfPages();

            // Process each page of the PDF file
            for (int pageNum = 1; pageNum <= numberOfPages; pageNum++) {
                stripper.setStartPage(pageNum);
                stripper.setEndPage(pageNum);
                String pageText = stripper.getText(document);

                // If the page text is not empty, continue processing
                if (pageText != null && !pageText.isEmpty()) {
                    // Remove lines matching the patterns
                    for (String pattern : patternsToRemove) {
                        pageText = pageText.replaceFirst(pattern, ""); // Remove only the first occurrence
                    }

                    // Remove the first encountered Roman numeral
                    pageText = pageText.replaceFirst(romanNumerals, "");

                    // Remove the first encountered Arabic number (sequence of digits)
                    pageText = pageText.replaceFirst(arabicNumbers, "");

                    // Add the processed text to the overall result
                    text.append(pageText.trim()).append("\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
