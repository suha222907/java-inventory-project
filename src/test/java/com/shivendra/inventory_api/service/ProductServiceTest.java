package com.shivendra.inventory_api.service;

import com.shivendra.inventory_api.exception.BusinessLogicException;
import com.shivendra.inventory_api.exception.ResourceNotFoundException;
import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.model.Transaction;
import com.shivendra.inventory_api.repository.ProductRepository;
import com.shivendra.inventory_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Notebook", "Stationery", 40.0, 100, 10);
        testProduct.setId(1L);
    }

    @Test
    void getAllProducts_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Notebook");
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_existingId_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_nonExistingId_throwsResourceNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id : '999'");
    }

    @Test
    void addProduct_validProduct_savesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.addProduct(testProduct);

        assertThat(result).isEqualTo(testProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    void addProduct_negativePrice_throwsBusinessLogicException() {
        testProduct.setPrice(-10.0);

        assertThatThrownBy(() -> productService.addProduct(testProduct))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Price cannot be negative");
    }

    @Test
    void addProduct_negativeQuantity_throwsBusinessLogicException() {
        testProduct.setQuantity(-5);

        assertThatThrownBy(() -> productService.addProduct(testProduct))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Quantity cannot be negative");
    }

    @Test
    void updateProduct_existingId_updatesAndReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updated = new Product("Updated Notebook", "Stationery", 50.0, 80, 15);
        Product result = productService.updateProduct(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated Notebook");
        assertThat(result.getPrice()).isEqualTo(50.0);
        assertThat(result.getQuantity()).isEqualTo(80);
        assertThat(result.getReorderThreshold()).isEqualTo(15);
    }

    @Test
    void deleteProduct_existingId_deletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void recordSale_sufficientStock_decrementsQuantityAndReturnsTransaction() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = productService.recordSale(1L, 5);

        assertThat(result.getQuantitySold()).isEqualTo(5);
        assertThat(result.getProduct().getQuantity()).isEqualTo(95);
        verify(productRepository).save(any(Product.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void recordSale_insufficientStock_throwsBusinessLogicException() {
        testProduct.setQuantity(3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThatThrownBy(() -> productService.recordSale(1L, 5))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void recordSale_zeroQuantity_throwsBusinessLogicException() {
        assertThatThrownBy(() -> productService.recordSale(1L, 0))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Sale quantity must be positive");
    }

    @Test
    void recordSale_negativeQuantity_throwsBusinessLogicException() {
        assertThatThrownBy(() -> productService.recordSale(1L, -2))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Sale quantity must be positive");
    }

    @Test
    void searchByCategory_returnsMatchingProducts() {
        when(productRepository.findByCategory("Stationery")).thenReturn(List.of(testProduct));

        List<Product> result = productService.searchByCategory("Stationery");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Stationery");
    }

    @Test
    void getLowStockProducts_returnsProductsBelowThreshold() {
        Product lowStock = new Product("Pen", "Stationery", 5.0, 5, 10);
        lowStock.setId(2L);
        when(productRepository.findAll()).thenReturn(List.of(testProduct, lowStock));

        List<Product> result = productService.getLowStockProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pen");
    }

    @Test
    void predictReorder_noSalesData_returnsFallbackMessage() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(transactionRepository.findByProductIdOrderBySaleDateDesc(1L)).thenReturn(List.of());

        Map<String, Object> result = productService.predictReorder(1L);

        assertThat(result.get("message")).isEqualTo("Not enough sales data to predict reorder timing");
        assertThat(result.get("currentQuantity")).isEqualTo(100);
    }

    @Test
    void predictReorder_withSalesData_calculatesCorrectly() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Transaction t1 = new Transaction(testProduct, 10, LocalDateTime.now().minusDays(5));
        Transaction t2 = new Transaction(testProduct, 10, LocalDateTime.now().minusDays(3));
        Transaction t3 = new Transaction(testProduct, 10, LocalDateTime.now().minusDays(1));
        t1.setId(1L);
        t2.setId(2L);
        t3.setId(3L);

        when(transactionRepository.findByProductIdOrderBySaleDateDesc(1L))
                .thenReturn(List.of(t3, t2, t1)); // descending order

        Map<String, Object> result = productService.predictReorder(1L);

        // Total sold: 30 over 5 days = 6 per day
        // Days until stockout: 100 / 6 = 16.66...
        // Suggested reorder: ceil(6 * 14) - 100 = 84 - 100 = -16 -> max(0) = 0
        assertThat(result.get("avgDailyUsage")).isEqualTo(6.0);
        assertThat(result.get("daysUntilStockout")).isEqualTo(16.7);
        assertThat(result.get("suggestedReorderQty")).isEqualTo(0);
        assertThat(result.get("urgency")).isEqualTo("LOW");
    }

    @Test
    void predictReorder_highUrgency_whenStockoutSoon() {
        Product lowStock = new Product("Critical Item", "Electronics", 100.0, 5, 10);
        lowStock.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(lowStock));

        Transaction t1 = new Transaction(lowStock, 5, LocalDateTime.now().minusDays(2));
        t1.setId(1L);
        when(transactionRepository.findByProductIdOrderBySaleDateDesc(2L))
                .thenReturn(List.of(t1));

        Map<String, Object> result = productService.predictReorder(2L);

        // Total sold: 5 over 2 days = 2.5 per day
        // Days until stockout: 5 / 2.5 = 2 days -> HIGH urgency
        assertThat(result.get("urgency")).isEqualTo("HIGH");
    }
}