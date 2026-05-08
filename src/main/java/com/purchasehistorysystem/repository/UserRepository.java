package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.User;

import java.sql.*;

public class UserRepository {
    public void saveUser(User user) throws SQLException {
        String sqlQuery = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        }
    }

    public User getUser(String email) throws SQLException {
        String sqlQuery = "SELECT * FROM users WHERE email = ?";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)
                ) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password_hash"),
                    resultSet.getString("email")
                );
            }
        }
        return null;
    }

    public boolean userDuplicateCheck(String username, String email) throws SQLException {
        String sqlQuery = "SELECT 1 FROM users WHERE username = ? OR email = ? LIMIT 1";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery()
                    ) {
                return resultSet.next();
            }
        }
    }
}
