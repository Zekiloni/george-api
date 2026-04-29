# Frontend Integration Guide

## Backend API Overview

This is a **Spring Boot 4.0.5** backend with **Java 25** providing REST APIs for a multi-tenant SaaS platform.

### Base Configuration
- **Default API Base Path**: `/api/v1`
- **Authentication**: Keycloak OAuth2 Resource Server (JWT)
- **Content-Type**: `application/json`
- **Tenant Context**: Automatically extracted from JWT token

## Authentication & Authorization

### How Authentication Works
1. Frontend gets JWT token from Keycloak
2. Include token in requests: `Authorization: Bearer <token>`
3. Backend validates token and extracts tenant context automatically

### Role-Based Access Control
- **ADMIN** - Full access to management operations
- **OPERATOR** - Can manage campaigns and leads
- **USER** - Read-only access to own data

### Frontend Auth Implementation
```typescript
// Example: Axios interceptor for JWT
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## Gateway Management API

### Gateway Types
- **SMTP** - Email gateway (Stalwart, other MTAs)
- **GSM** - SMS gateway (EJOIN, other providers)

### CRUD Operations

#### Create Gateway (Admin Only)
```http
POST /api/v1/gateway
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "type": "SMTP",
  "name": "Stalwart Mail Server",
  "description": "Primary SMTP gateway",
  "priority": 1,
  "maxConnections": 100,
  "smtpConfig": {
    "provider": "STALWART",
    "host": "smtp.example.com",
    "port": 587,
    "username": "admin",
    "password": "secret",
    "fromDomain": "example.com",
    "useTls": true
  }
}
```

#### Update Gateway (Admin Only)
```http
PUT /api/v1/gateway/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Updated Gateway Name",
  "description": "Updated description",
  "priority": 2,
  "maxConnections": 150,
  "smtpConfig": {
    "provider": "STALWART",
    "host": "smtp2.example.com",
    "port": 587,
    "username": "admin",
    "password": "newsecret",
    "fromDomain": "example.com",
    "useTls": true
  }
}
```

#### Get All Gateways
```http
GET /api/v1/gateway
Authorization: Bearer <token>
```

#### Get Gateway by ID
```http
GET /api/v1/gateway/{id}
Authorization: Bearer <token>
```

#### Get Gateways by Type
```http
GET /api/v1/gateway/type/{type}
Authorization: Bearer <token>

# Example: GET /api/v1/gateway/type/SMTP
```

#### Get Active Gateways
```http
GET /api/v1/gateway/active
Authorization: Bearer <token>
```

#### Get Least Loaded Gateway
```http
GET /api/v1/gateway/least-loaded/{type}
Authorization: Bearer <token>

# Example: GET /api/v1/gateway/least-loaded/SMTP
# Used for automatic gateway selection in provisioning
```

#### Delete Gateway (Admin Only)
```http
DELETE /api/v1/gateway/{id}
Authorization: Bearer <admin_token>
```

#### Enable/Disable Gateway (Admin Only)
```http
POST /api/v1/gateway/{id}/enable
Authorization: Bearer <admin_token>

POST /api/v1/gateway/{id}/disable
Authorization: Bearer <admin_token>
```

## Response Format

### Success Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "SMTP",
  "config": {
    "provider": "STALWART",
    "host": "smtp.example.com",
    "port": 587,
    "username": "admin",
    "password": "****", // Passwords are masked
    "fromDomain": "example.com"
  }
}
```

### Error Response
```json
{
  "timestamp": "2026-04-29T17:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

## Frontend Implementation Examples

### React + TypeScript

#### Gateway Management Component
```typescript
import React, { useState, useEffect } from 'react';
import apiClient from './apiClient';

interface Gateway {
  id: string;
  type: 'SMTP' | 'GSM';
  config: SmtpConfig | GsmConfig;
}

interface SmtpConfig {
  provider: string;
  host: string;
  port: number;
  username: string;
  password: string;
  fromDomain?: string;
}

export const GatewayManagement: React.FC = () => {
  const [gateways, setGateways] = useState<Gateway[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadGateways();
  }, []);

  const loadGateways = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get<Gateway[]>('/gateway');
      setGateways(response.data);
    } catch (error) {
      console.error('Failed to load gateways:', error);
    } finally {
      setLoading(false);
    }
  };

  const createGateway = async (gatewayData: any) => {
    try {
      const response = await apiClient.post<Gateway>('/gateway', gatewayData);
      setGateways([...gateways, response.data]);
      return response.data;
    } catch (error) {
      console.error('Failed to create gateway:', error);
      throw error;
    }
  };

  const deleteGateway = async (id: string) => {
    try {
      await apiClient.delete(`/gateway/${id}`);
      setGateways(gateways.filter(g => g.id !== id));
    } catch (error) {
      console.error('Failed to delete gateway:', error);
      throw error;
    }
  };

  return (
    <div>
      <h1>Gateway Management</h1>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <ul>
          {gateways.map(gateway => (
            <li key={gateway.id}>
              {gateway.type} - {gateway.config.host}
              <button onClick={() => deleteGateway(gateway.id)}>Delete</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};
```

### Vue 3 + TypeScript

#### Gateway Composable
```typescript
// composables/useGateways.ts
import { ref } from 'vue';
import apiClient from './apiClient';

export interface Gateway {
  id: string;
  type: 'SMTP' | 'GSM';
  config: any;
}

export function useGateways() {
  const gateways = ref<Gateway[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const loadGateways = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await apiClient.get<Gateway[]>('/gateway');
      gateways.value = response.data;
    } catch (e) {
      error.value = 'Failed to load gateways';
      console.error(e);
    } finally {
      loading.value = false;
    }
  };

  const createGateway = async (gatewayData: any) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await apiClient.post<Gateway>('/gateway', gatewayData);
      gateways.value.push(response.data);
      return response.data;
    } catch (e) {
      error.value = 'Failed to create gateway';
      throw e;
    } finally {
      loading.value = false;
    }
  };

  return {
    gateways,
    loading,
    error,
    loadGateways,
    createGateway
  };
}
```

## Setting Up Frontend Context

When working on the frontend, create this structure:

### Frontend Project Structure
```
frontend/
├── src/
│   ├── api/
│   │   ├── gatewayApi.ts      # Gateway-specific API calls
│   │   ├── client.ts           # Axios configuration
│   │   └── types.ts            # TypeScript interfaces
│   ├── components/
│   │   ├── gateway/
│   │   │   ├── GatewayList.tsx
│   │   │   ├── GatewayForm.tsx
│   │   │   └── GatewayCard.tsx
│   ├── composables/
│   │   └── useGateways.ts      # Vue composable
│   └── stores/
│       └── gatewayStore.ts     # State management
```

### Environment Variables
```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_KEYCLOAK_URL=http://localhost:8081/auth
VITE_KEYCLOAK_REALM=george
VITE_KEYCLOAK_CLIENT_ID=george-frontend
```

## CORS Configuration

The backend needs to allow frontend origins. Configure in `application.yaml`:

```yaml
spring:
  web:
    cors:
      allowed-origins: http://localhost:3000,http://localhost:5173
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allowed-headers: '*'
      allow-credentials: true
```

## Common Integration Patterns

### Handling Admin Authorization
```typescript
const checkAdminAccess = () => {
  const roles = getRolesFromToken(); // Extract from JWT
  if (!roles.includes('ADMIN')) {
    throw new Error('Admin access required');
  }
};
```

### Error Handling
```typescript
const handleApiError = (error: any) => {
  if (error.response?.status === 403) {
    // Handle authorization error
    showUnauthorizedModal();
  } else if (error.response?.status === 401) {
    // Handle authentication error
    redirectToLogin();
  } else {
    // Handle other errors
    showErrorMessage(error.response?.data?.message || 'An error occurred');
  }
};
```

### Loading States
```typescript
const [isLoading, setIsLoading] = useState(false);
const [isCreating, setIsCreating] = useState(false);
const [isDeleting, setIsDeleting] = useState(false);
```

## Testing the Integration

### Using cURL
```bash
# Get auth token from Keycloak first
export TOKEN="your-jwt-token"

# Test gateway API
curl -X GET http://localhost:8080/api/v1/gateway \
  -H "Authorization: Bearer $TOKEN"

# Create gateway
curl -X POST http://localhost:8080/api/v1/gateway \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "SMTP",
    "name": "Test Gateway",
    "smtpConfig": {
      "provider": "STALWART",
      "host": "smtp.test.com",
      "port": 587,
      "username": "test",
      "password": "test"
    }
  }'
```

## Next Steps

1. **Set up authentication** in your frontend with Keycloak
2. **Create API client** with proper interceptors
3. **Build UI components** for gateway management
4. **Implement error handling** and loading states
5. **Add role-based UI** to show admin-only features
6. **Test the integration** with the backend running locally

---

**Note**: This backend uses multi-tenancy. All requests are automatically scoped to the tenant context extracted from the JWT token.