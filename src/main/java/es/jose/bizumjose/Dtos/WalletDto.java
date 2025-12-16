package es.jose.bizumjose.Dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletDto {
    private Long id;
    private BigDecimal balance;
    private String currency;
}