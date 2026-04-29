---
name: domain-bounded-context
description: Apply domain bounded context principles from Domain-Driven Design
type: skill
---

# Domain Bounded Context Skill

## Bounded Context Principles

### What is a Bounded Context?
A bounded context is a distinct part of the domain logic that acts as a boundary around a cohesive domain model. Each context has its own ubiquitous language and should be developed independently.

### George API Bounded Contexts

#### Platform Context
**Responsibility**: Lead management, page building, real-time session tracking
- **Entities**: Lead, Page, TrackingLink, TrackingEvent, UserSession
- **Value Objects**: PhoneNumber, TrackingCode, PageDefinition
- **Ubiquitous Language**: "leads", "pages", "tracking", "sessions", "conversions"
- **Location**: `platform/` module

#### Commerce Context
**Responsibility**: Service offerings, orders, billing, provisioning
- **Entities**: Offering, Order, Invoice, ServiceAccess, SmtpServiceAccess
- **Value Objects**: Money, Quantity, TimePeriod, Characteristic
- **Ubiquitous Language**: "offerings", "orders", "subscriptions", "provisioning", "billing"
- **Location**: `commerce/` module

#### Common Context
**Responsibility**: Shared domain concepts and infrastructure
- **Value Objects**: Money, Quantity, TimePeriod, Characteristic, Ref
- **Ubiquitous Language**: Generic business concepts
- **Location**: `common/` module

#### Attachment Context
**Responsibility**: File upload/download handling
- **Entities**: Attachment
- **Ubiquitous Language**: "attachments", "files", "upload", "download"
- **Location**: `attachment/` module

### Bounded Context Rules

#### 1. Language Boundaries
Each context has its own ubiquitous language - terms have specific meaning within that context:
- "Service" in Commerce = provisioned capability
- "Service" in Platform = might mean web service or background job
- Use context-specific terms to avoid ambiguity

#### 2. Physical Boundaries
Each context is physically separated in code structure:
```
src/main/java/com/zekiloni/george/
├── platform/     # Platform bounded context
├── commerce/     # Commerce bounded context  
├── attachment/   # Attachment bounded context
└── common/       # Shared kernel (not a bounded context)
```

#### 3. Dependency Rules
- Contexts should be loosely coupled
- Prefer well-defined interfaces over direct dependencies
- Use domain events or integration layers for cross-context communication
- Avoid sharing domain models between contexts (create DTOs or value objects)

#### 4. Data Model Boundaries
- Each context owns its database schema (separate tables)
- Foreign keys across contexts should be minimized
- Use aggregate IDs (UUID references) for cross-context relationships
- Different contexts can have different models of similar concepts

### Cross-Context Communication

#### Context Mapping Patterns

1. **Shared Kernel** (Common module)
   - Shared value objects used by multiple contexts
   - Used by: Money, Quantity, TimePeriod in this project
   - Keep minimal - only truly shared concepts

2. **Conformist**
   - One context conforms to another's model
   - Use when downstream has no autonomy over upstream

3. **Anti-Corruption Layer**
   - Translate between different bounded context models
   - Prevents one context's model from "infecting" another
   - Use adapters/mappers for translation

4. **Domain Events**
   - Publish events for cross-context notifications
   - Example: OrderCompletedEvent triggers provisioning in Platform

### Identifying Bounded Contexts

When analyzing requirements, identify bounded contexts by looking for:
- Different ubiquitous languages
- Separate business capabilities
- Different user roles or concerns
- Natural boundaries for team ownership
- Independent lifecycle and deployment needs

### Adding New Bounded Contexts

When creating a new bounded context:
1. Define its ubiquitous language
2. Identify core domain entities and value objects
3. Define boundaries and responsibilities
4. Create physical package structure
5. Define interfaces for cross-context communication
6. Minimize dependencies on other contexts
7. Give it its own persistence model if needed

### Bounded Context Benefits
- **Team Autonomy**: Different teams can work on different contexts
- **Technological Freedom**: Different tech stacks within different contexts
- **Clear Focus**: Each context has a well-defined responsibility
- **Independent Evolution**: Changes in one context don't ripple to others
- **Strategic Design**: Aligns software with business organization

## When to Use This Skill
Invoke this skill when:
- Designing new features or modules
- Refactoring existing code structure
- Making decisions about code organization
- Identifying cross-context dependencies
- Creating integration between modules
- Analyzing requirements for new functionality
