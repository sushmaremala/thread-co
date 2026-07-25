package com.example.thread_co;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;
    private String orderId;
    private String userEmail;
    private String itemTitle;
    private String category;
    private double price;
    private LocalDateTime orderTimestamp;

    public OrderDocument() {}

    public OrderDocument(String orderId, String userEmail, String itemTitle, String category, double price) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.itemTitle = itemTitle;
        this.category = category;
        this.price = price;
        this.orderTimestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDateTime getOrderTimestamp() { return orderTimestamp; }
    public void setOrderTimestamp(LocalDateTime orderTimestamp) { this.orderTimestamp = orderTimestamp; }
}
