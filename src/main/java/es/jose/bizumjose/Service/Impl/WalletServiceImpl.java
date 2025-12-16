package es.jose.bizumjose.Service.Impl;

import es.jose.bizumjose.Dtos.WalletDto;
import es.jose.bizumjose.Entity.Wallet;
import es.jose.bizumjose.Repository.WalletRepository;
import es.jose.bizumjose.Service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletDto getWalletByUser(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet no encontrada"));

        WalletDto dto = new WalletDto();
        dto.setId(wallet.getId());
        dto.setBalance(wallet.getBalance());
        dto.setCurrency(wallet.getCurrency());
        return dto;
    }

    @Override
    public void deposit(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet no encontrada"));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }
}

