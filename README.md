# Programming in Java — Flipped Course Evaluated Project

Student: Suha Vora
Registration No.: 25BAI11500
Subject: Programming in Java (VITyarthi Flipped Course)
Deadline: Sep 18, 2026

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

## Project Documentation

- **Statement:** [statement.md](statement.md) — Problem statement, scope, target users, and high-level features
- **Design Documentation:** [docs/design.md](docs/design.md) — System architecture, UML diagrams, ER diagrams, and workflow diagrams
- **Project Report:** [docs/project-report.md](docs/project-report.md) — Complete project report with implementation details, testing approach, and learnings

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

| Method     | Endpoint                              | Description                                          |
| ---------- | ------------------------------------- | ---------------------------------------------------- |
| `GET`    | `/products`                         | Get all products                                     |
| `GET`    | `/products/{id}`                    | Get a single product by ID                           |
| `POST`   | `/products`                         | Add a new product                                    |
| `PUT`    | `/products/{id}`                    | Update an existing product                           |
| `DELETE` | `/products/{id}`                    | Delete a product                                     |
| `GET`    | `/products/search?category=X`       | Search products by category                          |
| `GET`    | `/products/low-stock`               | Get products below their reorder threshold           |
| `POST`   | `/products/{id}/sale`               | Record a sale (decrements stock, logs a transaction) |
| `GET`    | `/products/{id}/reorder-suggestion` | Get a demand-forecast reorder recommendation         |

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

### Prerequisites

Ensure the following are installed on your system before proceeding:

| Tool       | Minimum Version | Verification Command |
| ---------- | --------------- | -------------------- |
| Java (JDK) | 17              | `java -version`    |
| Maven      | 3.9+            | `mvn -version`     |
| Git        | 2.0+            | `git --version`    |

> **Note:** The project includes a Maven Wrapper (`mvnw`/`mvnw.cmd`), so a separate Maven installation is optional. If Maven is not installed, use `./mvnw` (Linux/macOS) or `mvnw.cmd` (Windows) in place of `mvn` in all commands below.

---

### Step 1: Clone the Repository

```bash
git clone https://github.com/suha222907/java-inventory-project.git
cd inventory-api
```

---

### Step 2: Verify Java Version

Confirm Java 17 is the active JDK:

```bash
java -version
```

Expected output (example):

```
openjdk version "17.0.10" 2024-01-16
OpenJDK Runtime Environment (build 17.0.10+7)
OpenJDK 64-Bit Server VM (build 17.0.10+7, mixed mode, sharing)
```

If multiple JDKs are installed, ensure `JAVA_HOME` points to Java 17:

```bash
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

---

### Step 3: Build the Project (Download Dependencies & Compile)

Using Maven Wrapper (recommended, no separate Maven install needed):

```bash
./mvnw clean compile
```

Or with system Maven:

```bash
mvn clean compile
```

This will:

- Download all declared dependencies (Spring Boot, Hibernate, H2, etc.)
- Compile the source code
- Run any configured annotation processors

---

### Step 4: Run the Application

**Option A: Using Maven Wrapper (Linux/macOS)**

```bash
./mvnw spring-boot:run
```

**Option B: Using Maven Wrapper (Windows)**

```cmd
mvnw.cmd spring-boot:run
```

**Option C: Using System Maven**

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**.

---

### Step 5: Verify the Application is Running

Open a new terminal and test the health endpoint:

```bash
curl http://localhost:8080/products
```

Expected response (empty array initially):

```json
[]
```

You can also verify via browser by navigating to `http://localhost:8080/products`.

---

### Step 6: Test Key Endpoints (Optional)

**Create a product:**

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Notebook", "category": "Stationery", "price": 40.0, "quantity": 100, "reorderThreshold": 10}'
```

**Record a sale:**

```bash
curl -X POST http://localhost:8080/products/1/sale \
  -H "Content-Type: application/json" \
  -d '{"quantitySold": 5}'
```

**Get reorder suggestion:**

```bash
curl http://localhost:8080/products/1/reorder-suggestion
```

---

### Step 7: Stop the Application

Press `Ctrl+C` in the terminal where the application is running.

---

### Database Notes

- **Database:** H2 (in-memory)
- **Console:** Accessible at `http://localhost:8080/h2-console` (enable in `application.properties` if needed)
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (empty)
- **Data persists only while the application is running** — all data resets on restart.

---

### Building a Runnable JAR (for deployment)

```bash
./mvnw clean package
```

This creates `target/inventory-api-0.0.1-SNAPSHOT.jar`. Run it with:

```bash
java -jar target/inventory-api-0.0.1-SNAPSHOT.jar
```

---

### Troubleshooting

| Issue                              | Resolution                                                                                               |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------- |
| `java: command not found`        | Install JDK 17 and ensure it's in PATH                                                                   |
| `./mvnw: Permission denied`      | Run`chmod +x mvnw`                                                                                     |
| Port 8080 already in use           | Stop the conflicting process or set`server.port=8081` in `src/main/resources/application.properties` |
| Build fails with dependency errors | Run`./mvnw dependency:purge-local-repository clean compile` to force re-download                       |

---

## Testing

### Running All Tests

Execute the full test suite (unit + integration tests):

```bash
./mvnw test
```

Or with system Maven:

```bash
mvn test
```

### Test Categories

| Test Type                     | Location                                                | Command                                                 | Coverage                                                     |
| ----------------------------- | ------------------------------------------------------- | ------------------------------------------------------- | ------------------------------------------------------------ |
| **Unit Tests**          | `src/test/java/.../service/`                          | `./mvnw test -Dtest=ProductServiceTest`               | Service layer logic, forecasting algorithm, validation       |
| **Integration Tests**   | `src/test/java/.../controller/`                       | `./mvnw test -Dtest=ProductControllerIntegrationTest` | Full HTTP request/response cycle, validation, error handling |
| **Application Context** | `src/test/java/.../InventoryApiApplicationTests.java` | `./mvnw test -Dtest=InventoryApiApplicationTests`     | Spring context loading, bean wiring                          |

### Running Specific Tests

```bash
# Run only unit tests
./mvnw test -Dtest=*ServiceTest

# Run only integration tests
./mvnw test -Dtest=*IntegrationTest

# Run with verbose output
./mvnw test -Dtest=ProductServiceTest#predictReorder_withSalesData_calculatesCorrectly
```

### Test Reports

After running tests, view the Surefire reports:

```bash
# HTML report
open target/surefire-reports/index.html

# Or list all test results
ls target/surefire-reports/
```

### Test Coverage (JaCoCo)

Generate code coverage report:

```bash
./mvnw jacoco:report
```

View report at `target/site/jacoco/index.html`.

### Manual API Testing with curl

**Create a product:**

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Notebook", "category": "Stationery", "price": 40.0, "quantity": 100, "reorderThreshold": 10}'
```

**List all products:**

```bash
curl http://localhost:8080/products
```

**Get product by ID:**

```bash
curl http://localhost:8080/products/1
```

**Update product:**

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Premium Notebook", "category": "Stationery", "price": 50.0, "quantity": 80, "reorderThreshold": 15}'
```

**Search by category:**

```bash
curl "http://localhost:8080/products/search?category=Stationery"
```

**Get low-stock products:**

```bash
curl http://localhost:8080/products/low-stock
```

**Record a sale:**

```bash
curl -X POST http://localhost:8080/products/1/sale \
  -H "Content-Type: application/json" \
  -d '{"quantitySold": 5}'
```

**Get reorder suggestion:**

```bash
curl http://localhost:8080/products/1/reorder-suggestion
```

**Delete product:**

```bash
curl -X DELETE http://localhost:8080/products/1
```

### Error Response Examples

**Validation Error (400):**

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -10, "quantity": -5}'
```

```json
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

**Not Found (404):**

```bash
curl http://localhost:8080/products/999
```

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "timestamp": "2026-09-18T10:30:00"
}
```

**Business Logic Error (400):**

```bash
curl -X POST http://localhost:8080/products/1/sale \
  -H "Content-Type: application/json" \
  -d '{"quantitySold": 1000}'
```

```json
{
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Insufficient stock for product 'Notebook'. Available: 100, Requested: 1000",
  "timestamp": "2026-09-18T10:30:00"
}
```

---

## Future Improvements

- Persistent database option (PostgreSQL/MySQL)
- Authentication for write operations
- API versioning
- OpenAPI/Swagger documentation
- Metrics and monitoring (Micrometer + Prometheus)
