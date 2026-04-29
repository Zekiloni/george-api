# George API Claude Skills

This directory contains specialized skills for working with the George API project. Each skill encapsulates specific expertise that can be invoked during development.

## Available Skills

### 1. **java-principles**
Best Java principles and modern Java practices (Java 25)
- SOLID principles
- Modern Java features (records, pattern matching, sealed classes)
- Spring Boot best practices
- Code quality checks

**When to use**: Writing/refactoring Java code, code reviews, architectural decisions

### 2. **hexagonal-architecture**
Hexagonal architecture (ports and adapters) principles
- Layer separation (Domain, Application, Infrastructure)
- Port definitions (input/output)
- Adapter implementations
- Dependency rules and boundaries

**When to use**: Adding features, refactoring, architectural decisions, code organization

### 3. **domain-bounded-context**
Domain bounded context principles from DDD
- Bounded context identification
- Ubiquitous language
- Context mapping patterns
- Cross-context communication

**When to use**: Designing new modules, refactoring code structure, cross-context integration

### 4. **domain-driven-design**
Domain-Driven Design strategic and tactical patterns
- Aggregates, entities, value objects
- Domain events and repositories
- Tactical patterns application
- Aggregate design guidelines

**When to use**: Designing domain models, refactoring domain logic, planning persistence

### 5. **design-patterns**
Gang of Four and enterprise design patterns
- Creational, structural, behavioral patterns
- Enterprise patterns (repository, specification, etc.)
- Pattern selection guidelines
- Project-specific pattern usage

**When to use**: Designing new features, solving design problems, architectural decisions

### 6. **clean-code**
Clean code principles for maintainable code
- Meaningful names
- Function design (small, single purpose)
- Error handling
- Object-oriented design
- Testing guidelines

**When to use**: Writing new code, refactoring, code reviews, improving maintainability

### 7. **lombok-mapstruct**
Lombok and MapStruct best practices for clean code
- Lombok annotation usage (@Data, @Value, @Builder, etc.)
- MapStruct mapper design patterns
- Clean mapper organization and testing
- Lombok + MapStruct integration
- Project-specific patterns and anti-patterns

**When to use**: Creating mappers/DTOs, refactoring mapper logic, domain model design, code reviews

## How to Use Skills

### Using Skills in Claude Code
When working with Claude Code, you can invoke these skills by mentioning them in your requests:

- "Apply java-principles to review this code"
- "Use hexagonal-architecture to organize this new feature"
- "Help me design this using domain-driven-design"
- "Refactor this following clean-code principles"

### Skill Combinations
Skills work well together:
- `hexagonal-architecture` + `domain-driven-design` for feature design
- `java-principles` + `clean-code` for code quality
- `design-patterns` + `hexagonal-architecture` for architectural decisions
- `domain-bounded-context` + `domain-driven-design` for domain modeling
- `lombok-mapstruct` + `clean-code` for clean DTOs and mappers
- `lombok-mapstruct` + `java-principles` for modern Java best practices

### Creating New Skills
To create a new skill:
1. Create a new `.md` file in this directory
2. Add frontmatter with name, description, and type
3. Include skill content with when-to-use guidance
4. Reference existing skills for consistency

## Project Context

These skills are tailored to the George API project:
- Spring Boot 4.0.5 with Java 25
- Hexagonal architecture with ports and adapters
- Domain-Driven Design with bounded contexts
- Multi-tenant SaaS platform

Each skill includes project-specific examples and guidelines that align with the existing codebase architecture.

## Skill Maintenance

Keep skills updated as the project evolves:
- Add new patterns as they emerge in the codebase
- Update examples to match current code structure
- Refine guidance based on team learnings
- Ensure skills remain relevant to project needs
