package com.he180773.testreact.controller.admin;


import com.he180773.testreact.dto.ProductDTO;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.ProductVariant;
import com.he180773.testreact.repository.ProductRepository;
import com.he180773.testreact.repository.ProductVariantRepository;
import com.he180773.testreact.service.ProductService;
import com.he180773.testreact.service.SizeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public AdminProductController(ProductService productService, ProductRepository productRepository,
                               ProductVariantRepository productVariantRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @GetMapping("/buddhists")
    public ResponseEntity<Page<Product>> getTop( HttpServletResponse response,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products= productRepository.findAllByCategory("buddhist",pageable);
        if(products.getTotalPages()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/bottoms")
    public ResponseEntity<Page<Product>> getBottom( HttpServletResponse response,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products= productRepository.findAllByCategory("bottom",pageable);
        System.out.println(products);
        if(products.getTotalPages()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/robes")
    public ResponseEntity<Page<Product>> getSet( HttpServletResponse response,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products= productRepository.findAllByCategory("robe",pageable);
        System.out.println(products);
        if(products.getTotalPages()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/accessories")
    public ResponseEntity<Page<Product>> getAccessory( HttpServletResponse response,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products= productRepository.findAllByCategory("accessory",pageable);
        if(products.getTotalPages()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/getAllBuddhists")
    public ResponseEntity<List<Product>> getAllBuddhists( HttpServletResponse response) {


        List<Product> products= productRepository.findAllByCategory("buddhist");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/getAllBottoms")
    public ResponseEntity<List<Product>> getAllBottoms( HttpServletResponse response) {


        List<Product> products= productRepository.findAllByCategory("bottom");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/getAllRobes")
    public ResponseEntity<List<Product>> getAllSets( HttpServletResponse response) {


        List<Product> products= productRepository.findAllByCategory("robe");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/getAllAccessories")
    public ResponseEntity<List<Product>> getAllAccessories( HttpServletResponse response) {


        List<Product> products= productRepository.findAllByCategory("accessory");
        if(products.size()>0) {
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.status(401).body(null);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Product> deleteProduct(HttpServletResponse response, @PathVariable Long id) {
        List<ProductVariant> productVariants = productVariantRepository.findAllByProductId(id);
        if(productVariants.size()>0) {
            for(ProductVariant productVariant : productVariants) {
                productVariantRepository.delete(productVariant);
            }
        }
        productRepository.deleteById(id);
        return ResponseEntity.status(200).body(null);
    }

}
