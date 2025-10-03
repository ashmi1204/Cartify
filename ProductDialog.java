package com.cartify;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

class ProductDialog extends JDialog {
    private Product product;
    private MockSellerService sellerService;
    private boolean success = false;

    // Make sure ALL fields are declared
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<String> categoryField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel imageLabel;

    public ProductDialog(Frame owner, Product product, MockSellerService sellerService) {
        super(owner, product == null ? "Add New Product" : "Edit Product", true);
        this.product = product;
        this.sellerService = sellerService;
        System.out.println("🚀 ProductDialog constructor - Product: " + (product == null ? "NEW" : product.getName()));
        initializeUI();
        System.out.println("✅ ProductDialog initialization complete");
    }

    private void initializeUI() {
        System.out.println("🔄 Initializing ProductDialog UI...");

        setSize(500, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ✅ STEP 1: Initialize ALL components first
        initializeComponents();

        // ✅ STEP 2: Create and setup panels
        JPanel imagePanel = createImagePanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        // ✅ STEP 3: Add panels to main panel
        mainPanel.add(imagePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ✅ STEP 4: Add main panel to dialog
        add(mainPanel);

        // ✅ STEP 5: Load data if editing existing product
        if (product != null) {
            loadProductData();
        }

        // ✅ STEP 6: Verify all components are initialized
        verifyComponents();

        System.out.println("✅ ProductDialog UI initialized successfully");
    }

    private void initializeComponents() {
        System.out.println("📦 Initializing components...");

        // Initialize ALL fields
        nameField = new JTextField(20);
        priceField = new JTextField(20);
        stockField = new JTextField(20);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryField = new JComboBox<>(new String[]{
                "Electronics", "Furniture", "Home & Kitchen", "Clothing",
                "Sports & Outdoors", "Books", "Toys", "Beauty"
        });
        categoryField.setEditable(true);

        saveButton = new JButton("💾 Save Product");
        cancelButton = new JButton("❌ Cancel");

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createTitledBorder("Product Image"));
        imageLabel.setPreferredSize(new Dimension(150, 150));

        System.out.println("✅ All components initialized");
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descriptionArea), gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // Style buttons
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setPreferredSize(new Dimension(140, 35));

        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(120, 35));

        // Add action listeners
        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> {
            System.out.println("❌ Dialog cancelled");
            dispose();
        });

        // Add document listener for real-time image updates
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateProductImage(); }
            public void removeUpdate(DocumentEvent e) { updateProductImage(); }
            public void insertUpdate(DocumentEvent e) { updateProductImage(); }
        });

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    private void verifyComponents() {
        System.out.println("🔍 Verifying component initialization:");
        System.out.println("   nameField: " + (nameField != null));
        System.out.println("   priceField: " + (priceField != null));
        System.out.println("   stockField: " + (stockField != null));
        System.out.println("   categoryField: " + (categoryField != null));
        System.out.println("   descriptionArea: " + (descriptionArea != null));
        System.out.println("   saveButton: " + (saveButton != null));
        System.out.println("   cancelButton: " + (cancelButton != null));
        System.out.println("   imageLabel: " + (imageLabel != null));

        if (nameField == null || priceField == null || stockField == null ||
                categoryField == null || descriptionArea == null) {
            System.out.println("❌ CRITICAL: Some components are null!");
            throw new RuntimeException("Component initialization failed");
        }
    }

    private void updateProductImage() {
        // Safety check
        if (nameField == null) {
            System.out.println("⚠️ nameField is null in updateProductImage");
            return;
        }

        String productName = nameField.getText().trim();
        if (!productName.isEmpty()) {
            ImageIcon productImage = ImageUtils.getProductImage(productName, 150, 150);
            imageLabel.setIcon(productImage);
            imageLabel.setText("");
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("Product Image Preview");
        }
    }

    private void loadProductData() {
        if (product != null) {
            System.out.println("📝 Loading product data: " + product.getName());

            // Safety checks
            if (nameField != null) nameField.setText(product.getName());
            if (descriptionArea != null) descriptionArea.setText(product.getDescription());
            if (priceField != null) priceField.setText(String.valueOf(product.getPrice()));
            if (stockField != null) stockField.setText(String.valueOf(product.getStockQuantity()));
            if (categoryField != null) categoryField.setSelectedItem(product.getCategory());

            updateProductImage();
        }
    }

    private void saveProduct() {
        System.out.println("💾 Save button clicked");

        // ✅ CRITICAL: Check if fields are initialized
        if (nameField == null || priceField == null || stockField == null || categoryField == null) {
            System.out.println("❌ CRITICAL ERROR: Form fields are null!");
            JOptionPane.showMessageDialog(this,
                    "Form initialization error. Please close and reopen the dialog.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            String category = categoryField.getSelectedItem().toString().trim();

            System.out.println("💾 Saving product:");
            System.out.println("   Name: " + name);
            System.out.println("   Price: $" + price);
            System.out.println("   Stock: " + stock);
            System.out.println("   Category: " + category);

            saveCustomCategory(category);

            // Validation
            if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this,
                        "Price must be positive and stock cannot be negative.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (product == null) {
                // Add new product
                System.out.println("➕ Creating new product...");
                product = new Product(0, name, description, price, stock, category, "seller1");
                boolean added = sellerService.addProduct(product);

                if (added) {
                    success = true;
                    JOptionPane.showMessageDialog(this,
                            "✅ Product '" + name + "' added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshProductPaneCategories();
                    dispose();
                }
            } else {
                // Update existing product
                System.out.println("✏️ Updating product: " + product.getName());
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setStockQuantity(stock);
                product.setCategory(category);

                boolean updated = sellerService.updateProduct(product);
                if (updated) {
                    success = true;
                    JOptionPane.showMessageDialog(this,
                            "✅ Product '" + name + "' updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for price and stock.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println("❌ Error in saveProduct: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ NEW: Refresh ProductPanel's categories
    private void refreshProductPaneCategories() {
        try {
            Container parent = getParent();
            while (parent != null && !(parent instanceof ProductPanel)) {
                parent = parent.getParent();
            }

            if (parent instanceof ProductPanel) {
                ProductPanel productPanel = (ProductPanel) parent;
                productPanel.refreshCategoryFilter();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not refresh ProductPanel categories: " + e.getMessage());
        }
    }


    private void saveCustomCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return;
            }

            String newCategory = category.trim();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) categoryField.getModel();

            // Check if category already exists
            boolean exists = false;
            for (int i = 0; i < model.getSize(); i++) {
                String existingCategory = model.getElementAt(i);
                if (existingCategory.equalsIgnoreCase(newCategory)) {
                    exists = true;
                    break;
                }
            }

            // Add if it doesn't exist
            if (!exists) {
                model.addElement(newCategory);
                System.out.println("✅ Added new category to dropdown: " + newCategory);
            }

        } catch (Exception e) {
            System.out.println("❌ Error saving category: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}