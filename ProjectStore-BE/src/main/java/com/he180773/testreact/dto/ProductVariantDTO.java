package com.he180773.testreact.dto;

import jakarta.persistence.Column;

import java.util.List;

public class ProductVariantDTO {
    private Long id;
    private String color;
    private String size;
    private Integer price;
    private Long productId;
    private List<String> images;
    private Integer quantity;

    public ProductVariantDTO( Long id,String color, String size, Integer price,Integer quantity, Long productId, List<String> images) {
        this.color = color;
        this.id = id;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

