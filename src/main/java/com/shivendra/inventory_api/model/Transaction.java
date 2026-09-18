   package com.shivendra.inventory_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product reference is required")
    private Product product;

    @NotNull(message = "Quantity sold is required")
    @Min(value = 1, message = "Quantity sold must be at least 1")
    @Column(nullable = false)
    private Integer quantitySold;

    @NotNull(message = "Sale date is required")
    @Column(nullable = false)
    private LocalDateTime saleDate;

    public Transaction() {}

    public Transaction(Product product, Integer quantitySold, LocalDateTime saleDate) {
        this.product = product;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
}