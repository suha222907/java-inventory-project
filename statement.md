# Project Statement

## Problem Statement

Traditional inventory management systems rely on static, manually-configured reorder thresholds that do not adapt to actual consumption patterns. This leads to two costly outcomes: **stockouts** (lost sales, customer dissatisfaction) when thresholds are set too low, and **overstocking** (tied-up capital, storage costs, spoilage risk) when thresholds are set too high.

Small and medium businesses, in particular, lack access to sophisticated demand forecasting tools. They need a lightweight, zero-configuration solution that learns from their own sales history to predict when products will run out and recommend optimal reorder quantities — without requiring data science expertise or external ML platforms.

---

## Scope of the Project

This project delivers a **Spring Boot REST API** for inventory management with built-in demand forecasting. The system:

- Tracks products, categories, pricing, and stock levels
- Records every sale as a timestamped transaction
- Computes average daily usage from historical sales data
- Predicts **days until stockout** and **recommended reorder quantity** with urgency classification
- Exposes all functionality via a clean, well-documented REST interface
- Runs entirely from the command line with zero external dependencies (in-memory H2 database)

**In Scope:**
- Product CRUD operations
- Sale recording with automatic stock decrement
- Category-based search and low-stock filtering
- Demand forecasting based on moving-window sales analysis
- Runnable JAR for deployment

**Out of Scope:**
- User authentication/authorization (planned as future enhancement)
- Persistent database configuration (PostgreSQL/MySQL)
- Multi-warehouse or multi-location support
- Purchase order generation or supplier integration
- Web UI / dashboard (API-only)

---

## Target Users

| User Type | Use Case |
|-----------|----------|
| **Small Business Owners** | Track stock, record sales, get reorder alerts without spreadsheets |
| **Retail Store Managers** | Automate low-stock identification across product catalog |
| **Developers / Integrators** | Embed forecasting logic into POS, ERP, or e-commerce systems via REST |
| **Students / Learners** | Reference implementation of layered Spring Boot architecture with domain-driven forecasting |

---

## High-Level Features

### 1. Product Catalog Management
- Create, read, update, delete products
- Fields: name, category, price, quantity, reorder threshold
- Search products by category
- List all products or fetch by ID

### 2. Sales Transaction Recording
- Record a sale for a product (decrements stock atomically)
- Persists transaction with product reference, quantity sold, timestamp
- Validates stock availability before recording

### 3. Demand Forecasting & Reorder Intelligence
- Calculates average daily usage from transaction history
- Estimates days until stockout based on current quantity
- Recommends reorder quantity covering a 14-day buffer
- Classifies urgency: HIGH (≤3 days), MEDIUM (≤7 days), LOW (>7 days)
- Handles edge cases: no sales data, zero usage, negative reorder suggestions

### 4. Operational Insights
- Low-stock detection (quantity < reorder threshold)
- Real-time stock levels after each sale
- Historical transaction audit trail per product

---

## Technical Approach

- **Architecture:** Layered (Controller → Service → Repository → Entity)
- **Framework:** Spring Boot 4 (Web MVC, Data JPA, Validation)
- **Database:** H2 in-memory (zero-config, resets on restart)
- **Build Tool:** Maven (with wrapper for portability)
- **Language:** Java 17
- **Testing:** JUnit 5 + Spring Boot Test (unit + integration)

---

## Success Criteria

- [ ] All API endpoints return correct responses for happy-path and error cases
- [ ] Forecasting algorithm produces mathematically sound predictions
- [ ] Application starts with `./mvnw spring-boot:run` (no manual setup)
- [ ] Runnable JAR generated via `./mvnw package` executes with `java -jar`
- [ ] Code compiles without warnings; passes basic test suite
- [ ] README provides complete setup, run, and test instructions