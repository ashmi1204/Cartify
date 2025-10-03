package com.cartify.services;

import com.cartify.interfaces.ReportGenerator;
import com.cartify.models.ReportRequest;
import com.cartify.models.SalesReport;
import com.cartify.repositories.SalesReportRepository;
import com.cartify.exceptions.ReportGenerationException;
import java.math.BigDecimal;
import java.util.*;

public class ReportServiceImpl implements ReportGenerator {
    private SalesReportRepository repository;
    
    public ReportServiceImpl() {
        this.repository = new SalesReportRepository();
    }
    
    @Override
    public List<SalesReport> generateSalesReport(ReportRequest request) {
        try {
            Vector<SalesReport> reportsVector = repository.getSalesReportBySellerAndDateRange(
                request.getSellerId(), 
                request.getStartDate(), 
                request.getEndDate()
            );
            return new ArrayList<>(reportsVector);
        } catch (Exception e) {
            System.err.println("Error generating sales report: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    @Override
    public byte[] generatePDFReport(ReportRequest request) {
        try {
            List<SalesReport> reports = generateSalesReport(request);
            String pdfContent = generatePDFContent(reports, request);
            return pdfContent.getBytes();
        } catch (Exception e) {
            try {
                throw new ReportGenerationException("PDF generation failed", e);
            } catch (ReportGenerationException ex) {
                System.err.println("Report generation exception: " + ex.getMessage());
                return new byte[0];
            }
        }
    }
    
    @Override
    public byte[] generateExcelReport(ReportRequest request) {
        String excelContent = "Cartify Sales Report\n====================\n";
        List<SalesReport> reports = generateSalesReport(request);
        
        for (SalesReport report : reports) {
            excelContent += report.getProductName() + "\t" + 
                           report.getQuantitySold() + "\t" + 
                           "₹" + report.getRevenue() + "\t" + 
                           report.getMonth() + "\n";
        }
        
        return excelContent.getBytes();
    }
    
    @Override
    public Map<String, Object> getSalesSummary(int sellerId, java.util.Date startDate, java.util.Date endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            Vector<SalesReport> reports = repository.getSalesReportBySellerAndDateRange(sellerId, startDate, endDate);
            BigDecimal totalRevenue = repository.calculateTotalRevenue(reports);
            int totalUnits = calculateTotalUnits(reports);
            
            summary.put("totalRevenue", "₹" + totalRevenue);
            summary.put("totalUnitsSold", totalUnits);
            summary.put("reportCount", reports.size());
            summary.put("period", startDate + " to " + endDate);
            summary.put("status", "Success");
            
        } catch (Exception e) {
            summary.put("error", "Unable to generate summary: " + e.getMessage());
            summary.put("status", "Error");
        }
        
        return summary;
    }
    
    private int calculateTotalUnits(Vector<SalesReport> reports) {
        int total = 0;
        for (SalesReport report : reports) {
            total += report.getQuantitySold();
        }
        return total;
    }
    
    private String generatePDFContent(List<SalesReport> reports, ReportRequest request) {
        StringBuilder content = new StringBuilder();
        
        content.append("Cartify Sales Report\n");
        content.append("====================\n");
        content.append("Period: ").append(request.getStartDate()).append(" to ").append(request.getEndDate()).append("\n");
        content.append("Seller ID: ").append(request.getSellerId()).append("\n\n");
        
        content.append("Product Name\tQuantity Sold\tRevenue\tMonth\n");
        content.append("--------------------------------------------\n");
        
        for (int i = 0; i < reports.size(); i++) {
            SalesReport report = reports.get(i);
            
            String productName = (report.getProductName().length() > 15) ? 
                report.getProductName().substring(0, 12) + "..." : report.getProductName();
                
            content.append(productName).append("\t")
                   .append(report.getQuantitySold()).append("\t")
                   .append("₹").append(report.getRevenue()).append("\t")
                   .append(report.getMonth()).append("\n");
            
            if (i >= 49) {
                content.append("... (more records truncated)\n");
                break;
            }
        }
        
        return content.toString();
    }
}