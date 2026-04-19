package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.DefaultCategoryModel;
import com.purchasehistorysystem.repository.CategoryRepository;
import com.purchasehistorysystem.util.UserSessionUtils;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getCustomCategories() throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();
        return categoryRepository.getAllCategories(userId);
    }

    public void getDefaultCategories(int userId) {
        try {
            if (requiredDefaultCategoriesExist(userId)) {
                return;
            }

            for (DefaultCategoryModel defaultCategoryModel : DefaultCategoryModel.values()) {
                Category newCategory = new Category(0, defaultCategoryModel.getCategoryName(),
                        defaultCategoryModel.getIconPath(), userId);

                categoryRepository.addCategory(newCategory, userId);
            }
        }
        catch (SQLException exception) {
            System.out.println("Помилка при збиранні вбудованих категорій");
        }
    }

    public void addCustomCategory(String name, String iconName) throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();

        if (requiredCustomCategoryExist(name, userId)) {
            throw new SQLException("Така категорія вже існує");
        }

        String iconPathDatabase = "/com/purchasehistorysystem/icons/custom/user_" + userId + "/" + iconName;

        Category category = new Category(0, name, iconPathDatabase, userId);
        categoryRepository.addCategory(category, userId);
    }

    public void deleteCategory(int id) throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();
        categoryRepository.deleteCategory(id, userId);
    }

    private boolean requiredDefaultCategoriesExist(int userId) {
        try {
            List<Category> categoryList = categoryRepository.getAllCategories(userId);

            for (Category category : categoryList) {
                if (!category.getName().equalsIgnoreCase("Всі")) {
                    return true;
                }
            }
        }

        catch (SQLException exception) {
            System.err.println("Помилка при збиранні вбудованих категорій");
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
}
