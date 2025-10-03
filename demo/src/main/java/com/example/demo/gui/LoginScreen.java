package com.example.demo.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoginScreen {
    private static final String BASE_URL = "http://localhost:8080/api/users";
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color ADMIN_COLOR = new Color(156, 39, 176);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public static void show() {
        JFrame frame = new JFrame("Login - Cartfy");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Cartfy");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = createStyledButton("Login", SUCCESS_COLOR);
        JButton registerButton = createStyledButton("Register", SECONDARY_COLOR);
        JButton adminButton = createStyledButton("Admin Login", ADMIN_COLOR);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(adminButton);
        panel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sendLoginRequest(email, password, frame);
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            RegisterScreen.show();
        });

        adminButton.addActionListener(e -> showAdminLoginDialog(frame));
        frame.setVisible(true);
    }

    private static void showAdminLoginDialog(JFrame parent) {
        JDialog adminDialog = new JDialog(parent, "Admin Login", true);
        adminDialog.setSize(300, 200);
        adminDialog.setLocationRelativeTo(parent);
        adminDialog.setLayout(new GridBagLayout());
        adminDialog.getContentPane().setBackground(BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("🔐 Admin Access");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(ADMIN_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        adminDialog.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        adminDialog.add(new JLabel("Admin ID:"), gbc);
        JTextField adminIdField = new JTextField(15);
        gbc.gridx = 1;
        adminDialog.add(adminIdField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        adminDialog.add(new JLabel("Password:"), gbc);
        JPasswordField adminPassField = new JPasswordField(15);
        gbc.gridx = 1;
        adminDialog.add(adminPassField, gbc);

        JButton adminLoginBtn = createStyledButton("Login as Admin", ADMIN_COLOR);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        adminDialog.add(adminLoginBtn, gbc);

        adminLoginBtn.addActionListener(e -> {
            String adminId = adminIdField.getText();
            String adminPass = new String(adminPassField.getPassword());
            if ("admin".equals(adminId) && "admin123".equals(adminPass)) {
                adminDialog.dispose();
                parent.dispose();
                new SellerDashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(adminDialog, "Invalid admin credentials!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
        adminDialog.setVisible(true);
    }

    private static void sendLoginRequest(String email, String password, JFrame frame) {
        HttpClient client = HttpClient.newHttpClient();
        String jsonInput = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            String message = json.has("message") ? json.get("message").getAsString() : "Unknown error.";

            if (json.has("success") && json.get("success").getAsBoolean()) {
                // --- STORE CUSTOMER ID ---
                CartfyGUI.currentCustomerId = json.get("customer_id").getAsLong();
                JOptionPane.showMessageDialog(frame, "✅ " + message);
                frame.dispose();
                CartfyGUI.createAndShowGUI();
            } else {
                JOptionPane.showMessageDialog(frame, "❌ " + message);
            }

        } catch (java.net.ConnectException ce) {
            JOptionPane.showMessageDialog(frame, "❌ Connection refused. Is the Spring Boot server running?");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "❌ Network Error: " + e.getMessage());
        }
    }

    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }
}