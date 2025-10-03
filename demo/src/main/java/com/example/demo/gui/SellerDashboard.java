package com.example.demo.gui;

import javax.swing.*;
import java.awt.*;

public class SellerDashboard extends JFrame {
    private final SellerService sellerService;
    private JTabbedPane tabbedPane;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;
    private JLabel statsLabel;

    // Color Scheme matching main application
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color DANGER_COLOR = new Color(186, 108, 108);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public SellerDashboard() {
        sellerService = new SellerService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Seller Dashboard - Cartify");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);

        // Create header with statistics
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Seller Dashboard - Cartify");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(224, 230, 228));
        updateStats();

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsLabel, BorderLayout.EAST);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Create panels
        productPanel = new ProductPanel(sellerService);
        orderPanel = new OrderPanel(sellerService);

        // Add tabs with matching color scheme
        tabbedPane.addTab("📦 Products", productPanel);
        tabbedPane.addTab("📋 Orders", orderPanel);

        // Style the tabbed pane
        tabbedPane.setBackgroundAt(0, CARD_BG);
        tabbedPane.setBackgroundAt(1, CARD_BG);

        // Add components to frame
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Refresh stats when tabs change
        tabbedPane.addChangeListener(e -> updateStats());
    }

    private void updateStats() {
        int totalProducts = sellerService.getTotalProducts();
        int totalOrders = sellerService.getTotalOrders();
        int pendingOrders = sellerService.getPendingOrders();

        statsLabel.setText(String.format(
                "📊 Stats: %d Products | %d Total Orders | %d Pending Orders",
                totalProducts, totalOrders, pendingOrders
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SellerDashboard dashboard = new SellerDashboard();
            dashboard.setVisible(true);
        });
    }
}