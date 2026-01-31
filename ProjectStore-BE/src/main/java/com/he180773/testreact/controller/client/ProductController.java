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
@RequestMapping("/api/products")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final SizeService sizeService;
    private final OrderService orderService;

    public ProductController(ProductService productService, ProductRepository productRepository,
                             SizeService sizeService, OrderService orderService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.sizeService = sizeService;
        this.orderService = orderService;
    }

    @GetMapping("/buddhists")
    public ResponseEntity<List<Product>> getBuddhist( HttpServletResponse response) {
        List<Product> products= productService.findAllByCategory("buddhist");

        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/bottoms")
    public ResponseEntity<List<Product>> getBottom( HttpServletResponse response) {
        List<Product> products= productService.findAllByCategory("bottom");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/robes")
    public ResponseEntity<List<Product>> getRobes( HttpServletResponse response) {
        List<Product> products= productService.findAllByCategory("robe");

        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/accessories")
    public ResponseEntity<List<Product>> getAccessory( HttpServletResponse response) {
        List<Product> products= productService.findAllByCategory("accessory");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts( HttpServletResponse response) {
        List<Product> products= productService.findAll();

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


    @GetMapping("/sort/buddhists")
    public Page<Product> getBuddhistProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "6") int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String name,
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
        return productRepository.findFilteredProducts("buddhist",name, minPrice, maxPrice, pageable);
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

    @GetMapping("/sort/bottoms")
    public Page<Product> getBottomProducts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String priceRange) {
        Pageable pageable;
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
        return productRepository.findFilteredProducts("bottom",name, minPrice, maxPrice, pageable);
    }

    @GetMapping("/sort/robes")
    public Page<Product> getRobeProducts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String priceRange) {
        Pageable pageable;
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
        return productRepository.findFilteredProducts("robe",name, minPrice, maxPrice, pageable);
    }

    @GetMapping("/sort/accessories")
    public Page<Product> getAccessoryProducts(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "6") int size,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String priceRange) {
        Pageable pageable;
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
        return productRepository.findFilteredProducts("accessory",name, minPrice, maxPrice, pageable);
    }

    @GetMapping("/new-arrivals")
    public List<Product> getNewArrivals() {
        return productService.getNewArrivals();
    }

    @GetMapping("/sort/new-arrivals")
    public Page<Product> getNewArrivalsSorted(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "6") int size,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String priceRange) {
        Pageable pageable;
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
        return productService.getNewArrivalsSorted(pageable,name, minPrice, maxPrice);
    }

    @GetMapping("/sort/top-sellers")
    public Page<Product> getTopSellersSorted(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "6") int size,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String priceRange) {

        Pageable pageable;
        switch (sort != null ? sort.toLowerCase() : "") {
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

        return orderService.getTopSellersSorted(pageable, name, minPrice, maxPrice);
    }


    @PostMapping("/add-product")
    public Product addTop(@RequestBody ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setArrivedDate(dto.getArrivedDate());
        product.setThumbnailImage(dto.getThumbnailImage());
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> products = productRepository.findAllByNameContains(query); // hoặc tên khác
        return ResponseEntity.ok(products);
    }

}
