package com.cartify.models;

import java.util.Date;

public class ReportRequest {
    private int sellerId;
    private Date startDate;
    private Date endDate;
    private String reportType;
    private String format;
    
    public ReportRequest() {}
    
    public ReportRequest(int sellerId, Date startDate, Date endDate, 
                        String reportType, String format) {
        this.sellerId = sellerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportType = reportType;
        this.format = format;
    }
    
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}