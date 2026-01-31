package com.he180773.testreact.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "[Product]")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "NAME", length = 50)
    private String name;
    @Column(name = "CATEGORY", length = 50)
    private String category;
    @Column(name = "ARRIVED_DATE")
    private LocalDateTime arrivedDate;
    @Column(name = "THUMBNAIL_IMAGE")
    private String thumbnailImage;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "price")
    private Integer price;
    @Column(name = "created_at" , nullable = true)
    private LocalDateTime createdAt;
    @Column(name = "created_by", nullable = true)
    private int createdBy;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getArrivedDate() {
        return arrivedDate;
    }

    public void setArrivedDate(LocalDateTime arrivedDate) {
        this.arrivedDate = arrivedDate;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
