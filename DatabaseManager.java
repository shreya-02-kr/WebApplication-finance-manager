package com.financemanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages a single, shared SQLite connection for the whole application and
 * creates the schema automatically the first time the app is run, so there
 * is no separate database-setup step for the user.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:data/finance_manager.db";
    private static Connection connection;

    /** Returns the single shared connection, opening it (and creating tables) if needed. */
    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                // Ensure the "data" folder exists so SQLite can create the file inside it
                new java.io.File("data").mkdirs();

                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
                createTables();
                seedDefaultCategories();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return connection;
    }

    private static void createTables() throws SQLException {
        String users = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            );
        """;

        String categories = """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL CHECK(type IN ('INCOME','EXPENSE')),
                UNIQUE(name, type)
            );
        """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                type TEXT NOT NULL CHECK(type IN ('INCOME','EXPENSE')),
                amount REAL NOT NULL,
                txn_date TEXT NOT NULL,
                description TEXT,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT
            );
        """;

        String budgets = """
            CREATE TABLE IF NOT EXISTS budgets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                month TEXT NOT NULL,
                limit_amount REAL NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE,
                UNIQUE(user_id, category_id, month)
            );
        """;

        try (Statement st = connection.createStatement()) {
            st.execute(users);
            st.execute(categories);
            st.execute(transactions);
            st.execute(budgets);
        }
    }

    /** Inserts a starter set of common categories the first time the app runs. */
    private static void seedDefaultCategories() throws SQLException {
        try (Statement st = connection.createStatement()) {
            var rs = st.executeQuery("SELECT COUNT(*) AS c FROM categories");
            rs.next();
            if (rs.getInt("c") > 0) return; // already seeded
        }

        String[][] defaults = {
            {"Salary", "INCOME"}, {"Freelance", "INCOME"}, {"Interest", "INCOME"}, {"Other Income", "INCOME"},
            {"Food & Groceries", "EXPENSE"}, {"Rent", "EXPENSE"}, {"Utilities", "EXPENSE"},
            {"Transport", "EXPENSE"}, {"Entertainment", "EXPENSE"}, {"Healthcare", "EXPENSE"},
            {"Shopping", "EXPENSE"}, {"Education", "EXPENSE"}, {"Other Expense", "EXPENSE"}
        };

        String sql = "INSERT INTO categories(name, type) VALUES (?, ?)";
        try (var ps = connection.prepareStatement(sql)) {
            for (String[] cat : defaults) {
                ps.setString(1, cat[0]);
                ps.setString(2, cat[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Call once when the application is shutting down. */
    public static synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
