package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.model.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class PurchaseRepository {
    private Category getCategory(int id) throws SQLException {
        String sqlQuery = "SELECT * FROM categories WHERE id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Category(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("icon_path"));
            }
            throw new SQLException("Категроію з ID " + id + " не знайдено");
        }
    }

    public List<Purchase> getAllPurchases() throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlQuery)
                ) {
            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"));
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate());

                purchases.add(purchase);
            }
        }
        return purchases;
    }

    public void addPurchase(Purchase purchase) throws SQLException {
        String sqlQuery = "INSERT INTO purchases (name, category_id, price, amount, purchase_date) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, purchase.getName());
            preparedStatement.setInt(2, purchase.getCategory().getId());
            preparedStatement.setDouble(3, purchase.getPrice());
            preparedStatement.setInt(4, purchase.getAmount());
            preparedStatement.setDate(5, Date.valueOf(purchase.getDate()));
            preparedStatement.executeUpdate();
        }
    }

    public void deletePurchase(int id) throws SQLException {
        String sqlQuery = "DELETE FROM purchases WHERE id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public List<Purchase> filterByCategory(int categoryId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE category_id = ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, categoryId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"));
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate());

                purchases.add(purchase);
            }
        }
        return purchases;
    }

    public List<Purchase> filterByDateRange(LocalDate from, LocalDate to) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE purchase_date BETWEEN ? AND ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setDate(1, Date.valueOf(from));
            preparedStatement.setDate(2, Date.valueOf(to));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"));
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate());

                purchases.add(purchase);
            }
        }
        return purchases;
    }
}
