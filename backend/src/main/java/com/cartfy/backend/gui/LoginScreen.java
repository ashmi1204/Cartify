// LoginScreen.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
public class LoginScreen {

    // API base URL
    private static final String BASE_URL = "http://localhost:8080/api/users";

    // Color Scheme (from CartfyGUI)
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);      // Muted Teal
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);  // Warm Taupe
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);    // Sage Green
    private static final Color BACKGROUND = new Color(245, 243, 240);       // Warm Off-White
    private static final Color CARD_BG = new Color(252, 251, 249);          // Soft Cream
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);        // Soft Charcoal

    public static void show() {
        JFrame frame = new JFrame("Login - Cartfy");
        frame.setSize(400, 250);
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

        // Title
        JLabel titleLabel = new JLabel("Welcome to Cartfy");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username (Email)
        JLabel userLabel = new JLabel("Email:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(userLabel, gbc);

        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Buttons
        JButton loginButton = createStyledButton("Login", SUCCESS_COLOR);
        JButton registerButton = createStyledButton("Register", SECONDARY_COLOR);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, gbc);

        // ---- Button Actions ----
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String response = sendLoginRequest(email, password);
            JOptionPane.showMessageDialog(frame, response);

            if (response.contains("✅ Login successful")) {
                frame.dispose();
                CartfyGUI.createAndShowGUI();
            }
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            RegisterScreen.show();
        });

        frame.setVisible(true);
    }

    // API Call for login
    private static String sendLoginRequest(String email, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = BASE_URL + "/login?email=" + email + "&password=" + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            if (json.getBoolean("success")) {
                return json.getString("message");
            } else {
                return "❌ " + json.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error connecting to server";
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
