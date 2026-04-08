package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getAllCategories() throws SQLException {
        return categoryRepository.getAllCategories();
    }

    public void addCategory(String name, String iconPath) throws SQLException {
        List<Category> categoryList = categoryRepository.getAllCategories();

        for (Category category : categoryList) {
            if (category.getName().equalsIgnoreCase(name)) {
                System.out.println("Така категорія вже існує");
            }
        }

        Category category = new Category(0, name, iconPath);
        categoryRepository.addCategory(category);
    }

    public void deleteCategory(int id) throws SQLException {
        categoryRepository.deleteCategory(id);
    }
}
