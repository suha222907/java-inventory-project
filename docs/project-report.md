# Inventory API with Demand Forecasting

## Project Report

**Programming in Java — Flipped Course Evaluated Project**

- **Student:** Suha Vora
- **Registration No.:** 25BAI11500
- **Subject:** Programming in Java (VITyarthi Flipped Course)
- **Deadline:** Sep 18, 2026

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Problem Statement](#2-problem-statement)
3. [Functional Requirements](#3-functional-requirements)
4. [Non-Functional Requirements](#4-non-functional-requirements)
5. [System Architecture](#5-system-architecture)
6. [Design Diagrams](#6-design-diagrams)
7. [Design Decisions & Rationale](#7-design-decisions--rationale)
8. [Implementation Details](#8-implementation-details)
9. [Screenshots / Results](#9-screenshots--results)
10. [Testing Approach](#10-testing-approach)
11. [Challenges Faced](#11-challenges-faced)
12. [Learnings & Key Takeaways](#12-learnings--key-takeaways)
13. [Future Enhancements](#13-future-enhancements)
14. [References](#14-references)

---

## 1. Introduction

The **Inventory API with Demand Forecasting** is a Spring Boot REST API designed to solve a real-world problem faced by small and medium businesses: managing inventory efficiently without expensive enterprise software or data science expertise.

Traditional inventory management relies on manually-set "low stock" thresholds — essentially guesses that do not adapt to actual consumption patterns. This leads to stockouts (lost sales) or overstocking (tied-up capital, storage costs).

This project addresses that gap by analyzing actual historical sales data to predict when products will run out of stock and recommending data-driven reorder quantities. The system is fully executable from the command line, uses zero external dependencies (in-memory H2 database), and can be run via a single command.

### Key Objectives

- Build a production-quality REST API using Spring Boot
- Implement a demand forecasting algorithm based on historical sales data
- Provide clean, well-documented API endpoints for inventory operations
- Ensure the project is fully runnable from the command line
- Write comprehensive tests (unit + integration)

---

## 2. Problem Statement

### The Problem

Small and medium businesses (SMBs) lack access to sophisticated demand forecasting tools. They rely on:

- **Static reorder thresholds** that do not adapt to changing demand
- **Manual spreadsheet tracking** that is error-prone and time-consuming
- **Guesswork** instead of data-driven decisions

This results in:
- **Stockouts:** Lost sales, customer dissatisfaction, damage to brand reputation
- **Overstocking:** Tied-up capital, increased storage costs, risk of spoilage/obsolescence

### The Solution

An intelligent inventory management API that:
1. Tracks all products and their stock levels
2. Records every sale as a timestamped transaction
3. Calculates average daily usage from historical data
4. Predicts days until stockout
5. Recommends optimal reorder quantities with urgency classification

### Target Users

- **Small Business Owners** — Track stock, record sales, get reorder alerts
- **Retail Store Managers** — Automate low-stock identification across product catalog
- **Developers / Integrators** — Embed forecasting logic into POS, ERP, or e-commerce systems via REST
- **Students / Learners** — Reference implementation of layered Spring Boot architecture

---

## 3. Functional Requirements

### 3.1 Product Catalog Management (Module 1)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-01 | Create a new product with name, category, price, quantity, and reorder threshold | High |
| FR-02 | Retrieve all products | High |
| FR-03 | Retrieve a single product by ID | High |
| FR-04 | Update an existing product | High |
| FR-05 | Delete a product | High |
| FR-06 | Search products by category | Medium |
| FR-07 | List products below their reorder threshold | Medium |

**Input/Output Structure:**

```
Input (POST /products):
{
    "name": "Notebook",
    "category": "Stationery",
    "price": 40.0,
    "quantity": 100,
    "reorderThreshold": 10
}

Output (201 Created):
{
    "id": 1,
    "name": "Notebook",
    "category": "Stationery",
    "price": 40.0,
    "quantity": 100,
    "reorderThreshold": 10
}
```

### 3.2 Sales Transaction Recording (Module 2)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-08 | Record a sale for a product (decrements stock) | High |
| FR-09 | Persist transaction with product reference, quantity, and timestamp | High |
| FR-10 | Validate stock availability before recording sale | High |
| FR-11 | Return transaction details after successful sale | Medium |

**Input/Output Structure:**

```
Input (POST /products/1/sale):
{
    "quantitySold": 5
}

Output (201 Created):
{
    "id": 1,
    "product": {
        "id": 1,
        "name": "Notebook",
        "category": "Stationery",
        "price": 40.0,
        "quantity": 95,
        "reorderThreshold": 10
    },
    "quantitySold": 5,
    "saleDate": "2026-09-18T10:30:00"
}
```

### 3.3 Demand Forecasting & Reorder Intelligence (Module 3)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-12 | Calculate average daily usage from transaction history | High |
| FR-13 | Predict days until stockout | High |
| FR-14 | Recommend reorder quantity (14-day buffer) | High |
| FR-15 | Classify urgency: HIGH (<=3 days), MEDIUM (<=7 days), LOW (>7 days) | High |
| FR-16 | Handle edge cases: no sales data, zero usage | Medium |

**Input/Output Structure:**

```
Input (GET /products/1/reorder-suggestion):

Output (200 OK):
{
    "productName": "Notebook",
    "currentQuantity": 95,
    "avgDailyUsage": 6.0,
    "daysUntilStockout": 15.8,
    "suggestedReorderQty": 0,
    "urgency": "LOW"
}
```

### 3.4 Operational Insights (Module 4)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-17 | Detect low-stock products | Medium |
| FR-18 | Provide real-time stock levels after each sale | Medium |
| FR-19 | Maintain historical transaction audit trail | Medium |

### Workflow

```
User/Client Application
       |
   REST API (HTTP)
       |
   Controller Layer
       |
   Service Layer (Business Logic)
       |
   Repository Layer (Data Access)
       |
   Database (H2)
       |
   Response -> User/Client Application
```

---

## 4. Non-Functional Requirements

| ID | Requirement | Implementation |
|----|-------------|----------------|
| NFR-01 | **Performance** | In-memory H2 database for fast reads/writes; efficient JPQL queries; indexed foreign keys |
| NFR-02 | **Security** | Input validation on all endpoints; no SQL injection (JPA); error messages do not leak internals |
| NFR-03 | **Usability** | Consistent REST API design; clear error responses with field-level validation details |
| NFR-04 | **Reliability** | `@Transactional` on write operations; atomic stock decrement + transaction creation; foreign key constraints |
| NFR-05 | **Scalability** | Stateless service design; layered architecture enables horizontal scaling; connection pooling via HikariCP |
| NFR-06 | **Maintainability** | Clean separation of concerns; comprehensive logging; unit & integration tests; documented code |
| NFR-07 | **Error Handling** | Global exception handler; custom exception types; structured error responses |
| NFR-08 | **Logging** | SLF4J/Logback with configurable levels; DEBUG for application code; SQL parameter binding traces |

---

## 5. System Architecture

### Architecture Overview

The application follows a standard **layered architecture** pattern:

```
+-----------------------------------------------------+
|                    Client Applications               |
|              (Mobile, Web, POS, Postman)            |
+------------------------------+----------------------+
                               | HTTP/REST
+------------------------------v----------------------+
|                  Presentation Layer                   |
|         +---------------------------------+          |
|         |      ProductController          |          |
|         |  - REST endpoints               |          |
|         |  - Request validation           |          |
|         |  - Response DTOs                |          |
|         +---------------------------------+          |
|         +---------------------------------+          |
|         |    GlobalExceptionHandler       |          |
|         |  - Centralized error handling   |          |
|         |  - Structured error responses   |          |
|         +---------------------------------+          |
+------------------------------+----------------------+
                               |
+------------------------------v----------------------+
|                   Business Layer                     |
|         +---------------------------------+          |
|         |       ProductService            |          |
|         |  - CRUD operations              |          |
|         |  - Sale recording               |          |
|         |  - Demand forecasting algorithm |          |
|         |  - Business validation          |          |
|         +---------------------------------+          |
+------------------------------+----------------------+
                               |
+------------------------------v----------------------+
|                 Data Access Layer                    |
|         +---------------------------------+          |
|         |    ProductRepository            |          |
|         |    TransactionRepository        |          |
|         |  - Spring Data JPA              |          |
|         |  - Custom query methods         |          |
|         +---------------------------------+          |
+------------------------------+----------------------+
                               |
+------------------------------v----------------------+
|                      Data Layer                      |
|         +---------------------------------+          |
|         |         H2 Database             |          |
|         |  - In-memory storage            |          |
|         |  - Zero configuration           |          |
|         +---------------------------------+          |
+-----------------------------------------------------+
```

### Component Responsibilities

| Component | Responsibility | Technology |
|-----------|---------------|------------|
| **ProductController** | HTTP request/response handling, input validation, routing | Spring Web MVC |
| **GlobalExceptionHandler** | Centralized error handling, structured error responses | @RestControllerAdvice |
| **ProductService** | Business logic, forecasting algorithm, transaction management | @Service, @Transactional |
| **ProductRepository** | Product data access, category search, low-stock queries | Spring Data JPA |
| **TransactionRepository** | Transaction data access, sales history queries | Spring Data JPA |
| **H2 Database** | In-memory data storage, SQL execution | H2 Database |

---

## 6. Design Diagrams

### 6.1 Use Case Diagram

```
                    +-----------------------------------------+
                    |           Inventory API                 |
                    |                                         |
  +------+         |  +-----------------------------+       |
  |Client|-------->|  |   Manage Products (CRUD)     |       |
  |  App |         |  +-----------------------------+       |
  +------+         |                                         |
       |           |  +-----------------------------+       |
       |---------->|  |   Record Sales               |       |
       |           |  +-----------------------------+       |
       |           |                                         |
  +------+         |  +-----------------------------+       |
  |Store |-------->|  |   Search Products by Category|       |
  |Manager|        |  +-----------------------------+       |
  +------+         |                                         |
       |           |  +-----------------------------+       |
       |---------->|  |   View Low Stock Products    |       |
       |           |  +-----------------------------+       |
       |           |                                         |
  +------+         |  +-----------------------------+       |
  |Devel-|-------->|  |   Get Reorder Suggestions    |       |
  | oper |         |  +-----------------------------+       |
  +------+         |                                         |
                    +-----------------------------------------+
```

### 6.2 Class Diagram

```
+--------------------------+
|    ProductController     |
+--------------------------+
| - productService         |
+--------------------------+
| + getAllProducts()        |
| + getProductById(id)     |
| + addProduct(product)    |
| + updateProduct(id, prod)|
| + deleteProduct(id)      |
| + searchByCategory(cat)  |
| + getLowStockProducts()  |
| + recordSale(id, req)    |
| + getReorderSuggestion(id|
+------------+-------------+
             | uses
             v
+--------------------------+
|     ProductService       |
+--------------------------+
| - productRepository      |
| - transactionRepository  |
+--------------------------+
| + getAllProducts()        |
| + getProductById(id)     |
| + addProduct(product)    |
| + updateProduct(id, prod)|
| + deleteProduct(id)      |
| + recordSale(id, qty)    |
| + searchByCategory(cat)  |
| + getLowStockProducts()  |
| + predictReorder(id)     |
| - validateProduct(prod)  |
| - validateSaleQuantity(q)|
+------------+-------------+
             | uses
             v
+--------------------------+
|   ProductRepository      |
+--------------------------+
| + findByCategory(cat)    |
| + findByQuantityLessThan |
+--------------------------+
```

### 6.3 Sequence Diagram - Record Sale

```
Client          Controller        Service         Repository       Database
   |                |                |                |                |
   | POST /sale     |                |                |                |
   |--------------->|                |                |                |
   |                | recordSale()   |                |                |
   |                |--------------->|                |                |
   |                |                | findById()     |                |
   |                |                |--------------->|                |
   |                |                |                | SELECT product |
   |                |                |                |--------------->|
   |                |                |                |<---------------|
   |                |                |<---------------|                |
   |                |                | validate qty   |                |
   |                |                | check stock    |                |
   |                |                | decrement qty  |                |
   |                |                |--------------->|                |
   |                |                |                | UPDATE product |
   |                |                |                |--------------->|
   |                |                |                |<---------------|
   |                |                | save transaction                |
   |                |                |--------------->|                |
   |                |                |                | INSERT trans   |
   |                |                |                |--------------->|
   |                |                |                |<---------------|
   |                |  Transaction   |                |                |
   |                |<---------------|                |                |
   |  201 Created   |                |                |                |
   |<---------------|                |                |                |
```

### 6.4 ER Diagram

```
+---------------------------+         +---------------------------+
|         products          |         |        transactions       |
+---------------------------+         +---------------------------+
| id (PK, BIGINT, AUTO)    |    +--->| id (PK, BIGINT, AUTO)    |
| name (VARCHAR, NOT NULL)  |    |    | product_id (FK, NOT NULL)|
| category (VARCHAR)        |    |    | quantity_sold (INT, >=1)  |
| price (DOUBLE, NOT NULL)  |<---+    | sale_date (TIMESTAMP)    |
| quantity (INT, NOT NULL)  |         +---------------------------+
| reorder_threshold (INT)   |
+---------------------------+

Relationships:
- products 1 ----< transactions (One product has many transactions)
```

### 6.5 Workflow Diagram

```
                    +------------------+
                    |   Start Request  |
                    +--------+---------+
                             |
                    +--------v---------+
                    |  Validate Input  |
                    +--------+---------+
                             |
              +--------------+--------------+
              |              |              |
    +---------v----+ +------v------+ +-----v-------+
    |   Create     | |  Read/Update| |   Delete    |
    |   Product    | |   Product   | |   Product   |
    +---------+----+ +------+------+ +-----+-------+
              |              |              |
              +--------------+--------------+
                             |
                    +--------v---------+
                    |  Save to DB      |
                    +--------+---------+
                             |
                    +--------v---------+
                    |  Return Response |
                    +------------------+
```

---

## 7. Design Decisions & Rationale

### 7.1 Layered Architecture

**Decision:** Use a 4-layer architecture (Controller -> Service -> Repository -> Entity)

**Rationale:**
- Separation of concerns makes code maintainable
- Each layer has a single responsibility
- Easy to test (mock at any layer boundary)
- Follows Spring Boot conventions

### 7.2 In-Memory H2 Database

**Decision:** Use H2 in-memory database instead of persistent database

**Rationale:**
- Zero configuration required
- Perfect for evaluation and demonstration
- Data resets on restart (clean state for each run)
- Can be easily swapped to PostgreSQL/MySQL for production

### 7.3 Custom Exception Handling

**Decision:** Create custom exceptions (ResourceNotFoundException, BusinessLogicException) with a global handler

**Rationale:**
- Consistent error responses across all endpoints
- Field-level validation errors returned to client
- No stack traces leaked to client
- Business logic errors clearly separated from system errors

### 7.4 DTO Pattern

**Decision:** Use separate DTOs for API responses (ProductResponse, TransactionResponse, ReorderSuggestionResponse)

**Rationale:**
- Decouples internal entity model from API contract
- Prevents over-exposing internal data
- Allows API versioning without changing entities
- Cleaner, more predictable API responses

### 7.5 Forecasting Algorithm

**Decision:** Use moving-window average for demand forecasting

**Rationale:**
- Simple to understand and implement
- Works with small amounts of data
- No external ML libraries required
- Provides actionable outputs (days until stockout, reorder quantity, urgency)

---

## 8. Implementation Details

### 8.1 Project Structure

```
inventory-api/
+-- src/
|   +-- main/
|   |   +-- java/com/shivendra/inventory_api/
|   |   |   +-- controller/
|   |   |   |   +-- ProductController.java
|   |   |   +-- dto/
|   |   |   |   +-- ProductResponse.java
|   |   |   |   +-- ReorderSuggestionResponse.java
|   |   |   |   +-- SaleRequest.java
|   |   |   |   +-- TransactionResponse.java
|   |   |   +-- exception/
|   |   |   |   +-- BusinessLogicException.java
|   |   |   |   +-- GlobalExceptionHandler.java
|   |   |   |   +-- ResourceNotFoundException.java
|   |   |   +-- model/
|   |   |   |   +-- Product.java
|   |   |   |   +-- Transaction.java
|   |   |   +-- repository/
|   |   |   |   +-- ProductRepository.java
|   |   |   |   +-- TransactionRepository.java
|   |   |   +-- service/
|   |   |   |   +-- ProductService.java
|   |   |   +-- InventoryApiApplication.java
|   |   +-- resources/
|   |       +-- application.properties
|   +-- test/
|       +-- java/com/shivendra/inventory_api/
|           +-- controller/
|           |   +-- ProductControllerIntegrationTest.java
|           +-- service/
|           |   +-- ProductServiceTest.java
|           +-- InventoryApiApplicationTests.java
+-- docs/
|   +-- design.md
|   +-- project-report.md
+-- pom.xml
+-- README.md
+-- statement.md
+-- mvnw (Maven Wrapper)
```

### 8.2 Key Files and Their Purpose

| File | Purpose |
|------|---------|
| ProductController.java | REST endpoints, request validation, response mapping |
| ProductService.java | Business logic, forecasting algorithm, transaction management |
| ProductRepository.java | Data access queries for products |
| TransactionRepository.java | Transaction data access |
| Product.java | Entity with validation annotations |
| Transaction.java | Transaction entity |
| GlobalExceptionHandler.java | Centralized error handling |
| SaleRequest.java | Request DTO with validation |
| ProductResponse.java | Response DTO for products |
| TransactionResponse.java | Response DTO for transactions |
| ReorderSuggestionResponse.java | Response DTO for forecasting |

### 8.3 Forecasting Algorithm

```
Input: Product ID
1. Fetch product by ID
2. Fetch all transactions for this product (ordered by date desc)
3. If no transactions: return "no data" message
4. Calculate:
   - totalSold = SUM(quantitySold)
   - daysTracked = days between first and last sale + 1
   - avgDailyUsage = totalSold / daysTracked
   - daysUntilStockout = currentQuantity / avgDailyUsage
   - suggestedReorderQty = ceil(avgDailyUsage * 14) - currentQuantity
   - urgency = HIGH if daysUntilStockout <= 3
              MEDIUM if daysUntilStockout <= 7
              LOW if daysUntilStockout > 7
5. Return prediction map
```

### 8.4 API Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | /products | Get all products | 200 |
| GET | /products/{id} | Get product by ID | 200, 404 |
| POST | /products | Create new product | 201, 400 |
| PUT | /products/{id} | Update product | 200, 400, 404 |
| DELETE | /products/{id} | Delete product | 204, 404 |
| GET | /products/search?category=X | Search by category | 200 |
| GET | /products/low-stock | Get low-stock products | 200 |
| POST | /products/{id}/sale | Record sale | 201, 400, 404 |
| GET | /products/{id}/reorder-suggestion | Get reorder suggestion | 200, 404 |

---

## 9. Screenshots / Results

### 9.1 Application Startup

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.1)
```

### 9.2 Create Product Response

```
POST /products
Request:  {"name":"Notebook","category":"Stationery","price":40.0,"quantity":100,"reorderThreshold":10}
Response: 201 Created
{
    "id": 1,
    "name": "Notebook",
    "category": "Stationery",
    "price": 40.0,
    "quantity": 100,
    "reorderThreshold": 10
}
```

### 9.3 Record Sale Response

```
POST /products/1/sale
Request:  {"quantitySold": 5}
Response: 201 Created
{
    "id": 1,
    "product": {
        "id": 1,
        "name": "Notebook",
        "category": "Stationery",
        "price": 40.0,
        "quantity": 95,
        "reorderThreshold": 10
    },
    "quantitySold": 5,
    "saleDate": "2026-09-18T10:30:00"
}
```

### 9.4 Reorder Suggestion Response

```
GET /products/1/reorder-suggestion
Response: 200 OK
{
    "productName": "Notebook",
    "currentQuantity": 95,
    "avgDailyUsage": 6.0,
    "daysUntilStockout": 15.8,
    "suggestedReorderQty": 0,
    "urgency": "LOW"
}
```

### 9.5 Error Response (Validation)

```
POST /products
Request:  {"name":"","price":-10,"quantity":-5}
Response: 400 Bad Request
{
    "status": 400,
    "error": "Validation Failed",
    "message": "Invalid request parameters",
    "timestamp": "2026-09-18T10:30:00",
    "validationErrors": {
        "name": "Product name is required",
        "price": "Price must be zero or positive",
        "quantity": "Quantity cannot be negative"
    }
}
```

### 9.6 Error Response (Not Found)

```
GET /products/999
Response: 404 Not Found
{
    "status": 404,
    "error": "Not Found",
    "message": "Product not found with id : '999'",
    "timestamp": "2026-09-18T10:30:00"
}
```

### 9.7 Test Results

```
Tests run: 33, Failures: 0, Errors: 0, Skipped: 0

- ProductServiceTest: 17 tests passed
- ProductControllerIntegrationTest: 14 tests passed
- InventoryApiApplicationTests: 2 tests passed
```

---

## 10. Testing Approach

### 10.1 Test Types

| Type | Location | Purpose |
|------|----------|---------|
| Unit Tests | src/test/java/.../service/ | Test service layer logic in isolation using mocks |
| Integration Tests | src/test/java/.../controller/ | Test full HTTP request/response cycle |
| Application Tests | src/test/java/.../ | Verify Spring context loads and beans are wired |

### 10.2 Unit Tests (ProductServiceTest)

**17 test cases covering:**

- Product CRUD operations
- Validation (negative price, quantity)
- Sale recording (sufficient/insufficient stock)
- Demand forecasting algorithm
- Edge cases (no sales data, zero quantity)

**Example test:**
```java
@Test
void recordSale_insufficientStock_throwsBusinessLogicException() {
    testProduct.setQuantity(3);
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

    assertThatThrownBy(() -> productService.recordSale(1L, 5))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessageContaining("Insufficient stock");
}
```

### 10.3 Integration Tests (ProductControllerIntegrationTest)

**14 test cases covering:**

- All REST endpoints
- Request validation
- Error responses (400, 404)
- Business logic errors (insufficient stock)
- Forecasting with real database

**Example test:**
```java
@Test
void createProduct_validRequest_returnsCreated() throws Exception {
    String productJson = createProductJson("Test Product", "Electronics", 99.99, 50, 5);

    mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(productJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.id").exists());
}
```

### 10.4 Running Tests

```bash
# Run all tests
./mvnw test

# Run only unit tests
./mvnw test -Dtest=*ServiceTest

# Run only integration tests
./mvnw test -Dtest=*IntegrationTest

# Run specific test
./mvnw test -Dtest=ProductServiceTest#predictReorder_withSalesData
```

---

## 11. Challenges Faced

### 11.1 Spring Boot 4 Compatibility

**Challenge:** Spring Boot 4.x introduced package changes (e.g., `spring-boot-starter-webmvc-test` instead of `spring-boot-starter-test` for MockMvc)

**Solution:** Used `spring-boot-webmvc-test` dependency and updated imports to `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`

### 11.2 Validation Integration

**Challenge:** Integrating Bean Validation (JSR 380) with Spring Boot 4 and returning field-level error messages

**Solution:** Used `@Valid` annotation on controller methods, created `GlobalExceptionHandler` with `MethodArgumentNotValidException` handler, and created `ValidationErrorResponse` DTO

### 11.3 Forecasting Algorithm Edge Cases

**Challenge:** Handling products with no sales data, zero daily usage, or negative reorder quantities

**Solution:** Added explicit checks for empty transaction list, zero avgDailyUsage, and clamped suggestedReorderQty to minimum 0

### 11.4 Transaction Management

**Challenge:** Ensuring atomicity of stock decrement and transaction creation

**Solution:** Used `@Transactional` annotation on service methods to ensure both operations succeed or fail together

---

## 12. Learnings & Key Takeaways

### 12.1 Technical Skills

- **Spring Boot 4:** Learned the latest Spring Boot conventions and package structure
- **REST API Design:** Designed clean, consistent REST endpoints following HTTP semantics
- **JPA/Hibernate:** Implemented efficient data access with Spring Data JPA
- **Bean Validation:** Integrated JSR 380 validation with Spring Boot
- **Testing:** Wrote comprehensive unit and integration tests using JUnit 5, Mockito, and MockMvc

### 12.2 Software Engineering Practices

- **Layered Architecture:** Separated concerns for maintainability
- **DTO Pattern:** Decoupled internal model from API contract
- **Exception Handling:** Created structured, consistent error responses
- **Logging:** Implemented comprehensive logging for debugging
- **Documentation:** Documented API, architecture, and design decisions

### 12.3 Domain Knowledge

- **Inventory Management:** Understood the challenges of stock management
- **Demand Forecasting:** Implemented a practical forecasting algorithm
- **Business Logic:** Validated stock availability, calculated reorder quantities

---

## 13. Future Enhancements

### 13.1 Short-term

- **Persistent Database:** Switch to PostgreSQL/MySQL for data persistence
- **Authentication:** Add JWT-based authentication for write operations
- **Input Validation:** Enhanced validation with custom validators
- **API Documentation:** Add OpenAPI/Swagger documentation

### 13.2 Medium-term

- **Dashboard:** Build a web-based dashboard for inventory visualization
- **Notifications:** Email/SMS alerts for low-stock products
- **Multi-warehouse:** Support multiple warehouse locations
- **Purchase Orders:** Generate purchase orders based on reorder suggestions

### 13.3 Long-term

- **Machine Learning:** Replace simple average with ML-based forecasting
- **Real-time Updates:** WebSocket support for real-time stock updates
- **Microservices:** Split into separate services (product, transaction, forecasting)
- **Cloud Deployment:** Deploy to AWS/GCP/Azure with containerization

---

## 14. References

1. **Spring Boot Documentation** - https://spring.io/projects/spring-boot
2. **Spring Data JPA** - https://spring.io/projects/spring-data-jpa
3. **Hibernate ORM** - https://hibernate.org/
4. **H2 Database** - https://www.h2database.com/
5. **Jakarta Bean Validation** - https://beanvalidation.org/
6. **JUnit 5** - https://junit.org/junit5/
7. **Mockito** - https://site.mockito.org/
8. **Maven** - https://maven.apache.org/

---

*Generated as part of the VITyarthi Flipped Course Evaluated Project*

*Student: Suha Vora | Registration No.: 25BAI11500*