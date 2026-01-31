package com.he180773.testreact.dto;

public class RevenueDTO {
    private String orderDay;
    private long totalRevenue;

    public RevenueDTO(String orderDay, long totalRevenue) {
        this.orderDay = orderDay;
        this.totalRevenue = totalRevenue;
    }

    // Getters & setters


    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

