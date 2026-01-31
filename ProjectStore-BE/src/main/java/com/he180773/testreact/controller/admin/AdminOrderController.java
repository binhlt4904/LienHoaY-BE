package com.he180773.testreact.controller.admin;

import com.he180773.testreact.controller.client.ProductVariantController;
import com.he180773.testreact.dto.AdminOrderResponseDTO;
import com.he180773.testreact.dto.OrderItemDTO;
import com.he180773.testreact.dto.OrderItemDetailDTO;
import com.he180773.testreact.dto.OrderRequest;
import com.he180773.testreact.entity.*;
import com.he180773.testreact.mapper.JsonMapper;
import com.he180773.testreact.repository.*;
import com.he180773.testreact.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/order")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class AdminOrderController {


    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final ProductVariantController productVariantController;
    private final WalletRepository walletRepository;

    public AdminOrderController(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                                ProductVariantRepository productVariantRepository, ProductRepository productRepository,
                                OrderService orderService, ProductVariantController productVariantController,
                                WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.productVariantController = productVariantController;
        this.walletRepository = walletRepository;
    }

    @GetMapping("/get")
    public ResponseEntity<List<AdminOrderResponseDTO>> getAllOrders(@RequestParam(required = false) String status) {
        List<Order> orders = (status != null && !status.isEmpty())
                ? orderRepository.findByStatus(status)
                : orderRepository.findAll();

        List<AdminOrderResponseDTO> result = orders.stream().map(order -> {
            AdminOrderResponseDTO dto = new AdminOrderResponseDTO();
            dto.setId(order.getId());
            dto.setUserAddress(order.getUserAddress());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getStatus());
            dto.setTotalAmount(order.getTotalPrice());
            dto.setPaymentMethod(order.getPaymentMethod());

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            List<OrderItemDetailDTO> itemDetails = new ArrayList<>();


            for (OrderItem item : items) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId()).orElse(null);
                if (variant != null) {
                    Product product = productRepository.findById(variant.getProductId()).orElse(null);
                    List<String> images = JsonMapper.jsonToList(variant.getImage());
                    System.out.println("images: " + images.get(0));
                    if (product != null) {
                        itemDetails.add(new OrderItemDetailDTO(
                                product.getName(),
                                variant.getColor(),
                                variant.getSize(),
                                item.getQuantity(),
                                images.get(0),
                                item.getPrice()
                        ));
                    }
                }
            }

            dto.setItems(itemDetails);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        AdminOrderResponseDTO dto = new AdminOrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserAddress(order.getUserAddress());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalPrice());
        dto.setPaymentMethod(order.getPaymentMethod());

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDetailDTO> itemDetails = new ArrayList<>();


        for (OrderItem item : items) {
            ProductVariant variant = productVariantRepository.findById(item.getProductVariantId()).orElse(null);
            if (variant != null) {
                Product product = productRepository.findById(variant.getProductId()).orElse(null);
                List<String> images = JsonMapper.jsonToList(variant.getImage());
                System.out.println("images: " + images.get(0));
                if (product != null) {
                    itemDetails.add(new OrderItemDetailDTO(
                            product.getName(),
                            variant.getColor(),
                            variant.getSize(),
                            item.getQuantity(),
                            images.get(0),
                            item.getPrice()
                    ));
                }
            }
        }

        dto.setItems(itemDetails);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{orderId}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable Long orderId) {
        orderService.updateStatus(orderId, "CONFIRMED");
        List<OrderItem> orderItem = orderItemRepository.findByOrderId(orderId);
        for (OrderItem orderItem1 : orderItem) {
            ProductVariant productVariant = productVariantRepository.findById(orderItem1.getProductVariantId()).orElse(null);
            productVariant.setQuantity(productVariant.getQuantity()- orderItem1.getQuantity());
            productVariantRepository.save(productVariant);
            productVariantController.updateProductQuantity(productVariant.getProductId());
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.updateStatus(orderId, "CANCELLED");
        System.out.println("Order ID: " + orderId);
        Order order = orderRepository.findById(orderId).orElse(null);
        Wallet wallet = walletRepository.findByUserId(order.getUserId()).orElse(null);
        System.out.println("Walletaaaa: " + wallet.getId());
        System.out.println(order.getPaymentMethod());
        if(order.getPaymentMethod().equals("wallet")){
            wallet.setBalance(wallet.getBalance()+order.getTotalPrice());
        }
        walletRepository.save(wallet);
        return ResponseEntity.ok(wallet.getBalance());
    }

    @PutMapping("/{orderId}/assign")
    public ResponseEntity<?> assignOrder(@PathVariable Long orderId) {
        orderService.updateStatus(orderId, "SHIPPING");
        return ResponseEntity.ok().build();
    }

}
