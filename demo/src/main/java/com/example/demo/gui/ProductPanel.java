package com.example.demo.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class ProductPanel extends JPanel {
    private final SellerService sellerService;
    private final JPanel productsPanel;
    private final JComboBox<String> categoryFilter;
    private final JTextField searchField;

    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color DANGER_COLOR = new Color(186, 108, 108);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public ProductPanel(SellerService sellerService) {
        this.sellerService = sellerService;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // Header with search and filters
        JPanel headerPanel = createHeaderPanel();

        // Products grid
        productsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchField = new JTextField(20);
        categoryFilter = new JComboBox<>();
        setupControls(headerPanel);

        loadProducts();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Product Catalog");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private void setupControls(JPanel headerPanel) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(PRIMARY_COLOR);

        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setText("Search products...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        categoryFilter.setPreferredSize(new Dimension(150, 35));
        categoryFilter.addActionListener(e -> filterProducts());

        JButton addButton = createStyledButton("➕ Add Product", SUCCESS_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        addButton.addActionListener(e -> showProductDialog(null));
        refreshButton.addActionListener(e -> loadProducts());

        controlPanel.add(searchField);
        controlPanel.add(categoryFilter);
        controlPanel.add(addButton);
        controlPanel.add(refreshButton);
        headerPanel.add(controlPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(backgroundColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(backgroundColor); }
        });
        return button;
    }

    private void loadProducts() {
        new SwingWorker<List<SellerService.Product>, Void>() {
            @Override
            protected List<SellerService.Product> doInBackground() {
                return sellerService.getAllProducts();
            }

            @Override
            protected void done() {
                try {
                    productsPanel.removeAll();
                    List<SellerService.Product> products = get();
                    if (products.isEmpty()) {
                        showNoProductsMessage();
                    } else {
                        for (SellerService.Product product : products) {
                            productsPanel.add(createProductCard(product));
                        }
                    }
                    refreshCategoryFilter(products);
                    productsPanel.revalidate();
                    productsPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoProductsMessage();
                }
            }
        }.execute();
    }

    private void filterProducts() {
        String searchText = searchField.getText().trim().toLowerCase();
        if ("search products...".equals(searchText)) {
            searchText = "";
        }
        String selectedCategory = (String) categoryFilter.getSelectedItem();

        productsPanel.removeAll();
        List<SellerService.Product> allProducts = sellerService.getAllProducts();
        final String finalSearchText = searchText;

        allProducts.stream()
                .filter(product -> {
                    boolean categoryMatch = "All Categories".equals(selectedCategory) || selectedCategory.equals(product.getCategory());
                    boolean searchMatch = finalSearchText.isEmpty() ||
                            product.getName().toLowerCase().contains(finalSearchText) ||
                            product.getCategory().toLowerCase().contains(finalSearchText);
                    return categoryMatch && searchMatch;
                })
                .forEach(product -> productsPanel.add(createProductCard(product)));

        if (productsPanel.getComponentCount() == 0) {
            showNoProductsMessage();
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void showNoProductsMessage() {
        productsPanel.removeAll();
        JLabel noResultsLabel = new JLabel("No products found.");
        noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        noResultsLabel.setForeground(TEXT_PRIMARY);
        productsPanel.add(noResultsLabel);
    }

    private JPanel createProductCard(SellerService.Product product) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(250, 350));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15))
        );

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(220, 150));
        try {
            URL url = new URL(product.getImage());
            ImageIcon icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (MalformedURLException e) {
            imageLabel.setText("No Image");
        }

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(SECONDARY_COLOR);

        JLabel stockLabel = new JLabel("In Stock: " + product.getQuantity());
        stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        stockLabel.setForeground(product.getQuantity() > 0 ? SUCCESS_COLOR : DANGER_COLOR);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLabel);

        // Action Panel
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(CARD_BG);
        JButton editBtn = createStyledButton("Edit", SECONDARY_COLOR);
        JButton deleteBtn = createStyledButton("Delete", DANGER_COLOR);
        editBtn.addActionListener(e -> showProductDialog(product));
        deleteBtn.addActionListener(e -> deleteProduct(product));
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private void deleteProduct(SellerService.Product product) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + product.getName() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return sellerService.deleteProduct(product.getId());
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            loadProducts();
                        } else {
                            JOptionPane.showMessageDialog(ProductPanel.this, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    private void showProductDialog(SellerService.Product product) {
        ProductDialog dialog = new ProductDialog((Frame) SwingUtilities.getWindowAncestor(this), product, sellerService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadProducts();
        }
    }

    private void refreshCategoryFilter(List<SellerService.Product> products) {
        Set<String> categories = new TreeSet<>();
        for (SellerService.Product p : products) {
            categories.add(p.getCategory());
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All Categories");
        categories.forEach(model::addElement);
        categoryFilter.setModel(model);
    }
}