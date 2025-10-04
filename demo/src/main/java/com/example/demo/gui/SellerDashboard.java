package com.example.demo.gui;

import javax.swing.*;
import java.awt.*;

public class SellerDashboard extends JFrame {
    private final SellerService sellerService;
    private JLabel statsLabel;

    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);

    public SellerDashboard() {
        sellerService = new SellerService();
        initializeUI();
        updateStats();
    }

    private void initializeUI() {
        setTitle("Seller Dashboard - Cartify");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Seller Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statsLabel = new JLabel("Loading stats...");
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(224, 230, 228));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.addTab("📦 Products", new ProductPanel(sellerService));
        tabbedPane.addTab("📋 Orders", new OrderPanel(sellerService));

        tabbedPane.setBackground(CARD_BG);
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> updateStats());
    }

    private void updateStats() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalProducts = sellerService.getTotalProducts();
                int totalOrders = sellerService.getTotalOrders();
                int pendingOrders = sellerService.getPendingOrders();

                SwingUtilities.invokeLater(() -> statsLabel.setText(String.format(
                        "📊 Stats: %d Products | %d Total Orders | %d Pending",
                        totalProducts, totalOrders, pendingOrders
                )));
                return null;
            }
        }.execute();
    }
}