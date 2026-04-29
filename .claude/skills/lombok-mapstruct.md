---
name: lombok-mapstruct
description: Apply best practices for Lombok, MapStruct, and clean mapper design
type: skill
---

# Lombok & MapStruct Best Practices Skill

## Lombok Best Practices

### When to Use Lombok Annotations

#### @Data - Use Carefully
```java
// ⚠️ Use sparingly - generates too much (equals, hashCode, setter, getter, toString)
// Better to be explicit for domain models

@Data
public class OrderDto { } // ❌ Avoid for domain models

// ✅ Better for simple DTOs or entities
@Data
public class ConfigProperty { }
```

#### @Value - For Immutable Value Objects
```java
// ✅ Perfect for value objects - immutable, clean
@Value
public class Money {
    BigDecimal amount;
    String currency;
}

@Value
public class Quantity {
    int value;
    String unit;
}
```

#### @Builder - For Complex Construction
```java
// ✅ Great for complex objects with many optional parameters
@Builder
public class PageDefinition {
    String title;
    String slug;
    List<PageStep> steps;
    Map<String, Object> metadata;
}

// Usage:
PageDefinition definition = PageDefinition.builder()
    .title("Contact Form")
    .slug("contact-form")
    .steps(stepList)
    .build();
```

#### @AllArgsConstructor and @NoArgsConstructor
```java
// ✅ Use for entities (required by JPA)
@Entity
@NoArgsConstructor  // JPA needs no-args constructor
@AllArgsConstructor // For testing and reflection
public class OrderEntity {
    @Id
    private UUID id;
    private String orderNumber;
}

// ✅ For domain models with specific constructor needs
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    // Factory method instead of public constructor
    public static Order create(OrderCreateCommand command) {
        return new Order(command);
    }
}
```

#### @Getter and @Setter
```java
// ✅ Prefer explicit getters/setters for domain models
@Getter // JPA entities need getters
@Entity
public class OrderEntity {
    private String status;
    
    // Setter with business logic validation
    public void setStatus(OrderStatus status) {
        if (this.status == OrderStatus.COMPLETED && status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change completed order status");
        }
        this.status = status;
    }
}

// ✅ For simple DTOs where setters are fine
@Getter
@Setter
public class OrderCreateDto {
    private String customerId;
    private List<OrderItemDto> items;
}
```

### Lombok Anti-Patterns to Avoid

#### Should You Use @Data on Domain Models?

**The Short Answer**: It depends on your design philosophy and the specific use case. Here's a nuanced view:

**❌ Avoid @Data when:**
- You need to enforce business rules on state changes
- The domain model has important invariants to maintain
- You want to prevent anemic domain model
- The model needs to control its internal state

```java
// ❌ Using @Data here bypasses business rules
@Data
public class Order {
    private OrderStatus status;
    private BigDecimal total;

    // Business rule: completed orders can't be modified
    // But @Data.generateSetter allows: order.setStatus(OrderStatus.CANCELLED);
}

// ✅ Better - control state changes
@Getter
public class Order {
    private OrderStatus status;
    private BigDecimal total;
    
    public void complete() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be completed");
        }
        this.status = OrderStatus.COMPLETED;
    }
    
    public void cancel() {
        if (status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed order");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
```

**✅ @Data can be acceptable when:**
- The domain model is simple with no business rules
- You're following pragmatic DDD over pure DDD
- The model is primarily a data holder in a CRUD context
- You put business logic in domain services instead of entities

```java
// ✅ Acceptable for simple entities
@Data
@Entity
public class Lead {
    private String phoneNumber;
    private String country;
    private String region;
    
    // Business rules in domain service instead
    // leadService.validatePhoneNumber(phoneNumber);
}
```

**Project-Specific Recommendation for George API:**

For your `domain/model/` classes (not JPA entities):
- **Value objects**: Use `@Value` (immutable) - you're already doing this correctly
- **Entities**: Use `@Data` if the entity is simple, otherwise use explicit getters/setters with business logic
- **JPA Entities**: Use `@Getter` only, add setters only where needed with validation

The key principle: **Prefer behavior-rich objects over data holders**, but be pragmatic about it.

#### JPA Entities with @Data
**Be cautious with @Data on JPA entities** due to potential issues:

```java
// ⚠️ Potentially problematic - equals/hashCode with lazy loading
@Data
@Entity
public class OrderEntity {
    @OneToMany
    private List<OrderItemEntity> items;
    // Generated equals/hashCode might cause issues with lazy collections
}

// ✅ Safer approach for JPA entities
@Getter
@Setter // Only if needed
@Entity
public class OrderEntity {
    // Use business key for equals, not generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderEntity)) return false;
        return id != null && id.equals(((OrderEntity) o).getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode(); // Constant hashCode
    }
}
```

**However**, if your JPA entities don't have lazy collections or complex relationships, `@Data` might be acceptable. The key is understanding the trade-offs.

## MapStruct Best Practices

### Mapper Design Principles

#### Keep Mappers Focused and Simple
```java
// ✅ Good - single responsibility mapper
@Mapper
public interface OrderDtoMapper {
    OrderDto toDto(Order order);
    Order toDomain(OrderDto dto);
    List<OrderDto> toDtoList(List<Order> orders);
}

// ❌ Bad - kitchen sink mapper
@Mapper
public interface MegaMapper {
    OrderDto toOrderDto(Order order);
    UserDto toUserDto(User user);
    ProductDto toProductDto(Product product);
    // ... 20 more mappings
}
```

#### ComponentModel Configuration
**Note**: Your project has `defaultComponentModel=spring` configured globally in pom.xml, so you don't need to specify it on each mapper.

```java
@Mapper // ✅ No need for componentModel - configured globally
public interface OrderDtoMapper {
    // Automatically generates a Spring bean
}
```

#### Handle Custom Mapping Logic
```java
@Mapper
public interface OrderDtoMapper {
    
    // Simple field mapping (automatic)
    @Mapping(target = "customerName", source = "customer.name")
    OrderDto toDto(Order order);
    
    // Custom method for complex logic
    default MoneyDto mapMoney(Money money) {
        if (money == null) return null;
        return new MoneyDto(money.getAmount(), money.getCurrency());
    }
    
    // Use expression for simple transformations
    @Mapping(target = "total", expression = "java(order.getSubtotal().add(order.getTax()))")
    OrderDto toDtoWithTotal(Order order);
}
```

### Collection Mapping

#### Batch Mapping Methods
```java
@Mapper
public interface LeadDtoMapper {
    LeadDto toDto(Lead lead);
    
    // ✅ MapStruct automatically generates efficient collection mapping
    List<LeadDto> toDtoList(List<Lead> leads);
    
    Set<LeadDto> toDtoSet(Set<Lead> leads);
    
    // For paginated results
    default Page<LeadDto> toDtoPage(Page<Lead> leads) {
        return leads.map(this::toDto);
    }
}
```

### Mapping with Inheritance and Abstractions

#### @SuperBuilder for Inheritance Hierarchies
**CRITICAL**: When working with inheritance hierarchies, always use `@SuperBuilder` instead of `@Builder`.

```java
// ✅ Base class with @SuperBuilder
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Gateway {
    private String id;
    private GatewayType type;
    private String name;
    private String description;
    private boolean enabled;
    private int priority;
    private String username;
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

// ✅ Subclasses also use @SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpGateway extends Gateway {
    private SmtpGatewayProvider provider;
    private String host;
    private int port;
    private String fromDomain;
    private boolean useTls;
}

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GsmGateway extends Gateway {
    private GsmProvider provider;
    private String ipAddress;
    private int port;
    private int totalPort;
    private List<GsmGatewaySlot> slot;
}
```

**Why @SuperBuilder?**
- Regular `@Builder` doesn't work properly with inheritance
- `@SuperBuilder` generates builder methods that handle parent class fields
- Required for MapStruct to recognize and instantiate subclasses
- Enables proper polymorphic object creation

#### @SubclassMappings for Polymorphic Mapping
**Use @SubclassMappings when mapping abstract classes/interfaces to concrete DTOs:**

```java
@Mapper(uses = {OrderDtoMapper.class})
public interface ServiceAccessDtoMapper {

    // ✅ Single method with @SubclassMappings for all subclasses
    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessDto.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessDto.class),
            @SubclassMapping(source = PageServiceAccess.class, target = PageServiceAccessDto.class)
    })
    @Mapping(source = "orderItem.id", target = "orderItemId")
    ServiceAccessDto toDto(ServiceAccess serviceAccess);
}
```

**Key Principles:**
- Use `@SubclassMappings` (plural) as a container annotation
- List all `@SubclassMapping` pairs inside it
- Single method signature returns the base type
- MapStruct generates the routing logic automatically

#### @ObjectFactory for Object Creation and Recognition
**Use @ObjectFactory for reverse mapping when MapStruct can't determine the concrete type:**

```java
@Mapper(uses = {OrderDtoMapper.class})
public interface ServiceAccessDtoMapper {

    // Forward mapping - automatic with @SubclassMappings
    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessDto.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessDto.class),
            @SubclassMapping(source = PageServiceAccess.class, target = PageServiceAccessDto.class)
    })
    @Mapping(source = "orderItem.id", target = "orderItemId")
    ServiceAccessDto toDto(ServiceAccess serviceAccess);

    // ✅ Reverse mapping - use @ObjectFactory with pattern matching
    @InheritInverseConfiguration
    @ObjectFactory
    default ServiceAccess createServiceAccess(ServiceAccessDto dto) {
        return switch (dto) {
            case LeadServiceAccessDto _ -> new LeadServiceAccess();
            case SmtpServiceAccessDto _ -> new SmtpServiceAccess();
            case PageServiceAccessDto _ -> new PageServiceAccess();
            default -> throw new IllegalArgumentException("Unknown ServiceAccessDto type: " + dto.getClass().getName());
        };
    }
}
```

**When to use @ObjectFactory:**
- Mapping from DTO to domain with inheritance
- MapStruct can't determine which concrete class to instantiate
- You need to control object creation logic
- Using sealed interfaces or abstract base classes

#### Complete Inheritance Mapping Pattern
**Here's the complete pattern for working with abstractions:**

```java
// 1. Domain model hierarchy
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class ServiceAccess {
    private String id;
    private ServiceSpecification serviceSpecification;
    private OrderItem orderItem;
    private OffsetDateTime provisionedAt;
    private ServiceAccessStatus status;
}

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpServiceAccess extends ServiceAccess {
    private String host;
    private int port;
    private String username;
    private String fromDomain;
    private boolean useTls;
}

// 2. DTO hierarchy (can be records or classes)
public sealed interface ServiceAccessDto {
    String id();
    ServiceSpecification serviceSpecification();
    Long orderItemId();
    OffsetDateTime provisionedAt();
    ServiceAccessStatus status();

    record SmtpServiceAccessDto(
        String id,
        ServiceSpecification serviceSpecification,
        Long orderItemId,
        OffsetDateTime provisionedAt,
        ServiceAccessStatus status,
        String host,
        int port,
        String username,
        String fromDomain,
        boolean useTls
    ) implements ServiceAccessDto {}
}

// 3. Mapper with @SubclassMappings
@Mapper(uses = {OrderDtoMapper.class})
public interface ServiceAccessDtoMapper {

    @SubclassMappings({
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessDto.class)
    })
    @Mapping(source = "orderItem.id", target = "orderItemId")
    ServiceAccessDto toDto(ServiceAccess serviceAccess);

    @InheritInverseConfiguration
    @ObjectFactory
    default ServiceAccess createServiceAccess(ServiceAccessDto dto) {
        return switch (dto) {
            case SmtpServiceAccessDto smtp -> SmtpServiceAccess.builder().build();
            default -> throw new IllegalArgumentException("Unknown type: " + dto.getClass());
        };
    }
}
```

#### Common Patterns and Gotchas

**✅ DO:**
- Use `@SuperBuilder` on all classes in inheritance hierarchy
- Use `@EqualsAndHashCode(callSuper = true)` on subclasses
- Use `@SubclassMappings` for polymorphic mapping
- Use `@ObjectFactory` with pattern matching for reverse mapping
- Keep DTO fields flat (single record with nullable fields)

**❌ DON'T:**
- Use regular `@Builder` with inheritance (won't work)
- Use sealed interfaces for DTOs with @SubclassMapping (MapStruct can't instantiate)
- Forget `callSuper = true` in @EqualsAndHashCode on subclasses
- Mix @Builder and @SuperBuilder in same hierarchy
- Use complex nested DTO structures that MapStruct can't handle

#### Simple DTO Pattern for Inheritance
**For simple cases, use a single DTO with nullable fields:**

```java
// ✅ Simple DTO - works great with @SubclassMapping
public record GatewayDto(
        String id,
        GatewayType type,
        String name,
        String description,
        boolean enabled,
        int priority,
        String username,
        String provider,
        String host,           // null for GSM gateways
        String ipAddress,      // null for SMTP gateways
        int port,
        String fromDomain,     // null for GSM gateways
        boolean useTls,        // false for GSM gateways
        int totalPort          // 0 for SMTP gateways
) {}

@Mapper
public interface GatewayDtoMapper {

    @SubclassMappings({
            @SubclassMapping(source = SmtpGateway.class, target = GatewayDto.class),
            @SubclassMapping(source = GsmGateway.class, target = GatewayDto.class)
    })
    @Mapping(target = "provider", source = "provider.name")
    GatewayDto toDto(Gateway gateway);

    @ObjectFactory
    default Gateway toDomain(GatewayCreateDto dto) {
        return switch (dto.type()) {
            case SMTP -> toSmtpDomain(dto);
            case GSM -> toGsmDomain(dto);
        };
    }
}
```

**Why this works:**
- Single concrete DTO type (MapStruct can instantiate it)
- Null fields for irrelevant properties per subtype
- Simple and maintainable
- Works well with @SubclassMapping

#### Entity Mapper Pattern (CRITICAL - No @Mapping Annotations!)
**When working with JPA entities and domain models, use this clean pattern:**

```java
@Mapper(uses = {OrderEntityMapper.class, LeadEntityMapper.class})
public interface ServiceAccessEntityMapper {

    // ✅ Only generic methods - no specific toSmtpDomain, toGsmEntity, etc.
    @SubclassMappings({
            @SubclassMapping(source = SmtpGatewayEntity.class, target = SmtpGateway.class),
            @SubclassMapping(source = GsmGatewayEntity.class, target = GsmGateway.class)
    })
    Gateway toDomain(GatewayEntity entity);

    @SubclassMappings({
            @SubclassMapping(source = SmtpGateway.class, target = SmtpGatewayEntity.class),
            @SubclassMapping(source = GsmGateway.class, target = GsmGatewayEntity.class)
    })
    GatewayEntity toEntity(Gateway gateway);

    // ✅ @ObjectFactory ONLY creates instances - NO mapping logic
    @ObjectFactory
    default Gateway createDomain(GatewayEntity entity) {
        return switch (entity) {
            case SmtpGatewayEntity ignored -> new SmtpGateway();
            case GsmGatewayEntity ignored -> new GsmGateway();
            default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
        };
    }

    @ObjectFactory
    default GatewayEntity createEntity(Gateway gateway) {
        return switch (gateway) {
            case SmtpGateway ignored -> new SmtpGatewayEntity();
            case GsmGateway ignored -> new GsmGatewayEntity();
            default -> throw new IllegalArgumentException("Unknown domain type: " + gateway.getClass());
        };
    }
}
```

**Key Principles:**
- ❌ **NO `@Mapping` annotations** - Let MapStruct handle everything automatically
- ❌ **NO specific methods** like `toSmtpDomain()`, `toGsmEntity()` - only generic `toDomain()`, `toEntity()`
- ✅ **Only 2 generic methods** - `toDomain()` and `toEntity()`
- ✅ **`@ObjectFactory` only creates instances** - NO field mapping in factory methods
- ✅ **Use pattern matching** - `switch (entity)` with `case SmtpGatewayEntity ignored`
- ✅ **Let MapStruct do the heavy lifting** - it handles all field mapping automatically

**Repository Adapter Pattern:**
```java
@Component
@RequiredArgsConstructor
public class GatewayRepositoryPortAdapter implements GatewayRepositoryPort {

    private final GatewayJpaRepository gatewayJpaRepository;
    private final GatewayEntityMapper entityMapper; // ✅ Inject entity mapper

    @Override
    @Transactional
    public Gateway save(Gateway gateway) {
        GatewayEntity entity = entityMapper.toEntity(gateway); // ✅ Use mapper
        return entityMapper.toDomain(gatewayJpaRepository.save(entity)); // ✅ Use mapper
    }

    @Override
    public Optional<Gateway> findById(String id) {
        return gatewayJpaRepository.findById(UUID.fromString(id))
                .map(entityMapper::toDomain); // ✅ Use mapper
    }

    // ✅ NO manual mapping methods in adapter
}
```

**Entity Pattern (Pure JPA - No toDomain methods):**
```java
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SmtpGatewayEntity extends GatewayEntity {
    private SmtpGatewayProvider provider;
    private String host;
    private int port;
    private String fromDomain;
    private boolean useTls;

    // ❌ NO toDomain() method - mapper handles conversion
}
```

**When to Use Entity Mappers:**
- ✅ All JPA repository adapters MUST use `{domain}entitymapper`
- ✅ Entities are pure JPA - NO conversion logic
- ✅ Repository adapters use entity mapper for all conversions
- ✅ NO manual mapping in adapters or entities

**Anti-Patterns to Avoid:**
- ❌ Adding `@Mapping` annotations (MapStruct handles it automatically)
- ❌ Creating specific methods like `toSmtpDomain()`, `toGsmEntity()`
- ❌ Putting mapping logic in `@ObjectFactory` methods
- ❌ Adding `toDomain()` methods to entities
- ❌ Manual field mapping in repository adapters

### Mapping Nested Objects

#### Use @Mapping for Nested Fields
```java
@Mapper
public interface OrderDtoMapper {
    
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "itemCount", expression = "java(order.getItems().size())")
    OrderSummaryDto toSummaryDto(Order order);
    
    // For nested DTOs, MapStruct will use other mappers if available
    @Mapping(target = "orderNumber", source = "orderNumber")
    OrderDetailDto toDetailDto(Order order);
}
```

## Clean Mapper Patterns

### Mapper Organization

#### One Mapper Per Domain Entity
```
infrastructure/in/web/dto/mapper/
├── OrderDtoMapper.java
├── InvoiceDtoMapper.java
├── ServiceAccessDtoMapper.java
└── LeadDtoMapper.java
```

#### Mapper Naming Conventions
```java
// ✅ Clear, consistent naming
OrderDtoMapper      // Order ↔ OrderDto
OrderEntityMapper   // Order ↔ OrderEntity
OrderJpaMapper      // Order ↔ OrderEntity (JPA-specific)

// ❌ Confusing naming
OrderMapper         // What does it map to?
OrderHelper         // Helper for what?
```

### Mapper Implementation Patterns

#### Interface vs Abstract Class
```java
// ✅ Prefer interface for simple mappers
@Mapper
public interface OrderDtoMapper {
    OrderDto toDto(Order order);
}

// ✅ Use abstract class for complex mapping logic
@Mapper
public abstract class OrderComplexMapper {
    
    @Mapping(target = "total", source = "subtotal")
    public abstract OrderDto toDto(Order order);
    
    // Additional methods with @AfterMapping
    @AfterMapping
    protected void enrichDto(Order order, @MappingTarget OrderDto dto) {
        dto.setDisplayStatus(formatStatus(order.getStatus()));
        dto.setCanBeCancelled(canCancel(order));
    }
    
    private String formatStatus(OrderStatus status) {
        return status.name().toLowerCase().replace("_", " ");
    }
}
```

### Handling Null Values

#### NullValueMappingStrategy
```java
@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL  // Default
)
public interface OrderDtoMapper {
    // Returns null if source is null
    OrderDto toDto(Order order);
}

// For returning default objects
@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface OrderDtoMapper {
    // Returns empty DTO if source is null
    OrderDto toDto(Order order);
}
```

## Lombok + MapStruct Integration

### Annotation Processor Order

**Critical**: Configure Maven compiler plugin with correct order (already in your pom.xml)

```xml
<annotationProcessorPaths>
    <!-- 1. Lombok first -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <!-- 2. MapStruct second -->
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${org.mapstruct.version}</version>
    </path>
    <!-- 3. Lombok-MapStruct binding last -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>${lombok-mapstruct-binding.version}</version>
    </path>
</annotationProcessorPaths>
```

### Using @Builder with MapStruct

#### Source has @Builder
```java
// Domain model with @Builder
@Builder
public class Order {
    private String orderNumber;
    private List<OrderItem> items;
    private Money total;
}

// MapStruct will use the builder
@Mapper
public interface OrderDtoMapper {
    // MapStruct detects @Builder and uses it automatically
    Order toDomain(OrderDto dto);
}
```

#### Target has @Builder
```java
// DTO with @Builder
@Data
@Builder
public class OrderDto {
    private String orderNumber;
    private List<OrderItemDto> items;
    private MoneyDto total;
}

// MapStruct will use the builder for creating DTOs
@Mapper
public interface OrderDtoMapper {
    OrderDto toDto(Order order);
}
```

## Clean Class Design with Lombok

### Immutable Domain Models

#### Value Objects Pattern
```java
// ✅ Perfect for value objects
@Value
@Builder
public class Money {
    @NonNull
    BigDecimal amount;
    
    @NonNull
    String currency;
    
    // Business method
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

### Entities with Controlled Mutability

#### JPA Entities Pattern
```java
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Not public
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Factory method instead
public class OrderEntity extends BaseEntity {
    
    private String orderNumber;
    private OrderStatus status;
    
    // Setter with validation
    public void setStatus(OrderStatus status) {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify completed order");
        }
        this.status = status;
    }
    
    // Business logic in entity
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING;
    }
}
```

### DTOs for Different Purposes

#### Request DTOs
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotEmpty
    private String customerId;
    
    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
    
    @Min(1)
    private Integer duration;
}
```

#### Response DTOs
```java
@Data
@Builder
public class OrderResponse {
    private String orderNumber;
    private OrderStatus status;
    private MoneyDto total;
    private List<OrderItemDto> items;
    private ZonedDateTime createdAt;
    
    // Computed fields
    @JsonProperty("canBeCancelled")
    public boolean getCanBeCancelled() {
        return status == OrderStatus.PENDING;
    }
}
```

## Project-Specific Best Practices

### For George API Project

#### Existing Patterns to Follow
```java
// ✅ Follow this pattern for new mappers
@Mapper
public interface {Domain}DtoMapper {
    {Domain}Dto toDto({Domain} domain);
    {Domain} toDomain({Domain}Dto dto);
    List<{Domain}Dto> toDtoList(List<{Domain}> domains);
}

// ✅ Use @Value for value objects in common/domain/model/
@Value
public class Characteristic {
    String name;
    String value;
}

// ✅ Use pragmatic Lombok approach for JPA entities
// For simple entities: @Data or @Getter is fine
// For complex entities with business rules: use explicit getters/setters
@Getter
@Entity
public class {Domain}Entity extends BaseEntity {
    // JPA-specific fields
}
```

#### Mapper Location
Place mappers in the same package as DTOs:
```
infrastructure/in/web/{domain}/mapper/
├── {Domain}DtoMapper.java
└── {Domain}Dto.java
```

#### Avoid Common Mistakes
- ❌ Don't use @Data on domain models in `domain/model/`
- ❌ Don't put business logic in mappers (use @AfterMapping sparingly)
- ❌ Don't create god mappers that handle too many types
- ✅ Keep mappers simple - one-way mapping per method
- ✅ Use composition for complex transformations
- ✅ Leverage MapStruct's automatic mapping where possible

## Testing Mappers

#### Unit Test Pattern
```java
@SpringBootTest
class OrderDtoMapperTest {
    
    @Autowired
    private OrderDtoMapper mapper;
    
    @Test
    void toDto_mapsAllFieldsCorrectly() {
        // Arrange
        Order order = Order.builder()
            .orderNumber("ORD-001")
            .status(OrderStatus.PENDING)
            .total(Money.of(BigDecimal.TEN, "USD"))
            .build();
        
        // Act
        OrderDto dto = mapper.toDto(order);
        
        // Assert
        assertEquals("ORD-001", dto.getOrderNumber());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(BigDecimal.TEN, dto.getTotal().getAmount());
    }
}
```

## When to Use This Skill

Invoke this skill when:
- Creating new mappers or DTOs
- Refactoring mapper logic
- Designing domain models with Lombok
- Reviewing mapper code quality
- Configuring MapStruct with Lombok integration
- Debugging mapper issues
- Making decisions about when to use Lombok annotations
- **Working with inheritance hierarchies in domain models**
- **Implementing polymorphic mapping with MapStruct**
- **Using abstract classes or interfaces as DTOs**
- **Need to map between subclasses and their DTOs**

## Recognizing When to Use Abstraction Patterns

### Red Flags That Indicate Need for Abstraction Patterns:

1. **Multiple similar DTOs with repeated fields:**
   ```java
   // ❌ Code smell - repeated fields
   record SmtpGatewayDto(String id, String name, String provider, ...) {}
   record GsmGatewayDto(String id, String name, String provider, ...) {}
   ```

2. **Manual type checking and casting:**
   ```java
   // ❌ Manual type routing - should use @SubclassMappings
   default GatewayDto toDto(Gateway gateway) {
       if (gateway instanceof SmtpGateway smtp) {
           return new GatewayDto(..., smtp.getHost(), null, ...);
       } else if (gateway instanceof GsmGateway gsm) {
           return new GatewayDto(..., null, gsm.getIpAddress(), ...);
       }
   }
   ```

3. **Separate mapper methods for each subtype:**
   ```java
   // ❌ Should use single method with @SubclassMappings
   SmtpGatewayDto toSmtpDto(SmtpGateway gateway);
   GsmGatewayDto toGsmDto(GsmGateway gateway);
   ```

4. **Complex factory methods:**
   ```java
   // ❌ Should use @ObjectFactory with pattern matching
   @ObjectFactory
   default Gateway toDomain(GatewayDto dto) {
       if (dto.type() == GatewayType.SMTP) {
           SmtpGateway gateway = new SmtpGateway();
           gateway.setHost(dto.host());
           // ... many more lines
           return gateway;
       }
   }
   ```

### When to Use Each Pattern:

**Use @SuperBuilder when:**
- You have inheritance hierarchies in domain models
- Subclasses need builder pattern
- Parent class has fields that need to be built
- You're working with MapStruct and polymorphic mapping

**Use @SubclassMappings when:**
- Mapping from abstract base class to DTOs
- Multiple subclasses map to same or different DTOs
- You want single method signature for polymorphic mapping
- You want MapStruct to handle type routing automatically

**Use @ObjectFactory when:**
- Reverse mapping (DTO to domain) with inheritance
- MapStruct can't determine concrete type to instantiate
- You need custom logic for object creation
- Working with sealed interfaces or abstract classes

**Use Single DTO with Nullable Fields when:**
- Subtypes have mostly similar fields
- Some fields are irrelevant for certain subtypes
- You want simple, maintainable DTO structure
- Using @SubclassMapping with concrete DTO type

## Anti-Patterns Checklist

Before committing mapper code, check:
- ❌ @Data on domain models with business invariants (should control state changes)
- ❌ Kitchen sink mappers (should be focused)
- ❌ Business logic in mappers (should be in domain/services)
- ❌ Not handling null values properly
- ❌ Complex custom logic that should be in services
- ❌ @Data on JPA entities with lazy collections (can cause issues)
- ❌ Using @Builder instead of @SuperBuilder in inheritance hierarchies
- ❌ Manual type routing instead of @SubclassMappings
- ❌ Sealed interfaces for DTOs with @SubclassMapping (MapStruct can't instantiate)
- ❌ Forgetting @EqualsAndHashCode(callSuper = true) on subclasses
- ❌ Complex nested DTOs when simple flat DTO with nullable fields would work
- ✅ Mappers are simple and focused
- ✅ @Value for immutable value objects
- ✅ @Data used pragmatically on simple entities
- ✅ Clean separation between layers
- ✅ Global componentModel configuration (no need to specify per mapper)
- ✅ @SuperBuilder for inheritance hierarchies
- ✅ @SubclassMappings for polymorphic mapping
- ✅ @ObjectFactory for reverse mapping with pattern matching
- ✅ Simple flat DTOs with nullable fields for inheritance scenarios
