package es.jose.bizumjose.Controller;

import es.jose.bizumjose.Dtos.TransactionDto;
import es.jose.bizumjose.Dtos.TransferRequestDto;
import es.jose.bizumjose.Service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDto> transfer(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody TransferRequestDto dto
    ) {
        return ResponseEntity.ok(
                transactionService.transfer(userId, dto)
        );
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionDto>> history(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(transactionService.history(userId));
    }
}

