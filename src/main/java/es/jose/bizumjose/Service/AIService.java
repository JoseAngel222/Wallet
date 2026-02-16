package es.jose.bizumjose.Service;

import es.jose.bizumjose.Dtos.AIRecommendationDto;
import java.util.List;

public interface AIService {
    void generateAndSaveRecommendation(Long userId);
    List<AIRecommendationDto> getRecommendationsForUser(Long userId);
}