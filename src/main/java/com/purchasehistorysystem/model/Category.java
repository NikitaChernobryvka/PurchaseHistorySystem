package com.purchasehistorysystem.model;

public class Category {
    private int id;
    private String name;
    private String iconPath;

    public Category(int id, String name, String iconPath) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return name;
    }
}

