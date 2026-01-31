package com.he180773.testreact.dto;


public class CartItemDTO {
    private Long id;
    private Long productVariantId;
    private String productName;
    private String color;
    private String size;
    private String imageUrl;
    private int quantity;
    private double price;        // đơn giá của variant
    private double totalPrice;   // price * quantity

    public CartItemDTO() {}

    public CartItemDTO(Long id, Long productVariantId, String productName,
                       String color, String size, String imageUrl,
                       int quantity, double price) {
        this.id = id;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price * quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
