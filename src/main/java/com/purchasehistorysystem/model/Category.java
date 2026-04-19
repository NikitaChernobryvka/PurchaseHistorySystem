package com.purchasehistorysystem.model;

public class Category {
    private int id;
    private String name;
    private String iconPath;
    private int userId;

    public Category(int id, String name, String iconPath, int userId) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public int getUserId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return name;
    }
}

