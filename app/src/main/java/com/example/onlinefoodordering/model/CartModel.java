package com.example.onlinefoodordering.model;

public class CartModel {
    private String foodName, foodImage;
    private long foodPrice;
    private int quantity; // Isse String ki jagah int kar diya taaki calculation aasaan ho

    public CartModel() { }

    public CartModel(String foodName, long foodPrice, String foodImage, int quantity) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.quantity = quantity;
    }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public long getFoodPrice() { return foodPrice; }
    public void setFoodPrice(long foodPrice) { this.foodPrice = foodPrice; }

    public String getFoodImage() { return foodImage; }
    public void setFoodImage(String foodImage) { this.foodImage = foodImage; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}