package com.he180773.testreact.service;

import com.he180773.testreact.dto.AddCartItemDTO;
import com.he180773.testreact.dto.CartItemDTO;
import com.he180773.testreact.entity.Cart;
import com.he180773.testreact.entity.CartItem;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.ProductVariant;
import com.he180773.testreact.mapper.JsonMapper;
import com.he180773.testreact.repository.CartItemRepository;
import com.he180773.testreact.repository.CartRepository;
import com.he180773.testreact.repository.ProductRepository;
import com.he180773.testreact.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    public CartItemService(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductVariantRepository productVariantRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);

        List<CartItem> items = cartItemRepository.findByCartId(cart.get().getId());

        for (CartItem item : items) {
            System.out.println("CartItem ID: " + item.getProductVariantId());
        }

        return items.stream().map(item -> {
            ProductVariant variant = productVariantRepository.findById(item.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            System.out.println("Id: "+ variant.getProductId());

            Optional<Product> product = productRepository.findById(variant.getProductId());
            String productName = product.get().getName();
            String color = variant.getColor();
            String size = variant.getSize();

            // Lấy ảnh đầu tiên
            String imageUrl = null;
            List<String> images = JsonMapper.jsonToList(variant.getImage());
            if (variant.getImage() != null && !variant.getImage().isEmpty()) {
                imageUrl = images.get(0); // Giả sử ảnh đầu tiên là ảnh đại diện
            }
            return new CartItemDTO(
                    item.getId(),
                    variant.getId(),
                    productName,
                    color,
                    size,
                    imageUrl,
                    item.getQuantity(),
                    variant.getPrice()
            );
        }).toList();
    }

    @Transactional
    public void addCartItem(AddCartItemDTO request) {
        // 1. Tìm hoặc tạo Cart của user
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(request.getUserId());
                    return cartRepository.save(newCart);
                });

        // 2. Tìm cart item đã tồn tại
        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());

        if (existingItemOpt.isPresent()) {
            // 3. Nếu có rồi → cộng số lượng
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // 4. Nếu chưa có → thêm mới
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductVariantId(request.getProductVariantId());
            newItem.setQuantity(request.getQuantity());
            newItem.setAddedAt(LocalDateTime.now());
            newItem.setLastModifiedAt(LocalDateTime.now());
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void decreaseItem(AddCartItemDTO request) {
        // 1. Tìm hoặc tạo Cart của user
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(request.getUserId());
                    return cartRepository.save(newCart);
                });

        // 2. Tìm cart item đã tồn tại
        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());


            CartItem item = existingItemOpt.orElse(null);
            item.setQuantity(item.getQuantity() - request.getQuantity());
            item.setLastModifiedAt(LocalDateTime.now());
            if (item.getQuantity() <= 0) {
                // Nếu số lượng giảm xuống 0, xóa item
                cartItemRepository.delete(item);
            } else {
                // Cập nhật lại item
                cartItemRepository.save(item);
            }

    }

}