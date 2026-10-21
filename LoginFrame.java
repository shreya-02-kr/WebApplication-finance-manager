package com.financemanager.ui;

import com.financemanager.dao.UserDAO;
import com.financemanager.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Personal Finance Manager - Login");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Personal Finance Manager", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create Account");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardFrame(user).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = userDAO.register(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account created! You can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "That username is already taken.",
                "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
