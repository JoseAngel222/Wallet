package es.jose.bizumjose.Service.Impl;

import es.jose.bizumjose.Dtos.TransactionDto;
import es.jose.bizumjose.Dtos.TransferRequestDto;
import es.jose.bizumjose.Entity.Transaction;
import es.jose.bizumjose.Entity.TransactionStatus;
import es.jose.bizumjose.Entity.TransactionType;
import es.jose.bizumjose.Entity.Wallet;
import es.jose.bizumjose.Repository.TransactionRepository;
import es.jose.bizumjose.Repository.WalletRepository;
import es.jose.bizumjose.Service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto transfer(Long fromUserId, TransferRequestDto dto) {

        Wallet from = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Wallet origen no encontrada"));

        Wallet to = walletRepository.findByUserId(dto.getToUserId())
                .orElseThrow(() -> new RuntimeException("Wallet destino no encontrada"));

        if (from.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));

        Transaction tx = new Transaction();
        tx.setFromWallet(from);
        tx.setToWallet(to);
        tx.setAmount(dto.getAmount());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setType(TransactionType.TRANSFER);
        tx.setDescription(dto.getDescription());
        tx.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(tx);

        TransactionDto result = new TransactionDto();
        result.setId(tx.getId());
        result.setAmount(tx.getAmount());
        result.setStatus(tx.getStatus().name());
        result.setType(tx.getType().name());
        result.setDescription(tx.getDescription());
        result.setCreatedAt(tx.getCreatedAt());

        return result;
    }

    @Override
    public List<TransactionDto> history(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow();

        return transactionRepository
                .findByFromWalletIdOrToWalletId(wallet.getId(), wallet.getId())
                .stream()
                .map(tx -> {
                    TransactionDto dto = new TransactionDto();
                    dto.setId(tx.getId());
                    dto.setAmount(tx.getAmount());
                    dto.setStatus(tx.getStatus().name());
                    dto.setType(tx.getType().name());
                    dto.setCreatedAt(tx.getCreatedAt());
                    dto.setDescription(tx.getDescription());
                    return dto;
                })
                .toList();
    }
}

