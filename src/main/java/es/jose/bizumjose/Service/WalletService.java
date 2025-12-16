package es.jose.bizumjose.Service;

import es.jose.bizumjose.Dtos.WalletDto;

import java.math.BigDecimal;

public interface WalletService {
    WalletDto getWalletByUser(Long userId);
    void deposit(Long userId, BigDecimal amount);
}

