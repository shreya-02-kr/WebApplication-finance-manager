package com.financemanager.dao;

import com.financemanager.db.DatabaseManager;
import com.financemanager.model.User;
import com.financemanager.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    /** Creates a new user. Returns false if the username is already taken. */
    public boolean register(String username, String plainPassword) {
        String sql = "INSERT INTO users(username, password_hash) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(plainPassword));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // SQLITE_CONSTRAINT is thrown when the UNIQUE(username) constraint is violated
            return false;
        }
    }

    /** Returns the User if username/password are correct, otherwise null. */
    public User login(String username, String plainPassword) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        return new User(rs.getInt("id"), rs.getString("username"), storedHash);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
