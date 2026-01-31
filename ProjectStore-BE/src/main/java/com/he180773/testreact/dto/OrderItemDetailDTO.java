package com.he180773.testreact.dto;

public class OrderItemDetailDTO {
    private String productName;
    private String color;
    private String size;
    private int quantity;
    private String image;
    private Integer productPrice;

    public OrderItemDetailDTO() {
    }

    public OrderItemDetailDTO(String productName, String color, String size, int quantity, String image, Integer productPrice) {
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.image = image;
        this.productPrice = productPrice;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }
}

