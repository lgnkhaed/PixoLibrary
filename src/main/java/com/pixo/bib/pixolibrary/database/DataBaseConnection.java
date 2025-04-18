package com.pixo.bib.pixolibrary.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnection {
    private static final String URL = "jdbc:derby:ImageDB;create=true"; // Embedded mode

    // Get a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Create all tables if they do not exist
    public static void createTablesIfNotExist() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // USERS table
            try {
                stmt.executeUpdate(
                        "CREATE TABLE Users (" +
                                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                                "email VARCHAR(255) UNIQUE NOT NULL, " +
                                "password VARCHAR(255) NOT NULL, " +
                                "name VARCHAR(255) NOT NULL, " +
                                "firstname VARCHAR(255) NOT NULL)"
                );
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e; // Table already exists
            }

            // IMAGES table
            try {
                stmt.executeUpdate(
                        "CREATE TABLE Images (" +
                                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                                "path VARCHAR(255) UNIQUE NOT NULL)"
                );
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

            // TAGS table
            try {
                stmt.executeUpdate(
                        "CREATE TABLE Tags (" +
                                "image_id INT NOT NULL, " +
                                "tag VARCHAR(50) NOT NULL, " +
                                "FOREIGN KEY (image_id) REFERENCES Images(id))"
                );
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

            // TRANSFORMATIONS table
            try {
                stmt.executeUpdate(
                        "CREATE TABLE Transformations (" +
                                "image_id INT NOT NULL, " +
                                "type VARCHAR(50) NOT NULL, " +
                                "parameters VARCHAR(255), " +
                                "FOREIGN KEY (image_id) REFERENCES Images(id))"
                );
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

        }
    }
}



