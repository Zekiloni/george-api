# Platform Domain

## Overview

The Platform domain manages the core operational capabilities of the multi-tenant SaaS system: lead management, interactive page construction, real-time visitor session tracking, and operator control consoles. It serves as the primary interaction layer between end users (operators) and their target audiences (leads/visitors).

The domain is built on three interconnected subsystems: a centralized lead repository that supports both sales and platform operations, a visual page builder enabling operators to create multi-step interactive flows, and a real-time session tracking engine that captures visitor interactions and enables bidirectional operator control.

## Lead Management

### Lead Data Model

Leads represent individual contacts within the platform's database. Each lead record captures geolocation metadata alongside core contact information, enabling targeted campaign distribution and sophisticated reporting.

```
Lead
├── id: UUID
├── phoneNumber: string
├── country: string
├── areaCode: string
├── regionCode: string
├── location: string
├── carrier: string
├── createdAt: timestamp
└── updatedAt: timestamp
```

The lead model is deliberately minimal at the domain layer. Operators can extend leads with custom attributes when importing data, but the core entity maintains geographic and carrier classification to support filtering and targeting operations.

### Lead Repository Architecture

The platform maintains a centralized lead database accessible to multiple operational contexts:

- **Administrator Lead Upload**: System administrators ingest lead datasets via bulk import, enriching the platform's shared lead pool. This enables the platform to serve its own outreach operations and creates inventory for resale to end users.

- **Operator Lead Import**: End users can upload their own contact lists. These imported leads are deduplicated against existing records and integrated into the centralized pool. Ownership attribution is maintained through tenant association, allowing operators to query their own leads while the platform retains rights to the aggregate dataset.

- **Lead Access Control**: When an operator purchases a Lead package offering, a snapshot of available leads is provisioned to their inventory. They gain read access to a paginated, searchable table containing their allocated leads. The underlying data remains in the centralized repository, but entitlements control visibility and operational boundaries.

### Lead Deduplication and Data Quality

When leads are imported, the system performs phonetic and exact matching against existing records using the PhoneResolutionResult model. Duplicate detection operates on normalized phone numbers, preventing accumulation of redundant contact records. The deduplication process respects tenant boundaries—duplicate detection prevents re-importing leads already assigned to the same tenant, while permitting the platform to leverage the same contact for multiple outreach campaigns or resale channels.

## Page Builder

### Page Definition Architecture

The page builder enables operators to construct guided interactive experiences without technical expertise. Pages are modeled as sequences of steps, each containing configurable components that define user interaction patterns.

```
Page
├── id: UUID
├── title: string
├── slug: string (URL-friendly identifier)
├── description: string
├── keywords: string (SEO metadata)
├── faviconUrl: string
├── status: PageStatus (DRAFT, PUBLISHED, ARCHIVED)
├── definition: PageDefinition
├── createdBy: string (user reference)
├── createdAt: timestamp
└── updatedAt: timestamp
```

The PageDefinition structure captures the composition logic and interaction rules defined at design time. It specifies all available steps, component configurations, field validation rules, and conditional navigation flows.

### Page Components and Step Types

Pages are composed of discrete steps, each representing a logical interaction milestone. Supported step types include:

- **Form Steps**: Text input, email input, phone number input, checkbox groups, radio selections, and dropdown menus. Each field supports validation rules (required/optional, format constraints, length limits) and can be marked sensitive to trigger secure input handling.

- **OTP Dialog Steps**: One-time password verification flows. The operator defines the OTP delivery channel and validation logic. During runtime, the operator can trigger OTP delivery to the visitor's phone or email, and the dialog enforces successful verification before progression.

- **Confirmation Modal Steps**: Modal dialogs presenting actionable confirmations. Typically used before conversions to confirm user intent. Operators define button labels and associated actions.

- **Informational Steps**: Read-only content presentation steps. Display terms, disclaimers, product information, or any narrative content without requiring visitor input. No conversion data is captured.

- **Personal Data Collection Steps**: Specialized forms for collecting identity information (name, email, phone). These steps are marked in the definition as sensitive and trigger appropriate security measures during transmission.

- **Login Steps**: Authentication-style screens requesting credentials. Useful for gated access or two-factor scenarios within a single page flow.

Each step component is stateless at the definition level—the definition specifies only what to render and what rules to enforce. Runtime behavior (like which modal to open or what field to focus) is determined by operator actions sent in real time.

### Page Linking and Distribution

When a page is published, the system generates a unique tracking link. Each tracking link is represented by a persistent TrackingLink entity:

```
TrackingLink
├── id: UUID
├── code: string (short, unique identifier)
├── page: reference to Page
├── isActive: boolean
├── source: string (campaign identifier, optional)
├── lastClickedAt: timestamp
├── createdAt: timestamp
└── updatedAt: timestamp
```

The code field serves as a short, memorable identifier for the link. When distributed via SMS or email, this code is embedded in the full tracking URL. The source field allows operators to tag links with campaign metadata (e.g., "sms_campaign_jan_2024", "email_list_v2"), enabling attribution reporting.

Tracking links remain active independently of page status. An operator can publish a page, distribute links, then archive the page while links remain trackable. This allows historical session data to persist.

## Real-Time Session Tracking

### Session Lifecycle

When a visitor clicks a tracking link and lands on a published page, a real-time tracking session is initiated. The session captures every interaction in the visitor's browser and streams events to the operator's console.

### Tracking Event Model

Every interaction generates a TrackingEvent:

```
TrackingEvent
├── id: UUID
├── trackingLinkId: string
├── sessionId: string (correlation across events)
├── eventType: string (click, input, step_transition, modal_open, form_submit, etc.)
├── eventTimestamp: timestamp
├── ipAddress: string
├── userAgent: string
├── referer: string
├── deviceType: string (mobile, desktop, tablet)
├── geographicLocation: string (resolved from IP)
├── metadata: JSON (event-specific data)
├── createdAt: timestamp
└── updatedAt: timestamp
```

Event types capture the full spectrum of visitor behavior:

- **Navigation Events**: step transitions, button clicks, link navigation
- **Input Events**: field focus, blur, text input (including partial typing for real-time visibility)
- **Modal Events**: modal_open, modal_close, confirmation acceptance/rejection
- **Form Events**: form_submit capturing the complete submission payload
- **Session Events**: page_load, session_start, session_end

The metadata field is JSON and accommodates event-specific details—for a form submission, it contains all submitted field values; for a step transition, it contains the step identifier and navigation context.

### Visitor Consent and Privacy

All tracking is predicated on explicit visitor consent. During page load, the visitor's browser evaluates consent settings defined in the page configuration. Tracking begins only after affirmative consent. This architecture ensures compliance with privacy regulations (GDPR, CCPA) while enabling comprehensive session visibility for operators who have obtained legitimate visitor authorization.

### Operator Console

The operator console presents a live, streaming view of active visitor sessions. As visitors interact with the page, events stream to the console in real time. The operator observes:

- **Session Summary**: visitor IP, device type, geographic location, session start time, current step
- **Interaction Timeline**: chronological list of events with full event details and metadata
- **Active Visitor State**: current step, filled form values, pending modal dialogs

The console updates are driven by WebSocket or Server-Sent Events (SSE) communication, ensuring operators see interactions as they occur without polling.

## Operator Control and Real-Time Actions

### Action Dispatch Model

While observing a session, the operator can dispatch actions back to the active visitor's page. Actions trigger specific UI behaviors in the visitor's browser:

- **Modal Actions**: open an OTP dialog, display a confirmation modal, close a modal
- **Navigation Actions**: advance to next step, return to previous step, jump to specific step
- **Field Actions**: focus a specific field, clear field value, trigger validation
- **UI Actions**: scroll to element, highlight element, trigger custom JavaScript hook

Actions are defined in the PageDefinition at design time. The operator specifies an action by its key (e.g., "send_otp", "confirm_order"), and the platform dispatches it to the active session. The client-side page renderer interprets the action and executes the corresponding UI modification.

### Action Authorization

The operator can only dispatch actions to sessions originating from their own pages. Tenant isolation is enforced: an operator seeing a session for their page_id can dispatch actions; an operator observing a session for another tenant's page receives a forbidden response.

### Bidirectional Communication Protocol

The tracking system uses WebSocket or Server-Sent Events (SSE) for efficient real-time communication:

**Operator → Visitor (Action Dispatch)**
```
action {
  sessionId: string
  actionKey: string
  payload: JSON (action-specific parameters)
  timestamp: ISO8601
}
```

**Visitor → Operator (Event Stream)**
```
event {
  sessionId: string
  trackingLinkId: string
  eventType: string
  metadata: JSON
  timestamp: ISO8601
}
```

The client-side page renderer maintains the active WebSocket connection, listening for dispatched actions. Upon receiving an action, it executes the corresponding behavior and continues streaming visitor interactions back to the operator.

## Session Submissions and Conversions

### Conversion Event Capture

When a visitor completes a page flow and submits a final form (if applicable), the submission is recorded as a conversion event. The conversion captures:

- **Form Data**: all submitted field values, preserving sensitive field indication
- **Visitor Context**: IP address, device, geographic location
- **Temporal Data**: submission timestamp, total session duration
- **Attribution**: source campaign (from tracking link), page identifier, visitor identifier (if captured during flow)

### Conversion Storage and Reporting

Conversion records are persisted in a dedicated repository and exposed to the operator through:

- **Live Console**: the submission appears in the operator's active session view
- **Submissions Log**: persistent paginated list of all conversions for a page or date range
- **Analytics Integration**: aggregated conversion counts, funnel metrics, attribution reports

The conversion data is associated back to the originating lead record (if the visitor's phone or email was captured during the flow), enabling full visitor journey attribution from initial outreach to final conversion.

## Page Versioning and Publishing

### Draft and Published States

Pages exist in two operational states:

- **DRAFT**: Pages under construction. Operators can modify structure, components, and actions. Draft pages are not distributed and generate no tracking links.

- **PUBLISHED**: Pages actively in distribution. Published pages generate tracking links and accept visitor traffic. Published pages are immutable from the visitor perspective—the page definition served to visitors is locked, ensuring consistency across all sessions for a given page version.

When an operator modifies a published page, the changes are staged as a new version. Publishing the new version leaves historical versions intact for reference, allowing operators to review past page configurations and understand session behavior in context.

## Integration Points with Commerce

The Platform domain integrates with Commerce to manage service entitlements. When an operator purchases a "Page Builder" service offering, their inventory is updated with a PageServiceAccess record. This record grants the operator permission to create and publish pages.

When leads are provisioned through a Lead package purchase, the Platform creates a LeadServiceAccess record linking purchased leads to the operator's inventory. The operator's lead dashboard applies these entitlements to filter visible leads.

---

This Platform domain documentation provides technical stakeholders with a complete operational view of lead management, page construction, real-time session tracking, and operator control. For integration details and the order-to-access provisioning pipeline, see the Commerce Domain documentation.
