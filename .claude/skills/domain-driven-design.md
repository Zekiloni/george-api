---
name: domain-driven-design
description: Apply Domain-Driven Design strategic and tactical patterns
type: skill
---

# Domain-Driven Design Skill

## DDD Strategic Patterns

### Domains, Subdomains, and Bounded Contexts

#### Core Domain
The business's differentiator - why customers choose you:
- **George API Core**: Interactive page builder + real-time session tracking + commerce integration
- Invest most effort here, customizing to business needs

#### Supporting Subdomains
Essential but not differentiating:
- **User Management** (handled by Keycloak)
- **Payment Processing** (BTCPay integration)
- Use off-the-shelf solutions where possible

#### Generic Subdomains
Commodity functionality:
- **File Storage** (attachments)
- Use standard solutions, minimize custom code

### Ubiquitous Language
A shared, rigorous language between developers and domain experts:
- Use business terminology in code (not technical jargon)
- Code should read like business requirements
- Example: "Lead" not "ContactRecord", "Offering" not "Product"

### Context Mapping
Document relationships between bounded contexts:
- **Upstream/Downstream**: Which context serves which?
- **Integration Pattern**: Shared kernel, ACL, conformist?
- **Organizational Alignment**: Teams and boundaries

## DDD Tactical Patterns

### Aggregates
Clusters of domain objects treated as a unit:

#### Aggregate Rules
1. **One Aggregate Root**: Entry point for access
2. **Consistency Boundary**: Transactions don't span aggregates
3. **Reference by ID**: Other aggregates reference root by ID, not object
4. **Protect Invariants**: Enforce business rules within aggregate

#### George API Aggregates
```
Order (Aggregate Root)
├── OrderItem (Entity)
└── status, totalPrice (invariants)

Page (Aggregate Root)
├── definition (Value Object)
├── trackingLinks (Entity collection)
└── status, slug (invariants)
```

### Entities
Objects with identity and lifecycle:
- Identified by ID, not attributes
- Mutable state over time
- Example: Order, Page, Lead, ServiceAccess

### Value Objects
Immutable objects defined by their attributes:
- No identity, equality based on all attributes
- Immutable, replace instead of modify
- Example: Money, Quantity, TimePeriod, PhoneNumber

```java
// Value Object Example
public final class Money {
    private final BigDecimal amount;
    private final String currency;
    
    // Immutable, no setters, equality based on all fields
}
```

### Domain Events
Something that happened in the domain that domain experts care about:
- Capture business-relevant events
- Enable eventual consistency and async processing
- Example: OrderCreated, PaymentCompleted, ServiceProvisioned

### Repositories
Collections-like interfaces for accessing aggregates:
- Persist and retrieve aggregates by ID
- Encapsulate data access logic
- Only accessible through aggregate root

### Factories
Complex object creation logic:
- Use when aggregate construction is complex
- Can be separate methods or domain services

### Domain Services
Operations that don't naturally fit in an entity or value object:
- Stateless operations
- Cross-aggregate operations (within same bounded context)
- Example: "Calculate pricing for offering based on duration"

## Applying DDD to George API

### Tactical Patterns in Use

#### Entities
- `Order`, `Invoice`, `Page`, `Lead`, `ServiceAccess`
- Mutable, have identity, lifecycle matters

#### Value Objects
- `Money`, `Quantity`, `TimePeriod`, `Characteristic`, `Ref`
- Immutable, defined by attributes, replace instead of modify

#### Aggregates
- **Order Aggregate**: Order (root) + OrderItems
- **Page Aggregate**: Page (root) + TrackingLinks
- **ServiceAccess Aggregate**: ServiceAccess (root) with characteristics

#### Domain Events
- `OrderCreatedEvent`, `InvoiceCreatedEvent`, `PaymentStatusChanged`
- Used for cross-context communication

### Repository Pattern Implementation
```java
// Port in application/port/out/
public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
}

// Implementation in infrastructure/out/persistence/adapter/
@Repository
public class OrderRepositoryPortAdapter implements OrderRepositoryPort {
    // JPA-specific implementation
}
```

### Aggregate Design Guidelines

#### Order Aggregate
- **Root**: Order
- **Entities**: OrderItem
- **Invariants**: Total price consistency, status transitions
- **Repository**: OrderRepositoryPort
- **Transaction Boundary**: Order + all items atomically

#### ServiceAccess Aggregate
- **Root**: ServiceAccess (abstract)
- **Subclasses**: SmtpServiceAccess, LeadServiceAccess, PageServiceAccess
- **Invariants**: Valid date ranges, status consistency
- **Repository**: ServiceAccessRepositoryPort

## DDD Implementation Checklist

### When Adding New Domain Features
1. Identify entities and value objects
2. Design aggregates with clear roots
3. Define repositories for aggregate persistence
4. Create domain services if needed
5. Use domain events for cross-aggregate communication
6. Ensure invariants are enforced within aggregate
7. Keep aggregate boundaries small and focused

### Red Flags to Avoid
- ❌ Anemic domain models (entities with no behavior)
- ❌ God aggregates (too many entities in one aggregate)
- ❌ Global scope (everything accessing everything)
- ❌ Treating entities as active records (mixing persistence logic)
- ❌ Violating aggregate boundaries (transactions across aggregates)

## When to Use This Skill
Invoke this skill when:
- Designing new domain models
- Refactoring existing domain logic
- Making decisions about aggregate boundaries
- Identifying opportunities for domain events
- Reviewing domain model code quality
- Planning persistence and transaction boundaries
