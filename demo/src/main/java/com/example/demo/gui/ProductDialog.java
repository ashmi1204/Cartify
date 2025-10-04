package com.example.demo.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProductDialog extends JDialog {
    private SellerService.Product product; // Use the nested Product class from SellerService
    private final SellerService sellerService;
    private boolean success = false;

    private JTextField nameField, priceField, stockField, imageField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;

    // Color Scheme
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public ProductDialog(Frame owner, SellerService.Product product, SellerService sellerService) {
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

        // Add form fields
        gbc.gridy = 0;
        nameField = addFormField(formPanel, gbc, "Product Name:");
        gbc.gridy = 1;
        descriptionArea = new JTextArea(3, 20);
        addFormField(formPanel, gbc, "Description:", new JScrollPane(descriptionArea));
        gbc.gridy = 2;
        priceField = addFormField(formPanel, gbc, "Price ($):");
        gbc.gridy = 3;
        stockField = addFormField(formPanel, gbc, "Stock Quantity:");
        gbc.gridy = 4;
        categoryCombo = new JComboBox<>(new String[]{
                "Electronics", "Furniture", "Clothing", "Books",
                "Home & Kitchen", "Sports", "Beauty", "Toys"
        });
        addFormField(formPanel, gbc, "Category:", categoryCombo);
        gbc.gridy = 5;
        imageField = addFormField(formPanel, gbc, "Image URL:");

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND);

        JButton saveButton = createStyledButton("Save", SUCCESS_COLOR);
        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        if (product != null) {
            loadProductData();
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String labelText) {
        JTextField textField = new JTextField(20);
        addFormField(panel, gbc, labelText, textField);
        return textField;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);
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
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (product == null) {
                product = new SellerService.Product();
            }

            product.setName(nameField.getText().trim());
            product.setDescription(descriptionArea.getText().trim());
            product.setPrice(price);
            product.setQuantity(stock);
            product.setCategory((String) categoryCombo.getSelectedItem());
            product.setImage(imageField.getText().trim());

            boolean result = (product.getId() == null)
                    ? sellerService.saveProduct(product)
                    : sellerService.updateProduct(product);

            if (result) {
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save the product to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and stock.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}