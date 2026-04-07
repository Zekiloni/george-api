# Invoice Event System - Implementacija Sažetak

## Arhitektura

```
┌─────────────────────────────────────────────────────────────────┐
│ INFRA SLOJ (Input Web)                                          │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ InvoiceWebhookController                                │   │
│ │ - Prima BtcPayEventDto iz webhook-a                     │   │
│ │ - Prosleđuje u InvoiceEventHandlerPort                  │   │
│ └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ DOMAIN SLOJ (Order Package)                                     │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ InvoiceEventFactory                                      │   │
│ │ - BtcPayEventDto → InvoiceEvent (domenski model)        │   │
│ └──────────────────────────────────────────────────────────┘   │
│                            ↓                                     │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ InvoiceEvent (apstraktna)                                │   │
│ │ - InvoiceCreatedEvent                                   │   │
│ │ - InvoiceSettledEvent     ← KRITIČNO ZA PROVIZIONIRANJE│   │
│ │ - InvoiceExpiredEvent                                   │   │
│ │ - ... (ostali eventi)                                   │   │
│ └──────────────────────────────────────────────────────────┘   │
│                            ↓                                     │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ InvoiceEventProcessor (Visitor Pattern Adapter)         │   │
│ │ - onInvoiceSettled() → triggeruj provizioniranje       │   │
│ │ - onInvoiceCreated() → kreiraj order                   │   │
│ │ - ... (ostale operacije po event tipu)                 │   │
│ └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ INFRA SLOJ (Output Adapter)                                     │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ InvoiceEventHandlerAdapter (Port Implementacija)        │   │
│ │ - Povezuje domain sa infra slojem                       │   │
│ │ - Koristi Visitor pattern                               │   │
│ └──────────────────────────────────────────────────────────┘   │
│                            ↓                                     │
│ ┌──────────────────────────────────────────────────────────┐   │
│ │ ProvisioningService (ili OrderService)                  │   │
│ │ - Učitaj order sa stavkama                              │   │
│ │ - Za svaku stavku primeni ProvisioningStrategy          │   │
│ │ - Kreiraj resurse (SMTP access, page builder, leads)   │   │
│ │ - Kreiraj inventory entries                             │   │
│ └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Tok Podataka

### 1. Webhook Dolazak
```
BtcPay → POST /api/v1/webhooks/invoice → InvoiceWebhookController
                                              ↓
                                    BtcPayEventDto
```

### 2. Transformacija u Domenske Modele
```
BtcPayEventDto → InvoiceEventFactory.createFromDto()
                              ↓
                        InvoiceEvent
                    (npr. InvoiceSettledEvent)
```

### 3. Event Hendlovanje
```
InvoiceEvent → InvoiceEventHandlerAdapter.handle()
                              ↓
                  event.accept(eventProcessor)
                              ↓
                    Visitor Pattern Dispatch
                              ↓
        InvoiceEventProcessor.onInvoiceSettled()
                              ↓
                 orderProvisioningService.provision()
```

### 4. Provizioniranje
```
provision(invoiceId)
    ↓
Učitaj Order sa stavkama
    ↓
Za svaku stavku:
    - Identifikuj tip servisa (SMTP, PageBuilder, Leads, OTP)
    - Primeni odgovarajuću ProvisioningStrategy
    - Kreiraj resurse u catalog/inventory
    ↓
Označi order kao provisioned
```

## Fleksibilnost i Proširivost

### Dodavanje Novog Event Tipa

1. **Kreira event klasu:**
```java
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceRefundedEvent extends InvoiceEvent {
    private String refundReason;
    
    // ... builder i implementacija
}
```

2. **Dodaj u factory:**
```java
case InvoiceRefunded -> mapInvoiceRefunded((InvoiceRefundedDto) dto);
```

3. **Dodaj u visitor:**
```java
T visit(InvoiceRefundedEvent event);
```

4. **Implementiraj u procesoru:**
```java
@Override
protected void onInvoiceRefunded(InvoiceRefundedEvent event) {
    // Logika za refund
}
```

### Dodavanje Novog Servisa za Provizioniranje

1. **Kreiraj service enum:**
```java
public enum ServiceType {
    SMTP_TO_SMS,
    PAGE_BUILDER,
    LEADS,
    OTP_SERVICE  // novi
}
```

2. **Kreiraj strategy:**
```java
public class OtpServiceProvisioningStrategy implements ProvisioningStrategy {
    @Override
    public boolean isApplicable(Order order) {
        return order.hasService(ServiceType.OTP_SERVICE);
    }
    
    @Override
    public void provision(Order order) {
        // OTP provizioniranje logika
    }
}
```

3. **Registruj strategiju:**
```java
strategies.add(new OtpServiceProvisioningStrategy());
```

## Primena Korišćenja

### Primer 1: Direktna Obrada Event-a
```java
// Kreira event iz DTO
BtcPayEventDto dtoEvent = receive();
InvoiceEvent event = InvoiceEventFactory.createFromDto(dtoEvent);

// Hendluj event
invoiceEventHandlerPort.handle(event);
```

### Primer 2: Visitor Pattern Obrada
```java
class CustomEventProcessor extends InvoiceEventAdapter {
    @Override
    protected void onInvoiceSettled(InvoiceSettledEvent event) {
        // Prilagođena logika
        log.info("Invoice {} je plaćena!", event.getInvoiceId());
    }
    
    @Override
    protected void onInvoiceCreated(InvoiceCreatedEvent event) {
        // Drugu logiku
    }
    
    // Ostali eventi...
}

// Koristi
InvoiceEvent event = InvoiceEventFactory.createFromDto(dtoEvent);
event.accept(new CustomEventProcessor());
```

### Primer 3: Strategija Provisioniranja
```java
public class ProvisioningContext {
    private List<ProvisioningStrategy> strategies;
    
    public void provision(Order order) {
        for (ProvisioningStrategy strategy : strategies) {
            if (strategy.isApplicable(order)) {
                strategy.provision(order);
            }
        }
    }
}
```

## Važne Točke

### ✅ Prednosti Ovog Pristupa

1. **Decoupling** - Domain ne zna ništa o DTO strukturi
2. **Type Safety** - Svaki event ima specifičan tip
3. **Validator** - Svaki event ima validate() metodu
4. **Visitor Pattern** - Fleksibilna obrada bez instanceof
5. **Extensibility** - Lako se dodaju novi eventi i strategije
6. **Testability** - Domenske modele je lako testirati
7. **Clear Intent** - Code je jasnog namene i lako se razume

### ⚠️ Stvari na Koje Trebate da Pazite

1. **Validacija** - Pozovite `validate()` nakon kreiranja
2. **Null Handling** - Provera null vrednosti u factory metodi
3. **Timestamp Konverzija** - Unix timestamp → OffsetDateTime
4. **PaymentStatus Maping** - DTO enum → Domain enum
5. **Error Handling** - Webhook bi trebao da vrati 5xx ako nešto pođe naopako

## Zaključak

Ovaj sistem omogućava:
- ✅ Generičnu obradu svih tipova invoice eventa
- ✅ Fleksibilan provisioniranje sa strategijama
- ✅ Lako dodavanje novih servisa bez promena u core kodu
- ✅ Čist kod sa jasnom separacijom concerns-a
- ✅ Robusna obrada grešaka i validacija

