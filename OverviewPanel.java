package com.financemanager.ui;

import com.financemanager.dao.TransactionDAO;
import com.financemanager.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Landing tab: shows this month's total income, total expense, and net
 * balance at a glance.
 */
public class OverviewPanel extends JPanel {

    private final User user;
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;
    private JLabel monthLabel;

    public OverviewPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String currentMonthName = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        monthLabel = new JLabel("Summary for " + currentMonthName, SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(monthLabel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        balanceLabel = new JLabel();

        cardsPanel.add(buildCard("Total Income", incomeLabel, new Color(46, 139, 87)));
        cardsPanel.add(buildCard("Total Expense", expenseLabel, new Color(178, 34, 34)));
        cardsPanel.add(buildCard("Net Balance", balanceLabel, new Color(30, 60, 120)));

        add(cardsPanel, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(color, 2, true));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(15));

        return card;
    }

    public void refresh() {
        String currentMonth = LocalDate.now().toString().substring(0, 7); // "YYYY-MM"

        double income = transactionDAO.sumForUserMonthTypeCategory(user.getId(), currentMonth, "INCOME", null);
        double expense = transactionDAO.sumForUserMonthTypeCategory(user.getId(), currentMonth, "EXPENSE", null);
        double balance = income - expense;

        incomeLabel.setText(String.format("$%,.2f", income));
        expenseLabel.setText(String.format("$%,.2f", expense));
        balanceLabel.setText(String.format("$%,.2f", balance));
        balanceLabel.setForeground(balance >= 0 ? new Color(30, 120, 60) : new Color(178, 34, 34));
    }
}
