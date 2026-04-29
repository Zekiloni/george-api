---
name: java-principles
description: Apply best Java principles and modern Java practices to code
type: skill
---

# Java Best Principles Skill

## Core Java Principles to Apply

### SOLID Principles
- **Single Responsibility**: Each class/method should have one reason to change
- **Open/Closed**: Open for extension, closed for modification (use interfaces/abstractions)
- **Liskov Substitution**: Subtypes must be substitutable for base types
- **Interface Segregation**: Prefer small, focused interfaces over large ones
- **Dependency Inversion**: Depend on abstractions, not concretions

### Modern Java Practices (Java 25)
- Use **records** for immutable data carriers
- Leverage **pattern matching** for instanceof and switch
- Use **sealed classes** for restricted inheritance hierarchies
- Apply **var** for local variable type inference where types are obvious
- Utilize **virtual threads** for concurrent operations (already enabled in this project)

### Java-Specific Best Practices
- Favor **composition over inheritance**
- Use **immutable objects** for thread safety (domain models should be immutable)
- Apply **defensive copying** for mutable fields in constructors/getters
- Use **enums** for fixed sets of constants (ServiceSpecification, OrderStatus, etc.)
- Leverage **Stream API** for data transformations over imperative loops
- Use **Optional** for possibly-null return values from methods

### Spring Boot Specifics
- Constructor injection over field injection
- Use `@Transactional` only on service methods that modify data
- Apply `@Qualifier` when multiple beans of same type exist
- Use Spring's dependency injection extensively
- Leverage Spring's validation framework for input validation

### Code Quality Checks
When reviewing or writing Java code, ensure:
1. No code smells (long methods, large classes, primitive obsession)
2. Proper exception handling (specific exceptions, proper logging)
3. Resource management (try-with-resources for IO operations)
4. Thread safety considerations for concurrent operations
5. Performance considerations (avoid unnecessary object creation, proper collection sizing)

## When to Use This Skill
Invoke this skill when:
- Writing new Java classes or methods
- Refactoring existing code
- Conducting code reviews
- Making architectural decisions about Java features to use
- Optimizing Java code for performance or readability

## Project-Specific Context
For George API project:
- Domain models in `domain/model/` should be immutable
- Services should use constructor injection
- Virtual threads are enabled - leverage for concurrent operations
- Use Java 25 features where appropriate (records, pattern matching, sealed classes)
- Follow existing patterns like use case services and port interfaces
