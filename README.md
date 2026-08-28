# Inventory API with Demand Forecasting

A Spring Boot REST API for inventory management that goes beyond basic CRUD — it tracks sales transactions over time and predicts when a product will run out of stock, recommending how much to reorder.

## Problem it solves

Traditional inventory systems rely on a manually-set "low stock" threshold, which is just a guess. This API instead analyzes actual historical sales data to calculate real consumption rates, predicts stockout timing, and recommends data-driven reorder quantities — turning raw transaction history into an actionable decision.

## Tech Stack

- **Java 17**
- **Spring Boot 4** (Spring Web, Spring Data JPA)
- **Hibernate** (ORM)
- **H2 Database** (in-memory, zero-config)
- **Maven** (build tool)

## Architecture

The project follows a standard layered architecture:

```
Controller -> Service -> Repository -> Entity -> Database
```

- **Entity**: `Product`, `Transaction` — define the data model
- **Repository**: Spring Data JPA interfaces for database access
- **Service**: business logic, including the demand-forecasting algorithm
- **Controller**: REST endpoints exposing the API over HTTP

## How the forecasting works

1. Every sale is logged as a `Transaction` (product, quantity, timestamp)
2. The service calculates **average daily usage** from recent transaction history
3. It divides current stock by that rate to estimate **days until stockout**
4. It recommends a **reorder quantity** sized to cover a 14-day buffer

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/products` | Get all products |
| `GET` | `/products/{id}` | Get a single product by ID |
| `POST` | `/products` | Add a new product |
| `PUT` | `/products/{id}` | Update an existing product |
| `DELETE` | `/products/{id}` | Delete a product |
| `GET` | `/products/search?category=X` | Search products by category |
| `GET` | `/products/low-stock` | Get products below their reorder threshold |
| `POST` | `/products/{id}/sale` | Record a sale (decrements stock, logs a transaction) |
| `GET` | `/products/{id}/reorder-suggestion` | Get a demand-forecast reorder recommendation |

## Example: Reorder Suggestion

**Request:**
```
GET /products/1/reorder-suggestion
```

**Response:**
```json
{
    "productName": "Notebook",
    "currentQuantity": 0,
    "avgDailyUsage": 20.0,
    "daysUntilStockout": 0.0,
    "suggestedReorderQty": 280,
    "urgency": "HIGH"
}
```

## Example: Recording a Sale

**Request:**
```
POST /products/1/sale
Content-Type: application/json

{
    "quantitySold": 5
}
```

**Response:**
```json
{
    "id": 1,
    "product": {
        "id": 1,
        "name": "Notebook",
        "category": "Stationery",
        "price": 40.0,
        "quantity": 15,
        "reorderThreshold": 10
    },
    "quantitySold": 5,
    "saleDate": "2026-08-29T01:52:08.8156333"
}
```

## Running Locally

**Prerequisites:** Java 17+, Maven (or use the included wrapper)

```bash
git clone https://github.com/Shivendra-Mishra04/inventory-api.git
cd inventory-api
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`. The database is an in-memory H2 instance — data resets on restart.

## Future Improvements

- Input validation and centralized exception handling
- Persistent database option (PostgreSQL/MySQL)
- Authentication for write operations
- Unit tests for the forecasting logic