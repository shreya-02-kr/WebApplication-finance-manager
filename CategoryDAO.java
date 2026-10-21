package com.financemanager.dao;

import com.financemanager.db.DatabaseManager;
import com.financemanager.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getByType(String type) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, type FROM categories WHERE type = ? ORDER BY name";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, type FROM categories ORDER BY type, name";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Adds a new custom category. Returns false if a category with that name+type already exists. */
    public boolean add(String name, String type) {
        String sql = "INSERT INTO categories(name, type) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Will fail if transactions/budgets still reference this category (FOREIGN KEY RESTRICT)
            return false;
        }
    }
}
