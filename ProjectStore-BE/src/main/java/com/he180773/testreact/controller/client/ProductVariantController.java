package com.he180773.testreact.controller.client;

import com.he180773.testreact.dto.ProductVariantDTO;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.ProductVariant;
import com.he180773.testreact.mapper.JsonMapper;
import com.he180773.testreact.repository.ProductRepository;
import com.he180773.testreact.repository.ProductVariantRepository;
import com.he180773.testreact.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productVariant")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class ProductVariantController {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    public ProductVariantController(ProductVariantRepository productVariantRepository, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<ProductVariantDTO>> getTops(HttpServletResponse response, @PathVariable Long id) {

        List<ProductVariant> productVariantss= productVariantRepository.findAllByProductId(id);
        List<ProductVariantDTO> variantDTOs = productVariantss.stream().map(variant -> {
            List<String> images = JsonMapper.jsonToList(variant.getImage());

            return new ProductVariantDTO(
                    variant.getId(),
                    variant.getColor(),
                    variant.getSize(),
                    variant.getPrice(),
                    variant.getQuantity(),
                    variant.getProductId(),
                    images
            );
        }).collect(Collectors.toList());
        if(productVariantss.size()>0) {
            return ResponseEntity.ok(variantDTOs);
        }
        return ResponseEntity.status(401).body(null);
    }



    @PostMapping("/add-new")
    public ResponseEntity<?> addNew(@RequestBody ProductVariantDTO dto
    ) {
        Optional<Product> productOpt = productRepository.findById(dto.getProductId());
        Product product = productOpt.get();
        String images = JsonMapper.listToJson(dto.getImages());

        if (!productOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Sản phẩm không tồn tại");
        }

        ProductVariant variant = new ProductVariant();
        variant.setColor(dto.getColor());
        variant.setSize(dto.getSize());
        variant.setPrice(dto.getPrice());
        variant.setQuantity(dto.getQuantity());
        variant.setImage(images);
        variant.setProductId(productOpt.get().getId());

        productVariantRepository.save(variant);

        // Lấy lại danh sách tất cả các variant để cập nhật giá sản phẩm
        List<ProductVariant> allVariants = productVariantRepository.findAllByProductId(dto.getProductId());


            int minPrice = allVariants.stream()
                    .mapToInt(ProductVariant::getPrice)
                    .min()
                    .orElse(dto.getPrice()); // fallback nếu có lỗi (hiếm)

            product.setPrice(minPrice);
            updateProductQuantity(product.getId());
            productRepository.save(product);


        // === Trả về giá mới để frontend dùng luôn ===
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tạo mẫu thành công");
        response.put("newPrice", minPrice);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/add-existing/{id}")
    public ResponseEntity<?> add(@PathVariable Long id,
                                 @RequestBody ProductVariantDTO dto) {
        ProductVariant productVariant = productVariantRepository.findById(id).get();
        productVariant.setQuantity( dto.getQuantity());
        productVariantRepository.save(productVariant);
        updateProductQuantity(productVariant.getProductId());
        return ResponseEntity.ok("hi");
    }

    @PutMapping("/update-images/{id}")
    public ResponseEntity<?> updateImages(@PathVariable Long id, @RequestBody List<String> images) {
        String imageJson = JsonMapper.listToJson(images);
        List<ProductVariant> variants = productVariantRepository.findAllById(id);
        for(ProductVariant variant : variants) {
            variant.setImage(imageJson);
        }
        productVariantRepository.saveAll(variants);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVariant(@PathVariable Long id, @RequestBody ProductVariantDTO dto) {
        ProductVariant productVariant = productVariantRepository.findById(id).get();
        productVariant.setPrice(dto.getPrice());
        productVariant.setQuantity(dto.getQuantity());
        productVariantRepository.save(productVariant);
        updateProductQuantity(productVariant.getProductId());

        return ResponseEntity.ok().build();
    }


    public void updateProductQuantity(Long id){
        Product product = productRepository.findById(id).get();
        List<ProductVariant> variants = productVariantRepository.findAllByProductId(id);
        int quantity = 0;
        for(ProductVariant variant : variants) {
            quantity += variant.getQuantity();
        }
        product.setTotalQuantity(quantity);
        productRepository.save(product);
    }



}
