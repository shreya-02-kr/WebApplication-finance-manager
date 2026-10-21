package com.financemanager.model;

import java.time.LocalDate;

/**
 * A single income or expense entry belonging to one user.
 */
public class Transaction {
    private int id;
    private int userId;
    private int categoryId;
    private String categoryName; // convenience field, populated by joins
    private String type;         // "INCOME" or "EXPENSE"
    private double amount;
    private LocalDate date;
    private String description;

    public Transaction(int id, int userId, int categoryId, String categoryName,
                        String type, double amount, LocalDate date, String description) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }

    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
}
