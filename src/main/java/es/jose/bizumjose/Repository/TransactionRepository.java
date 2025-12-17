package es.jose.bizumjose.Repository;

import es.jose.bizumjose.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromWalletIdOrToWalletId(
            Long fromWalletId,
            Long toWalletId
    );
    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.fromWallet.user.id = :userId
      AND t.status = 'COMPLETED'
      AND t.createdAt >= :since
""")
    BigDecimal sumTransferredSince(Long userId, LocalDateTime since);
}
