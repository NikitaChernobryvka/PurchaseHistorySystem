package com.purchasehistorysystem.model;

public class Category {
    private int id;
    private String name;
    private String iconPath;
    private int userId;
    private String type;

    public Category(int id, String name, String iconPath, int userId, String type) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.userId = userId;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
