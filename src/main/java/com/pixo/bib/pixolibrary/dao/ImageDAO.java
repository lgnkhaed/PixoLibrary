package com.pixo.bib.pixolibrary.dao;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;
import java.sql.*;

public class ImageDAO {
    public int insertImage(String path) throws SQLException {
        String sql = "INSERT INTO images (path) VALUES (?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, path);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("Échec de l'insertion");
            }
        }
    }

    // Récupérer le chemin d'une image par son ID
    public String getImagePath(int id) throws SQLException {
        String sql = "SELECT path FROM images WHERE id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("path");
                else return null;
            }
        }
    }

    // Récupérer l'ID d'une image par son chemin
    public int getImageIdByPath(String path) throws SQLException {
        String sql = "SELECT id FROM images WHERE path = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, path);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Image non trouvée : " + path);
                }
            }
        }
    }
}
