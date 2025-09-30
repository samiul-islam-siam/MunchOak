package com.example.munchoak;

import javafx.beans.property.*;

public class FoodItems {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty details;
    private final DoubleProperty price;
    private final DoubleProperty ratings;
    private final StringProperty imagePath; // NEW

    public FoodItems(int id, String name, String details, double price, double ratings, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.details = new SimpleStringProperty(details);
        this.price = new SimpleDoubleProperty(price);
        this.ratings = new SimpleDoubleProperty(ratings);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getDetails() { return details.get(); }
    public StringProperty detailsProperty() { return details; }
    public void setDetails(String details) { this.details.set(details); }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
    public void setPrice(double price) { this.price.set(price); }

    public double getRatings() { return ratings.get(); }
    public DoubleProperty ratingsProperty() { return ratings; }
    public void setRatings(double ratings) { this.ratings.set(ratings); }

    public String getImagePath() { return imagePath.get(); }
    public StringProperty imagePathProperty() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath.set(imagePath); }
}
