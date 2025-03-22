# Zoomind Assignment - Test Case Management Service

## 🚀 Live Deployment
The application is deployed at:
👉 [Zoomind Assignment - Live](https://zoomind-assignment.onrender.com/swagger-ui/index.html)

## 🐜 Overview
This is a **Test Case Management Service** built using **Spring Boot, MongoDB, Redis, and Docker**. It allows users to create, manage, and retrieve test cases efficiently.

## 📌 Key Decisions

### ✅ Using Testcontainers for MongoDB in Tests
- **Problem**: Testing was required, but the DB setup was unspecified.
- **Decision**: Use **Testcontainers** instead of an in-memory MongoDB instance.
- **Why?**
  - Ensures real-world testing with an actual MongoDB instance.
  - Avoids inconsistencies between test and production environments.

### ✅ Using Redis for Caching
- **Problem**: Caching was optional.
- **Decision**: Use **Redis** to cache GET requests and reduce DB load.
- **Why?**
  - Reduces duplicate DB queries for frequently accessed test cases.
  - In-memory storage makes GET requests much faster.
  - Cache eviction ensures data consistency after updates or deletions.

### ✅ Indexing Strategy
- **Compound Index** `{ status: 1, priority: 1 }` → Optimizes queries filtering by both and by status.
- **Separate Index** on `priority` → Ensures efficient filtering even when `status` is not used.

### ✅ Making the `GET /testcases` API Flexible
- **Problem**: The API needed to support multiple filtering options dynamically.
- **Decision**: Allow fetching test cases with **no filters, a single filter, or multiple filters**.
- **Why?**
  - Provides flexibility for different querying needs.
  - Avoids multiple redundant endpoints for each filter combination.
  - Enhances usability while keeping the implementation efficient.

## 🤔 Running Locally (Without Docker)

### 1⃣ Set Up Environment Variables
Before running the application, set up the required environment variables:

```sh
export MONGO_URI=mongodb://localhost:27017/testcase-db
export REDIS_HOST=localhost
export REDIS_PORT=6379
export SERVER_PORT=8080
```

### 2⃣ Run with Maven
Ensure you have **Java 17+** and **Maven** installed.

```sh
mvn clean install
mvn spring-boot:run
```

### 3⃣ Running Tests
To execute tests, ensure MongoDB is running and execute:

```sh
mvn test
```

---

## 🐫 Running with Docker Compose (Recommended)

To simplify the setup, we use `docker-compose` to:
- Spin up **MongoDB** and **Redis**.
- Start the application using the **Docker image**: `imrohit68/zoomind-assignment`.

### 1⃣ Install Docker & Docker Compose
Ensure **Docker** and **Docker Compose** are installed on your system.

### 2⃣ Create `docker-compose.yml`

```yaml
version: '3.8'

services:
  mongo:
    image: mongo:latest
    container_name: mongo_db
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: testcase-db

  redis:
    image: redis:latest
    container_name: redis_cache
    ports:
      - "6379:6379"

  app:
    image: imrohit68/zoomind-assignment
    container_name: zoomind-assignment
    depends_on:
      - mongo
      - redis
    ports:
      - "8080:8080"
    environment:
      MONGO_URI: mongodb://mongo:27017/testcase-db
      REDIS_HOST: redis
      REDIS_PORT: 6379
      SERVER_PORT: 8080
```

### 3⃣ Start Everything with One Command

```sh
docker-compose up -d
```

This will automatically:
- Start **MongoDB** and **Redis**.
- Launch your application using `imrohit68/zoomind-assignment`.
- Pass the required environment variables.

### 4⃣ Stopping Services
To stop the running containers:

```sh
docker-compose down
```
## 🚀 Future Improvements

- **Bulk Operations**: Support batch creation and deletion of test cases.
- **Role-Based Access Control (RBAC)**: Implement different roles (admin, tester, viewer) with access restrictions.
- **Versioning for Test Cases**: Track historical changes and allow rollbacks.
- **GraphQL Support**: Provide more flexible querying options.
- **Enhanced Caching Strategy**: Optimize Redis caching to handle different query patterns efficiently.
