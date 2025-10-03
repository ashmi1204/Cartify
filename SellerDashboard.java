package com.cartify;

import javax.swing.*;
import java.awt.*;

public class SellerDashboard extends JFrame {
    private final MockSellerService sellerService;
    private JTabbedPane tabbedPane;
    private ProductPanel productPanel;
    private OrderPanel orderPanel;
    private JLabel statsLabel;

    public SellerDashboard() {
        sellerService = new MockSellerService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Seller Dashboard - Cartify Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create header with statistics
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsLabel = new JLabel();
        updateStats();
        headerPanel.add(statsLabel);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Create panels
        productPanel = new ProductPanel(sellerService);
        orderPanel = new OrderPanel(sellerService);

        // Add tabs
        tabbedPane.addTab("📦 Products", productPanel);
        tabbedPane.addTab("📋 Orders", orderPanel);

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