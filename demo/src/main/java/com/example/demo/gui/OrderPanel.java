package com.example.demo.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors; // <-- ADD THIS IMPORT STATEMENT

class OrderPanel extends JPanel {
    private final SellerService sellerService;
    private final JTable orderTable;
    private final OrderTableModel tableModel;
    private final JComboBox<String> statusFilter;

    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(88, 129, 135);
    private static final Color SECONDARY_COLOR = new Color(178, 132, 102);
    private static final Color SUCCESS_COLOR = new Color(119, 158, 134);
    private static final Color BACKGROUND = new Color(245, 243, 240);
    private static final Color CARD_BG = new Color(252, 251, 249);

    public OrderPanel(SellerService sellerService) {
        this.sellerService = sellerService;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND);
        filterPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"});
        statusFilter.addActionListener(e -> filterOrders());
        filterPanel.add(statusFilter);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND);
        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        JButton updateStatusButton = createStyledButton("📝 Update Status", SUCCESS_COLOR);
        JButton viewDetailsButton = createStyledButton("👁️ View Details", PRIMARY_COLOR);

        refreshButton.addActionListener(e -> loadOrders());
        updateStatusButton.addActionListener(e -> updateOrderStatus());
        viewDetailsButton.addActionListener(e -> viewOrderDetails());

        buttonPanel.add(refreshButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(viewDetailsButton);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Center Table
        tableModel = new OrderTableModel();
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        orderTable.setBackground(CARD_BG);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadOrders();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private void loadOrders() {
        new SwingWorker<List<SellerService.Order>, Void>() {
            @Override
            protected List<SellerService.Order> doInBackground() {
                return sellerService.getAllOrders();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setOrders(get());
                    filterOrders(); // Apply current filter
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void filterOrders() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        tableModel.filter(selectedStatus);
    }

    private void updateOrderStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            SellerService.Order order = tableModel.getOrderAt(selectedRow);
            String[] statusOptions = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};
            String newStatus = (String) JOptionPane.showInputDialog(this,
                    "Select new status for Order #" + order.getId() + ":",
                    "Update Order Status",
                    JOptionPane.QUESTION_MESSAGE, null, statusOptions, order.getStatus());

            if (newStatus != null && !newStatus.equals(order.getStatus())) {
                if (sellerService.updateOrderStatus(order.getId(), newStatus)) {
                    loadOrders(); // Refresh all data
                    JOptionPane.showMessageDialog(this, "✅ Order status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to update order status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Please select an order to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            SellerService.Order order = tableModel.getOrderAt(selectedRow);
            StringBuilder details = new StringBuilder();
            details.append("📦 Order #").append(order.getId()).append("\n\n");
            details.append("👤 Customer: ").append(order.getCustomerName()).append("\n");
            details.append("📧 Email: ").append(order.getCustomerEmail()).append("\n");
            details.append("📊 Status: ").append(order.getStatus()).append("\n");

            Date orderDate = Date.from(order.getOrderDate().atZone(ZoneId.systemDefault()).toInstant());
            details.append("📅 Order Date: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(orderDate)).append("\n\n");
            details.append("🛒 Items:\n");

            for (SellerService.OrderItem item : order.getItems()) {
                details.append(String.format("   • %-20s (Qty: %d) @ $%-7.2f = $%.2f\n",
                        item.getProductName(), item.getQuantity(), item.getPrice(), item.getTotalPrice()));
            }
            details.append("\n💰 Total: $").append(String.format("%.2f", order.getTotalAmount()));

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(CARD_BG);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Order Details - #" + order.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Please select an order to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}

class OrderTableModel extends AbstractTableModel {
    private List<SellerService.Order> allOrders = new ArrayList<>();
    private List<SellerService.Order> filteredOrders = new ArrayList<>();
    private final String[] columnNames = {"Order ID", "Customer", "Total", "Status", "Order Date"};

    public void setOrders(List<SellerService.Order> orders) {
        this.allOrders = orders;
    }

    public void filter(String status) {
        if ("ALL".equalsIgnoreCase(status)) {
            this.filteredOrders = new ArrayList<>(allOrders);
        } else {
            this.filteredOrders = allOrders.stream()
                    .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public SellerService.Order getOrderAt(int row) {
        return filteredOrders.get(row);
    }

    @Override public int getRowCount() { return filteredOrders.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public String getColumnName(int column) { return columnNames.length > column ? columnNames[column] : ""; }
    @Override
    public Object getValueAt(int row, int column) {
        SellerService.Order order = filteredOrders.get(row);
        switch (column) {
            case 0: return order.getId();
            case 1: return order.getCustomerName();
            case 2: return String.format("$%.2f", order.getTotalAmount());
            case 3: return " " + order.getStatus();
            case 4:
                Date date = Date.from(order.getOrderDate().atZone(ZoneId.systemDefault()).toInstant());
                return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(date);
            default: return null;
        }
    }
}