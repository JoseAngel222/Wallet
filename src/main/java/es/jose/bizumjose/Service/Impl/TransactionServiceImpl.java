package es.jose.bizumjose.Service.Impl;

import es.jose.bizumjose.Dtos.TransactionDto;
import es.jose.bizumjose.Dtos.TransferRequestDto;
import es.jose.bizumjose.Entity.Transaction;
import es.jose.bizumjose.Entity.TransactionStatus;
import es.jose.bizumjose.Entity.TransactionType;
import es.jose.bizumjose.Entity.Wallet;
import es.jose.bizumjose.Exception.BadRequestException;
import es.jose.bizumjose.Exception.ResourceNotFoundException;
import es.jose.bizumjose.Repository.TransactionRepository;
import es.jose.bizumjose.Repository.WalletRepository;
import es.jose.bizumjose.Service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("500");
    private static final BigDecimal MONTHLY_LIMIT = new BigDecimal("3000");

    private void checkLimits(Long userId, BigDecimal amount) {

        BigDecimal today = transactionRepository.sumTransferredSince(
                userId, LocalDateTime.now().minusDays(1)
        );

        BigDecimal month = transactionRepository.sumTransferredSince(
                userId, LocalDateTime.now().minusMonths(1)
        );

        if (today.add(amount).compareTo(DAILY_LIMIT) > 0) {
            throw new BadRequestException("Daily transfer limit exceeded");
        }

        if (month.add(amount).compareTo(MONTHLY_LIMIT) > 0) {
            throw new BadRequestException("Monthly transfer limit exceeded");
        }
    }


    @Override
    public TransactionDto transfer(Long fromUserId, TransferRequestDto dto) {

        if (fromUserId.equals(dto.getToUserId())) {
            throw new BadRequestException("Cannot transfer to yourself");
        }

        Wallet from = walletRepository.findByUserIdForUpdate(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));

        Wallet to = walletRepository.findByUserIdForUpdate(dto.getToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Target wallet not found"));

        Transaction tx = Transaction.builder()
                .fromWallet(from)
                .toWallet(to)
                .amount(dto.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        try {
            if (from.getBalance().compareTo(dto.getAmount()) < 0) {
                throw new BadRequestException("Insufficient balance");
            }

            from.setBalance(from.getBalance().subtract(dto.getAmount()));
            to.setBalance(to.getBalance().add(dto.getAmount()));

            tx.setStatus(TransactionStatus.COMPLETED);

        } catch (RuntimeException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            throw ex;
        }

        return TransactionDto.from(tx);
    }


    @Override
    public List<TransactionDto> history(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        return transactionRepository
                .findByFromWalletIdOrToWalletId(wallet.getId(), wallet.getId())
                .stream()
                .map(TransactionDto::from)
                .toList();
    }
}
