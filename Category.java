package com.financemanager.model;

/**
 * A spending/income category, e.g. "Food", "Rent", "Salary".
 * type is either "INCOME" or "EXPENSE" -- used to keep the category
 * dropdowns relevant to the kind of transaction being entered.
 */
public class Category {
    private int id;
    private String name;
    private String type; // "INCOME" or "EXPENSE"

    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }

    @Override
    public String toString() { return name; }
}
