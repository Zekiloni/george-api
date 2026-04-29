# Token & Link Management Implementation Plan

## Overview
This is the **recommended starting point** for completing the platform domain. Token & link management is foundational for campaign dispatching, session tracking, analytics, and conversion tracking.

## Current State Analysis

### What Exists
```java
// Campaign entity with token configuration
@Data
public class Campaign {
    private TokenGenerationStrategy tokenStrategy;  // Enum exists but not implemented
    private int tokenLength;
    private String baseUrl;
    private Ref page;
    private Ref gateway;
}

// Outreach entity with token
@Data
public class Outreach {
    private String sessionToken;  // Field exists but no generation logic
    private String recipient;
    private String message;
    private OutreachStatus status;
}

// Basic session creation endpoint
@GetMapping("/{token}")
public ResponseEntity<Void> create(@PathVariable String token, ...)
```

### What's Missing
- TokenGenerationStrategy implementation
- Token generation service
- Token validation and expiration
- Link generation and tracking
- Click analytics

---

## Implementation Plan

### Step 1: Token Generation Strategy (1 day)

#### 1.1 Implement TokenGenerationStrategy enum
```java
// src/main/java/com/zekiloni/george/platform/domain/model/campaign/TokenGenerationStrategy.java
public enum TokenGenerationStrategy {
    UUID,           // Standard UUID v4
    RANDOM_ALPHANUMERIC,  // Random alphanumeric string
    RANDOM_NUMERIC,      // Random numeric string
    CUSTOM_PATTERN,      // Custom regex pattern
    HASH_BASED      // Hash-based on recipient + campaign ID
}
```

#### 1.2 Create TokenGenerator Service
```java
// src/main/java/com/zekiloni/george/platform/application/service/campaign/TokenGeneratorService.java
@Service
public class TokenGeneratorService {
    
    private final SecureRandom random = new SecureRandom();
    
    public String generateToken(TokenGenerationStrategy strategy, 
                                int length, 
                                String recipient, 
                                String campaignId) {
        return switch (strategy) {
            case UUID -> UUID.randomUUID().toString().replace("-", "");
            case RANDOM_ALPHANUMERIC -> generateRandomAlphanumeric(length);
            case RANDOM_NUMERIC -> generateRandomNumeric(length);
            case HASH_BASED -> generateHashBasedToken(recipient, campaignId, length);
            case CUSTOM_PATTERN -> throw new UnsupportedOperationException("Custom patterns not yet implemented");
        };
    }
    
    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        return result.toString();
    }
    
    private String generateRandomNumeric(int length) {
        String digits = "0123456789";
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(digits.charAt(random.nextInt(digits.length())));
        }
        return result.toString();
    }
    
    private String generateHashBasedToken(String recipient, String campaignId, int length) {
        String input = recipient + campaignId + Instant.now().toEpochMilli();
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(input.getBytes());
        String hex = new String(Hex.encode(hash));
        return hex.substring(0, Math.min(length, hex.length()));
    }
}
```

#### 1.3 Add Token Validation Service
```java
// src/main/java/com/zekiloni/george/platform/application/service/campaign/TokenValidationService.java
@Service
public class TokenValidationService {
    
    private final OutreachRepositoryPort outreachRepository;
    
    public ValidationResult validateToken(String token) {
        // Check if token exists
        Optional<Outreach> outreach = outreachRepository.findBySessionToken(token);
        
        if (outreach.isEmpty()) {
            return ValidationResult.invalid("Token not found");
        }
        
        Outreach o = outreach.get();
        
        // Check if outreach is still valid
        if (o.getStatus() == OutreachStatus.EXPIRED) {
            return ValidationResult.invalid("Token expired");
        }
        
        if (o.getStatus() == OutreachStatus.CANCELLED) {
            return ValidationResult.invalid("Campaign cancelled");
        }
        
        // Check token age (optional - implement if needed)
        // if (o.getCreatedAt().plusDays(30).isBefore(Instant.now())) {
        //     return ValidationResult.invalid("Token too old");
        // }
        
        return ValidationResult.valid(outreach);
    }
    
    public record ValidationResult(boolean isValid, String message, Outreach outreach) {
        public static ValidationResult valid(Outreach outreach) {
            return new ValidationResult(true, null, outreach);
        }
        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message, null);
        }
    }
}
```

### Step 2: Link Generation Service (1 day)

#### 2.1 Create TrackingLink Entity
```java
// src/main/java/com/zekiloni/george/platform/domain/model/campaign/TrackingLink.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingLink {
    private String id;
    private String shortCode;      // Short, memorable code
    private String fullUrl;        // Full tracking URL
    private String campaignId;
    private String outreachId;
    private String sessionToken;
    private String source;         // UTM source, campaign identifier
    private boolean isActive;
    private int clickCount;
    private OffsetDateTime lastClickedAt;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
```

#### 2.2 Create Link Generation Service
```java
// src/main/java/com/zekiloni/george/platform/application/service/campaign/LinkGenerationService.java
@Service
public class LinkGenerationService {
    
    @Value("${app.base-url:http://localhost:4200}")
    private String baseUrl;
    
    @Value("${app.tracking.path:/track}")
    private String trackingPath;
    
    private final TrackingLinkRepositoryPort trackingLinkRepository;
    private final TokenGeneratorService tokenGenerator;
    
    public String generateTrackingLink(Campaign campaign, Outreach outreach, String source) {
        // Generate short code for the link
        String shortCode = generateShortCode();
        
        // Build full tracking URL
        String fullUrl = buildTrackingUrl(outreach.getSessionToken(), shortCode, source);
        
        // Create tracking link entity
        TrackingLink link = TrackingLink.builder()
            .shortCode(shortCode)
            .fullUrl(fullUrl)
            .campaignId(campaign.getId())
            .outreachId(outreach.getId())
            .sessionToken(outreach.getSessionToken())
            .source(source)
            .isActive(true)
            .clickCount(0)
            .expiresAt(calculateExpiry(campaign))
            .createdAt(OffsetDateTime.now())
            .build();
        
        trackingLinkRepository.save(link);
        
        return fullUrl;
    }
    
    private String generateShortCode() {
        // Generate 6-character alphanumeric code
        String code;
        do {
            code = tokenGenerator.generateToken(
                TokenGenerationStrategy.RANDOM_ALPHANUMERIC, 
                6, 
                null, 
                null
            );
        } while (trackingLinkRepository.findByShortCode(code).isPresent());
        
        return code;
    }
    
    private String buildTrackingUrl(String token, String shortCode, String source) {
        return String.format("%s%s/%s", baseUrl, trackingPath, token);
    }
    
    private OffsetDateTime calculateExpiry(Campaign campaign) {
        // Default 30 days from creation
        return OffsetDateTime.now().plusDays(30);
    }
    
    public record LinkClickResult(String token, String shortCode, String source, 
                                   OffsetDateTime clickedAt, String ipAddress, String userAgent) {
    }
}
```

### Step 3: Click Tracking Implementation (1 day)

#### 3.1 Create Click Tracking Entity
```java
// src/main/java/com/zekiloni/george/platform/domain/model/campaign/LinkClick.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkClick {
    private String id;
    private String trackingLinkId;
    private String shortCode;
    private String sessionToken;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private String deviceType;
    private String geographicLocation;
    private OffsetDateTime clickedAt;
}
```

#### 3.2 Update UserSessionApiController
```java
// src/main/java/com/zekiloni/george/platform/infrastructure/in/web/UserSessionApiController.java
@RestController
@RequestMapping("${api.base.path:/api/v1}/user-session")
public class UserSessionApiController {
    
    private final UserSessionCreateUseCase createUseCase;
    private final TokenValidationService tokenValidationService;
    private final LinkTrackingService linkTrackingService;
    
    @GetMapping("/{token}")
    public ResponseEntity<TrackingResponse> create(
            @PathVariable String token,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String shortCode,
            HttpServletRequest request) {
        
        // Validate token
        ValidationResult validation = tokenValidationService.validateToken(token);
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // Track click if shortCode provided
        if (shortCode != null) {
            linkTrackingService.trackClick(
                token, 
                shortCode, 
                source, 
                getIpAddress(request), 
                getUserAgent(request)
            );
        }
        
        // Create or update session
        UserSession session = createUseCase.handle(
            token, 
            getUserAgent(request), 
            getIpAddress(request)
        );
        
        return ResponseEntity.ok(new TrackingResponse(
            session.getId(),
            validation.outreach().getPage(),
            validation.outreach().getCampaign()
        ));
    }
    
    public record TrackingResponse(String sessionId, String pageId, String campaignId) {}
}
```

### Step 4: Integration with Campaign Dispatch (1 day)

#### 4.1 Update CampaignDispatchService
```java
// src/main/java/com/zekiloni/george/platform/application/usecase/campaign/CampaignDispatchService.java
@Service
public class CampaignDispatchService {
    
    private final TokenGeneratorService tokenGenerator;
    private final LinkGenerationService linkGenerator;
    private final OutreachRepositoryPort outreachRepository;
    private final MessageGatewayAdapter gatewayAdapter;
    
    public void dispatchCampaign(Campaign campaign) {
        // Update campaign status
        campaign.setStatus(CampaignStatus.DISPATCHING);
        
        // Generate outreaches with tokens and links
        List<Outreach> outreaches = generateOutreaches(campaign);
        
        // Save outreaches
        outreachRepository.saveAll(outreaches);
        
        // Dispatch messages via gateway
        dispatchMessages(campaign, outreaches);
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.COMPLETED);
    }
    
    private List<Outreach> generateOutreaches(Campaign campaign) {
        // This would load targets from lead lists
        List<Lead> targets = loadTargets(campaign);
        
        return targets.stream()
            .map(lead -> createOutreach(campaign, lead))
            .toList();
    }
    
    private Outreach createOutreach(Campaign campaign, Lead lead) {
        // Generate unique token
        String token = tokenGenerator.generateToken(
            campaign.getTokenStrategy(),
            campaign.getTokenLength(),
            lead.getPhoneNumber(),
            campaign.getId()
        );
        
        // Generate tracking link
        String trackingLink = linkGenerator.generateTrackingLink(
            campaign, 
            Outreach.builder().sessionToken(token).build(),
            campaign.getName()
        );
        
        // Personalize message
        String personalizedMessage = personalizeMessage(
            campaign.getMessageTemplate(),
            lead,
            trackingLink
        );
        
        return Outreach.builder()
            .sessionToken(token)
            .recipient(lead.getPhoneNumber())
            .message(personalizedMessage)
            .status(OutreachStatus.PENDING)
            .scheduledAt(OffsetDateTime.now())
            .build();
    }
    
    private void dispatchMessages(Campaign campaign, List<Outreach> outreaches) {
        // Map to gateway message format
        List<Message> messages = outreaches.stream()
            .map(o -> new Message(o.getRecipient(), o.getMessage(), o.getSessionToken()))
            .toList();
        
        // Dispatch via gateway
        List<OutreachResult> results = gatewayAdapter.sendMessages(messages);
        
        // Update outreach statuses based on results
        updateOutreachStatuses(results);
    }
}
```

---

## Testing Strategy

### Unit Tests
```java
@SpringBootTest
class TokenGeneratorServiceTest {
    
    @Autowired
    private TokenGeneratorService tokenGenerator;
    
    @Test
    void generateToken_withUUIDStrategy_returnsValidUUID() {
        String token = tokenGenerator.generateToken(
            TokenGenerationStrategy.UUID, 32, "test", "campaign1"
        );
        
        assertNotNull(token);
        assertTrue(token.matches("[a-f0-9]{32}"));
    }
    
    @Test
    void generateToken_withRandomAlphanumeric_returnsValidString() {
        String token = tokenGenerator.generateToken(
            TokenGenerationStrategy.RANDOM_ALPHANUMERIC, 12, "test", "campaign1"
        );
        
        assertNotNull(token);
        assertEquals(12, token.length());
        assertTrue(token.matches("[a-zA-Z0-9]{12}"));
    }
}

@SpringBootTest
class TokenValidationServiceTest {
    
    @Autowired
    private TokenValidationService tokenValidationService;
    
    @Autowired
    private OutreachRepositoryPort outreachRepository;
    
    @Test
    void validateToken_withValidToken_returnsValid() {
        Outreach outreach = createTestOutreach();
        outreachRepository.save(outreach);
        
        ValidationResult result = tokenValidationService.validateToken(outreach.getSessionToken());
        
        assertTrue(result.isValid());
        assertEquals(outreach, result.outreach());
    }
    
    @Test
    void validateToken_withInvalidToken_returnsInvalid() {
        ValidationResult result = tokenValidationService.validateToken("nonexistent");
        
        assertFalse(result.isValid());
        assertEquals("Token not found", result.message());
    }
}
```

### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class LinkTrackingIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private LinkGenerationService linkGenerationService;
    
    @Test
    void whenUserClicksLink_thenClickIsTracked() throws Exception {
        // Create test link
        Campaign campaign = createTestCampaign();
        Outreach outreach = createTestOutreach();
        String link = linkGenerationService.generateTrackingLink(campaign, outreach, "test");
        
        // Simulate click
        mockMvc.perform(get("/track/" + outreach.getSessionToken())
                .param("source", "test")
                .header("User-Agent", "TestAgent")
                .header("X-Forwarded-For", "192.168.1.1"))
            .andExpect(status().isOk());
        
        // Verify click was tracked
        // Add assertions here
    }
}
```

---

## Configuration Updates

### application.yaml
```yaml
# Application URLs
app:
  base-url: ${APP_BASE_URL:http://localhost:4200}
  tracking:
    path: ${TRACKING_PATH:/track}
    token-expiry-days: ${TOKEN_EXPIRY_DAYS:30}

# Token Settings
token:
  default-strategy: ${TOKEN_STRATEGY:RANDOM_ALPHANUMERIC}
  default-length: ${TOKEN_LENGTH:12}
  hash-salt: ${TOKEN_HASH_SALT:your-secret-salt-here}
```

---

## Database Migration

### V2__add_token_tracking.sql
```sql
-- Tracking links table
CREATE TABLE tracking_links (
    id UUID PRIMARY KEY,
    short_code VARCHAR(10) UNIQUE NOT NULL,
    full_url TEXT NOT NULL,
    campaign_id VARCHAR(255) NOT NULL,
    outreach_id VARCHAR(255) NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    source VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    click_count INTEGER DEFAULT 0,
    last_clicked_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id VARCHAR(255) NOT NULL
);

CREATE INDEX idx_tracking_links_token ON tracking_links(session_token);
CREATE INDEX idx_tracking_links_shortcode ON tracking_links(short_code);
CREATE INDEX idx_tracking_links_campaign ON tracking_links(campaign_id);

-- Link clicks table
CREATE TABLE link_clicks (
    id UUID PRIMARY KEY,
    tracking_link_id VARCHAR(255) NOT NULL,
    short_code VARCHAR(10) NOT NULL,
    session_token VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referer TEXT,
    device_type VARCHAR(50),
    geographic_location VARCHAR(255),
    clicked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id VARCHAR(255) NOT NULL
);

CREATE INDEX idx_link_clicks_token ON link_clicks(session_token);
CREATE INDEX idx_link_clicks_shortcode ON link_clicks(short_code);
CREATE INDEX idx_link_clicks_clicked_at ON link_clicks(clicked_at);
```

---

## Success Criteria

### Functional Requirements
- ✅ Tokens can be generated using different strategies
- ✅ Tokens are validated correctly
- ✅ Tracking links are generated with short codes
- ✅ Clicks are tracked with full metadata
- ✅ Links are embedded in campaign messages
- ✅ Token expiration is enforced
- ✅ Integration with campaign dispatch works

### Non-Functional Requirements
- ✅ Token generation is cryptographically secure
- ✅ Link generation is performant (< 100ms)
- ✅ Click tracking doesn't impact page load time
- ✅ Database queries are optimized with proper indexes
- ✅ API responses are consistent and well-structured

---

## Next Steps After Implementation

Once token & link management is complete:
1. Implement one gateway adapter (SMTP/mail2SMS)
2. Test end-to-end campaign dispatching
3. Add basic session tracking
4. Implement real-time event streaming
5. Build operator console backend

This implementation provides the foundation for all remaining platform features and can be tested independently.
