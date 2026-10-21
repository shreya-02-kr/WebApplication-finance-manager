package com.financemanager.ui;

import com.financemanager.model.Transaction;
import com.financemanager.model.User;
import com.financemanager.dao.TransactionDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Category-wise expense breakdown for a chosen month, rendered as a simple
 * horizontal bar chart drawn directly with Graphics2D (keeps the project
 * dependency-free -- no external charting library required).
 */
public class ReportsPanel extends JPanel {

    private final User user;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private JTextField monthField;
    private BarChartPanel chartPanel;

    public ReportsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthField = new JTextField(LocalDate.now().toString().substring(0, 7), 8);
        JButton viewBtn = new JButton("Generate Report");
        topPanel.add(new JLabel("Month (YYYY-MM):"));
        topPanel.add(monthField);
        topPanel.add(viewBtn);
        add(topPanel, BorderLayout.NORTH);

        chartPanel = new BarChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        viewBtn.addActionListener(e -> refresh());
        refresh();
    }

    public void refresh() {
        String month = monthField.getText().trim();
        if (!month.matches("\\d{4}-\\d{2}")) return;

        List<Transaction> transactions = transactionDAO.getForUserAndMonth(user.getId(), month);

        Map<String, Double> expenseByCategory = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            if (t.getType().equals("EXPENSE")) {
                expenseByCategory.merge(t.getCategoryName(), t.getAmount(), Double::sum);
            }
        }
        chartPanel.setData(expenseByCategory, month);
    }

    /** Minimal horizontal bar chart, drawn manually so no external chart library is needed. */
    private static class BarChartPanel extends JPanel {
        private Map<String, Double> data = new LinkedHashMap<>();
        private String monthLabel = "";

        void setData(Map<String, Double> data, String monthLabel) {
            this.data = data;
            this.monthLabel = monthLabel;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Expense Breakdown by Category - " + monthLabel, 20, 25);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g2.drawString("No expense transactions recorded for this month.", 20, 60);
                return;
            }

            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
            int barHeight = 28;
            int gap = 14;
            int leftMargin = 180;
            int chartWidth = getWidth() - leftMargin - 100;
            int y = 60;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            Color[] palette = {
                new Color(66, 133, 244), new Color(219, 68, 55), new Color(244, 180, 0),
                new Color(15, 157, 88), new Color(171, 71, 188), new Color(255, 112, 67),
                new Color(0, 172, 193), new Color(158, 157, 36)
            };
            int colorIndex = 0;

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue();
                int barWidth = (int) ((value / max) * chartWidth);

                g2.setColor(palette[colorIndex % palette.length]);
                g2.fillRoundRect(leftMargin, y, Math.max(barWidth, 2), barHeight, 8, 8);

                g2.setColor(Color.DARK_GRAY);
                g2.drawString(entry.getKey(), 10, y + barHeight / 2 + 5);
                g2.drawString(String.format("$%,.2f", value), leftMargin + barWidth + 10, y + barHeight / 2 + 5);

                y += barHeight + gap;
                colorIndex++;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            int rows = Math.max(data.size(), 1);
            return new Dimension(600, 70 + rows * 42);
        }
    }
}
