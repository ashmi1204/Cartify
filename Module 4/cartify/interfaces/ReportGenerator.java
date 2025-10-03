package com.cartify.interfaces;

import com.cartify.models.ReportRequest;
import com.cartify.models.SalesReport;
import java.util.List;
import java.util.Map;

public interface ReportGenerator {
    List<SalesReport> generateSalesReport(ReportRequest request);
    byte[] generatePDFReport(ReportRequest request);
    byte[] generateExcelReport(ReportRequest request);
    
    // FIXED: Use java.util.Date explicitly
    Map<String, Object> getSalesSummary(int sellerId, java.util.Date startDate, java.util.Date endDate);
    
    default String getReportHeader() {
        return "Cartify Sales Report";
    }
    
    static String getVersion() {
        return "1.0";
    }
}