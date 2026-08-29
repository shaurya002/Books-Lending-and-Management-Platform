# 📚 Books Lending Management Platform

A production-style **Books Lending Management Platform** built using **Java, Spring Boot, Spring Data JPA, MySQL, and REST APIs**.

The project is designed to demonstrate enterprise backend development practices such as layered architecture, DTO mapping, JPA relationships, validation, exception handling, AOP logging, pagination, and clean code principles.

---

## 🚀 Features

- Book Management
- Author Management
- Member Management
- Borrow & Return Books
- Borrow Limit Validation
- Fine Calculation
- Search & Filtering
- Pagination
- Global Exception Handling
- AOP Logging & Audit
- RESTful APIs

---

## 🛠 Tech Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok
- Spring Validation
- Spring AOP
- Spring Security
- Git & GitHub
- Postman

---

## 📁 Project Structure

```text
src/main/java/com/shaurya/bookslendingmanagementplatform

├── controller
├── service
│   └── impl
├── repositories
├── model
│   ├── entity
│   └── enums
├── dto
│   ├── request
│   └── response
├── mapper
├── exception
├── aspect
├── config
└── BooksLendingManagementPlatformApplication
```

---

## 📖 Business Workflow

### Book Registration

- Register books with ISBN
- Prevent duplicate ISBNs
- Track available copies

### Member Registration

- Register library members
- Prevent duplicate email addresses

### Borrow Book

- Verify member exists
- Verify book exists
- Check availability
- Check borrow limit
- Create borrow record
- Update available copies

### Return Book

- Update return date
- Calculate overdue fine
- Increase available copies
- Maintain borrowing history

---

## 🗄 Database Design

### Entities

- Book
- Author
- Member
- BorrowRecord

### Relationships

- Author ↔ Book (Many-to-Many)
- Book → BorrowRecord (One-to-Many)
- Member → BorrowRecord (One-to-Many)

---

## 📌 Development Roadmap

- [x] Project Setup
- [x] Entity Design & Relationships
- [x] Repository Layer
- [x] Service Layer
- [x] REST APIs
- [x] Validation
- [x] Global Exception Handling
- [x] Pagination & Search
- [x] AOP Logging
- [x] Documentation
- [x] Spring Security
- [x] Testing
---

## 🌿 Git Branch Strategy

```
main

feature/entities

feature/repositories

feature/dto-mapper

feature/service-layer

feature/rest-api

feature/validation-exception

feature/pagination-search

feature/aop

feature/documentation

feature/spring-security

feature/testing
```

---

## 📮 API Documentation

API endpoints are documented using Swagger / OpenAPI.

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Use the Swagger UI to explore and test endpoints interactively. API testing is also performed using Postman.

---

### 🔐 Security

- HTTP Basic Authentication
- Role-based authorization
- LIBRARIAN and MEMBER roles
- Protected REST endpoints
- Password hashing using BCrypt

---

## 🧪 Testing

- Unit testing implemented for service and controller layers
- JUnit 5 and Mockito used for unit testing
- MockMvc used for controller-layer testing
- Postman used for API testing
- 66+ service-layer unit tests implemented
- Controller tests cover HTTP status codes, request/response handling, validation, and service interactions

Integration testing will be added in future milestones.

---

## 🚀 Future Enhancements

- JWT Authentication
- Docker Compose
- Redis Cache
- Spring AI Integration
- Email Notifications

---

## 👨‍💻 Author

**Shaurya Pratap Singh**

Java Backend Developer
