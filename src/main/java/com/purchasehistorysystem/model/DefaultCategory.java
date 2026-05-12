package com.purchasehistorysystem.model;

public enum DefaultCategory {
    FOOD("Їжа", "/com/purchasehistorysystem/icons/default/FoodIcon.png"),
    SPORT("Спорт", "/com/purchasehistorysystem/icons/default/SportIcon.png");

    private final String categoryName;
    private final String iconPath;

    DefaultCategory(String categoryName, String iconPath) {
        this.categoryName = categoryName;
        this.iconPath = iconPath;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getIconPath() {
        return iconPath;
    }
}
