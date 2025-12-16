package es.jose.bizumjose.Repository;

import es.jose.bizumjose.Entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long> {
    List<AIRecommendation> findByUserId(Long userId);
}