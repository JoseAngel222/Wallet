package es.jose.bizumjose.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {

    @NotNull
    @Positive
    private BigDecimal amount;
}
