# GSM Box Configuration System - Documentation

## Overview

The GSM Box Configuration system allows administrators to manage multiple GSM devices, configure protocols, handle authentication, and monitor box activities. This system is designed for telecommunications applications requiring GSM/cellular gateway management.

## System Components

### 1. Core Entities

#### GSMBoxConfig
Main entity representing a GSM Box device configuration.

**Key Properties:**
- `boxName` - Unique identifier
- `boxType` - QUAD_BAND, DUAL_BAND, SINGLE_BAND, LTE_MODEM, 4G_ROUTER, 5G_MODEM, HYBRID
- `ipAddress` & `port` - Network connectivity
- `imei` - International Mobile Equipment Identity (unique)
- `imsi` - International Mobile Subscriber Identity
- `phoneNumber` - Associated phone number
- `status` - ONLINE, OFFLINE, IDLE, BUSY, ERROR, MAINTENANCE, DISCONNECTED, INITIALIZING
- `signalStrength` - 0-31 signal bars
- `batteryLevel` - 0-100 percentage
- `manufacturer`, `model`, `firmwareVersion` - Device details

**Feature Flags:**
- `isActive` - Enable/disable box
- `isLocked` - Admin lock
- `allowSMS` - Allow SMS operations
- `allowCalls` - Allow call operations
- `allowData` - Allow data operations
- `maxConcurrentCalls` - Concurrent call limit
- `maxSMSPerMinute` - SMS rate limiting

**Relationships:**
- `protocols` - List of enabled protocols (1-to-many)
- `authCredentials` - Authentication methods (1-to-many)
- `logs` - Activity logs (1-to-many)

#### GSMProtocolConfig
Configures supported protocols for a GSM box.

**Supported Protocols:**
- GSM, GPRS, EDGE - 2G
- UMTS, HSPA - 3G
- LTE, LTE_ADVANCED - 4G
- NR - 5G
- VOIP_SIP, VOIP_IAX, VOIP_H323 - VoIP variants
- SS7 - Legacy signaling

**Properties:**
- `protocol` - Protocol type
- `isEnabled` - Enable/disable protocol
- `priority` - Fallback order (higher = first)
- `portNumber` - Protocol-specific port
- `settings` - JSON configuration
- `maxBandwidth` - Rate limiting (KB/s)

#### GSMAuthCredential
Manages authentication credentials for GSM boxes.

**Supported Auth Types:**
- BASIC - Username/password
- API_KEY - API key authentication
- OAUTH2 - OAuth 2.0 tokens
- BEARER_TOKEN - Bearer token
- CERTIFICATE - Certificate-based
- HMAC - HMAC signature
- JWT - JSON Web Token
- CUSTOM - Custom methods

**Properties:**
- `authType` - Authentication method
- `credentialName` - Credential identifier
- `credentialValue` - Primary credential (encrypted)
- `credentialSecret` - Secondary credential (encrypted)
- `additionalData` - JSON extra fields
- `isPrimary` - Use as default credential
- `isActive` - Enable/disable credential
- `expiresAt` - Expiration date (for tokens)
- `lastUsedAt` - Last usage timestamp

#### GSMBoxLog
Logs all GSM box activities and events.

**Event Types:**
- CONNECTED
- DISCONNECTED
- SMS_SENT
- SMS_RECEIVED
- CALL_OUTGOING
- CALL_INCOMING
- CALL_MISSED
- ERROR
- AUTHENTICATION_FAILED
- PROTOCOL_CHANGED
- FIRMWARE_UPDATE
- BATTERY_LOW
- SIGNAL_LOST

**Properties:**
- `eventType` - Type of event
- `eventMessage` - Human-readable message
- `errorDetails` - Error information
- `signalStrength` - Signal at event time
- `batteryLevel` - Battery at event time
- `relatedPhone` - Associated phone number
- `relatedData` - JSON context data

### 2. Enumerations

#### GSMBoxType
```
QUAD_BAND, DUAL_BAND, SINGLE_BAND
LTE_MODEM, 4G_ROUTER, 5G_MODEM
HYBRID
```

#### GSMProtocol
```
GSM, GPRS, EDGE, UMTS, HSPA, LTE, LTE_ADVANCED, NR
VOIP_SIP, VOIP_IAX, VOIP_H323, SS7
```

#### GSMBoxStatus
```
ONLINE, OFFLINE, IDLE, BUSY, ERROR
MAINTENANCE, DISCONNECTED, INITIALIZING
```

#### GSMAuthType
```
BASIC, API_KEY, OAUTH2, BEARER_TOKEN
CERTIFICATE, HMAC, JWT, CUSTOM
```

## Database Schema

### Tables

**gsm_box_configs**
- Main GSM box configuration table
- Contains device info and status

**gsm_protocol_configs**
- Protocol configurations per box
- Supports multiple protocols per box

**gsm_auth_credentials**
- Authentication credentials (encrypted)
- Multiple credentials per box
- Supports expiration and usage tracking

**gsm_box_logs**
- Activity and event logs
- Indexed for efficient querying
- Automatic timestamp tracking

### Key Relationships

```
GSMBoxConfig (1) ──→ (N) GSMProtocolConfig
GSMBoxConfig (1) ──→ (N) GSMAuthCredential
GSMBoxConfig (1) ──→ (N) GSMBoxLog
```

### Indexes

Strategic indexes for common queries:
- Box name lookup
- Status filtering
- Type filtering
- IMEI lookup (unique identifier)
- Phone number lookup
- IP address lookup
- Log event searches
- Date range queries

## Usage Examples

### Create a GSM Box Configuration

```java
GSMBoxConfig gsmBox = new GSMBoxConfig();
gsmBox.setBoxName("gsm_box_001");
gsmBox.setBoxLabel("Main GSM Gateway");
gsmBox.setBoxType(GSMBoxType.QUAD_BAND);
gsmBox.setIpAddress("192.168.1.100");
gsmBox.setPort(9000);
gsmBox.setImei("358075045148345");
gsmBox.setPhoneNumber("+1234567890");
gsmBox.setStatus(GSMBoxStatus.ONLINE);
gsmBox.setAllowSMS(true);
gsmBox.setAllowCalls(true);
gsmBox.setMaxSMSPerMinute(20);

gsmBoxConfigRepository.save(gsmBox);
```

### Add Protocol Configuration

```java
GSMProtocolConfig protocol = new GSMProtocolConfig();
protocol.setGsmBoxConfig(gsmBox);
protocol.setProtocol(GSMProtocol.LTE);
protocol.setIsEnabled(true);
protocol.setPriority(1); // Primary protocol
protocol.setPortNumber(9000);
protocol.setMaxBandwidth(1000L); // 1 MB/s

gsmProtocolConfigRepository.save(protocol);
```

### Add Authentication Credential

```java
GSMAuthCredential auth = new GSMAuthCredential();
auth.setGsmBoxConfig(gsmBox);
auth.setAuthType(GSMAuthType.API_KEY);
auth.setCredentialName("Primary API Key");
auth.setCredentialValue("sk_live_xxxxx");
auth.setIsPrimary(true);
auth.setIsActive(true);
auth.setCreatedBy("admin");

gsmAuthCredentialRepository.save(auth);
```

### Query Active Boxes

```java
// Get all online boxes
List<GSMBoxConfig> onlineBoxes = gsmBoxConfigRepository
    .findByStatusAndIsActiveTrue(GSMBoxStatus.ONLINE);

// Get boxes by type
List<GSMBoxConfig> quadBandBoxes = gsmBoxConfigRepository
    .findByBoxType(GSMBoxType.QUAD_BAND);

// Find by IMEI
Optional<GSMBoxConfig> box = gsmBoxConfigRepository.findByImei("358075045148345");

// Get box stats
long totalActive = gsmBoxConfigRepository.countByIsActiveTrue();
long onlineCount = gsmBoxConfigRepository.countByStatus(GSMBoxStatus.ONLINE);
```

### Query Enabled Protocols

```java
// Get enabled protocols for a box
List<GSMProtocolConfig> protocols = gsmProtocolConfigRepository
    .findByGsmBoxConfigIdAndIsEnabledTrue(boxId);

// Get by priority
List<GSMProtocolConfig> orderedProtocols = gsmProtocolConfigRepository
    .findByGsmBoxConfigIdOrderByPriorityDesc(boxId);
```

### Query Authentication Credentials

```java
// Get primary credential
Optional<GSMAuthCredential> primaryAuth = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndIsPrimaryTrue(boxId);

// Get active credentials
List<GSMAuthCredential> activeAuth = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndIsActiveTrue(boxId);

// Get by type
List<GSMAuthCredential> apiKeys = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndAuthType(boxId, GSMAuthType.API_KEY);
```

### Query Logs

```java
// Get recent logs with pagination
Page<GSMBoxLog> logs = gsmBoxLogRepository
    .findByGsmBoxConfigIdOrderByCreatedAtDesc(boxId, PageRequest.of(0, 50));

// Get error events
List<GSMBoxLog> errors = gsmBoxLogRepository
    .findByGsmBoxConfigIdAndEventType(boxId, "ERROR");

// Get logs in date range
List<GSMBoxLog> rangeLogs = gsmBoxLogRepository
    .findByGsmBoxConfigIdAndCreatedAtBetween(boxId, startDate, endDate);
```

## Security Considerations

1. **Credential Encryption**
   - Store `credentialValue` and `credentialSecret` encrypted
   - Use AES-256 encryption in production
   - Never log credential values

2. **Access Control**
   - Restrict GSM box management to admins only
   - Audit all credential changes
   - Lock boxes for security incidents

3. **Rate Limiting**
   - Enforce `maxSMSPerMinute` limits
   - Enforce `maxConcurrentCalls` limits
   - Monitor for abuse patterns

4. **Audit Trail**
   - Track all modifications via `createdBy`/`updatedBy`
   - Log all activity in GSMBoxLog
   - Maintain historical records

5. **Network Security**
   - Validate IP addresses
   - Use TLS/SSL for connections
   - Implement firewall rules
   - Rate limit connections

## Admin Operations

### Enable/Disable Box
```java
box.setIsActive(false);
gsmBoxConfigRepository.save(box);
```

### Lock Box (Emergency)
```java
box.setIsLocked(true);
gsmBoxConfigRepository.save(box);
```

### Update Status
```java
box.setStatus(GSMBoxStatus.MAINTENANCE);
gsmBoxConfigRepository.save(box);
```

### Add Maintenance Log
```java
GSMBoxLog log = new GSMBoxLog();
log.setGsmBoxConfig(box);
log.setEventType("MAINTENANCE");
log.setEventMessage("Firmware update scheduled");
gsmBoxLogRepository.save(log);
```

### Rotate Credentials
```java
// Deactivate old credential
oldAuth.setIsActive(false);
gsmAuthCredentialRepository.save(oldAuth);

// Add new credential as primary
newAuth.setIsPrimary(true);
gsmAuthCredentialRepository.save(newAuth);
```

## Monitoring & Alerts

Recommended alerts:
- Box goes offline
- Signal strength drops below threshold
- Battery level critical
- Authentication failure
- SMS rate limit exceeded
- Protocol failure
- Connection timeout

## API Endpoints (Recommended)

```
Admin GSM Box Management:
POST   /api/v1/admin/gsm-boxes           - Create GSM box
GET    /api/v1/admin/gsm-boxes           - List all boxes
GET    /api/v1/admin/gsm-boxes/{id}      - Get box details
PUT    /api/v1/admin/gsm-boxes/{id}      - Update box
DELETE /api/v1/admin/gsm-boxes/{id}      - Delete box

Protocol Management:
POST   /api/v1/admin/gsm-boxes/{id}/protocols    - Add protocol
PUT    /api/v1/admin/gsm-boxes/{id}/protocols/{pid} - Update protocol
DELETE /api/v1/admin/gsm-boxes/{id}/protocols/{pid} - Remove protocol

Authentication Management:
POST   /api/v1/admin/gsm-boxes/{id}/credentials      - Add credential
PUT    /api/v1/admin/gsm-boxes/{id}/credentials/{cid} - Update credential
DELETE /api/v1/admin/gsm-boxes/{id}/credentials/{cid} - Delete credential

Monitoring:
GET    /api/v1/admin/gsm-boxes/status/online  - Get online boxes
GET    /api/v1/admin/gsm-boxes/{id}/logs      - Get box logs
GET    /api/v1/admin/gsm-boxes/{id}/stats     - Get box statistics

Control Operations:
POST   /api/v1/admin/gsm-boxes/{id}/lock      - Lock box
POST   /api/v1/admin/gsm-boxes/{id}/unlock    - Unlock box
POST   /api/v1/admin/gsm-boxes/{id}/restart   - Restart box
```

## Best Practices

1. **Regular Monitoring** - Check box status frequently
2. **Credential Rotation** - Rotate credentials monthly
3. **Rate Limiting** - Set appropriate SMS/call limits
4. **Protocol Fallback** - Configure backup protocols
5. **Logging** - Maintain detailed activity logs
6. **Backup Boxes** - Have redundant GSM boxes
7. **Error Handling** - Implement proper error callbacks
8. **Security Updates** - Keep firmware current
9. **Access Control** - Restrict admin access
10. **Disaster Recovery** - Plan for box failures

## Future Enhancements

1. Load balancing across multiple boxes
2. Automatic failover
3. Real-time monitoring dashboard
4. SMS/call routing optimization
5. Advanced analytics
6. Machine learning anomaly detection
7. Integration with telephony systems
8. WebRTC support
9. Advanced reporting
10. Multi-tenancy support


