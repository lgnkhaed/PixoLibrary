package com.pixo.bib.pixolibrary.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnection {
    private static final String URL = "jdbc:derby://localhost:1527/ImageDB;create=true";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public static void createTablesIfNotExist() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Création de la table IMAGES
            stmt.executeUpdate(
                    "CREATE TABLE images (" +
                            "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                            "path VARCHAR(255) UNIQUE NOT NULL)"
            );

            // Création de la table TAGS
            stmt.executeUpdate(
                    "CREATE TABLE tags (" +
                            "image_id INT, " +
                            "tag VARCHAR(50), " +
                            "FOREIGN KEY (image_id) REFERENCES images(id))"
            );

            // Création de la table TRANSFORMATIONS
            stmt.executeUpdate(
                    "CREATE TABLE transformations (" +
                            "image_id INT, " +
                            "type VARCHAR(50) NOT NULL, " +
                            "parameters VARCHAR(255), " +
                            "FOREIGN KEY (image_id) REFERENCES images(id))"
            );

        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) { // Code d'erreur "table existe déjà"
                throw e;
            }
        }
    }

}
