package com.he180773.testreact.controller.client;


import com.he180773.testreact.dto.AddCartItemDTO;
import com.he180773.testreact.dto.CartItemDTO;
import com.he180773.testreact.entity.Cart;
import com.he180773.testreact.repository.CartItemRepository;
import com.he180773.testreact.repository.CartRepository;
import com.he180773.testreact.service.CartItemService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class CartController {


    private final CartItemService cartItemService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;


    public CartController(CartItemService cartItemService, CartItemRepository cartItemRepository,
                          CartRepository cartRepository) {
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/get")
    public ResponseEntity<List<CartItemDTO>> getCartItemsByUser(@RequestParam Long userId) {
        List<CartItemDTO> cartItems = cartItemService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItemToCart(@RequestBody AddCartItemDTO request) {
        cartItemService.addCartItem(request);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @PostMapping("/decrease")
    public ResponseEntity<String> decrease(@RequestBody AddCartItemDTO request) {
        cartItemService.decreaseItem(request);
        return ResponseEntity.ok("Item added to cart successfully");
    }

//
//    @PostMapping("/update")
//    public void updateQuantity(@RequestParam Long userId,
//                               @RequestParam Long productId,
//                               @RequestParam int quantity) {
//        cartService.updateQuantity(userId, productId, quantity);
//    }
//
    @Transactional
    @DeleteMapping("/remove")
    public void removeProduct(@RequestParam  Long userId,
                              @RequestParam  Long productVariantId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        cartItemRepository.deleteByCartIdAndProductVariantId(cart.get().getId(), productVariantId);
    }
//
//    @DeleteMapping("/clear")
//    public void clearCart(@RequestParam Long userId) {
//        cartService.clearCart(userId);
//    }
}
