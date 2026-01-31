package com.he180773.testreact.dto;

import java.time.LocalDateTime;

public class ProductDTO {

    private Long id;
    private String name;
    private String category;
    private LocalDateTime arrivedDate;
    private String thumbnailImage;
    private int totalQuantity;
    private Integer price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getArrivedDate() {
        return arrivedDate;
    }

    public void setArrivedDate(LocalDateTime arrivedDate) {
        this.arrivedDate = arrivedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
