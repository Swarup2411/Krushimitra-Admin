package com.mountrich.krushimitraadminapp.model;


public class Product {

    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private int price;
    private int stock;

    public Product() {
        // Required empty constructor
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
}
