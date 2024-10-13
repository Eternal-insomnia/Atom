package ru.bonch.szfo2024.service.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bonch.szfo2024.error.exception.common.BadRequest;

import java.util.List;

/**
 * Service for checking input files before processing.
 * @author Andrey Kurnosov (GutChoice)
 */
@Service
@Slf4j
public class CheckInputFilesService {

    /**
     * Checks the validity of input files for Use Cases and Regulations.
     * @param useCaseFiles List of Use Case files (expected to be DOCX)
     * @param regulationFiles List of Regulation files (expected to be PDF)
     * @throws BadRequest if files are missing or have incorrect format
     */
    public void check(List<MultipartFile> useCaseFiles, List<MultipartFile> regulationFiles) {

        // Check if Use Case files are present
        if (useCaseFiles == null || useCaseFiles.isEmpty()) {
            throw new BadRequest("Files DOCX (useCases) are required!");
        }

        // Check if Regulation files are present
        if (regulationFiles == null || regulationFiles.isEmpty()) {
            throw new BadRequest("Files PDF (regulations) are required!");
        }

        // Process and validate content type of Use Case files
        for (MultipartFile file : useCaseFiles) {
            if (file == null || file.getContentType() == null ||
                    !file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                throw new BadRequest("File " + file.getOriginalFilename() + " is not in DOCX format!");
            }
        }

        // Process and validate content type of Regulation files
        for (MultipartFile file : regulationFiles) {
            if (file == null || file.getContentType() == null ||
                    !file.getContentType().equals("application/pdf")) {
                throw new BadRequest("File " + file.getOriginalFilename() + " is not in PDF format!");
            }
        }
    }
}