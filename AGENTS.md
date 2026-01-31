# AGENTS.md - TKG Development Guide

## Overview
Spring Boot 3.3.4 + Java 17 backend with Vue 3 frontend. Knowledge graph platform for textile industry with user management, entity CRUD, file upload, and review workflows.

## Build, Lint & Test Commands

### Backend (Maven)
```bash
mvn clean compile                          # Full build
mvn clean package -DskipTests             # Package
mvn spring-boot:run                       # Run app
mvn spring-boot:run -Dspring-boot.run.fork=false  # With live reload
mvn test                                  # Run all tests
mvn test -Dtest=UserServiceImplTest       # Single test class
mvn test -Dtest=UserServiceImplTest#testUserLogin  # Single method
mvn dependency:tree                        # Check deps
mvn clean install -DskipTests             # Install
# API docs: http://localhost:8080/doc.html
```

### Frontend (Vue 3)
```bash
cd frontend
npm install                               # Install deps
npm run serve                             # Dev server (port 8081)
npm run build                             # Production build
```

## Code Style Guidelines

### Project Structure
```
src/main/java/com/wtu/
├── config/          # Configuration
├── controller/      # REST controllers
├── service/         # Business logic (interfaces + impl/)
├── mapper/          # MyBatis data access
├── entity/          # Domain entities
├── DTO/            # Data Transfer Objects
├── vo/             # Value Objects
├── utils/          # Utility classes
├── constant/       # Constants and enums
├── exception/      # Custom exceptions
├── interceptor/    # Request interceptors
├── result/         # Response wrappers
└── properties/     # Configuration properties
```

### Naming Conventions
- Base package: `com.wtu`
- Controllers: `[Feature]Controller`, Services: `I[Feature]Service`/`[Feature]ServiceImpl`
- Mappers: `[Feature]Mapper`, Entities: PascalCase (User, Entity)
- DTOs: `[Feature][Action]DTO` (UserLoginDTO), VOs: `[Feature][Action]VO`
- Utils: `[Feature]Util`/`[Feature]Utils`, Constants: `[Feature]Constant`
- Variables: camelCase, Constants: UPPER_SNAKE_CASE, Methods: camelCase action-oriented

### Import Order (ACTUAL PATTERN)
```java
// 1. Project internal imports
import com.wtu.constant.MessageConstant;
import com.wtu.entity.User;

// 2. Third-party imports
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
```

### Lombok & Class Structure
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User { }
```

### Dependency Injection
- Use `@RequiredArgsConstructor` with `final` fields for constructor injection
- Service methods: descriptive and action-oriented

### Validation
- Jakarta Validation on DTOs: `@NotBlank(message = "用户名不能为空")`
- Phone pattern: `@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")`

### Exception Handling
- Throw `BusinessException` with message constants
- Global handler: `@RestControllerAdvice` in `GlobalExceptionHandler`
- Never catch generic `Exception` without rethrowing/logging

### Logging
- `@Slf4j` annotation, levels: ERROR/INFO/DEBUG
- Include context: `log.info("用户登录 {}", userLoginDTO)`

### Transactions & Database
- `@Transactional(rollbackFor = Exception.class)` on data-modifying service methods
- MyBatis: XML for complex queries, annotations for simple CRUD
- Enable `map-underscore-to-camel-case`, use `@Param` for named params

### API Documentation
- `@Tag(name = "用户管理")`, `@Operation(summary = "用户登录")`
- Chinese descriptions required

### Security
- JWT authentication, SHA-256 + salt password encryption
- Never log sensitive data (passwords, tokens)
- Input validation on all public APIs

### Response Format
- `Result<T>` wrapper: `Result.success(data)` or `Result.error(message)`
- Pagination: `PageResult` for list endpoints

### Configuration
- Default: `application.yml`, Dev: `application-local.yml`
- Secrets via env vars: `DB_URL`, `DB_USER`, `DB_PWD`, `JWT_SECRET`, `OSS_AK`, `OSS_SK`
- Profile: `spring.profiles.active=local`

### Testing
- `src/test/java`, Spring Boot Test framework
- Mock external deps (database, OSS)
- Test service layer, integration tests for flows

### Development Environment
- JDK 17+, Maven 3.6+, MySQL 8.0+
- Configure environment variables

### Code Quality & Deployment
- Use `mvn compile` to check errors
- Manual review for: schema changes, security mods, API contract changes
- Build: `mvn clean package -DskipTests`, Run: `java -jar target/tkg-backend-0.0.1-SNAPSHOT.jar`

### Common Patterns
- DTO Pattern: Separate DTOs from entities
- Builder Pattern: `@Builder` for complex objects
- Strategy Pattern: Different user types or entity statuses
- Template Method: Repetitive CRUD operations

### Performance
- Tree queries optimized to avoid N+1 problems
- OSS client singleton, HikariCP connection pooling
- Transaction boundaries at service layer
