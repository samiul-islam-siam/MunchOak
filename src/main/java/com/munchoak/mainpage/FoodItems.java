package com.munchoak.mainpage;

import javafx.beans.property.*;

public class FoodItems {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty details;
    private final DoubleProperty price;
    private final StringProperty cuisine;
    private final StringProperty imagePath;
    private final StringProperty category;
    private final IntegerProperty quantity;
    private final StringProperty addOne;
    private final DoubleProperty addOnePrice;
    private final StringProperty addTwo;
    private final DoubleProperty addTwoPrice;


    public FoodItems(int id, String name, String details, double price, String cuisine, String imagePath, String category, int quantity, String addOne, double addOnePrice, String addTwo, double addTwoPrice) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.details = new SimpleStringProperty(details);
        this.price = new SimpleDoubleProperty(price);
        this.cuisine = new SimpleStringProperty(cuisine);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.addOne = new SimpleStringProperty(addOne);
        this.addOnePrice = new SimpleDoubleProperty(addOnePrice);
        this.addTwo = new SimpleStringProperty(addTwo);
        this.addTwoPrice = new SimpleDoubleProperty(addTwoPrice);
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
    public String getCuisine() {
        return cuisine.get();
    }

    public void setCuisine(String cuisine) {
        this.cuisine.set(cuisine);
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

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int q) {
        this.quantity.set(q);
    }

    public String getAddOne() {
        return addOne.get();
    }

    public void setAddOne(String addOne) {
        this.addOne.set(addOne);
    }

    public double getAddOnePrice() {
        return addOnePrice.get();
    }

    public void setAddOnePrice(double addOnePrice) {
        this.addOnePrice.set(addOnePrice);
    }

    public String getAddTwo() {
        return addTwo.get();
    }

    public void setAddTwo(String addTwo) {
        this.addTwo.set(addTwo);
    }

    public double getAddTwoPrice() {
        return addTwoPrice.get();
    }

    public void setAddTwoPrice(double addTwoPrice) {
        this.addTwoPrice.set(addTwoPrice);
    }
}
