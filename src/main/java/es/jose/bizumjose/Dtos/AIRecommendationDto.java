package es.jose.bizumjose.Dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AIRecommendationDto {
    private String message;
    private LocalDateTime createdAt;
}