# Common Module

## Overview
Provides shared domain models, value objects, and infrastructure configuration used across all modules. Implements core business concepts that are application-wide.

## Domain Models (`domain/model/`)

### Value Objects

#### `Money`
- **Purpose**: Represents monetary values with currency support
- **Usage**: Commerce domain (pricing, payments, orders)

#### `Quantity`
- **Purpose**: Represents item quantities
- **Usage**: Inventory and order management

#### `Characteristic`
- **Purpose**: Domain-specific attributes or properties
- **Usage**: Product and entity customization

#### `TimePeriod`
- **Purpose**: Represents a time range with start and end dates
- **Usage**: Event scheduling, reservations, subscriptions

#### `Ref`
- **Purpose**: Reference to another resource with metadata
- **Usage**: Linking between domains, API responses

### Events

#### `DomainEvent` (Interface)
- **Purpose**: Marker interface for all domain events
- **Role**: Enables event-driven architecture and event sourcing
- **Implementations**: Specific events from domain modules

## Infrastructure (`infrastructure/`)

### Configuration (`config/`)

#### `JacksonConfig`
- **Purpose**: JSON serialization/deserialization configuration
- **Usage**: API request/response handling

#### `SecurityConfig`
- **Purpose**: Spring Security setup
- **Features**: Authentication, authorization, CORS

#### `WebConfig`
- **Purpose**: Web layer configuration
- **Features**: Interceptors, formatters, validators

#### `tenant/`
- **Purpose**: Multi-tenancy support
- **Scope**: Tenant context management and isolation

### Adapters

#### `in/` (Inbound Adapters)
- REST controllers and request handlers
- Entry points to the application

#### `out/` (Outbound Adapters)
- Database repositories
- External service clients

## Key Principles

- **Lightweight**: No business logic, only domain concepts
- **Reusable**: Used by all domain modules
- **Framework-agnostic**: Core logic independent of Spring
- **Immutable Value Objects**: Prevents accidental mutations
- **Validation**: Currency and data validation at creation

## Dependencies
- **No module dependencies**: Common module is independent
- **Used by**: All other domain modules (Platform, Commerce, Attachment)

## Example Usage

```java
// Creating Money
Money price = Money.of("EUR", new BigDecimal("99.99"));

// Creating Reference
Ref productRef = Ref.builder()
    .id("prod-123")
    .name("Product Name")
    .href("/api/products/prod-123")
    .build();

// Domain Events
class OrderCreatedEvent implements DomainEvent { }
```
