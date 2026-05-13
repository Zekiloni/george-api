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
# Flyway is enabled (flyway.enabled=true) with hibernate.ddl-auto=validate.
# Migrations live in src/main/resources/db/migration/ — currently V1__init.sql,
# V2__add_user_session_key.sql, V3__add_categories_and_templates.sql.
#
# On a fresh dev DB the migrations run on first boot and Hibernate validates
# entity mappings against the resulting schema. On an existing dev DB that
# was previously created by ddl-auto, baseline-on-migrate=true lets Flyway
# adopt it without rerunning V1.
#
# To add schema changes:
# 1. Create a new V{N}__short_description.sql in db/migration/
# 2. Add/modify the corresponding @Entity fields
# 3. Restart — Flyway runs the new file, Hibernate validate confirms alignment
#
# If Hibernate validate fails on boot, the dev DB is out of sync with V1's
# original definitions (V1 is known to be older than current entities).
# Either reset the dev DB or write a V{N}__align_*.sql to reconcile.
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
- **Migrations**: Flyway 12.4.0 (enabled; Hibernate ddl-auto=validate)
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

**Flyway**: Enabled (`flyway.enabled=true`, `baseline-on-migrate=true`); Hibernate runs as `ddl-auto=validate`. Migrations are V1__init.sql, V2__add_user_session_key.sql, V3__add_categories_and_templates.sql.

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

Flyway-managed, validate mode. Migration history:

- **V1__init.sql** — initial schema dump; known to be older than current entities (e.g. missing `tenants` table, stale `campaigns.flow_page_ids` JSONB column). Reconcile drift with new `V{N}__align_*.sql` files rather than editing V1.
- **V2__add_user_session_key.sql** — adds `user_sessions.session_key` for E2E payload encryption.
- **V3__add_categories_and_templates.sql** — `category` (admin-curated tree), `template` (CHECK constraint enforces categoryId XOR tenantId), `campaigns.source_template_id/version`.

Future changes: drop new migrations under `db/migration/V{N}__*.sql`. Hibernate will fail boot if entities and schema don't align — treat that as the signal to write the reconciling migration.

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

## Key Features Already Shipped

These are non-obvious from skimming code — drop here so future sessions don't re-design them.

### Visitor session lifecycle
- Visitor enters via `/p/{token}` (token in URL, opaque short string).
- `UserSessionCreateService` looks up the outreach cross-tenant, generates a fresh **wsToken** + **sessionKey** (AES-256-GCM, base64), explicitly stamps `tenantId` on the new `UserSession` entity (the `@TenantId` resolver falls back to SYSTEM root when there's no JWT — explicit tenant on the builder avoids the silent SYSTEM-stamp bug).
- Session resumption: fingerprint = SHA-256(token + UA + IP). Matching fingerprint reuses the existing session and rotates the wsToken.
- `UserSessionLifecycleService` wraps every public method (`markCompleted`, `markAbandoned`, `persistEvents`) in `withTenantOf(sessionId)` so scheduled-sweep DB writes happen under the correct tenant context.

### WebSocket transport hardening
- Both `wsToken` (visitor) and operator JWT travel in the `Sec-WebSocket-Protocol` header (`visitor.<token>` / `bearer.<jwt>`), not URL query — handled by `VisitorTokenHandshakeInterceptor` and `JwtHandshakeInterceptor`. Legacy URL form still accepted with a deprecation warning.
- E2E payload encryption: `UserSession.sessionKey` is generated server-side, returned to the visitor in the bootstrap response and to the operator via `GET /api/v1/operator/sessions/{id}/key`. Both ends AES-GCM-encrypt the `payload` field of every `UserEvent`. Server stores and forwards the envelope `{$enc:true, iv, ct}` opaquely — never decrypts.
- Heartbeats stay plaintext (no payload to encrypt).

### Template + category library
- `template` table CHECK constraint: `(category_id IS NOT NULL) XOR (tenant_id IS NOT NULL)`. Public catalog templates have a category, tenant-owned templates don't.
- `category` is admin-curated, system-wide, no tenant column. Mutation use cases are guarded by `@PreAuthorize("hasRole('ADMIN')")`.
- `CreateCampaignFromTemplateService` clones template steps into tenant-owned Pages (after `{{var}}` substitution), assembles a Campaign with the new Refs, increments template `usage_count`, and stamps `Campaign.sourceTemplateId/Version` so a future sync flow can detect when the source template's been updated.

## Deferred / Designed but Not Built

Things we've discussed in detail but haven't shipped. Don't re-design these from scratch — read here first.

### Custom domains via Cloudflare for SaaS + Tunnel
- Customer adds one CNAME `links.acme.com → cname.your-platform.com`.
- Our zone in Cloudflare uses Custom Hostnames API to register each customer hostname; CF auto-issues a TLS cert via HTTP DCV.
- Cloudflare Tunnel (`cloudflared` Deployment in k3s) means the origin has zero public ports.
- Backend needs: `tenant_domain` table, `CustomDomainFilter` (Host header → tenant lookup), `CloudflareCustomDomainAdapter` (`POST /zones/{zone}/custom_hostnames`), webhook receiver for SSL status transitions.
- The URL builder in `CampaignCreateService.buildMessage` switches to `tenantDomainRepo.findPrimaryByTenant(tenantId)` for the link host.

### Template platform — UI/feature follow-ups
- Tenant-side "Save as template" UI (currently you can only browse + use; backend `POST /templates` accepts it).
- "Version available" banner on campaign detail when `template.version > campaign.sourceTemplateVersion`. Diff view to follow.
- Step-level validation rules, conditional branching, skip logic (add `condition`/`validation` to `TemplateStep`).
- Step completion / drop-off analytics — `UserSession.currentStep` is tracked; missing piece is the aggregation query.
- Drag-and-drop template editor (JSON shape is editor-ready).

### Schema work
- V1 vs current entities drift — Hibernate validate on a fresh DB may fail; write `V{N}__align_*.sql` to reconcile.
- Backfill SQL for legacy `user_sessions.tenant_id = 'system'` rows:
  ```sql
  UPDATE user_sessions s SET tenant_id = o.tenant_id
  FROM outreach o
  WHERE s.outreach_id = o.id AND s.tenant_id = 'system' AND o.tenant_id <> 'system';
  ```

### Refactors / cleanups outstanding
- Most MapStruct mappers (20 of 27) missing `@Mapper(componentModel = SPRING)` — works by accident, should be made explicit.
- `CampaignDto.java` is filed under `infrastructure/in/web/mapper/` instead of `dto/campaign/` — rename.
- 3 campaign services without UseCase ports: `CampaignDispatchService`, `CampaignStatusTransitionService`, `OutreachDeliveryEventService`.

## Documentation

Additional documentation is available in the `docs/` directory:
- `docs/setup.md` - Development environment setup
- `docs/usecases.md` - Complete use case definitions
- `docs/module/platform.md` - Platform domain documentation
- `docs/module/commerce.md` - Commerce domain documentation
- `docs/module/common.md` - Common module documentation
- `docs/ARCHITECTURE_ASSESSMENT.md` - Architecture flexibility analysis


## Parallelization

Always use parallel subagents when working on multiple tasks.
Spawn one subagent per task and execute them simultaneously.
Never work on tasks sequentially if they can be parallelized.