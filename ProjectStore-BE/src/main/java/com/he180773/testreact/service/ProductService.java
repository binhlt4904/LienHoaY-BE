package com.he180773.testreact.service;


import com.he180773.testreact.entity.Collection;
import com.he180773.testreact.entity.Product;
import com.he180773.testreact.entity.Size;
import com.he180773.testreact.repository.CollectionRepository;
import com.he180773.testreact.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CollectionRepository collectionRepository;

    public ProductService(ProductRepository productRepository, CollectionRepository collectionRepository) {
        this.productRepository = productRepository;
        this.collectionRepository = collectionRepository;
    }

    public List<Product> findAllByCategory(String category) {
        return productRepository.findAllByCategoryAndInStock(category);
    }

    public List<Product> findAll() {
        return productRepository.findAllInStock();
    }

    public List<Product> findAllByCollection(String collection) {
        Collection c = collectionRepository.findCollectionByName(collection);
        return productRepository.findAllByCollectionId(c.getId());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


    public Page<Product> getProductsSorted(String category, Pageable pageable) {


        Page<Product> products = productRepository.findAllByCategory(category, pageable);
        return products;
    }

    public Page<Product> getNewArrivalsSorted(Pageable pageable,String name, Integer minPrice, Integer maxPrice) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return productRepository.findNewArrivalsSorted(thirtyDaysAgo,name,minPrice,maxPrice,pageable);
    }

    public List<Product> getNewArrivals() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return productRepository.findNewArrivals(thirtyDaysAgo);
    }
}
