package com.mountrich.krushimitraadminapp.model;


public class OrderItem {

    private String productId;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;

    public OrderItem(){}

    public String getProductId() { return productId; }

    public String getName() { return name; }

    public String getImageUrl() { return imageUrl; }

    public double getPrice() { return price; }

    public int getQuantity() { return quantity; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}