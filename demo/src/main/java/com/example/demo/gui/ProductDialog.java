package com.example.demo.gui;

import com.example.demo.model.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProductDialog extends JDialog {
    private Product product;
    private SellerService sellerService;
    private boolean success = false;

    private JTextField nameField, priceField, stockField, imageField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JButton saveButton, cancelButton;

    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public ProductDialog(Frame owner, Product product, SellerService sellerService) {
        super(owner, product == null ? "Add New Product" : "Edit Product", true);
        this.product = product;
        this.sellerService = sellerService;
        initializeUI();
    }

    private void initializeUI() {
        setSize(500, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Product Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        nameField = createTextField();
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        priceField = createTextField();
        formPanel.add(priceField, gbc);

        // Stock Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1;
        stockField = createTextField();
        formPanel.add(stockField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>(new String[]{
                "Electronics", "Furniture", "Clothing", "Books",
                "Home & Kitchen", "Sports", "Beauty", "Toys"
        });
        formPanel.add(categoryCombo, gbc);

        // Image URL
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createLabel("Image URL:"), gbc);
        gbc.gridx = 1;
        imageField = createTextField();
        formPanel.add(imageField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND);

        saveButton = createStyledButton("Save", SUCCESS_COLOR);
        cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Load product data if editing
        if (product != null) {
            loadProductData();
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });

        return button;
    }

    private void loadProductData() {
        nameField.setText(product.getName());
        descriptionArea.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getQuantity()));
        categoryCombo.setSelectedItem(product.getCategory());
        imageField.setText(product.getImage());
    }

    private void saveProduct() {
        try {
            // Validation
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (product == null) {
                product = new Product();
            }

            product.setName(nameField.getText().trim());
            product.setDescription(descriptionArea.getText().trim());
            product.setPrice(price);
            product.setQuantity(stock);
            product.setCategory((String) categoryCombo.getSelectedItem());
            product.setImage(imageField.getText().trim());

            boolean result;
            if (product.getId() == null) {
                result = sellerService.saveProduct(product);
            } else {
                result = sellerService.updateProduct(product);
            }

            if (result) {
                success = true;
                dispose();
                JOptionPane.showMessageDialog(this,
                        "Product " + (product.getId() == null ? "added" : "updated") + " successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save product", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for price and stock", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
