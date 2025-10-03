package com.example.demo.gui;

import com.example.demo.model.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class ProductPanel extends JPanel {
    private SellerService sellerService;
    private JPanel productsPanel;
    private JButton addButton, refreshButton;
    private JComboBox<String> categoryFilter;
    private JTextField searchField;

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
        initializeUI();
        loadProducts();
        refreshCategoryFilter();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // Header with search and filters
        JPanel headerPanel = createHeaderPanel();

        // Products grid
        productsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productsPanel.setBackground(BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(1200, 80));

        // Left: Title
        JLabel titleLabel = new JLabel("Product Catalog");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Right: Search and filters
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(PRIMARY_COLOR);

        // Search field
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setText("Search products...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
        });

        // Category filter
        categoryFilter = new JComboBox<>(new String[] { "All Categories", "Electronics", "Furniture", "Clothing", "Books" });
        categoryFilter.setPreferredSize(new Dimension(150, 35));
        categoryFilter.addActionListener(e -> filterProducts());

        // Buttons
        addButton = createStyledButton("➕ Add Product", SUCCESS_COLOR);
        refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);

        addButton.addActionListener(e -> showProductDialog(null));
        refreshButton.addActionListener(e -> loadProducts());

        controlPanel.add(new JLabel("🔍"));
        controlPanel.add(searchField);
        controlPanel.add(categoryFilter);
        controlPanel.add(addButton);
        controlPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
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
        productsPanel.removeAll();
        List<Product> products = sellerService.getAllProducts();

        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productsPanel.add(productCard);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void filterProducts() {
        try {
            String searchText = searchField.getText().trim();
            String selectedCategory = (String) categoryFilter.getSelectedItem();

            if ("Search products...".equals(searchText)) {
                searchText = "";
            }

            productsPanel.removeAll();

            List<Product> allProducts = sellerService.getAllProducts();
            List<Product> filteredProducts = new ArrayList<>();

            for (Product product : allProducts) {
                boolean matches = true;

                // Category filter
                if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
                    if (!selectedCategory.equals(product.getCategory())) {
                        matches = false;
                    }
                }

                // Search filter
                if (matches && !searchText.isEmpty()) {
                    String searchLower = searchText.toLowerCase();
                    boolean nameMatch = product.getName().toLowerCase().contains(searchLower);
                    boolean descMatch = product.getDescription().toLowerCase().contains(searchLower);
                    boolean categoryMatch = product.getCategory().toLowerCase().contains(searchLower);

                    if (!nameMatch && !descMatch && !categoryMatch) {
                        matches = false;
                    }
                }

                if (matches) {
                    filteredProducts.add(product);
                }
            }

            if (filteredProducts.isEmpty()) {
                JLabel noResultsLabel = new JLabel(
                        "<html><div style='text-align:center; color: #666; padding: 40px;'>" +
                                "No products found matching your criteria.<br>" +
                                "Try different search terms or categories." +
                                "</div></html>"
                );
                noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
                noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                productsPanel.add(noResultsLabel);
            } else {
                for (Product product : filteredProducts) {
                    JPanel productCard = createProductCard(product);
                    productsPanel.add(productCard);
                }
            }

            productsPanel.revalidate();
            productsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            fallbackLoadProducts();
        }
    }

    private void fallbackLoadProducts() {
        try {
            productsPanel.removeAll();
            List<Product> products = sellerService.getAllProducts();

            for (Product product : products) {
                JPanel productCard = createProductCard(product);
                productsPanel.add(productCard);
            }

            productsPanel.revalidate();
            productsPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 350));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Product Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(new ImageIcon(getScaledImage(product.getImage(), 150, 150)));

        // Product Name
        JLabel nameLabel = new JLabel("<html><div style='text-align:left;'>" + product.getName() + "</div></html>");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);

        // Product Description
        String shortDesc = product.getDescription().length() > 60 ?
                product.getDescription().substring(0, 60) + "..." : product.getDescription();
        JLabel descLabel = new JLabel("<html><div style='text-align:left; color: #555;'>" + shortDesc + "</div></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(SECONDARY_COLOR);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Stock and Category
        JLabel stockLabel = new JLabel("In Stock: " + product.getQuantity());
        stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        stockLabel.setForeground(SUCCESS_COLOR);
        stockLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        categoryLabel.setForeground(TEXT_PRIMARY);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setBackground(CARD_BG);

        JButton editBtn = createStyledButton("Edit", SECONDARY_COLOR);
        editBtn.addActionListener(e -> showProductDialog(product));

        JButton deleteBtn = createStyledButton("Delete", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteProduct(product));

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        // Add mouse hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }
        });

        // Add components to card
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(buttonPanel);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    private Image getScaledImage(String imageUrl, int width, int height) {
        try {
            // For demo purposes - you might want to implement actual image loading
            ImageIcon icon = new ImageIcon(imageUrl);
            Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return image;
        } catch (Exception e) {
            // Return a placeholder image
            return new ImageIcon().getImage();
        }
    }

    private void deleteProduct(Product product) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + product.getName() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (sellerService.deleteProduct(product.getId())) {
                loadProducts();
                JOptionPane.showMessageDialog(this,
                        "Product '" + product.getName() + "' deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showProductDialog(Product product) {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            Frame parentFrame = null;

            if (parentWindow instanceof Frame) {
                parentFrame = (Frame) parentWindow;
            } else {
                Frame[] frames = Frame.getFrames();
                if (frames.length > 0) {
                    parentFrame = frames[0];
                }
            }

            ProductDialog dialog = new ProductDialog(parentFrame, product, sellerService);
            dialog.setLocationRelativeTo(parentFrame);
            dialog.setVisible(true);

            if (dialog.isSuccess()) {
                loadProducts();
                refreshCategoryFilter();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Dialog Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCategoryFilter() {
        try {
            List<Product> allProducts = sellerService.getAllProducts();
            Set<String> allCategories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            allCategories.add("Electronics");
            allCategories.add("Furniture");
            allCategories.add("Clothing");
            allCategories.add("Books");
            allCategories.add("Home & Kitchen");
            allCategories.add("Sports");

            for (Product product : allProducts) {
                if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                    allCategories.add(product.getCategory().trim());
                }
            }

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All Categories");

            for (String category : allCategories) {
                model.addElement(category);
            }

            String currentSelection = (String) categoryFilter.getSelectedItem();
            categoryFilter.setModel(model);

            if (currentSelection != null && model.getIndexOf(currentSelection) >= 0) {
                categoryFilter.setSelectedItem(currentSelection);
            } else {
                categoryFilter.setSelectedIndex(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
