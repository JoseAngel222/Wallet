package es.jose.bizumjose.Service.Impl;

import es.jose.bizumjose.Dtos.AIRecommendationDto;
import es.jose.bizumjose.Entity.AIRecommendation;
import es.jose.bizumjose.Entity.Transaction;
import es.jose.bizumjose.Entity.User;
import es.jose.bizumjose.Repository.AIRecommendationRepository;
import es.jose.bizumjose.Repository.TransactionRepository;
import es.jose.bizumjose.Repository.UserRepository;
import es.jose.bizumjose.Service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AIRecommendationRepository recommendationRepository;
    private final OpenAIService openAIService;

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("500");
    private static final BigDecimal MONTHLY_LIMIT = new BigDecimal("3000");
    private static final int RECENT_DAYS = 90; // para recurrentes

    @Override
    @Async  // Para que no bloquee la transferencia
    @Transactional
    public void generateAndSaveRecommendation(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        // 1. Obtener datos relevantes
        List<Transaction> transactions = transactionRepository
                .findByFromWalletUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusDays(RECENT_DAYS));

        // 2. Analizar patrones
        Map<String, Object> analysis = analyzeTransactions(userId, transactions);

        // 3. Construir prompt para OpenAI
        String prompt = buildPrompt(user, analysis);

        // 4. Llamar a OpenAI
        String aiMessage = openAIService.generateRecommendation(prompt);
        if (aiMessage == null) {
            aiMessage = "No se pudo generar una recomendación en este momento.";
        }

        // 5. Guardar en BD
        AIRecommendation recommendation = AIRecommendation.builder()
                .user(user)
                .message(aiMessage)
                .createdAt(LocalDateTime.now())
                .build();
        recommendationRepository.save(recommendation);
    }

    private Map<String, Object> analyzeTransactions(Long userId, List<Transaction> transactions) {
        Map<String, Object> analysis = new HashMap<>();

        // Transferencias recientes (últimos 7 días)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentCount = transactions.stream()
                .filter(t -> t.getCreatedAt().isAfter(weekAgo))
                .count();
        analysis.put("recentCount", recentCount);

        // Destinatarios recurrentes (más de 2 transferencias en 90 días)
        Map<Long, Long> recipientCount = transactions.stream()
                .filter(t -> t.getToWallet() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getToWallet().getUser().getId(),
                        Collectors.counting()
                ));
        List<Long> recurrentRecipients = recipientCount.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        analysis.put("recurrentRecipientsCount", recurrentRecipients.size());
        analysis.put("totalRecipients", recipientCount.size());

        // Porcentaje de transferencias pequeñas (<20€)
        long smallTransfers = transactions.stream()
                .filter(t -> t.getAmount().compareTo(new BigDecimal("20")) < 0)
                .count();
        double smallPercentage = transactions.isEmpty() ? 0 : (smallTransfers * 100.0 / transactions.size());
        analysis.put("smallPercentage", smallPercentage);

        // Uso de límites (último día y mes)
        BigDecimal todaySpent = transactionRepository.sumTransferredSince(userId, LocalDateTime.now().minusDays(1));
        BigDecimal monthSpent = transactionRepository.sumTransferredSince(userId, LocalDateTime.now().minusMonths(1));

        analysis.put("dailyUsed", todaySpent);
        analysis.put("dailyLimit", DAILY_LIMIT);
        analysis.put("monthlyUsed", monthSpent);
        analysis.put("monthlyLimit", MONTHLY_LIMIT);

        // Inactividad
        if (!transactions.isEmpty()) {
            Transaction lastTx = transactions.stream()
                    .max(Comparator.comparing(Transaction::getCreatedAt))
                    .orElse(null);
            if (lastTx != null) {
                long daysSinceLast = java.time.Duration.between(lastTx.getCreatedAt(), LocalDateTime.now()).toDays();
                analysis.put("daysSinceLastTx", daysSinceLast);
            }
        } else {
            analysis.put("daysSinceLastTx", null); // Sin transacciones
        }

        return analysis;
    }

    private String buildPrompt(User user, Map<String, Object> analysis) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente financiero personal. Genera una recomendación breve y útil para el usuario ")
                .append(user.getFullName()).append(" basada en estos datos:\n\n");

        prompt.append("- Transferencias en los últimos 7 días: ").append(analysis.get("recentCount")).append("\n");
        prompt.append("- Destinatarios recurrentes (≥2 veces en 90 días): ").append(analysis.get("recurrentRecipientsCount"))
                .append(" de ").append(analysis.get("totalRecipients")).append("\n");
        prompt.append("- Porcentaje de transferencias pequeñas (<20€): ").append(String.format("%.1f%%", analysis.get("smallPercentage"))).append("\n");
        prompt.append("- Gasto hoy: ").append(analysis.get("dailyUsed")).append("€ de ").append(analysis.get("dailyLimit")).append("€\n");
        prompt.append("- Gasto este mes: ").append(analysis.get("monthlyUsed")).append("€ de ").append(analysis.get("monthlyLimit")).append("€\n");

        if (analysis.get("daysSinceLastTx") != null) {
            prompt.append("- Días desde última transferencia: ").append(analysis.get("daysSinceLastTx")).append("\n");
        } else {
            prompt.append("- No hay transferencias registradas.\n");
        }

        prompt.append("\nBasado en esto, genera una recomendación personalizada (máximo 2 frases). ");
        prompt.append("Puedes sugerir agrupar pagos recurrentes, avisar si se acerca a límites, ");
        prompt.append("recomendar ahorro o alertar sobre inactividad. Sé conciso y amigable.");

        return prompt.toString();
    }

    // Método para obtener las últimas recomendaciones de un usuario
    @Transactional(readOnly = true)
    public List<AIRecommendationDto> getRecommendationsForUser(Long userId) {
        return recommendationRepository.findByUserId(userId).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AIRecommendationDto toDto(AIRecommendation rec) {
        AIRecommendationDto dto = new AIRecommendationDto();
        dto.setMessage(rec.getMessage());
        dto.setCreatedAt(rec.getCreatedAt());
        return dto;
    }

    // Scheduler diario para generar recomendaciones para todos los usuarios
    @Scheduled(cron = "0 0 6 * * ?") // Cada día a las 6 AM
    @Transactional
    public void generateDailyRecommendations() {
        log.info("Generando recomendaciones diarias para todos los usuarios");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            try {
                generateAndSaveRecommendation(user.getId());
            } catch (Exception e) {
                log.error("Error generando recomendación para usuario {}", user.getId(), e);
            }
        }
    }
}