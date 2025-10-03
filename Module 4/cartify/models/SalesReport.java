package com.cartify.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SalesReport implements Serializable {
    private int reportId;
    private int sellerId;
    private int productId;
    private String productName;
    private int quantitySold;
    private BigDecimal revenue;
    private Date reportDate;
    private String month;
    private static int reportCount = 0;
    public static final String REPORT_TYPE = "SALES_REPORT";
    
    public SalesReport() {
        reportCount++;
        this.reportDate = new Date();
    }
    
    public SalesReport(int sellerId, int productId, String productName, 
                      int quantitySold, BigDecimal revenue, String month) {
        this();
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
        this.month = month;
    }
    
    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
    
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    
    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }
    
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    
    public static int getReportCount() { return reportCount; }
    
    public void updateRevenue(BigDecimal newRevenue) {
        this.revenue = newRevenue;
    }
    
    public void updateRevenue(double newRevenue) {
        this.revenue = BigDecimal.valueOf(newRevenue);
    }
    
    @Override
    public String toString() {
        return "SalesReport{" +
                "reportId=" + reportId +
                ", productName='" + productName + '\'' +
                ", quantitySold=" + quantitySold +
                ", revenue=" + revenue +
                ", month='" + month + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SalesReport that = (SalesReport) obj;
        return reportId == that.reportId;
    }
}