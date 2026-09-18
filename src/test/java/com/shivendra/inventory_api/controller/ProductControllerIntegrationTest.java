package com.shivendra.inventory_api.controller;

import com.shivendra.inventory_api.dto.SaleRequest;
import com.shivendra.inventory_api.model.Product;
import com.shivendra.inventory_api.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String createProductJson(String name, String category, Double price, Integer quantity, Integer threshold) {
        return String.format(
                "{\"name\":\"%s\",\"category\":\"%s\",\"price\":%f,\"quantity\":%d,\"reorderThreshold\":%d}",
                name, category, price, quantity, threshold);
    }

    @Test
    void createProduct_validRequest_returnsCreated() throws Exception {
        String productJson = createProductJson("Test Product", "Electronics", 99.99, 50, 5);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.quantity").value(50))
                .andExpect(jsonPath("$.reorderThreshold").value(5))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createProduct_invalidRequest_returnsBadRequest() throws Exception {
        String invalidJson = "{\"name\":\"\",\"category\":\"Electronics\",\"price\":-10,\"quantity\":-5}";

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void getAllProducts_returnsList() throws Exception {
        // First create a product
        String productJson = createProductJson("List Product", "Books", 25.0, 100, 10);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void getProductById_existingId_returnsProduct() throws Exception {
        String productJson = createProductJson("Get Product", "Toys", 15.0, 30, 5);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Get Product"))
                .andExpect(jsonPath("$.id").value(Integer.parseInt(id)));
    }

    @Test
    void getProductById_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("Product not found")));
    }

    @Test
    void updateProduct_existingId_updatesProduct() throws Exception {
        String productJson = createProductJson("Original", "Category", 10.0, 20, 5);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        String updateJson = createProductJson("Updated", "Category", 20.0, 40, 8);

        mockMvc.perform(put("/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.price").value(20.0))
                .andExpect(jsonPath("$.quantity").value(40))
                .andExpect(jsonPath("$.reorderThreshold").value(8));
    }

    @Test
    void deleteProduct_existingId_returnsNoContent() throws Exception {
        String productJson = createProductJson("To Delete", "Category", 10.0, 20, 5);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        mockMvc.perform(delete("/products/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchByCategory_returnsMatchingProducts() throws Exception {
        String productJson = createProductJson("Searchable", "SearchCategory", 10.0, 20, 5);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/search")
                        .param("category", "SearchCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].category").value("SearchCategory"));
    }

    @Test
    void getLowStockProducts_returnsProductsBelowThreshold() throws Exception {
        String productJson = createProductJson("Low Stock Item", "LowStockCat", 10.0, 2, 10);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("Low Stock Item"));
    }

    @Test
    void recordSale_validRequest_returnsTransactionAndDecrementsStock() throws Exception {
        String productJson = createProductJson("Sale Product", "SalesCat", 10.0, 100, 10);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        String saleJson = "{\"quantitySold\": 5}";

        mockMvc.perform(post("/products/" + id + "/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantitySold").value(5))
                .andExpect(jsonPath("$.product.id").value(Integer.parseInt(id)))
                .andExpect(jsonPath("$.product.quantity").value(95))
                .andExpect(jsonPath("$.saleDate").exists());
    }

    @Test
    void recordSale_insufficientStock_returnsBadRequest() throws Exception {
        String productJson = createProductJson("Low Stock", "SalesCat", 10.0, 3, 10);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        String saleJson = "{\"quantitySold\": 10}";

        mockMvc.perform(post("/products/" + id + "/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saleJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Rule Violation"))
                .andExpect(jsonPath("$.message").value(containsString("Insufficient stock")));
    }

    @Test
    void recordSale_invalidQuantity_returnsBadRequest() throws Exception {
        String productJson = createProductJson("Test", "Cat", 10.0, 100, 10);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        String saleJson = "{\"quantitySold\": 0}";

        mockMvc.perform(post("/products/" + id + "/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saleJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.quantitySold").exists());
    }

    @Test
    void getReorderSuggestion_noSalesData_returnsFallback() throws Exception {
        String productJson = createProductJson("No Sales", "Cat", 10.0, 100, 10);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        mockMvc.perform(get("/products/" + id + "/reorder-suggestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Not enough sales data")))
                .andExpect(jsonPath("$.currentQuantity").value(100));
    }

    @Test
    void getReorderSuggestion_withSalesData_returnsPrediction() throws Exception {
        String productJson = createProductJson("Predict Product", "PredictCat", 10.0, 100, 10);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        // Record some sales
        String saleJson = "{\"quantitySold\": 10}";
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/products/" + id + "/sale")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(saleJson))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/products/" + id + "/reorder-suggestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Predict Product"))
                .andExpect(jsonPath("$.avgDailyUsage").exists())
                .andExpect(jsonPath("$.daysUntilStockout").exists())
                .andExpect(jsonPath("$.suggestedReorderQty").exists())
                .andExpect(jsonPath("$.urgency").exists());
    }
}