package es.jose.bizumjose.Repository;

import es.jose.bizumjose.Entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUserIdForUpdate(Long userId);
}

