package com.example.demo.gui;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class OrderPanel extends JPanel {
    private SellerService sellerService;
    private JTable orderTable;
    private OrderTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton, updateStatusButton, viewDetailsButton;

    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color DANGER_COLOR = new Color(186, 108, 108);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);
    private static final Color TEXT_PRIMARY = new Color(62, 62, 64);

    public OrderPanel(SellerService sellerService) {
        this.sellerService = sellerService;
        initializeUI();
        loadOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // TOP PANEL: Filter and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND);
        filterPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"});
        statusFilter.addActionListener(e -> filterOrders());
        filterPanel.add(statusFilter);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND);

        refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        updateStatusButton = createStyledButton("📝 Update Status", SUCCESS_COLOR);
        viewDetailsButton = createStyledButton("👁️ View Details", PRIMARY_COLOR);

        refreshButton.addActionListener(e -> loadOrders());
        updateStatusButton.addActionListener(e -> updateOrderStatus());
        viewDetailsButton.addActionListener(e -> viewOrderDetails());

        buttonPanel.add(refreshButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(viewDetailsButton);

        // Add filter and buttons to top panel
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // CENTER: Table
        tableModel = new OrderTableModel();
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        orderTable.setBackground(CARD_BG);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Orders List"));

        // Add all components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(bgColor); }
        });

        return button;
    }

    private void loadOrders() {
        tableModel.setOrders(sellerService.getAllOrders());
    }

    private void filterOrders() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        tableModel.setOrders(sellerService.getOrdersByStatus(selectedStatus));
    }

    private void updateOrderStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            Order order = tableModel.getOrderAt(selectedRow);
            String[] statusOptions = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};
            String newStatus = (String) JOptionPane.showInputDialog(this,
                    "Select new status for Order #" + order.getId() + ":",
                    "Update Order Status",
                    JOptionPane.QUESTION_MESSAGE, null, statusOptions, order.getStatus());

            if (newStatus != null && !newStatus.equals(order.getStatus())) {
                if (sellerService.updateOrderStatus(order.getId(), newStatus)) {
                    loadOrders();
                    JOptionPane.showMessageDialog(this,
                            "✅ Order status updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Failed to update order status.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Please select an order to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            Order order = tableModel.getOrderAt(selectedRow);
            showOrderDetails(order);
        } else {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Please select an order to view details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("📦 Order #").append(order.getId()).append("\n\n");
        details.append("👤 Customer: ").append(order.getCustomerName()).append("\n");
        details.append("📧 Email: ").append(order.getCustomerEmail()).append("\n");
        details.append("📊 Status: ").append(order.getStatus()).append("\n");
        details.append("📅 Order Date: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(
                java.sql.Timestamp.valueOf(order.getOrderDate()))).append("\n");
        details.append("💰 Total Amount: $").append(String.format("%.2f", order.getTotalAmount())).append("\n\n");
        details.append("🛒 Items:\n");

        for (OrderItem item : order.getItems()) {
            details.append("   • ").append(item.getProductName())
                    .append(" (Qty: ").append(item.getQuantity())
                    .append(") - $").append(String.format("%.2f", item.getPrice()))
                    .append(" each = $").append(String.format("%.2f", item.getTotalPrice()))
                    .append("\n");
        }

        details.append("\n💵 Total: $").append(String.format("%.2f", order.getTotalAmount()));

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(CARD_BG);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Order Details - #" + order.getId(),
                JOptionPane.INFORMATION_MESSAGE);
    }
}

class OrderTableModel extends AbstractTableModel {
    private List<Order> orders = new ArrayList<>();
    private String[] columnNames = {"Order ID", "Customer", "Email", "Total Amount", "Status", "Order Date"};

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        fireTableDataChanged();
    }

    public Order getOrderAt(int row) {
        return orders.get(row);
    }

    @Override
    public int getRowCount() {
        return orders.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Order order = orders.get(row);
        switch (column) {
            case 0: return order.getId();
            case 1: return order.getCustomerName();
            case 2: return order.getCustomerEmail();
            case 3: return String.format("$%.2f", order.getTotalAmount());
            case 4:
                String status = order.getStatus();
                switch (status) {
                    case "PENDING": return "⏳ " + status;
                    case "CONFIRMED": return "✅ " + status;
                    case "SHIPPED": return "🚚 " + status;
                    case "DELIVERED": return "📦 " + status;
                    case "CANCELLED": return "❌ " + status;
                    default: return status;
                }
            case 5: return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(
                    java.sql.Timestamp.valueOf(order.getOrderDate()));
            default: return null;
        }
    }
}