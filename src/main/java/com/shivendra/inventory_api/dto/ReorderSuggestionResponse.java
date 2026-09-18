package com.shivendra.inventory_api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ReorderSuggestionResponse {

    private String productName;
    private Integer currentQuantity;
    private Double avgDailyUsage;
    private Double daysUntilStockout;
    private Integer suggestedReorderQty;
    private String urgency;
    private String message;
    private LocalDateTime generatedAt;

    public ReorderSuggestionResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    public ReorderSuggestionResponse(String productName, Integer currentQuantity, Double avgDailyUsage,
                                     Double daysUntilStockout, Integer suggestedReorderQty, String urgency) {
        this();
        this.productName = productName;
        this.currentQuantity = currentQuantity;
        this.avgDailyUsage = avgDailyUsage;
        this.daysUntilStockout = daysUntilStockout;
        this.suggestedReorderQty = suggestedReorderQty;
        this.urgency = urgency;
    }

    public static ReorderSuggestionResponse fromMap(Map<String, Object> map) {
        ReorderSuggestionResponse response = new ReorderSuggestionResponse();
        response.setProductName((String) map.get("productName"));
        response.setCurrentQuantity((Integer) map.get("currentQuantity"));
        response.setAvgDailyUsage((Double) map.get("avgDailyUsage"));
        response.setDaysUntilStockout((Double) map.get("daysUntilStockout"));
        response.setSuggestedReorderQty((Integer) map.get("suggestedReorderQty"));
        response.setUrgency((String) map.get("urgency"));
        response.setMessage((String) map.get("message"));
        return response;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Integer currentQuantity) { this.currentQuantity = currentQuantity; }
    public Double getAvgDailyUsage() { return avgDailyUsage; }
    public void setAvgDailyUsage(Double avgDailyUsage) { this.avgDailyUsage = avgDailyUsage; }
    public Double getDaysUntilStockout() { return daysUntilStockout; }
    public void setDaysUntilStockout(Double daysUntilStockout) { this.daysUntilStockout = daysUntilStockout; }
    public Integer getSuggestedReorderQty() { return suggestedReorderQty; }
    public void setSuggestedReorderQty(Integer suggestedReorderQty) { this.suggestedReorderQty = suggestedReorderQty; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}