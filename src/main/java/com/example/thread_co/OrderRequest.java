package com.example.thread_co;

import java.io.Serializable;

public class OrderRequest implements Serializable {
    private String orderId;
    private String category;
    private String itemTitle;
    private double price;
    private String userEmail;

    public OrderRequest() {}

    public OrderRequest(String orderId, String category, String itemTitle, double price, String userEmail) {
        this.orderId = orderId;
        this.category = category;
        this.itemTitle = itemTitle;
        this.price = price;
        this.userEmail = userEmail;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}