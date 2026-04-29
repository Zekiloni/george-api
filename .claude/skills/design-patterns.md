---
name: design-patterns
description: Apply Gang of Four and enterprise design patterns appropriately
type: skill
---

# Design Patterns Skill

## Creational Patterns

### Factory Pattern
**Purpose**: Create objects without specifying exact class
**Use in George API**: Service provisioning strategies, entity creation

```java
// Example: Service provisioning factory
public interface ServiceProvisioningFactory {
    ServiceAccess createServiceAccess(OrderItem item);
}

@Component
public class SmtpProvisioningFactory implements ServiceProvisioningFactory {
    public ServiceAccess createServiceAccess(OrderItem item) {
        // Create SmtpServiceAccess with credentials
    }
}
```

### Builder Pattern
**Purpose**: Construct complex objects step by step
**Use in George API**: Complex DTO creation, domain model construction

```java
// Example: Page definition builder
public class PageDefinitionBuilder {
    public PageDefinitionBuilder addStep(PageStep step) { ... }
    public PageDefinitionBuilder withTitle(String title) { ... }
    public PageDefinition build() { ... }
}
```

## Structural Patterns

### Strategy Pattern
**Purpose**: Define interchangeable algorithms
**Use in George API**: Different service provisioning strategies, pricing calculations

```java
// Example: Service provisioning strategies
public interface ProvisioningStrategy {
    ServiceAccess provision(OrderItem item);
}

@Component
public class SmtpProvisioningStrategy implements ProvisioningStrategy {
    public ServiceAccess provision(OrderItem item) {
        // SMTP-specific provisioning logic
    }
}

@Component  
public class LeadProvisioningStrategy implements ProvisioningStrategy {
    public ServiceAccess provision(OrderItem item) {
        // Lead-specific provisioning logic
    }
}
```

### Adapter Pattern
**Purpose**: Convert interface of a class into another interface clients expect
**Use in George API**: Repository port adapters, external API integrations

```java
// Example: Repository port adapter
@Repository
public class OrderRepositoryPortAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository jpaRepository;
    
    // Adapts JPA repository to domain port interface
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### Decorator Pattern
**Purpose**: Add behavior to objects dynamically
**Use in George API**: Adding caching, logging, or validation to services

```java
// Example: Caching decorator for repository
public class CachedOrderRepository implements OrderRepositoryPort {
    private final OrderRepositoryPort delegate;
    private final Cache cache;
    
    public Order findById(String id) {
        return cache.get(id, () -> delegate.findById(id));
    }
}
```

### Composite Pattern
**Purpose**: Compose objects into tree structures
**Use in George API**: Page definitions, characteristic hierarchies

```java
// Example: Page definition composite
public interface PageComponent {
    void render();
}

public class PageStep implements PageComponent {
    private List<PageComponent> components = new ArrayList<>();
    
    public void addComponent(PageComponent component) {
        components.add(component);
    }
    
    public void render() {
        components.forEach(PageComponent::render);
    }
}
```

## Behavioral Patterns

### Observer Pattern
**Purpose**: Define one-to-many dependency so objects get notified automatically
**Use in George API**: Domain event handling, WebSocket updates

```java
// Example: Domain event publishing
@EventListener
public class OrderEventHandler {
    
    @Async
    public void handle(OrderCreatedEvent event) {
        // Notify other systems
    }
}
```

### Command Pattern
**Purpose**: Encapsulate requests as objects
**Use in George API**: Use case interfaces, operator actions

```java
// Example: Use case as command
public interface UserSessionCreateUseCase {
    UserSession createSession(UserSessionCreateCommand command);
}
```

### Chain of Responsibility
**Purpose**: Pass request along chain of handlers
**Use in George API**: Request validation, processing pipelines

```java
// Example: Validation chain
public interface ValidationHandler {
    ValidationResult validate(OrderItem item);
    void setNext(ValidationHandler next);
}

public class QuantityValidator implements ValidationHandler {
    private ValidationHandler next;
    
    public ValidationResult validate(OrderItem item) {
        if (item.getQuantity() <= 0) {
            return ValidationResult.invalid("Quantity must be positive");
        }
        return next != null ? next.validate(item) : ValidationResult.valid();
    }
}
```

### Template Method Pattern
**Purpose**: Define skeleton of algorithm, let subclasses override steps
**Use in George API**: Base entity behavior, common service logic

```java
// Example: Base service access lifecycle
public abstract class ServiceAccess {
    public void activate() {
        validateActivation();
        setStatus(ServiceStatus.ACTIVE);
        onActivated();
    }
    
    protected abstract void onActivated();
    protected abstract void validateActivation();
}
```

## Enterprise Patterns

### Repository Pattern
**Purpose**: Abstract data access, provide collection-like interface
**Use in George API**: All data access through repository ports

### Specification Pattern
**Purpose**: Encapsulate business rules as predicates
**Use in George API**: Complex queries, business rule validation

```java
// Example: Lead specifications
public interface LeadSpecification {
    boolean isSatisfiedBy(Lead lead);
}

public class ActiveLeadsInRegionSpec implements LeadSpecification {
    private String region;
    
    public boolean isSatisfiedBy(Lead lead) {
        return lead.getRegion().equals(region) && lead.isActive();
    }
}
```

### Unit of Work Pattern
**Purpose**: Manage transactions, track changes
**Use in George API**: Spring's `@Transactional` on use cases

### Dependency Injection Pattern
**Purpose**: Inject dependencies rather than creating them
**Use in George API**: Constructor injection throughout

## Pattern Selection Guidelines

### When to Use Patterns
✅ Use patterns when:
- The problem matches the pattern's intent exactly
- The pattern improves code clarity and maintainability
- The pattern doesn't add unnecessary complexity
- The pattern is already established in the codebase

❌ Avoid patterns when:
- A simpler solution exists
- The pattern adds complexity without benefit
- You're using patterns just to "use patterns"
- The pattern doesn't fit the specific problem

### George API Pattern Usage

#### Established Patterns
- **Repository**: All data access via repository ports
- **Strategy**: Service provisioning, pricing calculations  
- **Factory**: Entity creation, domain object construction
- **Adapter**: JPA to domain mapping, external API integration
- **Observer**: Domain event handling

#### Consider Adding
- **Specification**: For complex business rules and queries
- **Decorator**: For cross-cutting concerns (caching, logging)
- **Template Method**: For common workflow logic in services
- **Command**: For operator actions and use cases

## When to Use This Skill
Invoke this skill when:
- Designing new features or components
- Refactoring existing code for better structure
- Solving recurring design problems
- Making architectural decisions about patterns
- Reviewing code for appropriate pattern usage
