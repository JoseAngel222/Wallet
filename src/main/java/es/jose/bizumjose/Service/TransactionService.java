package es.jose.bizumjose.Service;

import es.jose.bizumjose.Dtos.TransactionDto;
import es.jose.bizumjose.Dtos.TransferRequestDto;

import java.util.List;

public interface TransactionService {
    TransactionDto transfer(Long fromUserId, TransferRequestDto dto);
    List<TransactionDto> history(Long userId);
}