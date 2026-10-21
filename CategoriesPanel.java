package com.financemanager.ui;

import com.financemanager.dao.CategoryDAO;
import com.financemanager.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriesPanel extends JPanel {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final Runnable onChange;

    private DefaultTableModel tableModel;
    private JTable table;

    public CategoriesPanel(Runnable onChange) {
        this.onChange = onChange;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        JButton addBtn = new JButton("Add Category");
        JButton deleteBtn = new JButton("Delete Selected");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(addBtn);
        formPanel.add(deleteBtn);

        add(formPanel, BorderLayout.NORTH);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a category name.");
                return;
            }
            boolean success = categoryDAO.add(name, type);
            if (success) {
                nameField.setText("");
                refresh();
                onChange.run();
            } else {
                JOptionPane.showMessageDialog(this,
                    "That category already exists for this type.",
                    "Duplicate Category", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a category to delete first.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            boolean success = categoryDAO.delete(id);
            if (success) {
                refresh();
                onChange.run();
            } else {
                JOptionPane.showMessageDialog(this,
                    "This category can't be deleted while it's used by existing transactions or budgets.",
                    "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            }
        });

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryDAO.getAll();
        for (Category c : categories) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getType()});
        }
    }
}
