package com.pixo.bib.pixolibrary.dao;

import com.pixo.bib.pixolibrary.Model.User;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;

import java.sql.*;

public class UserDAO {
    //register a new user
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO Users (email, password, name, firstname) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection()){
             PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword()); // Ideally hashed
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getFirstname());
            pstmt.executeUpdate();
            return true;
        }catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLIntegrityConstraintViolationException("Email address already in use");
        }
    }

    //login
    public User login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = DataBaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("firstname")
                );
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // Get full name by email
    public String getFullNameByEmail(String email) throws SQLException {
        String sql = "SELECT name, firstname FROM Users WHERE email = ?";
        try (Connection conn = DataBaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("firstname") + " " + rs.getString("name");
            }
            return null;
        }
    }
}
