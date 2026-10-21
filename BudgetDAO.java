package com.financemanager.dao;

import com.financemanager.db.DatabaseManager;
import com.financemanager.model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    /** Creates or updates the budget limit for a given user/category/month ("upsert"). */
    public boolean setBudget(int userId, int categoryId, String month, double limitAmount) {
        String sql = """
            INSERT INTO budgets(user_id, category_id, month, limit_amount)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(user_id, category_id, month)
            DO UPDATE SET limit_amount = excluded.limit_amount
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setString(3, month);
            ps.setDouble(4, limitAmount);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Budget> getForUserAndMonth(int userId, String month) {
        List<Budget> list = new ArrayList<>();
        String sql = """
            SELECT b.id, b.user_id, b.category_id, c.name AS category_name, b.month, b.limit_amount
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.user_id = ? AND b.month = ?
            ORDER BY c.name
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Budget(
                        rs.getInt("id"), rs.getInt("user_id"), rs.getInt("category_id"),
                        rs.getString("category_name"), rs.getString("month"), rs.getDouble("limit_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int budgetId) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
