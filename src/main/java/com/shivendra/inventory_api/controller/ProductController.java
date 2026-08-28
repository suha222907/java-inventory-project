package com.shivendra.inventory_api.controller;
import com.shivendra.inventory_api.model.Transaction;
import com.shivendra.inventory_api.dto.SaleRequest;
import java.util.Map;

import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product saved = productService.addProduct(product);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Product> searchByCategory(@RequestParam String category) {
        return productService.searchByCategory(category);
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    @PostMapping("/{id}/sale")
    public ResponseEntity<Transaction> recordSale(@PathVariable Long id, @RequestBody SaleRequest saleRequest) {
        Transaction transaction = productService.recordSale(id, saleRequest.getQuantitySold());
        return ResponseEntity.status(201).body(transaction);
    }

    @GetMapping("/{id}/reorder-suggestion")
    public ResponseEntity<Map<String, Object>> getReorderSuggestion(@PathVariable Long id) {
        return ResponseEntity.ok(productService.predictReorder(id));
    }
}