# 🚗 Fulkoping Rental API

This project is a backend REST API built with **Spring Boot** that allows users to rent vehicles.  
The primary goal of the project is to practice Spring Boot and build a REST API according to best practices, including proper HTTP methods, status codes, validation, error handling, and Swagger/OpenAPI documentation.

---

## ✨ Features

- CRUD operations for:
  - **Vehicles** (Car, Truck, Trailer)
  - **Users** (Customer, Admin)
  - **Rentals** (relation between users and vehicles)
- Validation of input data (non-empty fields, correct types)
- Proper HTTP responses for errors (400, 404, etc.)
- Logging of service operations
- API key-based authentication:
  - `UserKey` 
  - `AdminKey`
- Swagger/OpenAPI documentation with annotations
- Postman collection for API testing

---

## 📁 Project Structure

- **config/** – Swagger/OpenAPI configuration  
- **controller/** – REST controllers (`rental`, `vehicle`, `user`)  
- **dto/** – Data Transfer Objects for create, update, and get operations  
- **exceptions/** – Global exception handling  
- **mapper/** – Maps between entities and DTOs (`rental`, `vehicle`, `user`)  
- **model/** – Entities:
  - `User` (abstract) → `Customer`, `Admin`
  - `Vehicle` (abstract) → `Car`, `Truck`, `Trailer`
  - `Rental` (relation between User and Vehicle)
- **repository/** – Spring Data JPA repositories (`rental`, `car`, `user`)  
- **security/** – API key filter, service, and Spring Security configuration  
- **service/** – Business logic for `rental`, `user`, and `vehicle`  
- **logs/** – Application log file (`rental-app.log`)

---

## 🛠 Built With

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA & Hibernate**
- **MySQL** as database
- **Spring Security** (API key authentication)
- **Log4j2** for logging
- **Springdoc OpenAPI** for Swagger documentation

---

## 📄 Documentation

- Swagger UI is available at: `http://localhost:8080/swagger-ui.html`
- Full OpenAPI specification and Postman collection are included in the `docs/` folder

---

## 🚀 Getting Started

### 1. Clone the project
```
git clone https://github.com/fridastephanie/java24-webservice-frida-hassel-fulkopingsuthyrning
```
```
cd fulkoping_rental
```

### 2. Configure database
* Update MySQL credentials in application.properties if needed
* Database is automatically created if it does not exist

### 3. Build and run
```
mvn spring-boot:run
```
→ The API will be available at http://localhost:8080
