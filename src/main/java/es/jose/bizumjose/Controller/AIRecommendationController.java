package es.jose.bizumjose.Controller;

import es.jose.bizumjose.Dtos.AIRecommendationDto;
import es.jose.bizumjose.Service.Impl.AIRecommendationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationServiceImpl aiRecommendationService;

    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AIRecommendationDto>> getMyRecommendations(
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(aiRecommendationService.getRecommendationsForUser(userId));
    }
}