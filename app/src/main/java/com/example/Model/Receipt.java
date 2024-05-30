package com.example.Model;

import java.util.List;

public class Receipt {
    private int ReceiptId;
    private String Name;
    private Long Phone;
    private String Address;
    private OrderStatus OrderStatus;
    private String Key;
    private String EmailOrder;

    public String getEmailOrder() {
        return EmailOrder;
    }

    public void setEmailOrder(String emailOrder) {
        EmailOrder = emailOrder;
    }

    private List<Food> Items;
    public List<Food> getItems() {
        return Items;
    }
    public void setItems(List<Food> items) {
        this.Items = items;
    }

    public OrderStatus getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.OrderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return Name;
    }

    private float Total;

    public int getReceiptId() {
        return ReceiptId;
    }

    public void setReceiptId(int receiptId) {
        ReceiptId = receiptId;
    }

    public Receipt() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getPhone() {
        return Phone;
    }

    public void setPhone(Long phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public float getTotal() {
        return Total;
    }

    public void setTotal(float total) {
        Total = total;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
    public Receipt(int receiptId, String name, Long phone, String address, float total) {
        ReceiptId = receiptId;
        Name = name;
        Phone = phone;
        Address = address;
        Total = total;
    }



}
