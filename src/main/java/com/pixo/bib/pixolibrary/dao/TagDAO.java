package com.pixo.bib.pixolibrary.dao;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {
    public void addTag(int imageId, String tag) throws SQLException {
        String sql = "INSERT INTO tags (image_id, tag) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            pstmt.setString(2, tag);
            pstmt.executeUpdate();
        }
    }

    // Supprimer un tag d'une image
    public void removeTag(int imageId, String tag) throws SQLException {
        String sql = "DELETE FROM tags WHERE image_id = ? AND tag = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            pstmt.setString(2, tag);
            pstmt.executeUpdate();
        }
    }

    // Récupérer tous les tags d'une image
    public List<String> getTags(int imageId) throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT tag FROM tags WHERE image_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag"));
                }
                return tags;
            }
        }
    }

    public List<String> searchImagesByTag(String tagQuery) throws SQLException {
        List<String> matchingImagePaths = new ArrayList<>();

        // Requête SQL pour trouver les images ayant un tag correspondant (recherche insensible à la casse)
        String sql = "SELECT DISTINCT i.path FROM images i " +
                "JOIN tags t ON i.id = t.image_id " +
                "WHERE LOWER(t.tag) LIKE LOWER(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Paramètre avec wildcard pour une recherche partielle
            pstmt.setString(1, "%" + tagQuery + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matchingImagePaths.add(rs.getString("path"));
                }
            }
        }
        return matchingImagePaths;
    }
}
