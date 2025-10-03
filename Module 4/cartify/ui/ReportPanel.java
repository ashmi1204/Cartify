package com.cartify.ui;

import com.cartify.services.ReportServiceImpl;
import com.cartify.models.ReportRequest;
import com.cartify.models.SalesReport;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportPanel extends JPanel {
    private JComboBox<String> reportTypeCombo;
    private JTextField sellerIdField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton generateBtn;
    private JButton pdfBtn;
    private JButton excelBtn;
    private JTable reportTable;
    private JTextArea summaryArea;
    private ReportServiceImpl reportService;
    
    public ReportPanel() {
        reportService = new ReportServiceImpl();
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        
        panel.add(new JLabel("Seller ID:"));
        sellerIdField = new JTextField("1");
        panel.add(sellerIdField);
        
        panel.add(new JLabel("Start Date (yyyy-mm-dd):"));
        startDateField = new JTextField("2024-07-01");
        panel.add(startDateField);
        
        panel.add(new JLabel("End Date (yyyy-mm-dd):"));
        endDateField = new JTextField("2024-08-31");
        panel.add(endDateField);
        
        panel.add(new JLabel("Report Type:"));
        reportTypeCombo = new JComboBox<>(new String[]{"SALES", "PRODUCT", "INVENTORY"});
        panel.add(reportTypeCombo);
        
        generateBtn = new JButton("Generate Report");
        pdfBtn = new JButton("Export PDF");
        excelBtn = new JButton("Export Excel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateBtn);
        buttonPanel.add(pdfBtn);
        buttonPanel.add(excelBtn);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Report ID", "Product Name", "Quantity Sold", "Revenue", "Month"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        reportTable = new JTable(model);
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        panel.add(new JLabel("Sales Reports:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        summaryArea = new JTextArea(5, 50);
        summaryArea.setEditable(false);
        summaryArea.setBorder(BorderFactory.createTitledBorder("Sales Summary"));
        
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        
        pdfBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportPDF();
            }
        });
        
        excelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportExcel();
            }
        });
    }
    
    private void generateReport() {
        try {
            if (!validateInputs()) return;
            
            int sellerId = Integer.parseInt(sellerIdField.getText());
            Date startDate = parseDate(startDateField.getText());
            Date endDate = parseDate(endDateField.getText());
            String reportType = (String) reportTypeCombo.getSelectedItem();
            
            ReportRequest request = new ReportRequest(sellerId, startDate, endDate, reportType, "SCREEN");
            
            List<SalesReport> reports = reportService.generateSalesReport(request);
            displayReports(reports);
            
            Map<String, Object> summary = reportService.getSalesSummary(sellerId, startDate, endDate);
            displaySummary(summary);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportPDF() {
        try {
            if (!validateInputs()) return;
            
            int sellerId = Integer.parseInt(sellerIdField.getText());
            Date startDate = parseDate(startDateField.getText());
            Date endDate = parseDate(endDateField.getText());
            String reportType = (String) reportTypeCombo.getSelectedItem();
            
            ReportRequest request = new ReportRequest(sellerId, startDate, endDate, reportType, "PDF");
            byte[] pdfData = reportService.generatePDFReport(request);
            
            JOptionPane.showMessageDialog(this, 
                "PDF report generated successfully!\n" +
                "Size: " + pdfData.length + " bytes\n" +
                "Content preview created.",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error generating PDF: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportExcel() {
        try {
            if (!validateInputs()) return;
            
            int sellerId = Integer.parseInt(sellerIdField.getText());
            Date startDate = parseDate(startDateField.getText());
            Date endDate = parseDate(endDateField.getText());
            String reportType = (String) reportTypeCombo.getSelectedItem();
            
            ReportRequest request = new ReportRequest(sellerId, startDate, endDate, reportType, "EXCEL");
            byte[] excelData = reportService.generateExcelReport(request);
            
            JOptionPane.showMessageDialog(this, 
                "Excel report generated successfully!\n" +
                "Size: " + excelData.length + " bytes",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error generating Excel: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateInputs() {
        if (sellerIdField.getText().trim().isEmpty()) {
            showError("Please enter Seller ID");
            return false;
        }
        
        try {
            Integer.parseInt(sellerIdField.getText());
        } catch (NumberFormatException e) {
            showError("Seller ID must be a number");
            return false;
        }
        
        if (startDateField.getText().trim().isEmpty() || endDateField.getText().trim().isEmpty()) {
            showError("Please enter both start and end dates");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private Date parseDate(String dateStr) throws java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr);
    }
    
    private void displayReports(List<SalesReport> reports) {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);
        
        for (SalesReport report : reports) {
            model.addRow(new Object[]{
                report.getReportId(),
                report.getProductName(),
                report.getQuantitySold(),
                "₹" + report.getRevenue(),
                report.getMonth()
            });
        }
    }
    
    private void displaySummary(Map<String, Object> summary) {
        StringBuilder text = new StringBuilder();
        
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            text.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        summaryArea.setText(text.toString());
    }
    
    public static void main(String[] args) {
        // SIMPLE VERSION - No look and feel, just basic GUI
        JFrame frame = new JFrame("Cartify Reports System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        
        ReportPanel reportPanel = new ReportPanel();
        frame.add(reportPanel);
        
        frame.setVisible(true);
        
        System.out.println("Cartify Reports System started successfully!");
        System.out.println("Instructions:");
        System.out.println("1. Seller ID is already set to 1");
        System.out.println("2. Dates are pre-filled");
        System.out.println("3. Click 'Generate Report' to see sample data");
    }
}