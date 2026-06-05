package com.example.onlinefoodordering.model;

public class FoodModel {
    private String foodName, foodImage, resName, category;
    private long foodPrice;

    public FoodModel() { }

    public FoodModel(String foodName, String foodImage, String resName, long foodPrice, String category) {
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.resName = resName;
        this.foodPrice = foodPrice;
        this.category = category;
    }

    public String getFoodName() { return foodName; }
    public String getFoodImage() { return foodImage; }
    public String getResName() { return resName; }
    public long getFoodPrice() { return foodPrice; }
    public String getCategory() { return category; }
}