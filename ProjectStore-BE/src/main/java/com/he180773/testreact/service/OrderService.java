package com.he180773.testreact.service;


import com.he180773.testreact.entity.Order;
import com.he180773.testreact.entity.OrderItem;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.ProductVariant;
import com.he180773.testreact.repository.OrderItemRepository;
import com.he180773.testreact.repository.OrderRepository;
import com.he180773.testreact.repository.ProductRepository;
import com.he180773.testreact.repository.ProductVariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }
    public void updateStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    public List<Order> findOrders(Long userId, String status) {
        if(status == null|| status.equals("")){
            return orderRepository.findAllByUserIdOrderByOrderDateDesc(userId);
        } else{
            return orderRepository.findAllByUserIdAndStatusOrderByOrderDateDesc(userId, status);
        }

    }

    public Page<Product> getTopSellersSorted(Pageable pageable, String name, Integer minPrice, Integer maxPrice) {
        List<Order> shippedOrders = orderRepository.findByStatus("DELIVERED");

        Map<Product, Integer> productQuantityMap = new HashMap<>();

        for (Order order : shippedOrders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            System.out.println("orderItems: " + orderItems);
            for (OrderItem item : orderItems) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId()).orElse(null);
                System.out.println("variant: " + variant.getId());

                Product product = productRepository.findById(variant.getProductId()).orElse(null);
                System.out.println("product: " + product.getId() + " - " + product.getName());


                int quantity = item.getQuantity();
                productQuantityMap.put(product,
                        productQuantityMap.getOrDefault(product, 0) + quantity);
            }
        }

        // Lọc theo tên và giá
        List<Product> filtered = productQuantityMap.keySet().stream()
                .filter(p -> {
                    boolean matchName = (name == null || p.getName().toLowerCase().contains(name.toLowerCase()));
                    boolean matchPrice = (minPrice == null || p.getPrice() >= minPrice)
                            && (maxPrice == null || p.getPrice() <= maxPrice);
                    return matchName && matchPrice;
                })
                .collect(Collectors.toList());
        System.out.println("filtered: " + filtered);

        // Sort theo pageable (áp dụng sort fields)
        Comparator<Product> comparator = Comparator.comparing(Product::getId); // mặc định

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                if (order.getProperty().equalsIgnoreCase("price")) {
                    comparator = Comparator.comparing(Product::getPrice);
                } else if (order.getProperty().equalsIgnoreCase("name")) {
                    comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
                }

                if (order.getDirection() == Sort.Direction.DESC) {
                    comparator = comparator.reversed();
                }
            }
        }

        List<Product> sorted = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        System.out.println("sorted: " + sorted);

        // Paging thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<Product> pageContent = (start >= sorted.size()) ? Collections.emptyList() : sorted.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sorted.size());
    }

}
