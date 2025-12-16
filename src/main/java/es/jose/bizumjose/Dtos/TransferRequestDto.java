package es.jose.bizumjose.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto  {

    @NotNull
    private Long toUserId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @Size(max = 255)
    private String description;
}

