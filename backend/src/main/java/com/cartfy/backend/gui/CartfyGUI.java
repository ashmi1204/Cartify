package com.cartfy.backend.gui;
// CartfyGUI.java
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

public class CartfyGUI {

    // Color Scheme - Soft & User-Friendly Palette
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);      // Muted Teal
    private static final Color PRIMARY_HOVER = new Color(70, 108, 113);     // Darker Teal
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);  // Warm Taupe
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);    // Sage Green
    private static final Color DANGER_COLOR = new Color(186, 108, 108);     // Muted Rose
    private static final Color BACKGROUND = new Color(245, 243, 240);       // Warm Off-White
    private static final Color CARD_BG = new Color(252, 251, 249);          // Soft Cream
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);        // Soft Charcoal
    private static final Color TEXT_SECONDARY = new Color(130, 130, 130);   // Medium Gray
    private static final Color OUT_OF_STOCK_BG = new Color(238, 236, 233);  // Light Warm Gray

    private static List<CartItem> cartItems = new ArrayList<>();
    private static Map<String, Integer> productStockMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> LoginScreen.show());
    }

    public static void createAndShowGUI() {
        JFrame window = new JFrame("Cartfy - Your Shopping Destination");
        window.setSize(1200, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(BACKGROUND);

        final double[] totalPrice = {0.0};

        // --- Left Panel (Cart) ---
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(320, 0));
        cartPanel.setBackground(CARD_BG);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cart header
        JLabel cartHeader = new JLabel("Shopping Cart");
        cartHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        cartHeader.setForeground(TEXT_PRIMARY);
        cartHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Cart items panel with vertical layout
        JPanel cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(CARD_BG);

        JScrollPane cartScrollPane = new JScrollPane(cartItemsPanel);
        cartScrollPane.setBorder(BorderFactory.createEmptyBorder());
        cartScrollPane.getViewport().setBackground(CARD_BG);

        // Total and buttons panel
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

        // Clear cart action
        clearCartButton.addActionListener(e -> {
            cartItems.clear();
            cartItemsPanel.removeAll();
            totalPrice[0] = 0.0;
            totalLabel.setText("Total: $0.00");
            cartItemsPanel.revalidate();
            cartItemsPanel.repaint();
        });

        // --- Top Panel (Header) ---
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

        // --- Product Grid Panel ---
        JPanel productGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        productGridPanel.setBackground(BACKGROUND);
        productGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane productScrollPane = new JScrollPane(productGridPanel);
        productScrollPane.setBorder(BorderFactory.createEmptyBorder());
        productScrollPane.getViewport().setBackground(BACKGROUND);

        // --- Add main components to the window ---
        window.add(headerPanel, BorderLayout.NORTH);
        window.add(productScrollPane, BorderLayout.CENTER);
        window.add(cartPanel, BorderLayout.EAST);

        // Fetch products and build the UI
        List<Product> products = fetchProducts();
        if (products != null) {
            for (Product product : products) {
                productStockMap.put(product.name, product.stock);
                JPanel card = createProductCard(product, cartItemsPanel, totalPrice, totalLabel, productGridPanel);
                productGridPanel.add(card);
            }
        } else {
            JLabel errorLabel = new JLabel("Failed to load products. Is the backend server running?");
            errorLabel.setForeground(DANGER_COLOR);
            errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            productGridPanel.add(errorLabel);
        }

        // Checkout button action
        checkoutButton.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (CartItem item : cartItems) {
                int currentStock = productStockMap.get(item.product.name);
                productStockMap.put(item.product.name, currentStock - item.quantity);
            }

            JOptionPane.showMessageDialog(window,
                    "Order placed successfully!\nTotal: $" + String.format("%.2f", totalPrice[0]) + "\nPayment: Cash on Delivery",
                    "Order Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            cartItems.clear();
            cartItemsPanel.removeAll();
            totalPrice[0] = 0.0;
            totalLabel.setText("Total: $0.00");
            cartItemsPanel.revalidate();
            cartItemsPanel.repaint();

            // Refresh product grid to show updated stock
            productGridPanel.removeAll();
            List<Product> updatedProducts = fetchProducts();
            if (updatedProducts != null) {
                for (Product product : updatedProducts) {
                    int stockOverride = productStockMap.get(product.name);
                    product.stock = stockOverride;
                    JPanel card = createProductCard(product, cartItemsPanel, totalPrice, totalLabel, productGridPanel);
                    productGridPanel.add(card);
                }
            }
            productGridPanel.revalidate();
            productGridPanel.repaint();
        });

        window.setVisible(true);
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
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
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
            String jsonBody = response.body();
            Gson gson = new Gson();
            return gson.fromJson(jsonBody, new TypeToken<List<Product>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JPanel createProductCard(Product product, JPanel cartItemsPanel, double[] totalPrice,
                                            JLabel totalLabel, JPanel productGridPanel) {
        boolean isOutOfStock = product.stock <= 0;

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Image panel
        JLabel imageLabel = new JLabel("Loading...", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(240, 238, 235));

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);

        JLabel nameLabel = new JLabel(product.name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(isOutOfStock ? TEXT_SECONDARY : TEXT_PRIMARY);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel priceStockPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        priceStockPanel.setBackground(isOutOfStock ? OUT_OF_STOCK_BG : CARD_BG);

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.price));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        priceLabel.setForeground(isOutOfStock ? TEXT_SECONDARY : SECONDARY_COLOR);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel stockLabel = new JLabel(isOutOfStock ? "OUT OF STOCK" : "In Stock: " + product.stock);
        stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stockLabel.setForeground(isOutOfStock ? DANGER_COLOR : SUCCESS_COLOR);
        stockLabel.setHorizontalAlignment(JLabel.CENTER);

        priceStockPanel.add(priceLabel);
        priceStockPanel.add(stockLabel);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(priceStockPanel, BorderLayout.CENTER);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        // Out of stock overlay
        if (isOutOfStock) {
            JLabel outOfStockOverlay = new JLabel("OUT OF STOCK");
            outOfStockOverlay.setFont(new Font("SansSerif", Font.BOLD, 16));
            outOfStockOverlay.setForeground(Color.WHITE);
            outOfStockOverlay.setBackground(new Color(186, 108, 108, 200));
            outOfStockOverlay.setOpaque(true);
            outOfStockOverlay.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setLayout(new BorderLayout());
            imageLabel.add(outOfStockOverlay, BorderLayout.NORTH);
        }

        // Load image
        new Thread(() -> {
            try {
                URL imageUrl = new URL(product.imageUrl);
                Image image = ImageIO.read(imageUrl).getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("");
                    imageLabel.setIcon(new ImageIcon(image));
                });
            } catch (Exception e) {
                imageLabel.setText("Image Error");
                imageLabel.setForeground(TEXT_SECONDARY);
            }
        }).start();

        // Add to cart interaction (only if in stock)
        if (!isOutOfStock) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (product.stock <= 0) {
                        JOptionPane.showMessageDialog(null, "This product is out of stock!", "Out of Stock", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Check if product already in cart
                    CartItem existingItem = null;
                    for (CartItem item : cartItems) {
                        if (item.product.name.equals(product.name)) {
                            existingItem = item;
                            break;
                        }
                    }

                    if (existingItem != null) {
                        existingItem.quantity++;
                        existingItem.updateDisplay();
                    } else {
                        CartItem newItem = new CartItem(product, cartItemsPanel, totalPrice, totalLabel);
                        cartItems.add(newItem);
                        cartItemsPanel.add(newItem.panel);
                    }

                    totalPrice[0] += product.price;
                    totalLabel.setText("Total: $" + String.format("%.2f", totalPrice[0]));
                    product.stock--;
                    stockLabel.setText(product.stock <= 0 ? "OUT OF STOCK" : "In Stock: " + product.stock);

                    if (product.stock <= 0) {
                        card.setBackground(OUT_OF_STOCK_BG);
                        infoPanel.setBackground(OUT_OF_STOCK_BG);
                        priceStockPanel.setBackground(OUT_OF_STOCK_BG);
                        nameLabel.setForeground(TEXT_SECONDARY);
                        priceLabel.setForeground(TEXT_SECONDARY);
                        stockLabel.setForeground(DANGER_COLOR);
                        card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                        JLabel outOfStockOverlay = new JLabel("OUT OF STOCK");
                        outOfStockOverlay.setFont(new Font("SansSerif", Font.BOLD, 16));
                        outOfStockOverlay.setForeground(Color.WHITE);
                        outOfStockOverlay.setBackground(new Color(186, 108, 108, 200));
                        outOfStockOverlay.setOpaque(true);
                        outOfStockOverlay.setHorizontalAlignment(JLabel.CENTER);
                        imageLabel.setLayout(new BorderLayout());
                        imageLabel.add(outOfStockOverlay, BorderLayout.NORTH);
                    }

                    cartItemsPanel.revalidate();
                    cartItemsPanel.repaint();
                }

                public void mouseEntered(MouseEvent e) {
                    if (product.stock > 0) {
                        card.setBackground(new Color(245, 243, 240));
                        infoPanel.setBackground(new Color(245, 243, 240));
                        priceStockPanel.setBackground(new Color(245, 243, 240));
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (product.stock > 0) {
                        card.setBackground(CARD_BG);
                        infoPanel.setBackground(CARD_BG);
                        priceStockPanel.setBackground(CARD_BG);
                    }
                }
            });
        }

        return card;
    }

    // Inner class to represent a cart item with remove functionality
    static class CartItem {
        Product product;
        int quantity;
        JPanel panel;
        JLabel quantityLabel;
        JLabel priceLabel;
        JPanel cartItemsPanel;
        double[] totalPrice;
        JLabel totalLabel;

        CartItem(Product product, JPanel cartItemsPanel, double[] totalPrice, JLabel totalLabel) {
            this.product = product;
            this.quantity = 1;
            this.cartItemsPanel = cartItemsPanel;
            this.totalPrice = totalPrice;
            this.totalLabel = totalLabel;

            panel = new JPanel(new BorderLayout(10, 5));
            panel.setBackground(CARD_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 215, 210)),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
            ));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
            infoPanel.setBackground(CARD_BG);

            JLabel nameLabel = new JLabel(product.name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            nameLabel.setForeground(TEXT_PRIMARY);

            quantityLabel = new JLabel("Qty: " + quantity);
            quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            quantityLabel.setForeground(TEXT_SECONDARY);

            priceLabel = new JLabel("$" + String.format("%.2f", product.price * quantity));
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
            removeButton.setPreferredSize(new Dimension(40, 40));

            removeButton.addActionListener(e -> {
                totalPrice[0] -= product.price * quantity;
                totalLabel.setText("Total: $" + String.format("%.2f", totalPrice[0]));
                product.stock += quantity;
                cartItems.remove(this);
                cartItemsPanel.remove(panel);
                cartItemsPanel.revalidate();
                cartItemsPanel.repaint();
            });

            panel.add(infoPanel, BorderLayout.CENTER);
            panel.add(removeButton, BorderLayout.EAST);
        }

        void updateDisplay() {
            quantityLabel.setText("Qty: " + quantity);
            priceLabel.setText("$" + String.format("%.2f", product.price * quantity));
        }
    }
}

