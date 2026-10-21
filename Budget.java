package com.financemanager.model;

/**
 * A monthly spending limit set by the user for a specific category.
 * month is stored as "YYYY-MM" (e.g. "2026-09") so each category can have
 * a different limit each month.
 */
public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private String month; // "YYYY-MM"
    private double limitAmount;

    public Budget(int id, int userId, int categoryId, String categoryName,
                  String month, double limitAmount) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.month = month;
        this.limitAmount = limitAmount;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getMonth() { return month; }
    public double getLimitAmount() { return limitAmount; }
}
