package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class CategoryRepository {
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category(0, "Всі", null));

        String sqlQuery = "SELECT * FROM categories";

        try (
                Connection connection = Database.databaseConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlQuery)
                ) {
            while (resultSet.next()) {
                Category category = new Category(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("icon_path"));

                categories.add(category);
            }
        }
        return categories;
    }

    public void addCategory(Category category) throws  SQLException {
        String sqlQuery = "INSERT INTO categories (name, icon_path) VALUES (?, ?)";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getIconPath());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteCategory(int id) throws SQLException {
        String sqlQuery = "DELETE FROM categories WHERE id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
