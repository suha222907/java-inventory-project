package com.shivendra.inventory_api.dto;

import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private ProductResponse product;
    private Integer quantitySold;
    private LocalDateTime saleDate;

    public TransactionResponse() {}

    public TransactionResponse(Long id, ProductResponse product, Integer quantitySold, LocalDateTime saleDate) {
        this.id = id;
        this.product = product;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
    }

    public static TransactionResponse fromEntity(com.shivendra.inventory_api.model.Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                ProductResponse.fromEntity(transaction.getProduct()),
                transaction.getQuantitySold(),
                transaction.getSaleDate()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductResponse getProduct() { return product; }
    public void setProduct(ProductResponse product) { this.product = product; }
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
}