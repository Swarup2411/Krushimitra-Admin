package com.mountrich.krushimitraadminapp.model;

import java.util.List;

public class Order {

    private String orderId;
    private String deliveryAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private long timestamp; // keep for internal use
    private String timestampStr; // readable timestamp for UI
    private double totalAmount;
    private List<OrderItem> items;

    public Order() {}

    public Order(String orderId, String deliveryAddress, String paymentMethod,
                 String paymentStatus, String status, long timestamp,
                 String timestampStr, double totalAmount, List<OrderItem> items) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.timestamp = timestamp;
        this.timestampStr = timestampStr;
        this.totalAmount = totalAmount;
        this.items = items;
    }

//    public Order(String orderId, String deliveryAddress, String paymentMethod, String paymentStatus, String status, long timestamp, List<OrderItem> items) {
//    }

    // --- Getters & Setters ---
    public String getOrderId() { return orderId; }

    public Order(String orderId, String deliveryAddress, String paymentMethod, String paymentStatus, String status, long timestamp, List<OrderItem> items) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.timestamp = timestamp;
        this.items = items;
    }

    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getTimestampStr() { return timestampStr; }
    public double getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems() { return items; }

    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setTimestampStr(String timestampStr) { this.timestampStr = timestampStr; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}