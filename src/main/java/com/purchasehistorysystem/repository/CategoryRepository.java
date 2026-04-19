package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class CategoryRepository {
    public List<Category> getAllCategories(int userId) throws SQLException {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category(0, "Всі", null, 0));

        String sqlQuery = "SELECT * FROM categories WHERE user_id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Category category = new Category(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("icon_path"),
                        resultSet.getInt("user_id"));

                categories.add(category);
            }
        }
        return categories;
    }

    public void addCategory(Category category, int userId) throws  SQLException {
        String sqlQuery = "INSERT INTO categories (name, icon_path, user_id) VALUES (?, ?, ?)";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getIconPath());
            preparedStatement.setInt(3, userId);
            preparedStatement.executeUpdate();
        }
    }

    public void deleteCategory(int id, int userId) throws SQLException {
        String sqlQuery = "DELETE FROM categories WHERE id = ? AND user_id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        }
    }
}
