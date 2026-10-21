package com.financemanager.model;

/**
 * Represents an application user (supports multiple people using the same
 * installation, each with their own private set of transactions/budgets).
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    @Override
    public String toString() { return username; }
}
