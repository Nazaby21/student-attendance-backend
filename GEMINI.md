# Student Attendance Backend

This project is a Spring Boot-based backend for managing student attendance, featuring a layered architecture and robust data management patterns.

## 🏗️ Architecture & Standards

### Layered Structure
- **Controllers**: Entry points for HTTP requests (`com.example.demo.controller`).
- **Services**: Business logic interfaces (`com.example.demo.service`) and their implementations (`com.example.demo.service.ServiceImpl`).
- **Repositories**: Data access layer using Spring Data JPA (`com.example.demo.repository`).
- **Models/Entities**: JPA entities located in the `modal` (should be `model`) package.
- **DTOs**: Data Transfer Objects for requests and responses (`com.example.demo.dto`).
- **Mappers**: MapStruct interfaces for DTO-Entity conversion (`com.example.demo.mapper`).

### Core Technologies
- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Data JPA** (with PostgreSQL)
- **Spring Security**
- **Lombok**
- **MapStruct**

### Domain Model
- **User**: Students, Teachers, and Admins.
- **ClassEntity**: Academic classes (e.g., "Grade 10").
- **Enrollment**: Links a Student to a Class.
- **Subject**: Academic subjects.
- **ClassSession**: A specific instance of a class (links Class, Subject, and Teacher).
- **Attendance**: Status of a student in a ClassSession.

## 🛠️ Coding Guidelines

1. **DTO Pattern**: Never return JPA entities directly in Controllers. Always use `Request` DTOs for inputs and `Response` DTOs for outputs.
2. **Mapping**: Use MapStruct for all DTO/Entity transformations. Ensure `lombok-mapstruct-binding` is used to avoid conflicts.
3. **Business Logic**: Keep Controllers thin. All business rules must reside in `ServiceImpl` classes.
4. **Naming Conventions**:
    - Services should follow `[Entity]Service` / `[Entity]ServiceImpl`.
    - Repositories should follow `[Entity]Repository`.
    - DTOs should be in `dto.Request` or `dto.Response`.
5. **Boilerplate**: Use Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`) to minimize boilerplate.
6. **Enums**: Use the `enumeration` package for state-related constants like `Role`, `Gender`, and `Status`.

## 🔒 Security
- Authentication and Authorization are handled via Spring Security.
- Check `security` package for configuration details.
