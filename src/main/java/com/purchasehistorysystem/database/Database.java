package com.purchasehistorysystem.database;

import java.sql.*;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class Database {
    public static Connection DatabaseConnection() throws SQLException {
        Properties properties = new Properties();
        Connection connection = null;

        try {
            InputStream inputStream = Database.class.getResourceAsStream("/database.properties");
            properties.load(inputStream);

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, username, password);
        }

        catch (IOException exception) {
            throw new SQLException("Помилка завантаження конфігурації бази даних", exception);
        }

        return connection;
    }
}
