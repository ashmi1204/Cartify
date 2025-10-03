
package com.cartify;

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
    private MockSellerService sellerService;
    private JPanel productsPanel;
    private JButton addButton, refreshButton;
    private JComboBox<String> categoryFilter;
    private JTextField searchField;

    public ProductPanel(MockSellerService sellerService) {
        this.sellerService = sellerService;
        initializeUI();
        loadProducts();
        refreshCategoryFilter();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Header with search and filters (Amazon-style)
        JPanel headerPanel = createHeaderPanel();

        // Products grid (Amazon card layout)
        productsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        productsPanel.setBackground(new Color(240, 242, 245));
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(35, 47, 62));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(1200, 80));

        // Left: Title
        JLabel titleLabel = new JLabel("Product Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(35, 47, 62));
        titlePanel.add(titleLabel);

        // Right: Search and filters
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(new Color(35, 47, 62));

        // Search field
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setText("Search products...");

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Real-time search as you type
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterProducts();
            }
        });
        // Category filter
        categoryFilter = new JComboBox<>(new String[] { "All Categories", "Electronics", "Furniture", "Accessories" });
        categoryFilter.setPreferredSize(new Dimension(150, 35));
        categoryFilter.addActionListener(e -> filterProducts());

        // Buttons
        addButton = createStyledButton("➕ Add Product", new Color(255, 153, 0));
        refreshButton = createStyledButton("🔄 Refresh", new Color(102, 102, 102));

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
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        System.out.println("📦 Loaded " + products.size() + " products in card view");
    }
    private void filterProducts() {
        System.out.println("🔍 Filtering products...");

        try {
            String searchText = searchField.getText().trim();
            String selectedCategory = (String) categoryFilter.getSelectedItem();

            // Don't filter if it's the placeholder text
            if ("Search products...".equals(searchText)) {
                searchText = "";
            }

            System.out.println("   Search: '" + searchText + "', Category: " + selectedCategory);

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

                // Search filter (only if category matches)
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

            System.out.println("   Displaying " + filteredProducts.size() + " of " + allProducts.size() + " products");

            // Show message if no products found
            if (filteredProducts.isEmpty()) {
                JLabel noResultsLabel = new JLabel(
                        "<html><div style='text-align:center; color: #666; padding: 40px;'>" +
                                "No products found matching your criteria.<br>" +
                                "Try different search terms or categories." +
                                "</div></html>"
                );
                noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                productsPanel.add(noResultsLabel);
            } else {
                // Add product cards
                for (Product product : filteredProducts) {
                    JPanel productCard = createProductCard(product);
                    productsPanel.add(productCard);
                }
            }

            productsPanel.revalidate();
            productsPanel.repaint();

        } catch (Exception e) {
            System.out.println("❌ Error in filterProducts: " + e.getMessage());
            e.printStackTrace();

            // Fallback to loading all products
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
            System.out.println("📦 Fallback: Loaded " + products.size() + " products");
        } catch (Exception e) {
            System.out.println("❌ Even fallback failed: " + e.getMessage());
        }
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 350));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 221, 221), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Product Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon productImage = product.getProductImage(150, 150);
        imageLabel.setIcon(productImage);

        // Product Name
        JLabel nameLabel = new JLabel("<html><div style='text-align:left;'>" + product.getName() + "</div></html>");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Product Description (truncated)
        String shortDesc = product.getDescription().length() > 60 ? product.getDescription().substring(0, 60) + "..."
                : product.getDescription();
        JLabel descLabel = new JLabel(
                "<html><div style='text-align:left; color: #555;'>" + shortDesc + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(177, 39, 4));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Stock and Category
        JLabel stockLabel = new JLabel("In Stock: " + product.getStockQuantity());
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        stockLabel.setForeground(Color.GREEN);
        stockLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(255, 153, 0));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Arial", Font.BOLD, 11));
        editBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // editBtn.addActionListener(e -> showProductDialog(product));
        editBtn.addActionListener(e -> {
            System.out.println("✅ EDIT BUTTON CLICKED: " + product.getName());
            showProductDialog(product);
        });

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(204, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 11));
        deleteBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        deleteBtn.addActionListener(e -> deleteProduct(product));

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        // Add mouse hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 153, 0), 2),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(221, 221, 221), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }
        });

        // Add components to card
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

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

//    private void loadProduct() {
//        // Implement filtering logic here
//        loadProducts();
//    }

    private void showProductDialog(Product product) {
        System.out.println("🎯 showProductDialog called!");
        System.out.println("   Product: " + (product == null ? "NEW PRODUCT" : product.getName()));

        try {
            // Get the parent window
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            System.out.println("   Parent window: " + (parentWindow != null ? "Found" : "NULL"));

            Frame parentFrame = null;
            if (parentWindow instanceof Frame) {
                parentFrame = (Frame) parentWindow;
                System.out.println("   Using parent frame: " + parentFrame.getTitle());
            } else {
                System.out.println("   Parent is not a Frame, finding alternative...");
                // Try to get any available frame
                Frame[] frames = Frame.getFrames();
                if (frames.length > 0) {
                    parentFrame = frames[0];
                    System.out.println("   Using first available frame");
                }
            }

            // Create and show the dialog
            System.out.println("   Creating ProductDialog...");
            ProductDialog dialog = new ProductDialog(parentFrame, product, sellerService);

            // Center the dialog
            dialog.setLocationRelativeTo(parentFrame);

            System.out.println("   Showing dialog...");
            dialog.setVisible(true); // This will block until dialog is closed

            System.out.println("   Dialog closed. Success: " + dialog.isSuccess());

            if (dialog.isSuccess()) {
                System.out.println("✅ Product saved successfully!");
                loadProducts(); // Refresh the product list
            } else {
                System.out.println("❌ Product operation cancelled");
            }

        } catch (Exception e) {
            System.out.println("💥 ERROR in showProductDialog: " + e.getMessage());
            e.printStackTrace();

            // Show error to user
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Dialog Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    // ✅ ADD THIS METHOD TO PRODUCTPANEL
    public void refreshCategoryFilter() {
        try {
            System.out.println("🔄 Refreshing category filter...");

            // Get all products to extract unique categories
            List<Product> allProducts = sellerService.getAllProducts();
            Set<String> allCategories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            // Add default categories
            allCategories.add("Electronics");
            allCategories.add("Furniture");
            allCategories.add("Home & Kitchen");
            allCategories.add("Clothing");
            allCategories.add("Sports & Outdoors");
            allCategories.add("Books");
            allCategories.add("Toys");
            allCategories.add("Beauty");

            // Add categories from existing products
            for (Product product : allProducts) {
                if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                    allCategories.add(product.getCategory().trim());
                }
            }

            // Update the filter model
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All Categories"); // Always first

            // Add all categories alphabetically
            for (String category : allCategories) {
                model.addElement(category);
            }

            // Get currently selected category before updating
            String currentSelection = (String) categoryFilter.getSelectedItem();

            // Update the model
            categoryFilter.setModel(model);

            // Restore selection if it still exists, otherwise select "All Categories"
            if (currentSelection != null && model.getIndexOf(currentSelection) >= 0) {
                categoryFilter.setSelectedItem(currentSelection);
            } else {
                categoryFilter.setSelectedIndex(0);
            }

            System.out.println("✅ Category filter refreshed with " + (model.getSize() - 1) + " categories");

        } catch (Exception e) {
            System.out.println("❌ Error refreshing category filter: " + e.getMessage());
        }
    }
}

// Custom layout for wrapping product cards
class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0)
                targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            int maxWidth = targetWidth - hgap * 2;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            for (Component comp : target.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();

                    if (rowWidth + d.width > maxWidth) {
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0)
                        rowWidth += hgap;
                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight;

            return dim;
        }
    }
}
