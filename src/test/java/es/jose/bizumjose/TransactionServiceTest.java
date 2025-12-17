package es.jose.bizumjose;

import es.jose.bizumjose.Dtos.TransactionDto;
import es.jose.bizumjose.Dtos.TransferRequestDto;
import es.jose.bizumjose.Entity.TransactionStatus;
import es.jose.bizumjose.Entity.Wallet;
import es.jose.bizumjose.Exception.BadRequestException;
import es.jose.bizumjose.Repository.TransactionRepository;
import es.jose.bizumjose.Repository.WalletRepository;
import es.jose.bizumjose.Service.Impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    WalletRepository walletRepository;
    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl service;

    @Test
    void transfer_success() {
        Wallet from = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(100))
                .currency("EUR")
                .build();

        Wallet to = Wallet.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(50))
                .currency("EUR")
                .build();

        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(walletRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        TransferRequestDto dto = new TransferRequestDto(
                2L, BigDecimal.valueOf(30), "test"
        );

        TransactionDto result = service.transfer(1L, dto);

        assertEquals(BigDecimal.valueOf(70), from.getBalance());
        assertEquals(BigDecimal.valueOf(80), to.getBalance());
        assertEquals(TransactionStatus.COMPLETED.name(), result.getStatus());
    }



    @Test
    void transfer_insufficient_balance() {
        Wallet from = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(10))
                .currency("EUR")
                .build();

        Wallet to = Wallet.builder()
                .id(2L)
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .build();

        when(walletRepository.findByUserIdForUpdate(1L))
                .thenReturn(Optional.of(from));
        when(walletRepository.findByUserIdForUpdate(2L))
                .thenReturn(Optional.of(to));

        TransferRequestDto dto = new TransferRequestDto(
                2L, BigDecimal.valueOf(50), "fail"
        );

        assertThrows(
                BadRequestException.class,
                () -> service.transfer(1L, dto)
        );
    }


}
