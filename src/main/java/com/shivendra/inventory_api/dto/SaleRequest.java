package com.shivendra.inventory_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SaleRequest {

    @NotNull(message = "Quantity sold is required")
    @Min(value = 1, message = "Quantity sold must be at least 1")
    private Integer quantitySold;

    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
}
