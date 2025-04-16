package com.pixo.bib.pixolibrary.dao;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransformationDAO {
    public void addTransformation(int imageId, String type) throws SQLException {
        String sql = "INSERT INTO transformations (image_id, type) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        }
    }

    // method to delete Transformation
    public void deleteTransformation(int imageId, String type) throws SQLException {
        String sql = "DELETE FROM transformations WHERE image_id = ? AND type = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        }
    }



    // Récupérer toutes les transformations d'une image
    public List<String> getTransformations(int imageId) throws SQLException {
        List<String> transformations = new ArrayList<>();
        String sql = "SELECT type FROM transformations WHERE image_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transformations.add(rs.getString("type"));
                }
                return transformations;
            }
        }
    }
}
