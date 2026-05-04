package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsRepository {
    public Map<String, Double> getExpensesByCategory(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<String, Double> categoryExpenses = new HashMap<>();

        String sqlQuery = "SELECT categories.name, SUM(purchases.price * purchases.amount) as total " +
                "FROM purchases, categories WHERE purchases.category_id = categories.id " +
                "AND purchases.user_id = ? AND purchases.purchase_date >= ? AND purchases.purchase_date <= ? " +
                "GROUP BY categories.name";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, Date.valueOf(from));
            preparedStatement.setDate(3, Date.valueOf(to));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categoryExpenses.put(resultSet.getString("name"), resultSet.getDouble("total"));
            }
        }
        return categoryExpenses;
    }

    public Map<LocalDate, Double> getExpensesByDate(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<LocalDate, Double> dailyExpenses = new LinkedHashMap<>();

        String sqlQuery = "SELECT purchase_date, SUM(price * amount) as daily_total " +
                "FROM purchases WHERE user_id = ? AND purchase_date >= ? AND purchase_date <= ? " +
                "GROUP BY purchase_date ORDER BY purchase_date";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, Date.valueOf(from));
            preparedStatement.setDate(3, Date.valueOf(to));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                dailyExpenses.put(resultSet.getDate("purchase_date").toLocalDate(),
                        resultSet.getDouble("daily_total"));
            }
        }
        return dailyExpenses;
    }

    public Map<String, Integer> getCategoryCount(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<String, Integer> categoryCount = new LinkedHashMap<>();

        String sqlQuery = "SELECT categories.name, COUNT(purchases.id) as purchase_count " +
                "FROM purchases, categories WHERE purchases.category_id = categories.id " +
                "AND purchases.user_id = ? AND purchases.purchase_date >= ? AND purchases.purchase_date <= ? " +
                "GROUP BY categories.name ORDER BY purchase_count DESC";

        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, Date.valueOf(from));
            preparedStatement.setDate(3, Date.valueOf(to));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categoryCount.put(resultSet.getString("name"), resultSet.getInt("purchase_count"));
            }
        }
        return categoryCount;
    }
}
