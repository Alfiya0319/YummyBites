package com.example.onlinefoodordering.model;

public class OrderModel {
    // Variable names wahi rakhein jo Firebase mein save ho rahe hain
    String orderId, totalAmount, status, address, paymentMethod, foodImage, foodNames, orderDate, userId;

    public OrderModel() {}

    public OrderModel(String orderId, String totalAmount, String status, String address, String paymentMethod, String foodImage, String foodNames, String orderDate, String userId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.foodImage = foodImage;
        this.foodNames = foodNames;
        this.orderDate = orderDate;
        this.userId = userId;
    }

    // Getters (Adapter inhi se data uthayega)
    public String getOrderId() { return orderId; }
    public String getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getFoodImage() { return foodImage; }
    public String getFoodNames() { return foodNames; }
    public String getOrderDate() { return orderDate; }
    public String getUserId() { return userId; }
}