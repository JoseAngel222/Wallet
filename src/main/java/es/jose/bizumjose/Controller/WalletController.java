package es.jose.bizumjose.Controller;

import es.jose.bizumjose.Dtos.DepositRequestDto;
import es.jose.bizumjose.Dtos.WalletDto;
import es.jose.bizumjose.Service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WalletDto> myWallet(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(walletService.getWalletByUser(userId));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deposit(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody DepositRequestDto dto
    ) {
        walletService.deposit(userId, dto.getAmount());
        return ResponseEntity.ok().build();
    }
}

