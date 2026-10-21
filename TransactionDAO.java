package com.financemanager.dao;

import com.financemanager.db.DatabaseManager;
import com.financemanager.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean add(int userId, int categoryId, String type, double amount,
                        LocalDate date, String description) {
        String sql = """
            INSERT INTO transactions(user_id, category_id, type, amount, txn_date, description)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setString(3, type);
            ps.setDouble(4, amount);
            ps.setString(5, date.toString());
            ps.setString(6, description);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int transactionId, int categoryId, String type, double amount,
                           LocalDate date, String description) {
        String sql = """
            UPDATE transactions SET category_id=?, type=?, amount=?, txn_date=?, description=?
            WHERE id=?
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, date.toString());
            ps.setString(5, description);
            ps.setInt(6, transactionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** All transactions for a user, most recent first, with the category name already joined in. */
    public List<Transaction> getAllForUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT t.id, t.user_id, t.category_id, c.name AS category_name,
                   t.type, t.amount, t.txn_date, t.description
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.user_id = ?
            ORDER BY t.txn_date DESC, t.id DESC
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Transactions for a user within a given month, e.g. month = "2026-09". */
    public List<Transaction> getForUserAndMonth(int userId, String month) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT t.id, t.user_id, t.category_id, c.name AS category_name,
                   t.type, t.amount, t.txn_date, t.description
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.user_id = ? AND substr(t.txn_date, 1, 7) = ?
            ORDER BY t.txn_date DESC, t.id DESC
        """;
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Sum of amounts for a user/month/type, e.g. total EXPENSE for "2026-09". Used for dashboard + budgets. */
    public double sumForUserMonthTypeCategory(int userId, String month, String type, Integer categoryId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(amount), 0) AS total FROM transactions
            WHERE user_id = ? AND substr(txn_date, 1, 7) = ? AND type = ?
        """);
        if (categoryId != null) sql.append(" AND category_id = ?");

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql.toString())) {
            ps.setInt(1, userId);
            ps.setString(2, month);
            ps.setString(3, type);
            if (categoryId != null) ps.setInt(4, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getString("type"),
            rs.getDouble("amount"),
            LocalDate.parse(rs.getString("txn_date")),
            rs.getString("description")
        );
    }
}
