package ru.bonch.szfo2024.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bonch.szfo2024.dto.RequirementDto;
import ru.bonch.szfo2024.dto.UcAndRegulationDto;
import ru.bonch.szfo2024.dto.UseCaseDto;
import ru.bonch.szfo2024.dto.response.RegulationResponseDto;
import ru.bonch.szfo2024.dto.response.UcResponseDto;
import ru.bonch.szfo2024.service.CheckUCService;
import ru.bonch.szfo2024.service.NameRelationService;
import ru.bonch.szfo2024.service.RegulationItemisationService;
import ru.bonch.szfo2024.service.files.CheckInputFilesService;
import ru.bonch.szfo2024.service.files.DocxReadService;
import ru.bonch.szfo2024.service.files.PdfReadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Andrey Kurnosov (GutChoice)
 */
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class CheckUCController {

    private final CheckUCService checkUCService;
    private final CheckInputFilesService checkInputFilesService;
    private final NameRelationService nameRelationService;
    private final PdfReadService pdfReadService;
    private final DocxReadService docxReadService;
    private final RegulationItemisationService regulationItemisationService;

    /**
     * Handles the upload of Use Case and regulation files.
     *
     * @param useCaseFiles    list of Use Case files
     * @param regulationFiles list of regulation files
     * @return list of responses for each Use Case
     */
    @PostMapping("check-uc")
    public List<UcResponseDto> handleFileUpload(
            @RequestParam("useCases") List<MultipartFile> useCaseFiles,
            @RequestParam("regulations") List<MultipartFile> regulationFiles) {

        // Check input files for extensions
        checkInputFilesService.check(useCaseFiles, regulationFiles);

        // Read regulation files
        Map<String, String> regulationsText = pdfReadService.uploadFiles(regulationFiles);
        // Read use case files
        Map<String, UseCaseDto> useCasesText = docxReadService.getUseCases(useCaseFiles);

        List<UcResponseDto> response = new ArrayList<>();

        // Process each Use Case
        for (var useCase : useCasesText.entrySet()) {
            UcResponseDto ucResponse = new UcResponseDto();
            ucResponse.setFileName(useCase.getKey());
            // Check Use Case against each regulation
            for (var regulationText : regulationsText.entrySet()) {
                // Check relation between Use Case and regulation
                boolean isRelatedWithRegulation = useCase
                        .getValue()
                        .getSystemNames()
                        .stream()
                        .anyMatch(systemName ->
                                nameRelationService.areRelated(systemName, regulationText.getKey())
                        );
                if (isRelatedWithRegulation) {
                    log.debug(useCase.getKey() + " → " + regulationText.getKey());
                    // Break down regulation into individual requirements
                    List<RequirementDto> regulation = regulationItemisationService.itemise(regulationText.getValue());
                    // Prepare data for sending to AI
                    UcAndRegulationDto ucAndRegulationDto = new UcAndRegulationDto(useCase.getValue().getText(), regulation);
                    // Send data to AI and add result to response
                    RegulationResponseDto regulationResponse = new RegulationResponseDto(regulationText.getKey(), checkUCService.sendDataToNeuro(ucAndRegulationDto));
                    log.debug("regulation text -----------------------------------------");
                    log.debug(regulation.toString());
                    log.debug("regulation response -----------------------------------------");
                    log.debug(regulationResponse.toString());
                    ucResponse.getRegulations().add(regulationResponse);
                }
            }
            response.add(ucResponse);
        }

        log.debug("------------------------------------------------------------");
        log.debug(response.toString());
        return response;
    }

}
