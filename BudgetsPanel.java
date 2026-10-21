package com.financemanager.ui;

import com.financemanager.dao.BudgetDAO;
import com.financemanager.dao.CategoryDAO;
import com.financemanager.dao.TransactionDAO;
import com.financemanager.model.Budget;
import com.financemanager.model.Category;
import com.financemanager.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BudgetsPanel extends JPanel {

    private final User user;
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Runnable onChange;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField monthField;

    public BudgetsPanel(User user, Runnable onChange) {
        this.user = user;
        this.onChange = onChange;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Category", "Limit", "Spent So Far", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthField = new JTextField(LocalDate.now().toString().substring(0, 7), 8);
        JButton viewBtn = new JButton("View Month");

        JComboBox<Category> categoryCombo = new JComboBox<>();
        for (Category c : categoryDAO.getByType("EXPENSE")) categoryCombo.addItem(c);
        JTextField limitField = new JTextField(8);
        JButton setBtn = new JButton("Set Budget");
        JButton deleteBtn = new JButton("Delete Selected");

        topPanel.add(new JLabel("Month (YYYY-MM):"));
        topPanel.add(monthField);
        topPanel.add(viewBtn);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryCombo);
        topPanel.add(new JLabel("Limit:"));
        topPanel.add(limitField);
        topPanel.add(setBtn);
        topPanel.add(deleteBtn);

        add(topPanel, BorderLayout.NORTH);

        viewBtn.addActionListener(e -> refresh());

        setBtn.addActionListener(e -> {
            Category category = (Category) categoryCombo.getSelectedItem();
            String month = monthField.getText().trim();
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Add an expense category first (Categories tab).");
                return;
            }
            double limit;
            try {
                limit = Double.parseDouble(limitField.getText().trim());
                if (limit <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive limit amount.");
                return;
            }
            if (!month.matches("\\d{4}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Please enter the month as YYYY-MM.");
                return;
            }
            budgetDAO.setBudget(user.getId(), category.getId(), month, limit);
            limitField.setText("");
            refresh();
            onChange.run();
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a budget to delete first.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            budgetDAO.delete(id);
            refresh();
            onChange.run();
        });

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        String month = monthField.getText().trim();
        if (!month.matches("\\d{4}-\\d{2}")) return;

        List<Budget> budgets = budgetDAO.getForUserAndMonth(user.getId(), month);
        for (Budget b : budgets) {
            double spent = transactionDAO.sumForUserMonthTypeCategory(
                user.getId(), month, "EXPENSE", b.getCategoryId());
            String status;
            if (spent > b.getLimitAmount()) {
                status = "OVER BUDGET";
            } else if (spent > 0.8 * b.getLimitAmount()) {
                status = "Near limit";
            } else {
                status = "OK";
            }
            tableModel.addRow(new Object[]{
                b.getId(), b.getCategoryName(),
                String.format("%,.2f", b.getLimitAmount()),
                String.format("%,.2f", spent),
                status
            });
        }
    }
}
