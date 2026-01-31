package com.he180773.testreact.service;

import com.he180773.testreact.repository.CartItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CartItemCleanUpScheduler {

    private final CartItemRepository cartItemRepository;

    public CartItemCleanUpScheduler(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?") // chạy mỗi ngày lúc 2h sáng
    public void cleanOldCartItems() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        cartItemRepository.deleteOldItems(threshold);
        System.out.println("Đã xóa các cartItem cũ hơn 30 ngày.");
    }
}

