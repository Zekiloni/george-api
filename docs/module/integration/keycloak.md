# Keycloak Integration

## Overview

Keycloak is an open-source identity and access management (IAM) platform integrated into the George platform to provide authentication, authorization, multi-tenant user management, and role-based access control (RBAC). Keycloak acts as the centralized authentication provider, handling user identity validation, session management, and token-based authorization across all platform services.

The integration enables:
- **Single Sign-On (SSO)**: Users authenticate once and gain access to all platform services
- **Multi-Tenant Isolation**: Separate realms per tenant, preventing cross-tenant data access
- **Role-Based Access Control**: Administrator, Client, Viewer roles with fine-grained permissions
- **OAuth 2.0 / OpenID Connect**: Standard-based authentication and delegation
- **API Token Management**: Service-to-service authentication and API client access

## Architecture

### Integration Points

The Keycloak integration spans multiple layers:

1. **Authentication Layer**: User login, SSO, token validation
2. **Authorization Layer**: Role-based access control, permission checks
3. **API Gateway**: Token validation on every API request
4. **Multi-Tenancy**: Keycloak realms correspond to system tenants
5. **External Integrations**: BTCPay, payment webhooks receive service tokens

### Authentication Flow

```
User Login Flow:
  1. User navigates to platform login page
  2. Platform redirects to Keycloak login endpoint
  3. User enters credentials (email, password)
  4. Keycloak validates credentials against user database
  5. If valid, Keycloak redirects back to platform with:
     - Authorization code (single-use)
     - State parameter (CSRF protection)
  6. Platform exchanges authorization code for tokens:
     - Access token (for API calls)
     - Refresh token (for obtaining new access token)
     - ID token (user identity information)
  7. Platform stores tokens (securely, browser cookies with HttpOnly flag)
  8. User is now authenticated, dashboard displayed
  9. All API calls include Authorization: Bearer {accessToken} header
  10. API gateway validates token with Keycloak before processing request
```

### Service-to-Service Authentication

For backend services and webhooks:

```
Service Authentication Flow:
  1. Backend service needs to call another service (e.g., Platform calls Commerce)
  2. Service requests service account token from Keycloak
  3. Keycloak issues token with service account credentials
  4. Service includes token in Authorization header for inter-service call
  5. Receiving service validates token
```

## Configuration

### Keycloak Server Setup

Keycloak instance must be deployed and accessible. Core configuration:

```yaml
keycloak:
  server-url: "https://keycloak.example.com"
  realm: "george"  # Primary realm for platform
  client-id: "george-web"  # OAuth client for web application
  client-secret: "client-secret-from-keycloak"
  client-credentials-grant: true
  token-expiration: 15m  # Access token expiration
  refresh-token-expiration: 7d
  
  # For WebSocket and real-time sessions (page tracking)
  websocket-client-id: "george-websocket"
  websocket-client-secret: "websocket-secret"
```

### Realm Structure

Each tenant gets a dedicated Keycloak realm:

```
Keycloak:
├── Realm: "george" (master realm, administrators)
│   ├── Users: [admin1, admin2, support_team]
│   ├── Roles: [ADMINISTRATOR, SUPER_ADMIN, SUPPORT]
│   └── Clients:
│       ├── george-web (web UI)
│       ├── george-api (API backend)
│       └── external-integrations (webhooks, BTCPay callbacks)
│
├── Realm: "tenant-001" (Client A)
│   ├── Users: [client1, client2, operator1]
│   ├── Roles: [CLIENT_ADMIN, OPERATOR, VIEWER]
│   └── Clients: [george-web, george-api]
│
└── Realm: "tenant-002" (Client B)
    └── (Similar structure)
```

Each realm is isolated: Users in tenant-001 realm cannot access tenant-002 realm.

### User Types and Roles

**System Roles** (in "george" master realm):
- `SUPER_ADMIN`: Full platform access, can manage all tenants
- `ADMINISTRATOR`: Manage offerings, ingest leads, monitor health
- `SUPPORT`: Read-only access, can view tenant information for support

**Tenant Roles** (in tenant-specific realm):
- `CLIENT_ADMIN`: Tenant administrator, can configure services, manage team members
- `OPERATOR`: Tenant user, can purchase services, create pages, view analytics
- `VIEWER`: Read-only access to tenant data, cannot make changes

## OAuth 2.0 and OpenID Connect

### Grant Types

**Authorization Code Grant** (User login):
```
GET /auth/realms/george/protocol/openid-connect/auth?
  client_id=george-web
  &redirect_uri=https://platform.example.com/callback
  &response_type=code
  &scope=openid%20profile%20email
  &state=random-state-value

User authenticates, Keycloak redirects:
  https://platform.example.com/callback?
    code=authorization-code
    &state=random-state-value

Platform exchanges code for tokens:
POST /auth/realms/george/protocol/openid-connect/token
  client_id=george-web
  &client_secret=client-secret
  &grant_type=authorization_code
  &code=authorization-code
  &redirect_uri=https://platform.example.com/callback
```

**Client Credentials Grant** (Service-to-service):
```
POST /auth/realms/george/protocol/openid-connect/token
  client_id=george-api
  &client_secret=api-secret
  &grant_type=client_credentials
  &scope=api

Returns:
{
  "access_token": "jwt-token",
  "token_type": "Bearer",
  "expires_in": 900
}
```

**Refresh Token Grant** (Obtaining new access token):
```
POST /auth/realms/george/protocol/openid-connect/token
  client_id=george-web
  &client_secret=client-secret
  &grant_type=refresh_token
  &refresh_token=refresh-token-value

Returns new access_token with same scope and user context
```

### Token Structure

**Access Token** (JWT format):

```json
{
  "jti": "token-id",
  "exp": 1672531800,
  "iat": 1672531500,
  "auth_time": 1672531200,
  "username": "john@example.com",
  "email": "john@example.com",
  "email_verified": true,
  "name": "John Doe",
  "preferred_username": "john@example.com",
  "realm_access": {
    "roles": ["default-roles-george", "OPERATOR"]
  },
  "resource_access": {
    "george-web": {
      "roles": ["manage-account", "view-profile"]
    },
    "george-api": {
      "roles": ["api:write", "api:read"]
    }
  },
  "scope": "openid profile email",
  "client_id": "george-web",
  "azp": "george-web",
  "iss": "https://keycloak.example.com/auth/realms/george",
  "sub": "user-uuid"
}
```

**ID Token** (contains identity claims):
```json
{
  "sub": "user-uuid",
  "email": "john@example.com",
  "email_verified": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "preferred_username": "john@example.com"
}
```

## Implementation Details

### Spring Security Integration

Platform uses Spring Security with OAuth 2.0 client configuration:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: george-web
            client-secret: ${KEYCLOAK_CLIENT_SECRET}
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: openid,profile,email
        provider:
          keycloak:
            issuer-uri: https://keycloak.example.com/auth/realms/george
            user-name-attribute: preferred_username
```

### JWT Token Validation

On every API request, the API gateway (Spring Security OAuth 2.0 Resource Server) validates the JWT:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/admin/**").hasRole("ADMINISTRATOR")
                .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
                    .jwtAuthenticationConverter(jwtAuthenticationConverter());
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("preferred_username");
        converter.setAuthoritiesClaimName("roles");
        return converter;
    }
}
```

### Tenant Isolation via JWT Claims

Custom claims in JWT identify the user's tenant:

```json
{
  "preferred_username": "john@example.com",
  "tenant_id": "tenant-001",
  "realm": "tenant-001",
  "roles": ["OPERATOR"],
  ...
}
```

Every request is validated to ensure:
1. User has valid JWT token signed by Keycloak
2. User's tenant_id in token matches the resource being accessed
3. User has required role (OPERATOR, ADMINISTRATOR, etc.)

```java
@GetMapping("/api/pages")
@PreAuthorize("hasRole('OPERATOR')")
public ResponseEntity<List<Page>> getPages(@RequestAttribute("tenant_id") String tenantId) {
    // Spring automatically extracts tenant_id from JWT claims
    // Query only pages for this tenant
    List<Page> pages = pageRepository.findByTenantId(tenantId);
    return ResponseEntity.ok(pages);
}
```

### Session Management

User sessions are managed through:
1. **Browser**: JWT stored in secure, HttpOnly cookie
2. **Backend**: Token validation on each request via Keycloak public key (fetched once and cached)
3. **Token Refresh**: When access token expires, refresh token is used to obtain new access token (transparent to user)

Session timeout: 15 minutes of inactivity, user redirected to re-authenticate.

## Multi-Tenancy

### Tenant Creation Workflow

When a new client signs up:

1. Administrator creates new Keycloak realm:
   ```
   POST /admin/realms
   {
     "realm": "tenant-001",
     "displayName": "Client A",
     "enabled": true,
     "passwordPolicy": "length(8) and forceExpiredPasswordChange(365)"
   }
   ```

2. Administrator creates client applications within realm:
   ```
   POST /admin/realms/tenant-001/clients
   {
     "clientId": "george-web",
     "name": "George Web UI",
     "publicClient": true,
     "standardFlowEnabled": true,
     "redirectUris": ["https://platform.example.com/*"]
   }
   ```

3. Administrator creates initial users (CLIENT_ADMIN):
   ```
   POST /admin/realms/tenant-001/users
   {
     "username": "admin@clienta.com",
     "email": "admin@clienta.com",
     "firstName": "Admin",
     "lastName": "ClientA",
     "enabled": true
   }
   ```

4. Assign roles to users:
   ```
   POST /admin/realms/tenant-001/users/{userId}/role-mappings/realm
   [
     {
       "id": "role-uuid",
       "name": "CLIENT_ADMIN"
     }
   ]
   ```

5. Platform creates tenant record in database:
   ```
   Tenant {
     id: "tenant-001",
     keycloakRealm: "tenant-001",
     name: "Client A",
     status: "ACTIVE"
   }
   ```

### Cross-Tenant Access Prevention

Keycloak's realm isolation ensures:
- Users in tenant-001 realm cannot access tenant-002 realm APIs
- API calls include tenant_id from JWT; backend code verifies tenant_id matches resource
- Admin APIs use separate Keycloak accounts with limited scopes

Example protection:
```java
@GetMapping("/api/pages/{pageId}")
public ResponseEntity<Page> getPage(
    @PathVariable String pageId,
    @RequestAttribute("tenant_id") String tenantId
) {
    Page page = pageRepository.findById(pageId)
        .filter(p -> p.getTenantId().equals(tenantId))  // Ensure tenant match
        .orElseThrow(ResourceNotFoundException::new);
    return ResponseEntity.ok(page);
}
```

## API Security

### API Endpoint Protection

Public endpoints (health, login):
```java
@GetMapping("/health")
public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
}
```

Authenticated endpoints require valid token:
```java
@GetMapping("/api/pages")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<Page>> getPages() { ... }
```

Role-based endpoints:
```java
@PostMapping("/api/offerings")
@PreAuthorize("hasRole('ADMINISTRATOR')")
public ResponseEntity<Offering> createOffering(...) { ... }

@PostMapping("/api/pages")
@PreAuthorize("hasAnyRole('OPERATOR', 'CLIENT_ADMIN')")
public ResponseEntity<Page> createPage(...) { ... }
```

### External Webhook Integration

BTCPay and other external services call platform webhooks. These webhooks cannot use user authentication (no user context). Instead:

1. **Service Account Token**: Platform creates service account in Keycloak with limited scopes
2. **Token in Webhook**: External service includes service token in webhook request
3. **Validation**: Platform validates service token on webhook endpoint

BTCPay webhook example:
```
POST /webhooks/btcpay
Authorization: Bearer {service-account-token}
X-BTCPay-Signature: {signature}

{
  "invoiceId": "...",
  "orderId": "...",
  "status": "Complete"
}
```

Webhook endpoint:
```java
@PostMapping("/webhooks/btcpay")
public ResponseEntity<Void> handleBTCPayWebhook(
    HttpServletRequest request
) {
    // Spring Security validates Bearer token (service account)
    // If token invalid, request is rejected before controller is reached
    
    // Controller is only reached if token is valid and has webhook scope
    return ResponseEntity.ok().build();
}
```

## User Management

### Creating Users Programmatically

Platform admin interface provides user management:

```java
@PostMapping("/admin/tenants/{tenantId}/users")
@PreAuthorize("hasRole('CLIENT_ADMIN')")
public ResponseEntity<User> inviteUser(
    @PathVariable String tenantId,
    @RequestBody UserInviteRequest request,
    @RequestAttribute("tenant_id") String userTenantId
) {
    // Verify requester is admin of requested tenant
    if (!tenantId.equals(userTenantId)) {
        throw new ForbiddenException();
    }
    
    // Create user in Keycloak realm
    KeycloakUser keycloakUser = keycloakService.createUser(
        tenantId,
        request.getEmail(),
        request.getFirstName(),
        request.getLastName()
    );
    
    // Set temporary password, send reset email
    keycloakService.setTemporaryPassword(keycloakUser.getId());
    emailService.sendPasswordResetEmail(request.getEmail(), keycloakUser.getId());
    
    return ResponseEntity.created(...).body(keycloakUser);
}
```

### Password Policies

Password policies enforced by Keycloak:
- Minimum 8 characters
- Require uppercase letter
- Require number
- Expire every 365 days (force change)
- Prevent reuse of last 5 passwords

### Multi-Factor Authentication (MFA)

Optional MFA configuration:

```yaml
keycloak:
  mfa:
    enabled: true
    required-for-roles:
      - ADMINISTRATOR
      - CLIENT_ADMIN
    methods:
      - totp  # Time-based one-time password (Google Authenticator, Authy)
      - webauthn  # FIDO2 security keys
```

When MFA is enabled for a role, users must register an MFA device during next login and provide code on subsequent logins.

## Monitoring and Auditing

### Keycloak Event Logging

Keycloak logs authentication and authorization events:

```json
{
  "eventId": "...",
  "type": "LOGIN",
  "userId": "user-uuid",
  "username": "john@example.com",
  "realmId": "tenant-001",
  "ipAddress": "192.168.1.1",
  "clientId": "george-web",
  "timestamp": 1672531200000,
  "details": {
    "auth_method": "openid-connect",
    "response_type": "code"
  }
}
```

Platform aggregates Keycloak logs for auditing:
- Failed login attempts (brute force detection)
- User role changes
- API access patterns
- Token issuance and refresh

### Token Introspection

Platform can introspect tokens to check validity:

```
POST /auth/realms/george/protocol/openid-connect/token/introspect
  client_id=george-api
  &client_secret=api-secret
  &token=access-token

Response:
{
  "active": true,
  "scope": "openid profile email",
  "client_id": "george-web",
  "username": "john@example.com",
  "token_type": "Bearer",
  "exp": 1672531800,
  "iat": 1672531500
}
```

This is useful for checking token validity without parsing JWT.

## Testing and Development

### Local Keycloak Setup

For development, use Docker:

```yaml
# docker-compose.yml
keycloak:
  image: quay.io/keycloak/keycloak:latest
  environment:
    KEYCLOAK_ADMIN: admin
    KEYCLOAK_ADMIN_PASSWORD: admin
  ports:
    - "8080:8080"
  volumes:
    - ./keycloak-realm.json:/opt/keycloak/data/import/realm.json
  command: start-dev --import-realm
```

Import realm definition with test users:

```json
{
  "realm": "george",
  "displayName": "George Test",
  "users": [
    {
      "username": "admin@example.com",
      "email": "admin@example.com",
      "firstName": "Admin",
      "lastName": "User",
      "credentials": [
        {
          "type": "password",
          "value": "admin123"
        }
      ],
      "realmRoles": ["ADMINISTRATOR"],
      "enabled": true
    },
    {
      "username": "operator@example.com",
      "email": "operator@example.com",
      "firstName": "Operator",
      "lastName": "User",
      "credentials": [
        {
          "type": "password",
          "value": "operator123"
        }
      ],
      "realmRoles": ["OPERATOR"],
      "enabled": true
    }
  ]
}
```

### Testing Authentication

Unit tests mock Keycloak:

```java
@SpringBootTest
public class AuthenticationTests {
    @MockBean
    private JwtDecoder jwtDecoder;
    
    @Test
    public void testApiWithValidToken() {
        String token = "valid-jwt-token";
        when(jwtDecoder.decode(token)).thenReturn(
            Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .claim("sub", "user-uuid")
                .claim("preferred_username", "john@example.com")
                .claim("realm_access", Map.of("roles", List.of("OPERATOR")))
                .build()
        );
        
        mockMvc.perform(
            get("/api/pages")
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }
    
    @Test
    public void testApiWithInvalidToken() {
        mockMvc.perform(
            get("/api/pages")
                .header("Authorization", "Bearer invalid-token")
        ).andExpect(status().isUnauthorized());
    }
}
```

## Troubleshooting

### Token Validation Failures

**Symptom**: "Invalid token" or "Unauthorized" errors on all API requests

**Causes**:
1. Keycloak server unreachable (check server-url configuration)
2. Public key cache stale (Keycloak key rotated, cache not refreshed)
3. Token expired (client should use refresh token)
4. Token issued by different realm (tenant_id mismatch)

**Resolution**:
- Verify Keycloak connectivity: `curl https://keycloak.example.com/health`
- Clear token cache and re-fetch public key
- Check token expiration: `jwt.io` to decode token and inspect exp claim
- Verify token issued for correct realm

### Redirect Loop During Login

**Symptom**: Browser repeatedly redirects between Keycloak and platform

**Causes**:
1. Redirect URI mismatch (Keycloak expects different URI)
2. Invalid OAuth client configuration
3. Cookie domain mismatch

**Resolution**:
- Verify redirect_uri matches configured value in Keycloak client
- Check CORS configuration between platform and Keycloak
- Ensure cookie domain includes both platform and Keycloak servers

### User Cannot Access Tenant Data

**Symptom**: User authenticated but receives "Forbidden" on API calls

**Causes**:
1. User not assigned required role
2. User's tenant_id claim doesn't match resource tenant_id
3. User created in wrong Keycloak realm

**Resolution**:
- Verify user role assignment in Keycloak admin console
- Check token claims (decode token at jwt.io)
- Verify user created in correct tenant realm

---

This Keycloak integration documentation provides comprehensive coverage of authentication, authorization, multi-tenancy, OAuth 2.0/OIDC, API security, and operational considerations. The architecture ensures secure, scalable identity management supporting both user authentication and service-to-service authorization.

