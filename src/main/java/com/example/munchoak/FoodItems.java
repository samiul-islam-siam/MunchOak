package com.example.munchoak;

import javafx.beans.property.*;

public class FoodItems {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty details;
    private final DoubleProperty price;
    private final DoubleProperty ratings;
    private final StringProperty imagePath;
    private final StringProperty category;

    public FoodItems(int id, String name, String details, double price, double ratings, String imagePath, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.details = new SimpleStringProperty(details);
        this.price = new SimpleDoubleProperty(price);
        this.ratings = new SimpleDoubleProperty(ratings);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.category = new SimpleStringProperty(category);
    }

    // ----- ID -----
    public int getId() {
        return id.get();
    }

    // ----- Name -----
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // ----- Details -----
    public String getDetails() {
        return details.get();
    }

    public void setDetails(String details) {
        this.details.set(details);
    }

    // ----- Price -----
    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    // ----- Ratings -----
    public double getRatings() {
        return ratings.get();
    }

    public void setRatings(double ratings) {
        this.ratings.set(ratings);
    }

    // ----- Image Path -----
    public String getImagePath() {
        return imagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    // ----- Category -----
    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }
}
