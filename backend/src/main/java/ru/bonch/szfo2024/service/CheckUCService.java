package ru.bonch.szfo2024.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.bonch.szfo2024.dto.UcAndRegulationDto;

/**
 * Service for sending Use Case and Regulation data to a neural network for analysis.
 * @author Andrey Kurnosov (GutChoice)
 */
@Service
@RequiredArgsConstructor
public class CheckUCService {
    private final RestTemplate restTemplate;

    @Value("${makson.connection.url}")
    private String maskonUrl;


    /**
     * Sends Use Case and Regulation data to AI for analysis.
     * @param dto UcAndRegulationDto containing the Use Case and Regulation data
     * @return String response from the AI
     */
    public String sendDataToNeuro(UcAndRegulationDto dto) {
        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity with the DTO and headers
        HttpEntity<UcAndRegulationDto> request = new HttpEntity<>(dto, headers);
        // Send POST request to the neural network endpoint

        ResponseEntity<String> response = restTemplate.exchange(
                maskonUrl + "check-uc", // URL for the neural network endpoint
                HttpMethod.POST,        // HTTP method
                request,                // Request entity containing the data
                String.class            // Expected response type
        );
        return response.getBody();
    }


}
