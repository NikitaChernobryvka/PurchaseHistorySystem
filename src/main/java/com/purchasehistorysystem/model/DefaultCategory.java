package com.purchasehistorysystem.model;

public enum DefaultCategory {
    HOUSEHOLD("Побут", "com/purchasehistorysystem/icons/default/Household.png"),
    CLOTHING("Одяг", "com/purchasehistorysystem/icons/default/Clothing.png"),
    ELECTRONICS("Електроніка", "com/purchasehistorysystem/icons/default/Electronics.png"),
    ENTERTAINMENT("Розваги", "com/purchasehistorysystem/icons/default/Entertainment.png"),
    FOOD("Їжа", "com/purchasehistorysystem/icons/default/Food.png"),
    HEALTH("Здоров'я", "com/purchasehistorysystem/icons/default/Health.png"),
    TRANSPORT("Транспорт", "com/purchasehistorysystem/icons/default/Transport.png");

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
