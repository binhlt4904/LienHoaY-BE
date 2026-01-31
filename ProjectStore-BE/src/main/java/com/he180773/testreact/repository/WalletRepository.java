package com.he180773.testreact.repository;

import com.he180773.testreact.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    Optional<Wallet> findById(Long walletId);
}
