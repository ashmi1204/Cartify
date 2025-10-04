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

public class RegisterScreen {

    private static final String BASE_URL = "http://localhost:8080/api/users";
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public static void show() {
        JFrame frame = new JFrame("Register - Cartfy");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JTextField nameField = addField(panel, gbc, "Name:", 1);
        JTextField emailField = addField(panel, gbc, "Email:", 2);
        JTextField addressField = addField(panel, gbc, "Address:", 3);
        JTextField mobileField = addField(panel, gbc, "Mobile:", 4);
        JPasswordField passwordField = new JPasswordField(15);
        addField(panel, gbc, "Password:", 5, passwordField);

        JButton registerButton = createStyledButton("Register", SECONDARY_COLOR);
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String address = addressField.getText();
            String mobile = mobileField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || address.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String response = sendRegisterRequest(name, email, address, mobile, password);
            JOptionPane.showMessageDialog(frame, response);

            if (response.contains("✅ Registered successfully")) {
                frame.dispose();
                LoginScreen.show();
            }
        });

        JButton backToLoginButton = new JButton("Already have an account? Login");
        backToLoginButton.setForeground(PRIMARY_COLOR);
        backToLoginButton.setOpaque(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        panel.add(backToLoginButton, gbc);
        backToLoginButton.addActionListener(e -> {
            frame.dispose();
            LoginScreen.show();
        });


        frame.setVisible(true);
    }

    private static JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        return addField(panel, gbc, label, row, new JTextField(15));
    }

    private static JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row, JTextField field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
        return field;
    }

    private static String sendRegisterRequest(String name, String email, String address, String mobile, String password) {
        HttpClient client = HttpClient.newHttpClient();

        String jsonInput = String.format(
                "{\"customer_name\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"mobile_no\":\"%s\", \"password\":\"%s\"}",
                name, email, address, mobile, password
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            if (json.has("success") && json.get("success").getAsBoolean()) {
                return "✅ " + json.get("message").getAsString();
            } else if (json.has("message")) {
                return "❌ " + json.get("message").getAsString();
            } else {
                return "❌ Server sent an ambiguous response.";
            }

        } catch (java.net.ConnectException ce) {
            return "❌ Error: Connection refused. Is the Spring Boot server running?";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ General Network Error: " + e.getMessage();
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
        return button;
    }
}