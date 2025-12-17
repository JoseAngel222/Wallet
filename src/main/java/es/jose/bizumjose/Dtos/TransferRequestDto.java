package es.jose.bizumjose.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto  {

    @NotNull
    private Long toUserId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @Size(max = 255)
    private String description;
}

