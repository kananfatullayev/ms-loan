# MS-Loan Service

A RESTful microservice for managing loanEntity operations built with Spring Boot 4.0, JPA/Hibernate, and PostgreSQL.

## Features

- Create, read, update, and delete loans
- Query loans by user ID
- PostgreSQL database with JPA/Hibernate
- RESTful API endpoints
- Global exception handling
- Lombok for reducing boilerplate code
- Automatic timestamp management

## Prerequisites

- Java 21
- PostgreSQL 12 or higher
- Gradle (wrapper included)

## Database Setup

1. Create the PostgreSQL database:

```bash
psql -U postgres
CREATE DATABASE loandb;
```

2. (Optional) Run the SQL script to create the table manually:

```bash
psql -U postgres -d loandb -f database-setup.sql
```

**Note:** The application will automatically create the table using Hibernate DDL auto-update feature on startup.

## Configuration

Update the database credentials in `src/main/resources/application.yml` if needed:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loandb
    username: postgres
    password: postgres
```

## Running the Application

```bash
./gradlew bootRun
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/ms-loanEntity-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### 1. Create Loan

**POST** `/api/v1/loans`

**Request Body:**
```json
{
  "userId": 1,
  "amount": 50000.00,
  "monthlyAmount": 2500.00,
  "duration": 24.00
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "userId": 1,
  "amount": 50000.00,
  "monthlyAmount": 2500.00,
  "duration": 24.00,
  "createdAt": "2025-12-07T10:30:00",
  "updatedAt": "2025-12-07T10:30:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/loans \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 50000.00,
    "monthlyAmount": 2500.00,
    "duration": 24.00
  }'
```

### 2. Get Loan by ID

**GET** `/api/v1/loans/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "userId": 1,
  "amount": 50000.00,
  "monthlyAmount": 2500.00,
  "duration": 24.00,
  "createdAt": "2025-12-07T10:30:00",
  "updatedAt": "2025-12-07T10:30:00"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/v1/loans/1
```

### 3. Get All Loans

**GET** `/api/v1/loans`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "userId": 1,
    "amount": 50000.00,
    "monthlyAmount": 2500.00,
    "duration": 24.00,
    "createdAt": "2025-12-07T10:30:00",
    "updatedAt": "2025-12-07T10:30:00"
  },
  {
    "id": 2,
    "userId": 2,
    "amount": 30000.00,
    "monthlyAmount": 1500.00,
    "duration": 20.00,
    "createdAt": "2025-12-07T11:00:00",
    "updatedAt": "2025-12-07T11:00:00"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/v1/loans
```

### 4. Get Loans by User ID

**GET** `/api/v1/loans/user/{userId}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "userId": 1,
    "amount": 50000.00,
    "monthlyAmount": 2500.00,
    "duration": 24.00,
    "createdAt": "2025-12-07T10:30:00",
    "updatedAt": "2025-12-07T10:30:00"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/v1/loans/user/1
```

### 5. Update Loan

**PUT** `/api/v1/loans/{id}`

**Request Body:**
```json
{
  "userId": 1,
  "amount": 55000.00,
  "monthlyAmount": 2750.00,
  "duration": 24.00
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "userId": 1,
  "amount": 55000.00,
  "monthlyAmount": 2750.00,
  "duration": 24.00,
  "createdAt": "2025-12-07T10:30:00",
  "updatedAt": "2025-12-07T11:45:00"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/loans/1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 55000.00,
    "monthlyAmount": 2750.00,
    "duration": 24.00
  }'
```

### 6. Delete Loan

**DELETE** `/api/v1/loans/{id}`

**Response:** `204 No Content`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/loans/1
```

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2025-12-07T11:45:00",
  "message": "Loan not found with id: 999",
  "details": "uri=/api/v1/loans/999",
  "status": 404
}
```

### 400 Bad Request
```json
{
  "timestamp": "2025-12-07T11:45:00",
  "message": "Invalid request data",
  "details": "uri=/api/v1/loans",
  "status": 400
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-12-07T11:45:00",
  "message": "An error occurred processing your request",
  "details": "uri=/api/v1/loans",
  "status": 500
}
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── msloan/
│   │               ├── MsLoanApplication.java       # Main application class
│   │               ├── controller/
│   │               │   └── LoanController.java      # REST endpoints
│   │               ├── dao/
│   │               │   └── LoanRepository.java      # JPA repository
│   │               ├── exception/
│   │               │   ├── ErrorResponse.java       # Error response DTO
│   │               │   ├── GlobalExceptionHandler.java  # Exception handling
│   │               │   └── LoanNotFoundException.java   # Custom exception
│   │               ├── model/
│   │               │   ├── Loan.java                # Loan entity
│   │               │   └── dto/
│   │               │       ├── LoanRequestDto.java  # Request DTO
│   │               │       └── LoanResponseDto.java # Response DTO
│   │               └── service/
│   │                   ├── LoanService.java         # Service interface
│   │                   └── impl/
│   │                       └── LoanServiceImpl.java # Service implementation
│   └── resources/
│       └── application.yml                          # Application configuration
└── test/
    └── java/
        └── com/
            └── example/
                └── msloan/
                    └── MsLoanApplicationTests.java
```

## Database Schema

```sql
CREATE TABLE loans (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    amount NUMERIC(16, 2) NOT NULL,
    monthly_amount NUMERIC(16, 2) NOT NULL,
    duration NUMERIC(8, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loans_user_id ON loans(user_id);
```

## Technologies Used

- **Spring Boot 4.0.0** - Application framework
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Database
- **Lombok** - Reduce boilerplate code
- **Hibernate** - ORM framework
- **Spring Cloud OpenFeign** - Declarative REST client (for future microservices communication)
- **Gradle** - Build tool

## Development

### Build the project
```bash
./gradlew build
```

### Run tests
```bash
./gradlew test
```

### Clean build
```bash
./gradlew clean build
```

### Run without building
```bash
./gradlew bootRun
```

## License

This project is licensed under the MIT License.

