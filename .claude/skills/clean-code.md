---
name: clean-code
description: Apply clean code principles for maintainable, readable code
type: skill
---

# Clean Code Skill

## Core Clean Code Principles

### Meaningful Names
Names should reveal intent and answer: what, why, how

#### Variable Names
```java
// ❌ Bad - doesn't reveal intent
int d; // elapsed time in days

// ✅ Good - reveals intent
int elapsedTimeInDays;
int daysSinceCreation;
```

#### Function/Method Names
```java
// ❌ Bad - vague
void process();

// ✅ Good - specific, verb-noun
void processOrderPayment();
void validateUserInput();
```

#### Class Names
```java
// ❌ Bad - generic, doesn't convey responsibility
class Manager, class Processor, class Data

// ✅ Good - specific, conveys responsibility
class OrderPaymentProcessor, class UserInputValidator
```

### Functions - Small and Do One Thing
Functions should be small, do one thing, and do it well.

#### Single Responsibility
```java
// ❌ Bad - does multiple things
public void processOrder(Order order) {
    // Validate
    if (order.getItems().isEmpty()) throw new Exception();
    
    // Calculate total
    BigDecimal total = order.getItems().stream()
        .map(item -> item.getPrice())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // Save
    orderRepository.save(order);
    
    // Send notification
    emailService.sendNotification(order.getUserEmail());
}

// ✅ Good - each function does one thing
public void processOrder(Order order) {
    validateOrder(order);
    BigDecimal total = calculateOrderTotal(order);
    order.setTotalPrice(total);
    saveOrder(order);
    sendOrderNotification(order);
}
```

#### Function Arguments
- Avoid >3 arguments (use parameter objects for many args)
- Flag arguments indicate function does multiple things (split it)

```java
// ❌ Bad - too many arguments
void createUser(String name, String email, String password, 
                boolean isAdmin, boolean sendEmail) { }

// ✅ Good - parameter object
void CreateUser(CreateUserRequest request) { }
```

### Comments - Code Should Speak for Itself
Comments are often apologizing for bad code. Try to make code self-explanatory.

#### When Comments Are Bad
```java
// ❌ Bad - comment explains what code does (code should speak for itself)
// Check if user is admin and has permission
if (user.getRole() == Role.ADMIN && user.hasPermission()) { }

// ✅ Good - code is self-explanatory
if (user.isAdmin() && user.hasPermission()) { }
```

#### When Comments Are Good
- Explaining WHY (not WHAT)
- Legal requirements
- Warning about consequences
- TODO markers for future improvements
- Public API documentation

### Error Handling
Handle errors properly, don't ignore them.

#### Specific Exceptions
```java
// ❌ Bad - generic exception
throw new Exception("Error processing order");

// ✅ Good - specific exception
throw new OrderValidationException("Order has no items");
```

#### Don't Ignore Exceptions
```java
// ❌ Bad - swallows exception
try {
    processOrder(order);
} catch (Exception e) {
    // Do nothing
}

// ✅ Good - handle or propagate
try {
    processOrder(order);
} catch (OrderProcessingException e) {
    log.error("Failed to process order: {}", order.getId(), e);
    throw e;
}
```

### Code Structure

#### Avoid Deep Nesting
```java
// ❌ Bad - deeply nested
if (order != null) {
    if (order.getItems() != null) {
        if (!order.getItems().isEmpty()) {
            processItems(order.getItems());
        }
    }
}

// ✅ Good - guard clauses, flat structure
if (order == null) return;
if (order.getItems() == null || order.getItems().isEmpty()) return;
processItems(order.getItems());
```

#### Avoid God Classes
Classes should have one reason to change (Single Responsibility Principle).

```java
// ❌ Bad - God class doing everything
class OrderService {
    void createOrder() { }
    void validateOrder() { }
    void calculateTotal() { }
    void sendNotification() { }
    void generateInvoice() { }
    void updateInventory() { }
}

// ✅ Good - focused classes
class OrderCreationService { void createOrder() { } }
class OrderValidator { void validate() { } }
class OrderPricingCalculator { BigDecimal calculate() { } }
class OrderNotificationService { void send() { } }
```

### Object-Oriented Design

#### Encapsulation
Hide implementation details, expose operations.

```java
// ❌ Bad - exposes internal state
class Order {
    public List<OrderItem> items;
    public BigDecimal total;
}

// ✅ Good - encapsulates behavior
class Order {
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal total;
    
    public void addItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
    }
    
    private void recalculateTotal() {
        // Calculation logic
    }
}
```

#### Tell, Don't Ask
Tell objects what to do, don't ask them about their state.

```java
// ❌ Bad - asks about state
if (order.getStatus() == OrderStatus.PENDING) {
    order.setStatus(OrderStatus.PROCESSING);
}

// ✅ Good - tells object what to do
order.startProcessing();
```

### Testing

#### Test Names Should Describe Scenarios
```java
// ❌ Bad - vague
@Test
void testOrder() { }

// ✅ Good - descriptive
@Test
void orderCreation_withValidItems_createsPendingOrder() { }
```

#### Arrange-Act-Assert Pattern
```java
@Test
void orderCreation_withEmptyItems_throwsException() {
    // Arrange
    Order order = new Order();
    
    // Act
    Throwable thrown = assertThrows(
        OrderValidationException.class,
        () -> orderService.createOrder(order)
    );
    
    // Assert
    assertTrue(thrown.getMessage().contains("no items"));
}
```

## Clean Code Checklist

### Before Committing Code
1. **Names**: Are names descriptive and consistent?
2. **Functions**: Are functions small and do one thing?
3. **Structure**: Is code flat, not deeply nested?
4. **Responsibilities**: Does each class have one responsibility?
5. **Duplication**: Is DRY principle followed (Don't Repeat Yourself)?
6. **Tests**: Are tests readable and cover key scenarios?
7. **Comments**: Are comments only for WHY, not WHAT?

## Project-Specific Guidelines

### For George API
- Use use case services to orchestrate business logic (not controllers)
- Keep domain models free of framework dependencies
- Prefer immutable value objects (Money, Quantity)
- Make business rules explicit in domain models, not hidden in services
- Use guard clauses to reduce nesting
- Keep methods under 20 lines
- Classes should be under 300 lines

### Code Review Focus
When reviewing code, check for:
- Single Responsibility Principle violations
- Long methods that should be extracted
- Deep nesting that can be flattened
- Vague naming that reveals intent
- Business logic in wrong layer (controllers, entities)
- Missing or misleading comments
- Exception handling issues
- Test quality and coverage

## When to Use This Skill
Invoke this skill when:
- Writing new code (write clean from the start)
- Refactoring existing code for better readability
- Conducting code reviews
- Trying to understand complex code
- Making code more maintainable
- Reducing technical debt
