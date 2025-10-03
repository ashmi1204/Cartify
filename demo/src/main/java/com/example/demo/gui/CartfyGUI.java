package com.example.demo.gui;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CartfyGUI {

    // --- Static state for logged-in user ---
    public static Long currentCustomerId;

    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color PRIMARY_HOVER = new Color(70, 108, 113);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color DANGER_COLOR = new Color(186, 108, 108);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);
    private static final Color TEXT_SECONDARY = new Color(130, 130, 130);
    private static final Color OUT_OF_STOCK_BG = new Color(238, 236, 233);

    private static final List<CartItem> cartItems = new ArrayList<>();
    private static JPanel productGridPanel; // Make this accessible to refresh

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::show);
    }

    public static void createAndShowGUI() {
        JFrame window = new JFrame("Cartfy - Your Shopping Destination");
        window.setSize(1200, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(BACKGROUND);
        window.setLocationRelativeTo(null);

        final double[] totalPrice = {0.0};

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(320, 0));
        cartPanel.setBackground(CARD_BG);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel cartHeader = new JLabel("Shopping Cart");
        cartHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        cartHeader.setForeground(TEXT_PRIMARY);
        cartHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(CARD_BG);

        JScrollPane cartScrollPane = new JScrollPane(cartItemsPanel);
        cartScrollPane.setBorder(BorderFactory.createEmptyBorder());
        cartScrollPane.getViewport().setBackground(CARD_BG);

        JPanel cartBottomPanel = new JPanel(new BorderLayout(10, 10));
        cartBottomPanel.setBackground(CARD_BG);
        cartBottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(TEXT_PRIMARY);

        JButton checkoutButton = createStyledButton("Checkout", SUCCESS_COLOR);
        JButton clearCartButton = createStyledButton("Clear Cart", DANGER_COLOR);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(clearCartButton);

        cartBottomPanel.add(totalLabel, BorderLayout.NORTH);
        cartBottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        cartPanel.add(cartHeader, BorderLayout.NORTH);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        clearCartButton.addActionListener(e -> {
            cartItems.clear();
            cartItemsPanel.removeAll();
            totalPrice[0] = 0.0;
            totalLabel.setText("Total: $0.00");
            cartItemsPanel.revalidate();
            cartItemsPanel.repaint();
            refreshProductGrid(cartItemsPanel, totalPrice, totalLabel); // Refresh to restore stock visuals
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Welcome to Cartfy");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Discover amazing products at great prices");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(224, 230, 228));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerTextPanel.setBackground(PRIMARY_COLOR);
        headerTextPanel.add(welcomeLabel);
        headerTextPanel.add(subtitleLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        productGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        productGridPanel.setBackground(BACKGROUND);
        productGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane productScrollPane = new JScrollPane(productGridPanel);
        productScrollPane.setBorder(BorderFactory.createEmptyBorder());
        productScrollPane.getViewport().setBackground(BACKGROUND);

        window.add(headerPanel, BorderLayout.NORTH);
        window.add(productScrollPane, BorderLayout.CENTER);
        window.add(cartPanel, BorderLayout.EAST);

        refreshProductGrid(cartItemsPanel, totalPrice, totalLabel);

        // --- CHECKOUT LOGIC ---
        checkoutButton.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (currentCustomerId == null) {
                JOptionPane.showMessageDialog(window, "Error: Not logged in. Please restart.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = placeOrderRequest();
            if (success) {
                JOptionPane.showMessageDialog(window,
                        "Order placed successfully!\nTotal: $" + String.format("%.2f", totalPrice[0]),
                        "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

                cartItems.clear();
                cartItemsPanel.removeAll();
                totalPrice[0] = 0.0;
                totalLabel.setText("Total: $0.00");
                cartItemsPanel.revalidate();
                cartItemsPanel.repaint();
                refreshProductGrid(cartItemsPanel, totalPrice, totalLabel);
            } else {
                JOptionPane.showMessageDialog(window, "Failed to place order. Please try again.", "Order Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        window.setVisible(true);
    }

    private static void refreshProductGrid(JPanel cartItemsPanel, double[] totalPrice, JLabel totalLabel) {
        productGridPanel.removeAll();
        List<Product> products = fetchProducts();
        if (products != null) {
            for (Product product : products) {
                JPanel card = createProductCard(product, cartItemsPanel, totalPrice, totalLabel);
                productGridPanel.add(card);
            }
        } else {
            JLabel errorLabel = new JLabel("Failed to load products. Is the backend server running?");
            errorLabel.setForeground(DANGER_COLOR);
            errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            productGridPanel.add(errorLabel);
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    // --- API CALL: PLACE ORDER ---
    private static boolean placeOrderRequest() {
        // Build the request body DTO
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("customerId", currentCustomerId);

        List<Map<String, Object>> items = cartItems.stream()
                .map(cartItem -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productId", cartItem.product.getId());
                    itemMap.put("quantity", cartItem.quantity);
                    itemMap.put("price", cartItem.product.getPrice());
                    return itemMap;
                })
                .collect(Collectors.toList());
        orderRequest.put("items", items);

        try {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(orderRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201; // 201 Created indicates success
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
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
        button.setPreferredSize(new Dimension(0, 45));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private static List<Product> fetchProducts() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/products"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            // This is a special type token to tell Gson to parse into a List of Products
            return gson.fromJson(response.body(), new TypeToken<List<Product>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JPanel createProductCard(Product product, JPanel cartItemsPanel, double[] totalPrice, JLabel totalLabel) {
        final int[] currentStock = {product.getQuantity()};
        boolean isOutOfStock = currentStock[0] <= 0;

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel imageLabel = new JLabel("Loading...", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(240, 238, 235));

        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(isOutOfStock ? TEXT_SECONDARY : TEXT_PRIMARY);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel priceStockPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        priceStockPanel.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        priceLabel.setForeground(isOutOfStock ? TEXT_SECONDARY : SECONDARY_COLOR);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel stockLabel = new JLabel(isOutOfStock ? "OUT OF STOCK" : "In Stock: " + currentStock[0]);
        stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stockLabel.setForeground(isOutOfStock ? DANGER_COLOR : SUCCESS_COLOR);
        stockLabel.setHorizontalAlignment(JLabel.CENTER);

        priceStockPanel.add(priceLabel);
        priceStockPanel.add(stockLabel);
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(priceStockPanel, BorderLayout.CENTER);
        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        if (!isOutOfStock) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (currentStock[0] <= 0) {
                        JOptionPane.showMessageDialog(null, "This product is out of stock!", "Out of Stock", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    CartItem existingItem = cartItems.stream()
                            .filter(item -> item.product.getId().equals(product.getId()))
                            .findFirst().orElse(null);

                    if (existingItem != null) {
                        existingItem.quantity++;
                        existingItem.updateDisplay();
                    } else {
                        CartItem newItem = new CartItem(product, cartItemsPanel, totalPrice, totalLabel);
                        cartItems.add(newItem);
                        cartItemsPanel.add(newItem.panel);
                    }

                    totalPrice[0] += product.getPrice();
                    totalLabel.setText("Total: $" + String.format("%.2f", totalPrice[0]));
                    currentStock[0]--;
                    stockLabel.setText(currentStock[0] <= 0 ? "OUT OF STOCK" : "In Stock: " + currentStock[0]);

                    if (currentStock[0] <= 0) {
                        card.setBackground(OUT_OF_STOCK_BG);
                        infoPanel.setBackground(OUT_OF_STOCK_BG);
                        priceStockPanel.setBackground(OUT_OF_STOCK_BG);
                        nameLabel.setForeground(TEXT_SECONDARY);
                        priceLabel.setForeground(TEXT_SECONDARY);
                        stockLabel.setForeground(DANGER_COLOR);
                        card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }

                    cartItemsPanel.revalidate();
                    cartItemsPanel.repaint();
                }

                public void mouseEntered(MouseEvent e) { if (currentStock[0] > 0) card.setBackground(new Color(245, 243, 240)); }
                public void mouseExited(MouseEvent e) { if (currentStock[0] > 0) card.setBackground(CARD_BG); }
            });
        }

        new Thread(() -> {
            try {
                URL imageUrl = new URL(product.getImage());
                Image image = ImageIO.read(imageUrl).getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("");
                    imageLabel.setIcon(new ImageIcon(image));
                });
            } catch (Exception e) {
                imageLabel.setText("Image Error");
            }
        }).start();

        return card;
    }

    static class CartItem {
        Product product;
        int quantity;
        JPanel panel;
        JLabel quantityLabel;
        JLabel priceLabel;

        CartItem(Product product, JPanel cartItemsPanel, double[] totalPrice, JLabel totalLabel) {
            this.product = product;
            this.quantity = 1;

            panel = new JPanel(new BorderLayout(10, 5));
            panel.setBackground(CARD_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 215, 210)),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
            ));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
            infoPanel.setBackground(CARD_BG);

            JLabel nameLabel = new JLabel(product.getName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            nameLabel.setForeground(TEXT_PRIMARY);

            quantityLabel = new JLabel("Qty: " + quantity);
            quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            quantityLabel.setForeground(TEXT_SECONDARY);

            priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice() * quantity));
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            priceLabel.setForeground(SECONDARY_COLOR);

            infoPanel.add(nameLabel);
            infoPanel.add(quantityLabel);
            infoPanel.add(priceLabel);

            JButton removeButton = new JButton("×");
            removeButton.setFont(new Font("SansSerif", Font.BOLD, 20));
            removeButton.setForeground(DANGER_COLOR);
            removeButton.setBackground(CARD_BG);
            removeButton.setBorderPainted(false);
            removeButton.setFocusPainted(false);
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            removeButton.addActionListener(e -> {
                totalPrice[0] -= product.getPrice() * quantity;
                totalLabel.setText("Total: $" + String.format("%.2f", Math.max(0, totalPrice[0])));
                cartItems.remove(this);
                cartItemsPanel.remove(panel);
                cartItemsPanel.revalidate();
                cartItemsPanel.repaint();
                refreshProductGrid(cartItemsPanel, totalPrice, totalLabel);
            });

            panel.add(infoPanel, BorderLayout.CENTER);
            panel.add(removeButton, BorderLayout.EAST);
        }

        void updateDisplay() {
            quantityLabel.setText("Qty: " + quantity);
            priceLabel.setText("$" + String.format("%.2f", product.getPrice() * quantity));
        }
    }

    // Simple models for GUI API communication. Not for database.
    public static class Product {
        private Long id;
        private String name;
        private String description;
        private double price;
        private int quantity;
        private String category;
        private String image;
        public Long getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getImage() { return image; }
        public int getQuantity() { return quantity; }
    }
}