package com.he180773.testreact.repository;

import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findAllByProductId(Long productId);

    List<ProductVariant> findAllById(Long id);

    Optional<ProductVariant> findById(long id);
}
