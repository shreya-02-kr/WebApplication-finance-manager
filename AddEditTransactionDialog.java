package com.financemanager.ui;

import com.financemanager.dao.CategoryDAO;
import com.financemanager.dao.TransactionDAO;
import com.financemanager.model.Category;
import com.financemanager.model.Transaction;
import com.financemanager.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AddEditTransactionDialog extends JDialog {

    private final User user;
    private final Transaction existing; // null when adding a new transaction
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private JComboBox<String> typeCombo;
    private JComboBox<Category> categoryCombo;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField descriptionField;
    private boolean saved = false;

    public AddEditTransactionDialog(Window owner, User user, Transaction existing) {
        super(owner, existing == null ? "Add Transaction" : "Edit Transaction", ModalityType.APPLICATION_MODAL);
        this.user = user;
        this.existing = existing;

        setSize(380, 320);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        typeCombo = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        categoryCombo = new JComboBox<>();
        amountField = new JTextField(12);
        dateField = new JTextField(LocalDate.now().toString(), 12);
        descriptionField = new JTextField(12);

        int y = 0;
        addRow(panel, gbc, y++, "Type:", typeCombo);
        addRow(panel, gbc, y++, "Category:", categoryCombo);
        addRow(panel, gbc, y++, "Amount:", amountField);
        addRow(panel, gbc, y++, "Date (YYYY-MM-DD):", dateField);
        addRow(panel, gbc, y++, "Description:", descriptionField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);

        typeCombo.addActionListener(e -> populateCategories());
        populateCategories();

        if (existing != null) {
            typeCombo.setSelectedItem(existing.getType());
            populateCategories();
            selectCategoryById(existing.getCategoryId());
            amountField.setText(String.valueOf(existing.getAmount()));
            dateField.setText(existing.getDate().toString());
            descriptionField.setText(existing.getDescription());
        }

        saveBtn.addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void populateCategories() {
        String type = (String) typeCombo.getSelectedItem();
        categoryCombo.removeAllItems();
        List<Category> categories = categoryDAO.getByType(type);
        for (Category c : categories) categoryCombo.addItem(c);
    }

    private void selectCategoryById(int categoryId) {
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getId() == categoryId) {
                categoryCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void handleSave() {
        String type = (String) typeCombo.getSelectedItem();
        Category category = (Category) categoryCombo.getSelectedItem();

        if (category == null) {
            JOptionPane.showMessageDialog(this,
                "No categories exist for this type yet. Add one in the Categories tab first.",
                "No Category Available", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount.",
                "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter the date as YYYY-MM-DD.",
                "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String description = descriptionField.getText().trim();

        boolean success;
        if (existing == null) {
            success = transactionDAO.add(user.getId(), category.getId(), type, amount, date, description);
        } else {
            success = transactionDAO.update(existing.getId(), category.getId(), type, amount, date, description);
        }

        if (success) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Could not save the transaction. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean wasSaved() { return saved; }
}
