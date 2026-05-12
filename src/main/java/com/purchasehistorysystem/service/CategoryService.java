package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.DefaultCategory;
import com.purchasehistorysystem.repository.CategoryRepository;
import com.purchasehistorysystem.util.UserSessionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getCustomCategories() throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();
        return categoryRepository.getAllCategories(userId);
    }

    public List<Category> getCategoriesForSelection() throws SQLException {
        List<Category> categories = getCustomCategories();
        List<Category> filteredCategories = new ArrayList<>();

        for (Category category : categories) {
            if (!category.getName().equalsIgnoreCase("Всі")) {
                filteredCategories.add(category);
            }
        }

        return filteredCategories;
    }

    public void getDefaultCategories(int userId) throws SQLException {
        if (requiredDefaultCategoriesExist(userId)) {
            return;
        }

        for (DefaultCategory defaultCategory : DefaultCategory.values()) {
            Category newCategory = new Category(0, defaultCategory.getCategoryName(),
                    defaultCategory.getIconPath(), userId);

            categoryRepository.addCategory(newCategory, userId);
        }
    }

    public void addCustomCategory(String name, String iconName) throws SQLException, IllegalArgumentException {
        int userId = UserSessionUtils.getCurrentUser().getId();

        if (requiredCustomCategoryExist(name, userId)) {
            throw new IllegalArgumentException("Така категорія вже існує");
        }

        String iconPathDatabase = "/com/purchasehistorysystem/icons/custom/user_" + userId + "/" + iconName;

        Category category = new Category(0, name, iconPathDatabase, userId);
        categoryRepository.addCategory(category, userId);
    }

    public void deleteCategory(int oldCategoryId, Integer newCategoryId) throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();
        categoryRepository.deleteCategory(oldCategoryId, newCategoryId, userId);
    }

    private boolean requiredDefaultCategoriesExist(int userId) throws SQLException {
        List<Category> categoryList = categoryRepository.getAllCategories(userId);

        for (Category category : categoryList) {
            if (!category.getName().equalsIgnoreCase("Всі")) {
                return true;
            }
        }
        
        return false;
    }

    private boolean requiredCustomCategoryExist(String categoryName, int userId) throws SQLException {
        List<Category> categoryList = categoryRepository.getAllCategories(userId);

        for (Category category : categoryList) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public void updateDeletedIcon(String oldIconPath, String newIconPath) throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();

        categoryRepository.updateIconPath(oldIconPath, newIconPath, userId);
    }
}
