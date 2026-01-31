package com.he180773.testreact.repository;


import com.he180773.testreact.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findAllByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findAllByUserIdAndStatusOrderByOrderDateDesc(Long id, String status);
}