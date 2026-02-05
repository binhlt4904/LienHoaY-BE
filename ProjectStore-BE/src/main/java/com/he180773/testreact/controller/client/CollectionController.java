package com.he180773.testreact.controller.client;

import com.he180773.testreact.dto.ProductDTO;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.repository.ProductRepository;
import com.he180773.testreact.service.OrderService;
import com.he180773.testreact.service.ProductService;
import com.he180773.testreact.service.SizeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final SizeService sizeService;
    private final OrderService orderService;

    public CollectionController(ProductService productService, ProductRepository productRepository,
                             SizeService sizeService, OrderService orderService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.sizeService = sizeService;
        this.orderService = orderService;
    }



    @GetMapping("/sac-xuan")
    public ResponseEntity<List<Product>> getSacXuanCollection( HttpServletResponse response) {
        List<Product> products= productService.findAllByCollection("Sac Xuan");

        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }



    @GetMapping("/sort")
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "6") int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) String priceRange) {
        Pageable pageable ;

        switch (sort.toLowerCase()) {
            case "price-asc":
                pageable = PageRequest.of(page, size, Sort.by("price").ascending());
                break;
            case "price-desc":
                pageable = PageRequest.of(page, size, Sort.by("price").descending());
                break;
            case "name-asc":
                pageable = PageRequest.of(page, size, Sort.by("name").ascending());
                break;
            case "name-desc":
                pageable = PageRequest.of(page, size, Sort.by("name").descending());
                break;
            default:
                pageable = PageRequest.of(page, size);
                break;
        }
        Integer minPrice = null;
        Integer maxPrice = null;

        if (priceRange != null && priceRange.contains("-")) {
            String[] parts = priceRange.split("-");
            try {
                minPrice = Integer.parseInt(parts[0]);
                maxPrice = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        return productRepository.findFilteredProducts(category,name, minPrice, maxPrice, pageable);
    }




}
