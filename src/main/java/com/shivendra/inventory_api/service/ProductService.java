package com.shivendra.inventory_api.service;

import com.shivendra.inventory_api.exception.BusinessLogicException;
import com.shivendra.inventory_api.exception.ResourceNotFoundException;
import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.model.Transaction;
import com.shivendra.inventory_api.repository.ProductRepository;
import com.shivendra.inventory_api.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public ProductService(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public Product addProduct(Product product) {
        log.info("Adding new product: {}", product.getName());
        validateProduct(product);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Updating product with id: {}", id);
        Product existing = getProductById(id);
        validateProduct(updatedProduct);
        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setReorderThreshold(updatedProduct.getReorderThreshold());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        getProductById(id); // verify exists
        productRepository.deleteById(id);
    }

    @Transactional
    public Transaction recordSale(Long productId, Integer quantitySold) {
        log.info("Recording sale for product {}: quantity={}", productId, quantitySold);
        validateSaleQuantity(quantitySold);

        Product product = getProductById(productId);

        if (product.getQuantity() < quantitySold) {
            throw new BusinessLogicException("INSUFFICIENT_STOCK",
                    String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                            product.getName(), product.getQuantity(), quantitySold));
        }

        product.setQuantity(product.getQuantity() - quantitySold);
        productRepository.save(product);

        Transaction transaction = new Transaction(product, quantitySold, java.time.LocalDateTime.now());
        Transaction saved = transactionRepository.save(transaction);
        log.debug("Sale recorded with transaction id: {}", saved.getId());
        return saved;
    }

    public List<Product> searchByCategory(String category) {
        log.debug("Searching products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        log.debug("Fetching low-stock products");
        return productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < p.getReorderThreshold())
                .toList();
    }

    public Map<String, Object> predictReorder(Long productId) {
        log.debug("Generating reorder prediction for product: {}", productId);
        Product product = getProductById(productId);
        List<Transaction> recentSales = transactionRepository.findByProductIdOrderBySaleDateDesc(productId);

        if (recentSales.isEmpty()) {
            log.info("No sales data for product {}: returning fallback", productId);
            return Map.of(
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

        return Map.of(
                "productName", product.getName(),
                "currentQuantity", product.getQuantity(),
                "avgDailyUsage", Math.round(avgDailyUsage * 100.0) / 100.0,
                "daysUntilStockout", Math.round(daysUntilStockout * 10.0) / 10.0,
                "suggestedReorderQty", Math.max(suggestedReorderQty, 0),
                "urgency", urgency);
    }

    private void validateProduct(Product product) {
        if (product.getPrice() != null && product.getPrice() < 0) {
            throw new BusinessLogicException("INVALID_PRICE", "Price cannot be negative");
        }
        if (product.getQuantity() != null && product.getQuantity() < 0) {
            throw new BusinessLogicException("INVALID_QUANTITY", "Quantity cannot be negative");
        }
        if (product.getReorderThreshold() != null && product.getReorderThreshold() < 0) {
            throw new BusinessLogicException("INVALID_THRESHOLD", "Reorder threshold cannot be negative");
        }
    }

    private void validateSaleQuantity(Integer quantitySold) {
        if (quantitySold == null || quantitySold <= 0) {
            throw new BusinessLogicException("INVALID_SALE_QUANTITY", "Sale quantity must be positive");
        }
    }
}
