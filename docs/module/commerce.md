# Commerce Domain

## Overview

The Commerce domain governs the complete product lifecycle: from service specification and catalog management, through pricing and offering configuration, to order processing and inventory provisioning. It acts as the transactional backbone connecting the platform's service offerings to tenant entitlements, implementing both the business rules around what services are available and the technical provisioning pipeline that makes those services operational.

The domain is structured around three core aggregates: the Catalog (service definitions and offerings), Orders (purchase transactions), and Inventory (provisioned service access records). These work in concert to implement a flexible, extensible product and billing model capable of supporting recurring subscriptions, usage-based services, and one-time purchases.

## Service Specifications and Offerings

### Service Specification Enumeration

The platform defines a fixed set of service types, each representing a distinct capability that can be packaged and sold:

```
ServiceSpecification
├── SMTP: Email delivery via SMTP credentials or UI interface
├── LEADS: Curated or raw contact datasets for outreach
└── PAGE: Visual page builder and session tracking platform
```

This enumeration establishes the taxonomy of offerings. When an administrator configures a new offering, they select from this list to define what service is being sold.

### Service Specification and Offering Architecture

Each offering is built on two conceptual layers: a base service specification (the template) and the business configuration (pricing, entitlements, characteristics).

```
Offering
├── id: UUID
├── identifier: string (e.g., "smtp-unlimited-monthly")
├── name: string
├── description: string
├── serviceSpecification: enum (SMTP, LEADS, PAGE)
├── characteristicSpecification: List<CharacteristicSpecification>
├── billingConfig: BillingConfig
├── pricing: List<OfferingPrice>
├── status: OfferingStatus (ACTIVE, INACTIVE, ARCHIVED)
└── createdAt: timestamp
```

### Characteristics and Service Limits

Characteristics represent enumerated attributes that define what is included or constrained in an offering. Common characteristics include:

- **Quota Characteristics**: daily send limits, maximum recipients per campaign, total storage capacity
- **Access Characteristics**: number of SMTP credentials issued, concurrent page instances, custom domain allowance
- **Feature Characteristics**: advanced analytics access, dedicated support tier, white-label capability

Each characteristic is specified at the offering level as a CharacteristicSpecification:

```
CharacteristicSpecification
├── name: string
├── value: string or numeric
├── unit: string (e.g., "emails/day", "GB", "count")
└── metadata: JSON (additional constraint details)
```

When an order is fulfilled and inventory is provisioned, the characteristics from the offering are copied into the ServiceAccess record, becoming the enforceable constraints for that tenant's usage.

### Billing Configuration

Offerings support three distinct billing models, configured through the BillingConfig:

```
BillingConfig
├── type: OfferingType (ONE_TIME, RECURRING, USAGE_BASED)
├── quantityAllowed: boolean (can customer order multiple units?)
├── maxQuantity: integer (if quantityAllowed, maximum units permitted)
├── durationAllowed: boolean (for RECURRING: can customer choose duration?)
└── durationUnit: enum (DAYS, MONTHS, YEARS - for RECURRING only)
```

**One-Time Offerings**: Lead packages and single purchases. No duration, fixed quantity per order. Price is constant.

**Recurring Offerings**: SMTP and Page subscriptions billed on a schedule. Pricing is duration-based (e.g., monthly, annual). Customers can optionally choose between predefined durations, or the offering enforces a fixed duration.

**Usage-Based Offerings**: Charged per unit of consumption (e.g., per SMS sent, per API call). Pricing may include a base tier and overage rates.

### Pricing Model

Pricing flexibility is achieved through a tiered pricing structure:

```
OfferingPrice
├── id: UUID
├── duration: integer (for RECURRING: repeating interval)
├── durationUnit: enum (DAYS, MONTHS, YEARS)
├── baseUnitPrice: Money
├── effectiveUnitPrice: Money (after discounts)
├── currency: string
└── createdAt: timestamp
```

For recurring offerings, multiple OfferingPrice entries define the tier structure:

- A monthly plan tier (duration=1, durationUnit=MONTHS) establishes the base unit price
- An annual tier (duration=12, durationUnit=MONTHS) may offer a discount per month
- The Offering.getPrice(duration, durationUnit) method calculates pricing for arbitrary durations by multiplying base tiers

For one-time offerings, a single price entry captures the fixed amount.

For usage-based offerings, a single base unit price is defined; overage rates are captured in metadata.

## Order and Purchase Lifecycle

### Order Model

Orders represent purchase transactions initiated by tenants (end users). Each order captures the complete transaction context:

```
Order
├── id: UUID
├── items: List<OrderItem>
├── status: OrderStatus (PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED)
└── tenantId: string
```

### Order Items

Each item in an order represents one or more units of a specific offering:

```
OrderItem
├── id: UUID
├── offering: reference to Offering
├── quantity: integer (units ordered, if offering allows)
├── duration: integer (for RECURRING: subscription period)
├── durationUnit: enum (for RECURRING: period unit)
├── totalPrice: Money
└── status: OrderStatus
```

The OrderItem captures the specifics of the purchase: which offering, how many units, for what duration, at what price. When the order is completed, this item becomes the provenance record for inventory entries.

### Order Status Transitions

Orders progress through a well-defined state machine:

- **PENDING**: Order created, awaiting payment processing
- **COMPLETED**: Payment successful, inventory provisioning triggered
- **FAILED**: Payment processing failed, no inventory created
- **CANCELLED**: Order explicitly cancelled before completion
- **REFUNDED**: Order completed but subsequently refunded; corresponding inventory entries are marked inactive

### Provisioning Trigger

Upon successful order completion (COMPLETED status), the commerce domain triggers inventory provisioning. For each OrderItem in the completed order, a ServiceAccess record is created tailored to the ordered service type.

## Inventory and Service Access

### Service Access Abstraction

Service access is represented as a hierarchy of domain models sharing a common base:

```
ServiceAccess (abstract base)
├── id: UUID
├── serviceSpecification: enum (SMTP, LEADS, PAGE)
├── orderItem: reference to OrderItem (provenance)
├── characteristics: List<Characteristic> (enforced limits)
├── validFrom: timestamp
├── validTo: timestamp (optional, for time-bound access)
├── status: ServiceStatus (ACTIVE, SUSPENDED, EXPIRED, CANCELLED)
└── tenantId: string
```

The ServiceAccess model captures three critical aspects:

1. **What is provisioned**: The characteristics list copies the offering's constraints into the access record, making them queryable and auditable
2. **When it's valid**: validFrom and validTo dates enforce temporal boundaries (e.g., annual subscriptions expire after 365 days)
3. **Authorization scope**: tenantId ties the access to a specific tenant, preventing cross-tenant access

### SMTP Service Access

SMTP service access provisions email delivery credentials:

```
SmtpServiceAccess extends ServiceAccess
├── smtpServer: string
├── port: integer
├── username: string
├── password: string
└── characteristics: (e.g., daily_send_limit, max_recipients_per_message)
```

When provisioned, the SMTP credentials are generated or retrieved from external providers and stored encrypted in the ServiceAccess record. The tenant's email sending service retrieves these credentials at runtime, checking characteristics (daily limits) before executing sends.

### Lead Service Access

Lead service access grants permission to view and operate on a specific set of leads:

```
LeadServiceAccess extends ServiceAccess
├── leads: List<Lead> (snapshot of allocated leads)
└── characteristics: (e.g., total_lead_count, lead_freshness_days)
```

When a Lead offering is purchased, the inventory system allocates a subset of leads from the platform's centralized lead pool. This allocation is captured as a LeadServiceAccess record with a snapshot of the lead list and constraints.

### Page Service Access

Page service access unlocks the page builder interface:

```
PageServiceAccess extends ServiceAccess
├── characteristics: (e.g., max_pages, max_monthly_visitors, custom_domains_allowed)
```

PageServiceAccess is a permission record with no credential payload. When provisioned, it signals that the tenant has permission to create pages, build flows, and publish tracking links. Usage is metered through tracking event counts and page counts.

### Service Status Lifecycle

ServiceAccess records progress through status states:

- **ACTIVE**: Provisioned and valid. The tenant can use the service.
- **SUSPENDED**: Service temporarily disabled (e.g., payment failed, quota exceeded). Visible in inventory but not usable.
- **EXPIRED**: validTo date has passed. Automatically transitioned by a scheduled cleanup process.
- **CANCELLED**: Explicitly cancelled (e.g., refund issued). Persisted for audit trails.

## Invoice and Billing Records

Order completion generates invoice records capturing the financial transaction:

```
Invoice
├── id: UUID
├── orderId: reference to Order
├── amount: Money
├── items: List<InvoiceLineItem> (detail per OrderItem)
├── issuedAt: timestamp
├── dueAt: timestamp
├── paidAt: timestamp (null if unpaid)
└── status: InvoiceStatus (ISSUED, PAID, OVERDUE, CANCELLED)
```

Invoices serve dual purposes: financial accounting and audit trails. They document what was ordered, at what price, when payment was due, and when payment was received.

## Billing Model Implementation

### Recurring Subscription Billing

For recurring offerings, the system implements automated renewal:

1. **Initial Purchase**: Tenant orders a recurring offering with selected duration. Order is created and completed, triggering ServiceAccess provisioning with validFrom = now and validTo = now + duration.

2. **Pre-Renewal Notification**: As validTo approaches (e.g., 7 days before), the system generates a renewal reminder to the tenant.

3. **Automatic Renewal**: On the renewal date (validTo), if payment information is valid and the tenant has not cancelled, an automatic renewal order is created using the same offering and duration. A new ServiceAccess is provisioned, and old access is marked EXPIRED.

4. **Renewal Failure**: If payment fails during renewal, the system:
   - Marks the ServiceAccess as SUSPENDED
   - Sends payment failure notification to tenant
   - Provides a grace period (e.g., 7 days) for payment recovery
   - After grace period, marks access CANCELLED

5. **Subscription Cancellation**: Tenant can cancel at any time. Cancellation takes effect at the next renewal boundary (or immediately, depending on terms). The validTo of the current ServiceAccess is respected.

### One-Time Purchase Billing

One-time purchases (like Lead packages) have simpler lifecycle:

1. **Purchase**: Tenant orders offering. Order is completed, ServiceAccess provisioned.
2. **Consumption**: Tenant consumes the purchased leads. No renewal occurs.
3. **Expiration**: If offering defines a validTo (e.g., leads are only valid for 90 days), ServiceAccess is marked EXPIRED automatically.

### Usage-Based Billing

Usage-based offerings are metered and billed in arrears:

1. **Access Provisioning**: Tenant purchases a usage allowance (e.g., "1000 SMS per month"). ServiceAccess is provisioned with monthly validTo.

2. **Usage Tracking**: As tenant sends SMS, the platform increments usage counters tied to the ServiceAccess record.

3. **Quota Enforcement**: Before executing a requested action (send SMS), the platform checks current usage against characteristics limits. If quota would be exceeded, the action is rejected with a quota exceeded error.

4. **Overage Handling**: If tenant exceeds quota, overage may be:
   - Blocked entirely (service stops)
   - Allowed with overage charges (tracked for end-of-period billing)
   - Allowed with auto-replenishment (tenant is charged for additional allowances)

The specific overage behavior is configured in the offering's characteristics.

5. **End-of-Period Billing**: At the end of the month (or period defined by validTo), the system calculates total usage, applies overage charges if applicable, and generates an invoice.

## Pricing and Discount Rules

The Offering model provides hooks for dynamic pricing:

- **Base Unit Price**: The standard per-unit charge
- **Effective Unit Price**: After applying discounts, promotional codes, or volume discounts
- **Tier-Based Pricing**: Different prices for different durations (e.g., monthly vs. annual)

Discount rules can be applied at offering definition time (e.g., "annual subscriptions receive 15% discount") or at order time (e.g., promotional codes, loyalty discounts).

The getPrice(duration, durationUnit) method on Offering handles these calculations, returning the effective unit price for the requested duration.

## Entitlement Enforcement

Runtime enforcement of entitlements happens at the point of use. For example:

**SMTP Service Example**:
```
When tenant requests to send email:
  1. Look up SmtpServiceAccess for tenant
  2. Check status == ACTIVE
  3. Check validFrom <= now <= validTo
  4. Check characteristics.daily_send_limit > current_daily_count
  5. If all checks pass, execute send and increment daily_count
  6. If any check fails, return entitlement error
```

**Lead Access Example**:
```
When tenant requests to view leads:
  1. Look up LeadServiceAccess for tenant
  2. Check status == ACTIVE
  3. Serve leads from the ServiceAccess.leads list (the allocated snapshot)
  4. Apply pagination and filtering
```

**Page Builder Example**:
```
When tenant requests to create new page:
  1. Look up PageServiceAccess for tenant
  2. Check status == ACTIVE
  3. Check characteristics.max_pages > current_page_count
  4. If check passes, create page and increment page_count
  5. If check fails, reject with quota exceeded
```

## Integration with Platform Domain

The Commerce domain is consumed by the Platform domain at two key junctures:

### 1. Service Access Consumption

When the Platform domain needs to provision a capability (SMTP credentials, lead list, page permissions), it queries the Inventory subsystem:

```
Platform: "What services can tenant ABC use?"
Commerce: [SmtpServiceAccess{...}, LeadServiceAccess{...}, PageServiceAccess{...}]
```

The Platform domain respects the ServiceAccess constraints when implementing business operations.

### 2. Catalog Publishing

When the Platform domain's admin UI needs to display available offerings for purchase, it queries the Catalog:

```
Platform: "What offerings are currently ACTIVE?"
Commerce: [SMTP Monthly, SMTP Annual, Lead Package Basic, Page Builder Pro, ...]
```

The Platform domain renders these offerings in a shopping interface, allowing tenants to initiate purchase flows.

## Audit and Compliance

All commerce entities maintain audit trails:

- **createdAt / updatedAt**: Automatic temporal tracking of entity lifecycle
- **tenantId**: All service access is scoped to a specific tenant
- **orderItem**: ServiceAccess records maintain a reference back to the OrderItem that triggered their provisioning, enabling full traceability from purchase decision to actual access

Invoice records are immutable once issued, satisfying accounting and tax compliance requirements. Refunds do not modify historical invoices but instead create new invoice records with negative amounts.

---

This Commerce domain documentation provides technical stakeholders with a complete understanding of the product catalog, pricing and billing models, order lifecycle, and provisioning pipeline. Together with the Platform domain documentation, it describes the end-to-end flow from service specification through customer entitlement and runtime enforcement.
