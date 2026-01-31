package com.he180773.testreact.service;

import com.he180773.testreact.dto.RevenueDTO;
import com.he180773.testreact.entity.Order;
import com.he180773.testreact.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RevenueService {

    private final OrderRepository orderRepository;

    public RevenueService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public long getTotalRevenueFromShippedOrders() {
        List<Order> shippedOrders = orderRepository.findByStatus("SHIPPED");

        long total = 0;
        for (Order order : shippedOrders) {
            total += order.getTotalPrice();
        }

        return total;
    }

    public List<RevenueDTO> getRevenueByDay() {
        List<Order> shippedOrders = orderRepository.findByStatus("SHIPPED");

        Map<LocalDate, Long> revenueByDay = new TreeMap<>();
        for (Order order : shippedOrders) {
            LocalDate date = order.getOrderDate().toLocalDate();
            revenueByDay.put(date,
                    revenueByDay.getOrDefault(date, 0L) + order.getTotalPrice()
            );
        }

        // Convert to list of DTOs
        List<RevenueDTO> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : revenueByDay.entrySet()) {
            result.add(new RevenueDTO(entry.getKey().toString(), entry.getValue()));
        }

        return result;
    }

    public List<RevenueDTO> getRevenueFiltered(Integer year, Integer month, Integer day) {
        List<Order> allOrders = orderRepository.findByStatus("DELIVERED");

        // Tạo map để lưu doanh thu theo từng ngày
        Map<LocalDate, Long> revenueMap = new TreeMap<>();

        for (Order order : allOrders) {
            LocalDate date = order.getOrderDate().toLocalDate();

            // Bỏ qua đơn nếu không khớp điều kiện lọc
            if (year != null && date.getYear() != year) continue;
            if (month != null && date.getMonthValue() != month) continue;
            if (day != null && date.getDayOfMonth() != day) continue;

            // Cộng dồn doanh thu theo ngày
            revenueMap.put(date, revenueMap.getOrDefault(date, 0L) + order.getTotalPrice());
        }

        // Chuyển map thành list DTO
        List<RevenueDTO> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : revenueMap.entrySet()) {
            String dateStr = entry.getKey().toString(); // yyyy-MM-dd,zzzzzzzzzzzzzzzzzzzzzzzzzz
            long revenue = entry.getValue();
            result.add(new RevenueDTO(dateStr, revenue));
        }

        return result;
    }

}
