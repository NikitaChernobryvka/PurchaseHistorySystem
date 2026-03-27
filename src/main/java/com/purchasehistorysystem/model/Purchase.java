package com.purchasehistorysystem.model;

import java.time.LocalDate;

public class Purchase {
    private int id;
    private String name;
    private Category category;
    private double price;
    private int amount;
    private LocalDate date;

    public Purchase(int id, String name, Category category, double price, int amount, LocalDate date) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
