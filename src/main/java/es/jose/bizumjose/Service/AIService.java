package es.jose.bizumjose.Service;

import es.jose.bizumjose.Dtos.AIRecommendationDto;

import java.util.List;

public interface AIService {
    List<AIRecommendationDto> generateRecommendations(Long userId);
}
