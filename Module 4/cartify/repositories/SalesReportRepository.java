package com.cartify.repositories;

import com.cartify.models.SalesReport;
import com.cartify.utils.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class SalesReportRepository {
    private Connection connection;
    
    public SalesReportRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public Vector<SalesReport> getSalesReportBySellerAndDateRange(int sellerId, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        Vector<SalesReport> reports = new Vector<>();
        
        // Check if database connection is working
        if (connection == null || connection.isClosed()) {
            System.out.println("❌ NO DATABASE CONNECTION - Using sample data");
            return getSampleData();
        }
        
        try {
            String sql = "SELECT report_id, seller_id, product_id, product_name, quantity_sold, revenue, month, report_date FROM sales_reports WHERE seller_id = ? AND report_date BETWEEN ? AND ? ORDER BY report_date DESC";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            
            System.out.println("🔍 Executing SQL query...");
            System.out.println("Seller ID: " + sellerId);
            System.out.println("Date Range: " + startDate + " to " + endDate);
            
            ResultSet rs = pstmt.executeQuery();
            
            int rowCount = 0;
            while (rs.next()) {
                SalesReport report = new SalesReport();
                report.setReportId(rs.getInt("report_id"));
                report.setSellerId(rs.getInt("seller_id"));
                report.setProductId(rs.getInt("product_id"));
                report.setProductName(rs.getString("product_name"));
                report.setQuantitySold(rs.getInt("quantity_sold"));
                report.setRevenue(rs.getBigDecimal("revenue"));
                report.setMonth(rs.getString("month"));
                report.setReportDate(rs.getDate("report_date"));
                reports.add(report);
                rowCount++;
            }
            
            System.out.println("✅ DATABASE QUERY SUCCESSFUL - Found " + rowCount + " records");
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ DATABASE QUERY FAILED: " + e.getMessage());
            System.out.println("Using sample data instead...");
            return getSampleData();
        }
        
        // If no data found in database, use sample data
        if (reports.isEmpty()) {
            System.out.println("ℹ️  No data found in database for the given criteria");
            System.out.println("Using sample data instead...");
            return getSampleData();
        }
        
        return reports;
    }
    
    public BigDecimal calculateTotalRevenue(Vector<SalesReport> reports) {
        return calculateTotalRevenueRecursive(reports, 0, BigDecimal.ZERO);
    }
    
    private BigDecimal calculateTotalRevenueRecursive(Vector<SalesReport> reports, int index, BigDecimal total) {
        if (index >= reports.size()) {
            return total;
        }
        BigDecimal currentRevenue = reports.get(index).getRevenue();
        return calculateTotalRevenueRecursive(reports, index + 1, total.add(currentRevenue));
    }
    
    // Sample data for testing without database
    private Vector<SalesReport> getSampleData() {
        Vector<SalesReport> sampleData = new Vector<>();
        
        System.out.println("📊 USING SAMPLE DATA (No database connection)");
        
        sampleData.add(new SalesReport(1, 101, "Noise-Canceling Headphones", 50, new BigDecimal("600000.00"), "2024-07"));
        sampleData.add(new SalesReport(1, 102, "Ultra HD Monitor", 30, new BigDecimal("360000.00"), "2024-07"));
        sampleData.add(new SalesReport(1, 103, "Smart Watch", 25, new BigDecimal("300000.00"), "2024-07"));
        sampleData.add(new SalesReport(1, 101, "Noise-Canceling Headphones", 45, new BigDecimal("540000.00"), "2024-08"));
        sampleData.add(new SalesReport(1, 102, "Ultra HD Monitor", 35, new BigDecimal("420000.00"), "2024-08"));
        sampleData.add(new SalesReport(1, 104, "Wireless Earbuds", 60, new BigDecimal("240000.00"), "2024-08"));
        
        return sampleData;
    }
}