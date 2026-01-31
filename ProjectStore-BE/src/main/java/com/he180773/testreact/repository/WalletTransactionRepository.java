package com.he180773.testreact.repository;

import com.he180773.testreact.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<WalletTransaction> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAt);

    List<WalletTransaction> findByStatus(String status);

}
