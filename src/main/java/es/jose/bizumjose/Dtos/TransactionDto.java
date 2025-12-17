package es.jose.bizumjose.Dtos;

import es.jose.bizumjose.Entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {

    private Long id;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionDto from(Transaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setType(tx.getType().name());
        dto.setStatus(tx.getStatus().name());
        dto.setDescription(tx.getDescription());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
