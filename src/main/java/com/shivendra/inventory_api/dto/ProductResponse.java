package com.shivendra.inventory_api.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
    private Integer reorderThreshold;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String category, Double price, Integer quantity, Integer reorderThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }

    public static ProductResponse fromEntity(com.shivendra.inventory_api.model.Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity(),
                product.getReorderThreshold()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; }
}