package com.financemanager.ui;

import com.financemanager.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window shown after a successful login. Hosts the
 * Overview, Transactions, Budgets, Categories and Reports tabs.
 */
public class DashboardFrame extends JFrame {

    private final User currentUser;
    private OverviewPanel overviewPanel;
    private TransactionsPanel transactionsPanel;
    private BudgetsPanel budgetsPanel;
    private CategoriesPanel categoriesPanel;
    private ReportsPanel reportsPanel;

    public DashboardFrame(User user) {
        this.currentUser = user;

        setTitle("Personal Finance Manager - Welcome, " + user.getUsername());
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        overviewPanel = new OverviewPanel(currentUser);
        transactionsPanel = new TransactionsPanel(currentUser, this::refreshAll);
        budgetsPanel = new BudgetsPanel(currentUser, this::refreshAll);
        categoriesPanel = new CategoriesPanel(this::refreshAll);
        reportsPanel = new ReportsPanel(currentUser);

        tabs.addTab("Overview", overviewPanel);
        tabs.addTab("Transactions", transactionsPanel);
        tabs.addTab("Budgets", budgetsPanel);
        tabs.addTab("Categories", categoriesPanel);
        tabs.addTab("Reports", reportsPanel);

        // Refresh whichever tab becomes visible, so figures are always current
        tabs.addChangeListener(e -> refreshAll());

        add(tabs);
    }

    /** Called after any add/edit/delete so every tab reflects the latest data. */
    private void refreshAll() {
        overviewPanel.refresh();
        transactionsPanel.refresh();
        budgetsPanel.refresh();
        categoriesPanel.refresh();
        reportsPanel.refresh();
    }
}
