package com.financemanager;

import com.financemanager.db.DatabaseManager;
import com.financemanager.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize the database connection and schema up front, so any
        // setup error is reported immediately rather than after the login screen opens.
        DatabaseManager.getConnection();

        // Use the operating system's native look & feel for a more polished UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default cross-platform look & feel if this fails
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::close));
    }
}
