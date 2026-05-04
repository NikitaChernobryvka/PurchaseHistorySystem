package com.purchasehistorysystem.model;

public enum DefaultCategoryModel {
    FOOD("Їжа", "/com/purchasehistorysystem/icons/default/FoodIcon.png"),
    SPORT("Спорт", "/com/purchasehistorysystem/icons/default/SportIcon.png"),
    DELETED("Видалено", "/com/purchasehistorysystem/icons/default/DeletedIcon.png");

    private final String categoryName;
    private final String iconPath;

    DefaultCategoryModel(String categoryName, String iconPath) {
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
