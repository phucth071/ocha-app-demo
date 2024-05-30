package com.example.Model;

import java.io.Serializable;

public class Food implements Serializable {
    private int CategoryID;
    private boolean BestFood;
    private String Description;
    private int ID;
    private String ImagePath;
    private int LocationID;
    private double Price;
    private int TimeID;
    private int TimeValue;
    private double Star;
    private String Title;
    private int numberInCart;
    private String Key;
    private int Quantity;
    public String getKey() {
        return Key;
    }

    public int getQuantity() {
        return Quantity;
    }

    public Food(String title, int quantity) {
        Title = title;
        Quantity = quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Food(){}


    @Override
    public String toString() {
        return Title;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public boolean isBestFood() {
        return BestFood;
    }

    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getTimeID() {
        return TimeID;
    }

    public void setTimeID(int timeID) {
        TimeID = timeID;
    }

    public int getTimeValue() {
        return TimeValue;
    }

    public void setTimeValue(int timeValue) {
        TimeValue = timeValue;
    }

    public double getStar() {
        return Star;
    }

    public void setStar(double star) {
        Star = star;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }


}
