package com.purchasehistorysystem.repository;

import com.purchasehistorysystem.database.Database;
import com.purchasehistorysystem.model.User;

import java.sql.*;

public class UserRepository {
    public void saveUser(User user) throws SQLException {
        String sqlQuery = "INSERT INTO users (username, password_hash, password_hint) VALUES (?, ?, ?)";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getPasswordHint());
            preparedStatement.executeUpdate();
        }
    }

    public User getUser(String username) throws SQLException {
        String sqlQuery = "SELECT * FROM users WHERE username = ?";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)
                ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password_hash"),
                    resultSet.getString("password_hint")
                );
            }
        }
        return null;
    }

    public boolean usernameDuplicateCheck(String username) throws SQLException {
        String sqlQuery = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (
                Connection connection = Database.databaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ) {
            preparedStatement.setString(1, username);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery()
                    ) {
                return resultSet.next();
            }
        }
    }
}
