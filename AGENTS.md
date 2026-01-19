# AGENTS.md - TKG Backend Development Guide

## Overview
This is a Spring Boot 3.3.4 application using Java 17, providing a knowledge graph backend with user management, entity CRUD operations, and file upload capabilities. The project uses Maven for build management and follows a layered architecture pattern.

## Build, Lint & Test Commands

### Full Build
```bash
mvn clean compile
```

### Package Application
```bash
mvn clean package -DskipTests
```

### Run Application
```bash
mvn spring-boot:run
```
For development with live reload, use:
```bash
mvn spring-boot:run -Dspring-boot.run.fork=false
```

### Run All Tests
```bash
mvn test
```

### Run Single Test Class
```bash
mvn test -Dtest=UserServiceImplTest
```

### Run Single Test Method
```bash
mvn test -Dtest=UserServiceImplTest#testUserLogin
```

### Run Tests with Coverage (if jacoco plugin added)
```bash
mvn test jacoco:report
```

### Check Dependencies
```bash
mvn dependency:tree
```

### Clean and Install
```bash
mvn clean install -DskipTests
```

### Generate API Documentation
```bash
mvn spring-boot:run
# Then visit: http://localhost:8080/doc.html
```

## Code Style Guidelines

### Project Structure
```
src/main/java/com/wtu/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business logic (interfaces in service/, impl in service/impl/)
├── mapper/          # MyBatis data access layer
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

### Package Naming
- Use `com.wtu` as base package
- Layer-specific subpackages: `controller`, `service`, `mapper`, `entity`, etc.
- Feature-based naming within layers when needed

### Class Naming
- **Controllers**: `[Feature]Controller` (e.g., `UserController`, `EntityController`)
- **Services**: `I[Feature]Service` interface, `[Feature]ServiceImpl` implementation
- **Mappers**: `[Feature]Mapper`
- **Entities**: PascalCase domain nouns (e.g., `User`, `Entity`)
- **DTOs**: `[Feature][Action]DTO` (e.g., `UserLoginDTO`, `UserRegisterDTO`)
- **VOs**: `[Feature][Action]VO` (e.g., `UserLoginVO`)
- **Utils**: `[Feature]Util` or `[Feature]Utils` (e.g., `JwtUtils`, `PasswordEncoder`)
- **Constants**: `[Feature]Constant` (e.g., `MessageConstant`, `StatusConstant`)

### Import Order
```java
// 1. Java standard library imports
import java.time.LocalDateTime;
import java.util.stream.Collectors;

// 2. Blank line
// 3. Third-party imports (Spring, validation, etc.)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotBlank;

// 4. Blank line
// 5. Project internal imports (grouped by package)
import com.wtu.constant.MessageConstant;
import com.wtu.entity.User;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.UserMapper;
```

### Lombok Usage
Always use Lombok annotations in this order:
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    // fields
}
```

### Method Signatures
- Use `final` for injected dependencies
- Prefer constructor injection with `@RequiredArgsConstructor`
- Service methods should be descriptive and action-oriented

### Validation
- Use Jakarta Validation annotations on DTOs
- Custom validation messages in Chinese for user-facing APIs
- Example:
```java
@NotBlank(message = "用户名不能为空")
@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
private String phone;
```

### Exception Handling
- **Business exceptions**: Throw `BusinessException` with message constants
- **Global handling**: Use `@RestControllerAdvice` in `GlobalExceptionHandler`
- **Validation errors**: Automatically handled by global exception handler
- **Never catch generic Exception** without rethrowing or logging

### Logging
- Use `@Slf4j` annotation from Lombok
- Log levels: ERROR for exceptions, INFO for important business logic, DEBUG for detailed operations
- Include relevant context in log messages:
```java
log.info("用户登录 {}", userLoginDTO);
log.error("业务异常：{}", ex.getMessage());
```

### Transactions
- Use `@Transactional(rollbackFor = Exception.class)` on service methods that modify data
- Include in method documentation when transactions are critical

### API Documentation
- Use Swagger annotations (`@Tag`, `@Operation`) on controllers and methods
- Include Chinese descriptions for API documentation
- Example:
```java
@Tag(name = "用户管理")
@Operation(summary = "用户登录", description = "通过用户名, 密码登录")
```

### Naming Conventions
- **Variables**: camelCase (e.g., `userName`, `phoneNumber`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_AVATAR = "default.png"`)
- **Methods**: camelCase, action-oriented (e.g., `findNameById`, `validatePassword`)
- **Database columns**: snake_case in SQL, camelCase in Java (auto-mapped by MyBatis)

### Database Operations
- Use MyBatis XML mappers for complex queries
- Simple CRUD can use annotations
- Enable `map-underscore-to-camel-case` for automatic field mapping
- Use `@Param` for named parameters in mappers

### Security
- JWT tokens for authentication
- Password encryption with SHA-256 + salt
- Sensitive data never logged (passwords, tokens)
- Input validation on all public APIs

### File Upload
- Maximum file size: 10MB (configured in application.yml)
- Use Alibaba Cloud OSS for file storage
- Validate file types and sizes in controllers

### Response Format
- Always return `Result<T>` wrapper objects
- Success responses: `Result.success(data)`
- Error responses: `Result.error(message)`
- Include pagination info in `PageResult` for list endpoints

### Configuration
- Use `application.yml` for default config
- Override with `application-local.yml` for development
- Use environment variables for secrets (JWT secret, database credentials, OSS keys)
- Profile activation: `spring.profiles.active=local`

### Testing
- Unit tests in `src/test/java`
- Use Spring Boot Test framework
- Mock external dependencies (database, OSS)
- Test service layer business logic primarily
- Integration tests for full request flows

### Development Environment Setup
1. JDK 17+
2. Maven 3.6+
3. MySQL 8.0+
4. Configure environment variables:
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/tkg
   export DB_USER=root
   export DB_PWD=your_password
   export JWT_SECRET=your-32-char-secret-key
   export OSS_AK=your-oss-access-key
   export OSS_SK=your-oss-secret-key
   ```

### Code Quality Checks
- Compile-time validation with Jakarta Validation
- Manual code review required for:
  - Database schema changes
  - Security-related modifications
  - API contract changes
- Use `mvn compile` to check for compilation errors

### Deployment
- Build JAR: `mvn clean package -DskipTests`
- Run JAR: `java -jar target/tkg-backend-0.0.1-SNAPSHOT.jar`
- Docker support available (see README.md)

### Common Patterns
- **DTO Pattern**: Separate data transfer objects from entities
- **Builder Pattern**: Use Lombok `@Builder` for complex object construction
- **Strategy Pattern**: Consider for different user types or entity statuses
- **Template Method**: For repetitive CRUD operations

### Performance Considerations
- Tree structure queries optimized to avoid N+1 problems
- OSS client singleton pattern
- Database connection pooling via HikariCP
- Transaction boundaries at service layer

This guide ensures consistent code quality and maintainability across the TKG backend project.</content>
<parameter name="filePath">AGENTS.md