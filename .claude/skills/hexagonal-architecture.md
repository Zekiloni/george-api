---
name: hexagonal-architecture
description: Apply hexagonal architecture (ports and adapters) principles to code organization
type: skill
---

# Hexagonal Architecture Skill

## Hexagonal Architecture Principles

### Core Concept
Hexagonal architecture separates domain logic from external concerns through ports (interfaces) and adapters (implementations). The domain knows nothing about infrastructure.

### Architecture Layers

#### Domain Layer (Core)
- **Purpose**: Pure business logic, no external dependencies
- **Location**: `domain/model/`, `domain/service/`
- **Characteristics**:
  - Framework-agnostic
  - Contains business rules and domain models
  - No Spring annotations, no HTTP, no database specifics

#### Application Layer (Use Cases)
- **Purpose**: Orchestrate business operations using ports
- **Location**: `application/usecase/`, `application/port/`
- **Characteristics**:
  - Contains business workflows
  - Defines input ports (interfaces for use cases)
  - Defines output ports (interfaces for repositories/integrations)
  - May use Spring `@Service` and `@Transactional`

#### Infrastructure Layer (Adapters)
- **Purpose**: Implement ports and handle technical details
- **Location**: `infrastructure/in/` (inbound), `infrastructure/out/` (outbound)
- **Characteristics**:
  - Inbound adapters: REST controllers, message consumers
  - Outbound adapters: JPA repositories, external API clients
  - Contains all framework-specific code

### Port Definitions

#### Input Ports (Use Case Interfaces)
```java
// application/port/in/
public interface UserCreateUseCase {
    User createUser(UserCreateCommand command);
}
```

#### Output Ports (Repository/Integration Interfaces)
```java
// application/port/out/
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
}
```

### Adapter Implementations

#### Inbound Adapters (Controllers)
```java
// infrastructure/in/web/controller/
@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserCreateUseCase userCreateUseCase;
    
    // Constructor injection, delegates to use case
}
```

#### Outbound Adapters (Repositories)
```java
// infrastructure/out/persistence/adapter/
@Repository
public class UserRepositoryPortAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;
    
    // Implements port interface using JPA
}
```

## Dependency Rule
**Dependencies must point inward**: Infrastructure → Application → Domain
- Domain has zero dependencies on outer layers
- Application depends only on Domain
- Infrastructure implements interfaces defined in Application

## Project-Specific Implementation

### Current George API Structure
```
domain/
├── model/              # Pure domain models (immutable, framework-free)
application/
├── port/in/           # Use case interfaces
├── port/out/          # Repository/integration interfaces
└── usecase/           # Business logic services
infrastructure/
├── in/                # REST controllers, web handlers
├── out/               # JPA repositories, external service clients
└── config/            # Spring configuration
```

### Adding New Features Using Hexagonal Architecture

1. **Define Domain Model** (if needed)
   - Create in `domain/model/`
   - Make immutable, framework-agnostic
   - Add business logic methods

2. **Create Output Port** (if data persistence needed)
   - Interface in `application/port/out/`
   - Define operations required by domain

3. **Implement Outbound Adapter**
   - Create in `infrastructure/out/persistence/adapter/`
   - Implement port interface
   - Handle JPA/HTTP specifics

4. **Define Input Port**
   - Interface in `application/port/in/`
   - Define use case contract

5. **Implement Use Case**
   - Create in `application/usecase/`
   - Implement input port
   - Use output ports for dependencies
   - Add `@Transactional` if modifying data

6. **Create Inbound Adapter**
   - REST controller in `infrastructure/in/web/`
   - Delegate to use case
   - Handle HTTP-specific concerns (validation, serialization)

## Benefits
- **Testability**: Domain logic can be tested without frameworks
- **Flexibility**: Adapters can be replaced without changing business logic
- **Maintainability**: Clear separation of concerns
- **Independent evolution**: Infrastructure changes don't affect domain

## Common Anti-Patterns to Avoid
- ❌ Domain models with Spring annotations
- ❌ Use cases depending directly on concrete implementations
- ❌ Business logic in controllers or repositories
- ❌ Domain models depending on external libraries
- ❌ Violating dependency rule (inner layers depending on outer)

## When to Use This Skill
Invoke this skill when:
- Adding new features or modules
- Refactoring existing code
- Making architectural decisions
- Organizing new code into proper layers
- Reviewing code structure for architectural compliance
