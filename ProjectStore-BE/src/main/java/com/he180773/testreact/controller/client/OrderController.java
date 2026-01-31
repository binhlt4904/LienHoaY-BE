package com.he180773.testreact.controller.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.he180773.testreact.dto.*;
import com.he180773.testreact.entity.*;
import com.he180773.testreact.repository.*;
import com.he180773.testreact.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/order")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantController productVariantController;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final WalletRepository walletRepository;


    public OrderController(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                           CartRepository cartRepository, CartItemRepository cartItemRepository,
                           ProductVariantRepository productVariantRepository, ProductVariantController productVariantController,
                           OrderService orderService, ProductRepository productRepository,
                           WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.productVariantController = productVariantController;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    @PostMapping("/handle")
    public ResponseEntity<?> handleOrder(@RequestBody OrderRequest orderRequest) {

        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setPaymentMethod(orderRequest.getPaymentMethod());

        AddressDTO addressDTO = orderRequest.getAddress();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String addressJson = objectMapper.writeValueAsString(addressDTO);
            order.setUserAddress(addressJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // hoặc log lỗi lại
            throw new RuntimeException("Lỗi khi convert addressDTO sang JSON", e);
        }
        order.setOrderDate(orderRequest.getOrderDate());
        order.setStatus("PENDING");
        order.setTotalPrice(orderRequest.getTotalPrice());
        orderRepository.save(order);

        Optional<Cart> cart = cartRepository.findByUserId(orderRequest.getUserId());



        List<OrderItemDTO> orderItems = orderRequest.getItems();

        for (OrderItemDTO orderItemDTO : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setProductVariantId(orderItemDTO.getProductVariantId());
            orderItem.setPrice(orderItemDTO.getPrice());
            orderItemRepository.save(orderItem);

            System.out.println("cartId: "+cart.get().getId());
            System.out.println("pid: "+ orderItemDTO.getProductVariantId());

            cartItemRepository.deleteByCartIdAndProductVariantId(cart.get().getId(), orderItemDTO.getProductVariantId());
        }
        System.out.println("hihihihi");

        return ResponseEntity.ok(order);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getOrder(@CookieValue(value = "token", required = false) String token,
                                      @RequestParam Long userId,
                                      @RequestParam String status) {
        System.out.println("id: "+userId);
        System.out.println("status: "+status);
        List<Order> order= orderService.findOrders(userId, status);
        List<AdminOrderResponseDTO> adminDTO = new ArrayList<>();
        for(Order orderItem : order) {
            AdminOrderResponseDTO adminOrderResponseDTO = new AdminOrderResponseDTO();
            adminOrderResponseDTO.setId(orderItem.getId());
            adminOrderResponseDTO.setStatus(orderItem.getStatus());
            adminOrderResponseDTO.setOrderDate(orderItem.getOrderDate());
            adminOrderResponseDTO.setPaymentMethod(orderItem.getPaymentMethod());
            adminOrderResponseDTO.setUserAddress(orderItem.getUserAddress());
            adminOrderResponseDTO.setTotalAmount(orderItem.getTotalPrice());

            List<OrderItemDetailDTO> orderItemDetailDTO = new ArrayList<>();

            List<OrderItem> orderItems = new ArrayList<>();
            orderItems= orderItemRepository.findByOrderId(orderItem.getId());

            List<ProductVariant> variants = new ArrayList<>();
            for(OrderItem orderItem1 : orderItems) {
                ProductVariant variant = productVariantRepository.findById(orderItem1.getProductVariantId()).get();
                variant.setQuantity(orderItem1.getQuantity());
                variants.add(variant);

            }

            for(ProductVariant productVariant : variants) {
                OrderItemDetailDTO orderItemDetailDTO1 = new OrderItemDetailDTO();
                orderItemDetailDTO1.setColor(productVariant.getColor());
                orderItemDetailDTO1.setQuantity(productVariant.getQuantity());
                orderItemDetailDTO1.setSize(productVariant.getSize());
                orderItemDetailDTO1.setImage(productVariant.getImage());
                Optional<Product> product =  productRepository.findById(productVariant.getProductId());
                orderItemDetailDTO1.setProductName(product.get().getName());
                orderItemDetailDTO1.setProductPrice(product.get().getPrice());
                orderItemDetailDTO.add(orderItemDetailDTO1);
            }
            adminOrderResponseDTO.setItems(orderItemDetailDTO);
            adminDTO.add(adminOrderResponseDTO);

        }

        return ResponseEntity.ok(adminDTO);
        }


    @PostMapping("/check-wallet")
    public ResponseEntity<?> checkWallet(@RequestBody OrderRequest orderRequest) {
        System.out.println(orderRequest.getUserId()+" " + orderRequest.getTotalPrice());
        Long userId = orderRequest.getUserId();
        int totalPrice = orderRequest.getTotalPrice();

        Wallet wallet = walletRepository.findByUserId(userId).orElse(null);
        if (wallet == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "NO_WALLET"));
        }

        if (wallet.getBalance() < totalPrice) {
            return ResponseEntity.ok(Map.of("status", "INSUFFICIENT_FUNDS"));
        }

        wallet.setBalance(wallet.getBalance() - totalPrice);
        walletRepository.save(wallet);

        return ResponseEntity.ok(Map.of("status", "SUFFICIENT"));
    }

    @GetMapping("/get/{userId}") 
    public ResponseEntity<?> getOrder(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findAllByUserIdOrderByOrderDateDesc(userId);
        int count=0;
        for(Order order : orders) {
            if(order.getStatus().equals("DELIVERED") ) {
                count++;
            }
        }
        return ResponseEntity.ok(count);
    }

    @GetMapping("/totalSpent/{userId}")
    public ResponseEntity<?> getTotalSpent(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findAllByUserIdOrderByOrderDateDesc(userId);
        int count=0;
        for(Order order : orders) {
            if(order.getStatus().equals("DELIVERED") ) {
                count+=order.getTotalPrice();
            }
        }
        return ResponseEntity.ok(count);
    }

    @PostMapping("/confirm-received/{orderId}")
    public ResponseEntity<?> confirmReceived(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.setStatus(("DELIVERED"));
        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("status", "DELIVERED"));
    }

}
