# 📚 Library Management System (LMS)

A production-style **Library Management System** built using **Java, Spring Boot, Spring Data JPA, MySQL, and REST APIs**.

The project is designed to demonstrate enterprise backend development practices such as layered architecture, DTO mapping, JPA relationships, validation, exception handling, AOP logging, pagination, and clean code principles.

> 🚧 This project is being developed incrementally. Each feature is implemented in a separate Git branch and merged after completion.

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
- Git & GitHub
- Postman

---

## 📁 Project Structure

```text
src/main/java/com/project/lms

├── controller
├── service
│   └── impl
├── repository
├── model
│   ├── entity
│   └── enums
├── dto
│   ├── request
│   └── response
├── mapper
├── exception
├── response
├── aspect
├── config
├── util
└── LmsApplication
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
- [In Progress] Spring Security
- [ ] Testing
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

## 🧪 Testing

API testing is performed using Postman.

Unit and integration testing will be added in future milestones.

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
