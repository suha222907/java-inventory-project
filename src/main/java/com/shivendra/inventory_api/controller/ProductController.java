package com.shivendra.inventory_api.controller;

import com.shivendra.inventory_api.dto.ProductResponse;
import com.shivendra.inventory_api.dto.ReorderSuggestionResponse;
import com.shivendra.inventory_api.dto.SaleRequest;
import com.shivendra.inventory_api.dto.TransactionResponse;
import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.model.Transaction;
import com.shivendra.inventory_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponse.fromEntity(productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody Product product) {
        Product saved = productService.addProduct(product);
        return ResponseEntity.status(201).body(ProductResponse.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(ProductResponse.fromEntity(productService.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ProductResponse> searchByCategory(@RequestParam String category) {
        return productService.searchByCategory(category).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts() {
        return productService.getLowStockProducts().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/sale")
    public ResponseEntity<TransactionResponse> recordSale(@PathVariable Long id, @Valid @RequestBody SaleRequest saleRequest) {
        Transaction transaction = productService.recordSale(id, saleRequest.getQuantitySold());
        return ResponseEntity.status(201).body(TransactionResponse.fromEntity(transaction));
    }

    @GetMapping("/{id}/reorder-suggestion")
    public ResponseEntity<ReorderSuggestionResponse> getReorderSuggestion(@PathVariable Long id) {
        Map<String, Object> prediction = productService.predictReorder(id);
        return ResponseEntity.ok(ReorderSuggestionResponse.fromMap(prediction));
    }
}