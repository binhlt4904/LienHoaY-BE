package com.he180773.testreact.dto;

public class AddCartItemDTO {
    private Long userId;
    private Long productVariantId;
    private int quantity;

    public AddCartItemDTO() {}

    public AddCartItemDTO(Long userId, Long productVariantId, int quantity) {
        this.userId = userId;
        this.productVariantId = productVariantId;
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
