package com.example.onlinefoodordering.model;

public class DiningModel {

    private String restaurantName;
    private String restaurantAddress;
    private String cuisines;
    private String openingHours;
    private int restaurantImage;
    private double diningRating;
    private String diningReviewCount;
    private double deliveryRating;
    private String deliveryReviewCount;
    private String category;
    private int restaurantPrice;
    private String description;

    public DiningModel(String restaurantName, String restaurantAddress, String cuisines, String openingHours, int restaurantImage, double diningRating, String diningReviewCount, double deliveryRating, String deliveryReviewCount, String category, String description) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.cuisines = cuisines;
        this.openingHours = openingHours;
        this.restaurantImage = restaurantImage;
        this.diningRating = diningRating;
        this.diningReviewCount = diningReviewCount;
        this.deliveryRating = deliveryRating;
        this.deliveryReviewCount = deliveryReviewCount;
        this.category = category;
        this.description = description;
    }

    public DiningModel(String restaurantName, String restaurantAddress, double diningRating, int restaurantImage, int restaurantPrice, String category, String description) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.diningRating = diningRating;
        this.restaurantImage = restaurantImage;
        this.restaurantPrice = restaurantPrice;
        this.category = category;
        this.description = description;
        this.cuisines = ""; // Default value
        this.openingHours = ""; // Default value
        this.diningReviewCount = "0"; // Default value
        this.deliveryRating = 0.0; // Default value
        this.deliveryReviewCount = "0"; // Default value
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getCuisines() {
        return cuisines;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public int getRestaurantImage() {
        return restaurantImage;
    }

    public double getDiningRating() {
        return diningRating;
    }

    public String getDiningReviewCount() {
        return diningReviewCount;
    }

    public double getDeliveryRating() {
        return deliveryRating;
    }

    public String getDeliveryReviewCount() {
        return deliveryReviewCount;
    }

    public String getCategory() {
        return category;
    }

    public int getRestaurantPrice() {
        return restaurantPrice;
    }

    public String getDescription() {
        return description;
    }
}
