# The Wild Child Management

The Wild Child Management is a backend management system for a cafe/POS.

It provides APIs for managing users, menu items, menu categories, add-ons, cafe tables, dining sessions, orders, kitchen order tickets, invoices, payments, and reports.

The project is built as a modular monolith and is intended as a practice project for learning and applying real-world backend development concepts.
## Tech Stack

- **Java 21** — Programming language
- **Spring Boot 3.5.x** — Backend framework
- **Spring Web** — REST API development
- **Spring Data JPA** — Data access
- **Hibernate** — ORM
- **PostgreSQL** — Database
- **Spring Security** — Authentication and authorization
- **JWT (JJWT 0.12.6)** — Token-based authentication
- **BCrypt** — Password hashing
- **Maven** — Build and dependency management
- **Flyway** — Database migration
- **JUnit 5** — Testing
- **Mockito** — Mocking
- **MockMvc** — Controller integration testing
- **OpenAPI / Swagger** — API documentation and testing
## Architecture

The application follows a **Modular Monolith** architecture.

The system is organized into business modules, with each module responsible for a specific domain of the cafe management system.

### Modules

- Auth
- User
- Role
- Menu
- Table
- Dining Session
- Order
- KOT
- Invoice
- Payment
- Report
- Common

### Layered Structure

Each module follows a layered structure where applicable:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/thewildchild/management/
│   │       ├── auth/
│   │       ├── common/
│   │       ├── config/
│   │       ├── diningsession/
│   │       ├── invoice/
│   │       ├── menu/
│   │       ├── order/
│   │       ├── payment/
│   │       ├── report/
│   │       ├── role/
│   │       ├── table/
│   │       └── user/
│   │
│   └── resources/
│       └── db/
│           └── migration/
│
└── test/
    └── java/
        └── com/thewildchild/management/
```
## Authentication & Authorization

The application uses **Spring Security** with **JWT-based authentication**.

### Authentication Flow

```text
Login Request
     ↓
AuthenticationManager
     ↓
User Authentication
     ↓
JWT Generation
     ↓
Bearer Token
     ↓
JwtAuthenticationFilter
     ↓
JWT Validation
     ↓
SecurityContext
     ↓
Protected API
```
## API Documentation

The application uses **OpenAPI** and **Swagger UI** for API documentation and testing.

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Database

The application uses **PostgreSQL** as its relational database.

**Flyway** is used to manage database schema migrations.

Database-related responsibilities include:

- Entity mapping using JPA/Hibernate
- Repository-based data access using Spring Data JPA
- Database schema versioning using Flyway
- Transaction management using Spring's `@Transactional`


## Main Modules

### Authentication & Authorization

- User login
- JWT authentication
- Bearer token authorization
- Role-based access control

### User Management

- Create users
- Get user by ID
- Get all users
- Update users
- Soft delete users

### Menu Management

- Manage menu categories
- Manage menu items
- Manage add-ons
- Attach add-ons to menu items
- Remove add-ons from menu items

### Table Management

- Create cafe tables
- Get table details
- Get all tables
- Update tables
- Soft delete tables

### Dining Sessions

- Open dining sessions
- Get dining session details
- Get open session for a table
- Get all open sessions
- Close dining sessions
- Cancel dining sessions

### Order Management

- Create orders
- Get order details
- Add menu items to orders
- Add add-ons to order items
- Close orders

### Kitchen Order Tickets

- Manage kitchen order tickets
- Track order items sent to the kitchen
- Track KOT status

### Invoice Management

- Generate invoices
- Manage invoice information
- Track invoice status

### Payment Management

- Process payments
- Track payment status
- Manage invoice payments

### Reporting

- Sales reports
- Payment reports
- Menu/item reports

## Main Business Workflow

The main cafe workflow is:

```text
Cafe Table
    ↓
Dining Session
    ↓
Order
    ↓
Menu Items + Add-ons
    ↓
Kitchen Order Ticket (KOT)
    ↓
Close Order
    ↓
Invoice
    ↓
Payment
    ↓
Reports
```

## API Endpoints

Base URL:

```text
http://localhost:8080/api/v1
```
## Testing

The project includes automated tests for the backend components and APIs.

### Testing Technologies

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Spring Security Test

### Test Coverage

Tests cover areas such as:

- Service-layer business logic
- Repository/database integration
- Controller integration
- Request validation
- Authentication
- JWT-based authorization
- Business rule validation
- Database state verification

Controller integration tests use **MockMvc** and JWT authentication for protected endpoints.

## Configuration & Setup

### Requirements

- Java 21
- PostgreSQL
- Maven

### Database Configuration

Configure the PostgreSQL database connection in:

```text
src/main/resources/application.properties
```
### Run the Application

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

### Run Tests

Windows:

```bash
mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```
