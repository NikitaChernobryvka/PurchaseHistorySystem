package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.model.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class PurchaseRepository {
    private Category getCategory(int id, int userId) throws SQLException {
        if (id == 0) {
            return new Category(0,
                    "Видалено",
                    "/com/purchasehistorysystem/icons/default/DeletedIcon.png",
                    userId, null);
        }

        String sqlQuery = "SELECT * FROM categories WHERE id = ? AND user_id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Category(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("icon_path"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("type"));
            }
            return new Category(0,
                    "Видалено",
                    "/com/purchasehistorysystem/icons/default/DeletedIcon.png",
                    userId, null);
        }
    }

    public List<Purchase> getAllPurchases(int userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE user_id = ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"), userId);
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getInt("user_id"));

                purchases.add(purchase);
            }
        }
        return purchases;
    }

    public void addPurchase(Purchase purchase, int userId) throws SQLException {
        String sqlQuery = "INSERT INTO purchases (name, category_id, price, amount, purchase_date, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, purchase.getName());
            preparedStatement.setInt(2, purchase.getCategory().getId());
            preparedStatement.setDouble(3, purchase.getPrice());
            preparedStatement.setInt(4, purchase.getAmount());
            preparedStatement.setDate(5, Date.valueOf(purchase.getDate()));
            preparedStatement.setInt(6, userId);
            preparedStatement.executeUpdate();
        }
    }

    public void deletePurchase(int id, int userId) throws SQLException {
        String sqlQuery = "DELETE FROM purchases WHERE id = ? AND user_id = ?";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        }
    }

    public List<Purchase> filterByCategory(int categoryId, int userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE category_id = ? AND user_id = ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"), userId);
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getInt("user_id"));

                purchases.add(purchase);
            }
        }
        return purchases;
    }

    public List<Purchase> filterByDateRange(LocalDate from, LocalDate to, int userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE (purchase_date BETWEEN ? AND ?) AND user_id = ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setDate(1, Date.valueOf(from));
            preparedStatement.setDate(2, Date.valueOf(to));
            preparedStatement.setInt(3, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"), userId);
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getInt("user_id"));

                purchases.add(purchase);
            }
        }

        return purchases;
    }

    public List<Purchase> filterByPriceRange(double minPrice, double maxPrice, int userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE (price BETWEEN ? AND ?) AND user_id = ? ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setDouble(1, minPrice);
            preparedStatement.setDouble(2, maxPrice);
            preparedStatement.setInt(3, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"), userId);
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getInt("user_id"));

                purchases.add(purchase);
            }
        }

        return purchases;
    }

    public List<Purchase> filter(Integer categoryId, LocalDate from, LocalDate to, double minPrice, double maxPrice, int userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        String sqlQuery = "SELECT * FROM purchases WHERE user_id = ? AND " +
                "(?::int IS NULL OR category_id = ?::int) AND " +
                "(?::date IS NULL OR purchase_date >= ?::date) AND " +
                "(?::date IS NULL OR purchase_date <= ?::date) AND " +
                "price BETWEEN ? AND ? " +
                "ORDER BY purchase_date DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);

            if (categoryId != null) {
                preparedStatement.setInt(2, categoryId);
                preparedStatement.setInt(3, categoryId);
            }

            else {
                preparedStatement.setNull(2, Types.INTEGER);
                preparedStatement.setNull(3, Types.INTEGER);
            }

            if (from != null) {
                preparedStatement.setDate(4, Date.valueOf(from));
                preparedStatement.setDate(5, Date.valueOf(from));
            }

            else {
                preparedStatement.setNull(4, Types.DATE);
                preparedStatement.setNull(5, Types.DATE);
            }

            if (to != null) {
                preparedStatement.setDate(6, Date.valueOf(to));
                preparedStatement.setDate(7, Date.valueOf(to));
            }

            else {
                preparedStatement.setNull(6, Types.DATE);
                preparedStatement.setNull(7, Types.DATE);
            }

            preparedStatement.setDouble(8, minPrice);
            preparedStatement.setDouble(9, maxPrice);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = getCategory(resultSet.getInt("category_id"), userId);
                Purchase purchase = new Purchase(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getInt("user_id"));

                purchases.add(purchase);
            }
        }

        return purchases;
    }
}
