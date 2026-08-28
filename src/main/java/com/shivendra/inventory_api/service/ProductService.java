package com.shivendra.inventory_api.service;

import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.repository.ProductRepository;
import com.shivendra.inventory_api.repository.TransactionRepository;
import com.shivendra.inventory_api.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public ProductService(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setReorderThreshold(updatedProduct.getReorderThreshold());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Transaction recordSale(Long productId, Integer quantitySold) {
        Product product = getProductById(productId);
        product.setQuantity(product.getQuantity() - quantitySold);
        productRepository.save(product);

        Transaction transaction = new Transaction(product, quantitySold, java.time.LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Product> searchByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < p.getReorderThreshold())
                .toList();
    }

    public java.util.Map<String, Object> predictReorder(Long productId) {
        Product product = getProductById(productId);
        List<Transaction> recentSales = transactionRepository.findByProductIdOrderBySaleDateDesc(productId);

        if (recentSales.isEmpty()) {
            return java.util.Map.of(
                    "message", "Not enough sales data to predict reorder timing",
                    "currentQuantity", product.getQuantity());
        }

        int totalSold = recentSales.stream().mapToInt(Transaction::getQuantitySold).sum();
        long daysTracked = java.time.temporal.ChronoUnit.DAYS.between(
                recentSales.get(recentSales.size() - 1).getSaleDate(),
                recentSales.get(0).getSaleDate()) + 1;

        double avgDailyUsage = (double) totalSold / daysTracked;
        double daysUntilStockout = avgDailyUsage > 0 ? product.getQuantity() / avgDailyUsage : Double.MAX_VALUE;
        int suggestedReorderQty = (int) Math.ceil(avgDailyUsage * 14) - product.getQuantity();

        String urgency = daysUntilStockout <= 3 ? "HIGH" : daysUntilStockout <= 7 ? "MEDIUM" : "LOW";

        return java.util.Map.of(
                "productName", product.getName(),
                "currentQuantity", product.getQuantity(),
                "avgDailyUsage", Math.round(avgDailyUsage * 100.0) / 100.0,
                "daysUntilStockout", Math.round(daysUntilStockout * 10.0) / 10.0,
                "suggestedReorderQty", Math.max(suggestedReorderQty, 0),
                "urgency", urgency);
    }
}
