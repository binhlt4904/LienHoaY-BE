package com.he180773.testreact.repository;

import com.he180773.testreact.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductVariantId(Long cartId, Long productVariantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.lastModifiedAt < :threshold")
    void deleteOldItems(@Param("threshold") LocalDateTime threshold);

    void deleteByCartIdAndProductVariantId(Long cartId, Long productVariantId);

}
