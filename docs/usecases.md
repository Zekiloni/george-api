# Use Cases

This document describes the primary use cases and user workflows across all platform roles: Administrators, Clients (Tenants/Operators), and End Users (Visitors/Leads).

## Actor Definitions

**Administrator**: Platform operator responsible for system configuration, service offering management, lead ingestion, and platform operations.

**Client / Tenant / Operator**: End user who purchases services and uses the platform to conduct outreach campaigns, manage leads, and track visitor interactions.

**Visitor / Lead**: Individual who receives outreach via SMS or email, clicks a tracking link, and interacts with a published page.

---

## Administrator Use Cases

### UC-ADM-001: Create and Configure Service Offerings

**Actor**: Administrator

**Goal**: Establish a new service offering available for purchase by clients.

**Preconditions**:
- Administrator is authenticated with administrative privileges
- The service specification type (SMTP, LEADS, PAGE) is defined in the system

**Main Flow**:
1. Administrator navigates to Catalog Management console
2. Selects "Create New Offering"
3. Chooses a service specification (e.g., SMTP)
4. Enters offering details: name, identifier, description
5. Defines characteristics that will be included (e.g., daily_send_limit=1000, max_recipients=500)
6. Selects billing model:
   - For RECURRING: Sets duration unit (MONTHS, YEARS), enables/disables client duration choice
   - For ONE_TIME: Confirms fixed quantity
   - For USAGE_BASED: Defines base unit and overage rules
7. Configures pricing:
   - For RECURRING: Enters base unit price and optional discount tiers (monthly, annual, quarterly)
   - For ONE_TIME: Enters fixed price
8. Sets offering status to ACTIVE
9. Saves offering

**Postconditions**:
- Offering is created and marked ACTIVE
- Offering appears in client catalog
- New offering is immediately available for purchase

**Variations**:
- **Bulk Configuration**: Administrator imports offering definitions from JSON/CSV template
- **Tiered Pricing**: Administrator creates multiple price tiers (basic, pro, enterprise) as separate offerings or within single offering with characteristic variations

---

### UC-ADM-002: Ingest Lead Dataset

**Actor**: Administrator

**Goal**: Upload a curated lead dataset into the centralized lead repository for platform operations and client resale.

**Preconditions**:
- Administrator has prepared lead data file (CSV, JSON, or database export)
- File contains minimally: phone numbers; optionally: country, region, carrier, location, name metadata

**Main Flow**:
1. Administrator navigates to Lead Management console
2. Selects "Upload Leads"
3. Specifies data source (internal campaign, vendor partnership, etc.)
4. Uploads lead file
5. System validates format and phone number normalization
6. System performs deduplication:
   - Checks for exact phone number matches in existing database
   - Performs phonetic matching to catch common variations
   - Flags potential duplicates for review
7. Administrator reviews flagged duplicates and approves/rejects each
8. System ingests deduplicated leads into centralized repository
9. Administrator assigns metadata tags (e.g., "US_TELECOM", "FRESH_Q1_2024") for filtering and reporting

**Postconditions**:
- Leads are added to centralized repository
- Leads are available for assignment to future Lead offerings
- Platform can use leads for internal outreach

**Variations**:
- **Incremental Import**: Administrator uploads updated lead datasets; system identifies new records vs. updates to existing
- **Vendor Integration**: System connects to external lead provider API and automatically syncs new leads on schedule

---

### UC-ADM-003: Monitor Platform Health and Usage

**Actor**: Administrator

**Goal**: Observe platform-wide metrics, service utilization, and system health.

**Preconditions**:
- Administrator is authenticated with administrative privileges

**Main Flow**:
1. Administrator accesses Dashboard / Analytics console
2. Views high-level metrics:
   - Active tenants, total subscriptions, monthly recurring revenue (MRR)
   - Total pages published, active tracking sessions, conversions this period
   - Lead inventory size, leads consumed vs. available
3. Drills into service-specific metrics:
   - SMTP: Total credentials issued, active subscriptions, daily send volumes
   - PAGE: Active pages, visitor funnel metrics, conversion rates
   - LEADS: Total inventory, allocated to clients, consumed this period
4. Reviews system health:
   - Active services, database connectivity, API response times
   - Failed payment events, subscription renewal failures
   - Error logs and anomalies
5. Generates reports (daily, weekly, monthly) for business intelligence

**Postconditions**:
- Administrator has visibility into platform operations
- Administrator can identify bottlenecks, failed renewals, or service issues

**Variations**:
- **Automated Alerts**: System sends alert notifications if metrics exceed thresholds (e.g., error rate > 1%, failed renewals > 10)
- **Custom Reports**: Administrator creates ad-hoc reports filtered by date range, service type, tenant segment

---

### UC-ADM-004: Manage Tenant Suspension and Account Controls

**Actor**: Administrator

**Goal**: Suspend or reactivate a tenant account due to policy violations, payment delinquency, or other administrative actions.

**Preconditions**:
- Administrator has identified a tenant requiring account action
- Tenant currently has active services

**Main Flow**:
1. Administrator navigates to Tenant Management
2. Searches for and selects target tenant
3. Reviews tenant activity history, outstanding invoices, service status
4. Selects "Suspend Account" action
5. Enters reason (payment delinquency, policy violation, requested closure, etc.)
6. System marks all active ServiceAccess records as SUSPENDED
7. Tenant receives notification of suspension
8. Tenant loses access to all services immediately
9. Administrator optionally configures grace period (e.g., 7 days for payment recovery)

**Postconditions**:
- Tenant services are unavailable
- ServiceAccess records are in SUSPENDED state
- Tenant is notified

**Reactivation Flow**:
1. Administrator reviews reason for suspension (payment received, policy resolved, etc.)
2. Selects "Reactivate Account"
3. System restores all ServiceAccess records to ACTIVE status
4. Tenant regains service access

---

### UC-ADM-005: Configure Payment Processing and Billing Rules

**Actor**: Administrator

**Goal**: Set up payment gateway integrations, billing cycles, and renewal policies.

**Preconditions**:
- Administrator has payment gateway credentials (Stripe, PayPal, etc.)

**Main Flow**:
1. Administrator navigates to Billing Configuration
2. Configures payment gateway:
   - Selects primary payment processor
   - Enters API keys and merchant account details
   - Sets webhook endpoints for payment confirmation
3. Configures billing cycle:
   - Sets renewal grace period (e.g., 7 days before expiration, sends reminder)
   - Sets renewal failure handling (suspend after X days, retry schedule)
   - Configures overage policies for usage-based services
4. Configures invoice settings:
   - Invoice numbering scheme
   - Payment terms (net 15, net 30, etc.)
   - Tax calculation rules and nexus settings
5. Tests payment flow end-to-end

**Postconditions**:
- Payment processing is active
- Automatic renewals are configured
- Billing cycles execute on schedule

---

## Client / Tenant / Operator Use Cases

### UC-CLIENT-001: Browse and Purchase Service Offerings

**Actor**: Client (Tenant)

**Goal**: Discover available services and initiate a purchase.

**Preconditions**:
- Client is authenticated
- Client is a member of a tenant account with billing authority
- Offerings are configured and marked ACTIVE

**Main Flow**:
1. Client navigates to Catalog / Shop section
2. Filters by service type (SMTP, LEADS, PAGE) or views all
3. Views offering cards showing:
   - Service name and description
   - Characteristics (e.g., "1000 emails/day", "100 pages max")
   - Pricing (monthly $49, annual $490 with discount)
   - Customer reviews or feature highlights
4. Clicks on offering to view detailed comparison with other tiers
5. Selects desired duration (if RECURRING with client choice) or quantity (if USAGE_BASED)
6. Clicks "Add to Cart"
7. Optionally continues shopping or proceeds to checkout
8. Reviews cart: items, quantities, pricing, applied discounts
9. Applies promotional code (if available)
10. Confirms billing address and payment method
11. Submits order

**Postconditions**:
- Order is created with status PENDING
- Client is redirected to payment processing
- Order awaits payment confirmation

**Variations**:
- **Bulk Purchase**: Client purchases multiple units of lead packages
- **Promotional Code**: Client applies code reducing price; effective unit price is recalculated
- **Saved Cart**: Client saves cart and completes purchase later

---

### UC-CLIENT-002: Access Inventory and Provisioned Services

**Actor**: Client (Tenant)

**Goal**: View all purchased and provisioned services, and access their respective features.

**Preconditions**:
- Client has completed orders with status COMPLETED
- ServiceAccess records have been provisioned

**Main Flow**:
1. Client navigates to "My Services" or Inventory dashboard
2. Views all active ServiceAccess records grouped by type:
   - **SMTP**: Lists all provisioned SMTP credentials
     - Displays: Server, port, username (password hidden for security)
     - Shows characteristics: daily_send_limit, current daily usage
     - Provides option to "Copy Credentials" or "Download Configuration"
   - **LEADS**: Lists all allocated lead packages
     - Shows: Total lead count, lead freshness date range, country/region filters
     - Provides link to "View Leads" table
   - **PAGE**: Lists page builder access
     - Shows: Max pages allowed, current page count, max monthly visitors allowed
     - Provides link to "Go to Page Builder"
3. Client selects a service to view additional details
4. For credential-based services (SMTP), can download or copy connection string
5. For feature services (PAGE, LEADS), navigates directly to feature UI

**Postconditions**:
- Client sees all provisioned services
- Client can access service features via links

---

### UC-CLIENT-003: Manage SMTP Credentials and Configuration

**Actor**: Client (Tenant)

**Goal**: Access and manage email sending credentials, configure integrations, and monitor usage against limits.

**Preconditions**:
- Client has purchased and provisioned SMTP service
- SmtpServiceAccess record exists in client inventory

**Main Flow**:
1. Client navigates to SMTP service in inventory
2. Views provisioned credential(s):
   - SMTP server host
   - Port number
   - Username and encrypted password (show/copy option)
   - Characteristics: daily send limit, max recipients per message
3. Integrates credentials into email client or application:
   - Copies credentials for manual entry into email marketing tool
   - Downloads auto-configuration file (.conf, .xml) for import into third-party system
   - Uses API to configure service automatically (if supported)
4. Tests credential validity by sending test email
5. Monitors usage dashboard:
   - Current daily email count
   - Progress bar showing daily limit utilization
   - Historical send volumes (last 7 days, last 30 days)
6. Can request additional credentials if offering allows (characteristic: num_credentials)

**Postconditions**:
- Client can use SMTP credentials to send email through external services
- Client can observe send volumes and limits

---

### UC-CLIENT-004: Import, Manage, and Query Leads

**Actor**: Client (Tenant)

**Goal**: Upload own leads, view purchased leads, segment leads, and export for outreach campaigns.

**Preconditions**:
- Client has either:
  - Purchased a LEADS offering and has LeadServiceAccess
  - Has permission to upload leads to their tenant account

**Main Flow**:
1. Client navigates to Leads section
2. Chooses "Import Leads" to upload own contact list:
   - Uploads CSV/JSON file with phone numbers, optional metadata (name, email, country, etc.)
   - System deduplicates against client's existing leads
   - Shows summary: X new leads imported, Y duplicates skipped
   - Imported leads are added to client's lead inventory and centralized database
3. Views lead table:
   - Shows paginated list of available leads (allocated from purchased packages + own imports)
   - Displays: phone number, name, country, carrier, region, createdAt
4. Filters and segments leads:
   - By country, region, carrier
   - By freshness (date range)
   - By metadata tags (if applied during import)
   - Full-text search on name/phone
5. Selects leads for outreach campaign
6. Exports filtered lead list:
   - As CSV for external use
   - As list for use in Page Builder distribution (below)
7. Optionally applies tags to leads (e.g., "contacted_jan", "converted")

**Postconditions**:
- Client has imported leads into system
- Client can query and filter leads for targeting
- Client can export lead lists for campaigns

---

### UC-CLIENT-005: Create and Publish Interactive Page

**Actor**: Client (Tenant)

**Goal**: Design a multi-step interactive page flow and publish for lead outreach.

**Preconditions**:
- Client has purchased PAGE offering and has PageServiceAccess with status ACTIVE
- PageServiceAccess characteristics include max_pages, and current page count < max_pages

**Main Flow**:
1. Client navigates to Page Builder
2. Creates new page:
   - Enters page title, slug (URL-friendly identifier), description
   - Optionally uploads favicon, sets SEO keywords
3. Builds page composition by adding steps:
   - Step 1: Informational step with terms and conditions
   - Step 2: Personal data collection form (name, email, phone)
   - Step 3: OTP verification dialog
   - Step 4: Confirmation modal before conversion
4. For each step component:
   - Configures validation rules (required/optional, format, length)
   - Sets display text and button labels
   - Marks sensitive fields (password, phone)
   - Defines conditional navigation (if user selects "Yes", go to step X; if "No", go to step Y)
5. Configures operator actions available during runtime:
   - "send_otp": Trigger OTP delivery
   - "confirm_order": Accept submission
   - "navigate_step_3": Jump to specific step
   - Custom actions as needed
6. Sets visitor consent requirements (tracking acceptance required before data collection)
7. Saves page as DRAFT
8. Previews page in mobile/desktop viewport
9. Makes adjustments and saves
10. Once satisfied, publishes page:
    - Status changes to PUBLISHED
    - System generates unique tracking links (one per distribution channel if desired)
    - Page definition is locked (immutable to visitors)

**Postconditions**:
- Page is published and ready for visitor traffic
- Tracking links are generated and ready for distribution

---

### UC-CLIENT-006: Distribute Tracking Links and Launch Campaign

**Actor**: Client (Tenant)

**Goal**: Send published page tracking links to leads via SMS/email, initiating visitor sessions.

**Preconditions**:
- Client has published a page
- Client has SMTP or messaging service to send outreach
- Client has lead list to target

**Main Flow**:
1. Client navigates to published page
2. Selects "Create Distribution Link":
   - Specifies campaign source identifier (e.g., "sms_campaign_jan", "email_list_v2")
   - Optionally adds tracking parameters
3. System generates short, unique tracking link with code embedded
4. Client copies link and prepares outreach message:
   - For SMS: "Click here to verify: https://platform.com/link/abc123"
   - For email: Embeds link in HTML email template
5. Client selects lead segment to target from Leads table (filtered list)
6. Initiates campaign:
   - Via SMTP integration: Sends email to lead email addresses with link
   - Via external messaging: Copies link into external SMS/messaging tool and sends batch
7. Campaign is launched; links are distributed to leads
8. Leads begin clicking links and landing on page

**Postconditions**:
- Tracking links are distributed to leads
- TrackingLink records are created with source metadata
- Visitor sessions begin as leads click

---

### UC-CLIENT-007: Monitor Live Visitor Sessions in Real-Time Console

**Actor**: Client (Tenant / Operator)

**Goal**: Watch visitor interactions with published page in real time, understand visitor intent, and intervene if needed.

**Preconditions**:
- Page is published and receiving visitor traffic
- Client is logged in and navigated to live session console

**Main Flow**:
1. Client navigates to live session console for published page
2. Views list of active sessions:
   - Each session shows: visitor IP, device type, geographic location, session duration, current step
3. Clicks on a session to enter detailed observation mode
4. Sees real-time interaction stream:
   - Timeline of events: page_load, step_transition, field_input (including partial typing), modal_open, etc.
   - Each event shows: event type, timestamp, metadata (field name, value, etc.)
5. Observes visitor current state:
   - Which step visitor is on
   - What form fields are filled, what are empty
   - Any modal dialogs currently displayed
6. Sends action back to visitor in real time:
   - Clicks "Send OTP" action → visitor receives OTP dialog in their browser
   - Clicks "Navigate to Next Step" → visitor's page advances
   - Can send custom actions like "Highlight field" to guide visitor
7. Watches visitor respond to action (e.g., enters OTP, completes field)
8. Can continue sending actions or simply observe

**Postconditions**:
- Client has full visibility into visitor journey
- Client can nudge visitor through flow as needed

---

### UC-CLIENT-008: Handle Conversion and Collect Form Submission

**Actor**: Client (Tenant / Operator)

**Goal**: Receive and process visitor form submission when visitor completes page flow.

**Preconditions**:
- Visitor has completed all steps and submitted final form
- Form submission is valid and meets all requirements

**Main Flow**:
1. Visitor fills out final form (name, email, phone, etc.) and clicks "Submit"
2. Page validates form client-side and sends submission to platform
3. Platform creates conversion event with submitted data:
   - Form field values
   - Visitor context (IP, device, geographic location, session duration)
   - Attribution (campaign source, tracking link code, page id)
4. System records conversion as complete
5. Client receives notification:
   - Real-time notification in live console
   - Conversion appears in session timeline with full form data
6. Client navigates to "Conversions Log" to see all submissions for page:
   - Paginated list of conversions
   - Filterable by date range, campaign source
   - Can download conversion data as CSV
7. Client can manually follow up with converted lead:
   - Mark conversion as "contacted"
   - Add notes for team
   - Export lead info for external CRM import

**Postconditions**:
- Conversion is recorded and visible to client
- Client can follow up with converted visitor
- Data is available for analytics and reporting

---

### UC-CLIENT-009: View Analytics and Conversion Funnel

**Actor**: Client (Tenant)

**Goal**: Understand visitor behavior, page performance, and conversion metrics.

**Preconditions**:
- Page has been published and received visitor traffic
- Multiple sessions and conversions have occurred

**Main Flow**:
1. Client navigates to Analytics section for a published page
2. Selects date range for analysis
3. Views high-level metrics:
   - Total unique visitors (by tracking link clicks)
   - Total sessions
   - Total conversions
   - Conversion rate (conversions / visitors)
   - Average session duration
4. Views funnel metrics by step:
   - Step 1: X visitors entered
   - Step 2: Y visitors advanced (drop-off: (X-Y)/X %)
   - Step 3: Z visitors advanced
   - Final: C conversions
5. Views demographic breakdown:
   - Conversions by country
   - Conversions by device type
   - Conversions by time of day
6. Views campaign attribution:
   - Breakdown by source/campaign (if multiple distribution links)
   - Top performing sources
7. Segments by custom metadata if available

**Postconditions**:
- Client understands page performance
- Client can identify optimization opportunities (high drop-off at step X, etc.)

---

### UC-CLIENT-010: Manage Subscription Renewal and Billing

**Actor**: Client (Tenant)

**Goal**: View upcoming renewals, billing history, and manage subscription preferences.

**Preconditions**:
- Client has active recurring subscriptions
- ServiceAccess records have validTo dates approaching or in past

**Main Flow**:
1. Client navigates to Billing section
2. Views upcoming renewals:
   - Lists all active subscriptions expiring within 30 days
   - Shows renewal date, current plan, renewal price
   - Displays "Auto-renew: On" or "Auto-renew: Off"
3. Can manage renewal settings:
   - Toggle auto-renewal on/off
   - Pause subscription until manual renewal
   - Cancel subscription immediately (service stops at next renewal)
4. Views billing history:
   - List of past invoices with dates, amounts, payment status
   - Can download invoice PDF
   - Can see itemized details (what services, how many units, discount applied)
5. Updates payment method if needed:
   - Adds new credit card or banking information
   - Sets as default for future renewals
6. Views outstanding balance if any (overdue invoices)

**Postconditions**:
- Client is informed of upcoming costs
- Client can control renewal behavior
- Client has full billing transparency

---

## Visitor / Lead Use Cases

### UC-VISITOR-001: Receive Outreach and Click Tracking Link

**Actor**: Visitor (Lead)

**Goal**: Receive campaign outreach and access the published page.

**Preconditions**:
- Visitor's phone or email is in client's lead list
- Client has distributed tracking link via SMS or email
- Visitor has received the message

**Main Flow**:
1. Visitor receives SMS or email containing tracking link
   - Message example: "Claim your offer: https://platform.com/link/abc123"
2. Visitor clicks link in SMS or email
3. Client's browser makes HTTP request to tracking URL
4. Platform records TrackingEvent:
   - Event type: "page_load"
   - Visitor IP, user agent, device type, referer
   - Timestamp
5. Platform initializes tracking session with unique sessionId
6. Platform delivers published page HTML to visitor's browser
7. Page renders in visitor's browser
8. First step displays with consent dialog:
   - "We use analytics to improve your experience. Accept?"
9. Visitor accepts consent
10. Page displays Step 1 content

**Postconditions**:
- Visitor has landed on page
- Session is active and tracking events
- Visitor is ready to interact with page

---

### UC-VISITOR-002: Interact with Multi-Step Page Flow

**Actor**: Visitor (Lead)

**Goal**: Navigate through defined page steps, fill forms, and progress toward conversion.

**Preconditions**:
- Visitor has loaded page (UC-VISITOR-001 complete)
- Consent has been accepted
- First step is displayed

**Main Flow**:
1. Visitor reads Step 1 content (e.g., terms and conditions)
2. Clicks "I Agree" button
3. Platform records TrackingEvent:
   - Event type: "button_click"
   - Button identifier, timestamp
4. Page transitions to Step 2
5. Step 2 displays form fields (name, email, phone)
6. Visitor starts typing in form fields
7. Platform records TrackingEvent for each field interaction:
   - Event type: "field_focus", "field_input", "field_blur"
   - Field name, current value (if not marked sensitive)
8. Client receives live updates in console showing visitor is typing
9. Visitor completes form and clicks "Next"
10. Page validates form (client-side checks):
    - Required fields filled
    - Email format valid
    - Phone format valid
11. Page transitions to Step 3 (OTP verification)
12. Step 3 displays message: "Enter the OTP we sent to your phone"
13. Client (in console) sees visitor at Step 3, still needs OTP
14. Client clicks "Send OTP" action in console
15. Platform delivers action to visitor's browser
16. Visitor receives OTP via SMS (separate channel)
17. Visitor enters OTP in form field
18. Page validates OTP and automatically advances to Step 4
19. Step 4 displays confirmation modal: "Complete this action?"
20. Visitor clicks "Yes, proceed"
21. Page transitions to final step (submission)

**Postconditions**:
- Visitor has completed all steps
- Visitor is ready to submit form

**Variations**:
- **Conditional Navigation**: If visitor selects "No" at confirmation, page returns to previous step instead of advancing
- **Operator Intervention**: Client can send custom actions (navigate to step X, highlight field, show modal) to guide visitor

---

### UC-VISITOR-003: Submit Conversion Form and Complete Journey

**Actor**: Visitor (Lead)

**Goal**: Submit final form, completing the conversion event.

**Preconditions**:
- Visitor has navigated through all steps
- Final step displays form with submission button

**Main Flow**:
1. Visitor views final step form with collected data summary and final CTA
2. Optionally reviews or modifies any fields
3. Clicks "Submit" button
4. Page collects all form data and sensitive field indicators
5. Page sends submission to platform with:
   - All form field values
   - Session ID
   - Timestamp
6. Platform creates conversion event:
   - Event type: "form_submit"
   - Submission timestamp
   - All submitted values
   - Visitor IP, device, geographic location
   - Session duration
   - Campaign source attribution
7. Platform stores conversion record in database
8. Client receives real-time notification in live console
9. Page displays success message: "Thank you! Your submission has been received."
10. Visitor optionally enters email for follow-up communications
11. Page displays confirmation number (optional)
12. Visitor's session is marked as COMPLETE

**Postconditions**:
- Conversion is recorded
- Client can follow up with visitor
- Visitor journey is complete

---

### UC-VISITOR-004: Abandon Page and End Session

**Actor**: Visitor (Lead)

**Goal**: Exit page without completing conversion.

**Preconditions**:
- Visitor has loaded page but not submitted
- Visitor navigates away or closes browser

**Main Flow**:
1. Visitor is on Step 2 of page flow
2. Visitor decides page is not relevant and clicks back button (or closes browser)
3. Platform detects page unload event
4. Platform records TrackingEvent:
   - Event type: "session_end"
   - Final step reached, timestamp
5. Platform calculates session duration
6. Session is marked as ABANDONED (no conversion)
7. Client sees abandoned session in console:
   - Final step: Step 2 (personal data collection)
   - Duration: 2 minutes
   - No conversion recorded

**Postconditions**:
- Session is terminated
- Client can analyze abandonment (dropped at Step 2, implies interest but hesitation on personal data collection)
- Conversion is not recorded

---

## Cross-Role Workflows

### Workflow W-001: End-to-End Campaign Execution

**Actors**: Administrator, Client, Visitor

**Goal**: Complete campaign from lead procurement through conversion tracking.

**Flow**:
1. **Administrator** (UC-ADM-002) uploads lead dataset to platform
2. **Client** (UC-CLIENT-001) browses offerings and purchases LEADS + PAGE offerings
3. **Commerce** system provisions SmartServiceAccess (leads) and PageServiceAccess
4. **Client** (UC-CLIENT-004) views leads and filters by country
5. **Client** (UC-CLIENT-005) creates multi-step page flow in page builder
6. **Client** publishes page; system generates tracking links
7. **Client** (UC-CLIENT-006) exports filtered lead list and distributes tracking link via SMS
8. **Visitor** (UC-VISITOR-001) receives SMS and clicks link
9. **Client** (UC-CLIENT-007) monitors visitor in real-time console
10. **Client** sends "Send OTP" action; visitor receives OTP and completes verification
11. **Visitor** (UC-VISITOR-003) submits form and completes conversion
12. **Client** (UC-CLIENT-008) receives conversion notification and follows up
13. **Client** (UC-CLIENT-009) views analytics showing 42% conversion rate on this page

---

### Workflow W-002: Subscription Purchase to Service Activation

**Actors**: Client, Commerce, Platform

**Goal**: Execute purchase flow and activate provisioned service.

**Flow**:
1. **Client** (UC-CLIENT-001) browses catalog and selects SMTP Monthly ($49/month)
2. **Client** proceeds to checkout, enters payment details, and completes order
3. **Commerce** system receives payment confirmation
4. **Commerce** creates Order with status COMPLETED
5. **Commerce** triggers provisioning:
   - Generates SMTP credentials (server, port, username, password)
   - Creates SmtpServiceAccess record with characteristics (daily_send_limit: 1000)
   - Sets validFrom = now, validTo = now + 1 month
6. **Client** (UC-CLIENT-002) navigates to "My Services" and sees new SMTP service
7. **Client** (UC-CLIENT-003) clicks on SMTP service and copies credentials
8. **Client** integrates credentials into external email marketing tool
9. **Client** sends test email; system accepts and decrements daily quota
10. **Client** monitors usage dashboard showing "5 emails sent today / 1000 limit"

---

### Workflow W-003: Payment Renewal and Grace Period

**Actors**: Commerce, Client, Payment Gateway

**Goal**: Handle recurring subscription renewal with failure recovery.

**Flow**:
1. **Client** has active monthly SMTP subscription expiring 2024-05-01
2. **Commerce** system (day before expiration) sends renewal reminder to client
3. On renewal date (2024-05-01):
   - **Commerce** attempts to charge payment method on file
   - Payment gateway returns DECLINED (insufficient funds)
4. **Commerce** marks ServiceAccess status as SUSPENDED
5. **Client** receives payment failure notification with "Grace period until 2024-05-08"
6. **Client** updates payment method and retries payment
7. Payment succeeds; **Commerce** restores ServiceAccess status to ACTIVE
8. **Client** service is restored with no service interruption within grace period

---

This document provides comprehensive coverage of all primary user workflows. Each use case defines preconditions, main flow, postconditions, and variations to support development, testing, and product management activities.

