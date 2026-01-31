package com.he180773.testreact.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "[Productvariant]")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "COLOR", length = 50)
    private String color;
    @Column(name = "size", length = 50)
    private String size;
    @Column(name = "price")
    private Integer price;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "image",  columnDefinition = "TEXT")
    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
