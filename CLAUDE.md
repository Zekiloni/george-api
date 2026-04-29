# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

George is a multi-tenant SaaS platform built with Spring Boot 4.0.5 and Java 25. It provides lead management, interactive page builder, real-time visitor session tracking, and commerce capabilities for managing service offerings, orders, and subscriptions.

## Build and Development Commands

### Running the Application
```bash
# Run the application (requires PostgreSQL and Keycloak)
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=GeorgeApiApplicationTests

# Build the project
./mvnw clean install

# Package without tests
./mvnw clean package -DskipTests
```

### Database Operations
```bash
# Flyway migrations are currently disabled (flyway.enabled=false)
# The application uses Hibernate ddl-auto: create-drop for development
# Migrations are in src/main/resources/db/migration/
# When enabling Flyway:
# 1. Set flyway.enabled=true in application.yaml
# 2. Set spring.jpa.hibernate.ddl-auto=validate
# 3. Ensure V1__init_DDL.sql exists and matches current schema
```

## Architecture

### Hexagonal/Ports-and-Adapters Pattern

The codebase follows hexagonal architecture with clear separation between domain, application, and infrastructure layers:

```
src/main/java/com/zekiloni/george/
├── common/              # Shared domain models and infrastructure
├── platform/            # Lead management, page builder, session tracking
├── commerce/            # Catalog, orders, billing, provisioning
└── attachment/          # File attachment handling
```

Each domain module is structured as:
```
domain/
├── model/              # Domain models and value objects
application/
├── port/in/            # Input ports (use case interfaces)
├── port/out/           # Output ports (repository/integration interfaces)
└── usecase/            # Business logic services
infrastructure/
├── in/                 # Inbound adapters (REST controllers)
├── out/                # Outbound adapters (repositories, integrations)
└── config/             # Configuration beans
```

### Domain-Driven Design Bounded Contexts

- **Platform**: Lead management, page builder, real-time session tracking, operator console
- **Commerce**: Service offerings, pricing, orders, subscriptions, inventory provisioning
- **Common**: Shared value objects (Money, Quantity, TimePeriod), multi-tenancy, security
- **Attachment**: File upload/download functionality

### Multi-Tenancy

The platform uses discriminator-based multi-tenancy with tenant_id columns on all entities. Tenant context is resolved from JWT tokens and stored in ThreadLocal:

- `TenantContext` stores current tenant ID
- `TenantRequestFilter` extracts tenant from authentication
- `TenantIdentifierResolver` integrates with Hibernate
- All entities must include `tenantId` field

### Key Design Patterns

1. **Use Case Pattern**: Business operations are implemented as use case services (e.g., `LeadCreateService`, `PageUpdateService`)
2. **Port/Adapter Pattern**: Input/Output ports define contracts, infrastructure provides implementations
3. **Repository Pattern**: JPA repositories are wrapped in port adapters for domain access
4. **Event-Driven**: Domain events for invoice lifecycle, order processing
5. **Strategy Pattern**: Service provisioning uses strategy pattern for different service types

## Technology Stack

- **Framework**: Spring Boot 4.0.5, Spring Security OAuth2 Resource Server
- **Language**: Java 25 (with preview features enabled)
- **Database**: PostgreSQL with Hibernate JPA
- **Migrations**: Flyway 12.4.0 (currently disabled, using Hibernate ddl-auto)
- **Mapping**: MapStruct 1.6.3 for DTO mapping
- **Linting**: Lombok for boilerplate reduction
- **Security**: Keycloak for authentication and authorization
- **Payment**: BTCPay Server integration for Bitcoin payments
- **Email**: libphonenumber for phone validation and geocoding

## Configuration

### Application Profiles
- `application.yaml` - Default development configuration
- `application-prod.yaml` - Production settings

### Environment Variables
Key configuration via environment variables:
- `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` - PostgreSQL connection
- `SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` - Keycloak realm URL
- `BTC_PAY_URL`, `BTC_PAY_API_KEY`, `BTC_PAY_STORE_ID` - BTCPay configuration
- `SERVER_PORT` - Application port (default 8080)
- `CORS_ALLOWED_ORIGIN` - CORS allowed origins

### Important Configurations

**Virtual Threads**: Enabled via `spring.threads.virtual.enabled=true`

**Multi-Tenancy**: Configured as DISCRIMINATOR in Hibernate properties

**Flyway**: Currently disabled (`flyway.enabled=false`), using `ddl-auto: create-drop`

**JSONB**: PostgreSQL JSONB used for flexible attributes (characteristics, metadata)

## Conventions

### Naming Conventions
- Entities: `{Name}Entity` in `infrastructure/out/persistence/.../entity/`
- Domain Models: `{Name}` in `domain/model/`
- DTOs: `{Name}Dto` in `infrastructure/in/web/dto/`
- Repositories: `{Name}JpaRepository` in `infrastructure/out/persistence/.../repository/`
- Use Cases: `{Name}Service` in `application/usecase/`
- Controllers: `{Name}ApiController` in `infrastructure/in/web/`

### Code Organization
- Business logic goes in `application/usecase/`
- Domain models in `domain/model/` should be framework-agnostic
- Infrastructure concerns (persistence, REST) in `infrastructure/`
- Shared value objects in `common/domain/model/`

### Transaction Management
Service methods that modify data should be annotated with `@Transactional`.

### Testing
Tests are located in `src/test/java/`. Integration tests should use `@SpringBootTest` with test configuration.

## Integration Points

### Keycloak
- OAuth2 Resource Server with JWT validation
- Role-based access control via Spring Security
- JWT issuer-uri configured in application.yaml

### BTCPay Server
- Webhook-based payment event handling
- Invoice creation and status tracking
- Configured in `BtcPayApiClientConfig`

### Email Services
- SMTP credential provisioning via `SmtpServiceAccess`
- Integration with external email providers
- Phone number validation via libphonenumber

## Schema Evolution

Currently using `ddl-auto: create-drop` for development. To enable Flyway migrations:

1. Create initial migration `V1__init_DDL.sql` from current Hibernate schema
2. Set `spring.jpa.hibernate.ddl-auto=validate`
3. Set `flyway.enabled=true`
4. All future schema changes require new migration files (V2__, V3__, etc.)

## Common Patterns

### Adding New Use Cases
1. Create port interface in `application/port/in/`
2. Implement service in `application/usecase/`
3. Create controller in `infrastructure/in/web/`
4. Define DTOs in `infrastructure/in/web/dto/`
5. Add MapStruct mapper if needed

### Adding New Domain Models
1. Create domain model in `domain/model/`
2. Create entity in `infrastructure/out/persistence/entity/`
3. Create repository port in `application/port/out/`
4. Implement repository adapter in `infrastructure/out/persistence/adapter/`
5. Create JPA repository in `infrastructure/out/persistence/repository/`

### Adding New Service Types
1. Add enum value to `ServiceSpecification`
2. Create domain model extending `ServiceAccess`
3. Create entity extending `ServiceAccessEntity`
4. Implement provisioning strategy for the new service type
5. Update order processing to handle new type

## Documentation

Additional documentation is available in the `docs/` directory:
- `docs/setup.md` - Development environment setup
- `docs/usecases.md` - Complete use case definitions
- `docs/module/platform.md` - Platform domain documentation
- `docs/module/commerce.md` - Commerce domain documentation
- `docs/module/common.md` - Common module documentation
- `docs/ARCHITECTURE_ASSESSMENT.md` - Architecture flexibility analysis
