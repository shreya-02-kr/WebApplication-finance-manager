package com.financemanager.ui;

import com.financemanager.dao.TransactionDAO;
import com.financemanager.model.Transaction;
import com.financemanager.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionsPanel extends JPanel {

    private final User user;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Runnable onChange; // notifies DashboardFrame to refresh other tabs

    private DefaultTableModel tableModel;
    private JTable table;

    public TransactionsPanel(User user, Runnable onChange) {
        this.user = user;
        this.onChange = onChange;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Date", "Type", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // hide ID column visually, still usable
        table.setAutoCreateRowSorter(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Transaction");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.NORTH);

        addBtn.addActionListener(e -> openAddEditDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a transaction to edit first.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            openAddEditDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a transaction to delete first.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this transaction? This cannot be undone.", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                transactionDAO.delete(id);
                refresh();
                onChange.run();
            }
        });

        refresh();
    }

    private void openAddEditDialog(Integer transactionId) {
        Transaction existing = null;
        if (transactionId != null) {
            existing = transactionDAO.getAllForUser(user.getId()).stream()
                .filter(t -> t.getId() == transactionId)
                .findFirst().orElse(null);
        }
        AddEditTransactionDialog dialog = new AddEditTransactionDialog(
            SwingUtilities.getWindowAncestor(this), user, existing);
        dialog.setVisible(true);

        if (dialog.wasSaved()) {
            refresh();
            onChange.run();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllForUser(user.getId());
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getDate().toString(),
                t.getType(),
                t.getCategoryName(),
                String.format("%,.2f", t.getAmount()),
                t.getDescription()
            });
        }
    }
}
