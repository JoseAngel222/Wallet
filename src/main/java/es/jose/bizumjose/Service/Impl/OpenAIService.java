package es.jose.bizumjose.Service.Impl;

import es.jose.bizumjose.Dtos.OpenAIRequest;
import es.jose.bizumjose.Dtos.OpenAIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate openAiRestTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String generateRecommendation(String prompt) {
        try {
            OpenAIRequest request = new OpenAIRequest();
            request.setModel(model);
            request.setMax_tokens(maxTokens);
            request.setTemperature(temperature);
            request.setMessages(Collections.singletonList(
                    new OpenAIRequest.Message("user", prompt)
            ));

            OpenAIResponse response = openAiRestTemplate.postForObject(apiUrl, request, OpenAIResponse.class);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
        }
        return null;
    }
}