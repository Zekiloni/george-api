## Code Fromatting

- Indentation: 4 spaces.
- Blank Lines: Use to separate logical blocks of code.
- Line Length: Maximum 120 characters.
- Use IntelliJ IDEA default code style for Java.

## Functional Programming
- Use Stream API for collection processing.
- Use `Optional` to handle nullable values instead of null checks.
- Use method references and lambda expressions for cleaner code.
- Avoid side effects in lambda expressions; prefer pure functions.
- Use `Collectors` for grouping, partitioning, and summarizing data in streams.
- Prefer `map` and `flatMap` for transforming data in streams instead of `forEach` when possible.
- Use `filter` to exclude unwanted elements from streams instead of conditional logic inside `forEach`.
- Use `reduce` for aggregating stream elements when appropriate, but prefer `collect` for most cases.
- Avoid using `forEach` for stream processing when the intention is to transform data; use `map` instead.
- Use `peek` for debugging purposes in streams, but avoid it for production code.
- Use `Stream.ofNullable()` to handle potential null collections gracefully.

## Coding Conventions
- Follow standard Java naming conventions: classes in PascalCase, methods and variables in camelCase, constants in UPPER_SNAKE_CASE.
- Use `final` keyword for method parameters and local variables to indicate immutability.
- Avoid using `this` keyword unless necessary for disambiguation.
- Use `@Override` annotation when overriding methods to improve readability and catch errors.
- Avoid using magic numbers and strings; use constants instead.
- Use meaningful variable and method names that clearly indicate their purpose.
- Avoid deep nesting of code; prefer early returns to reduce complexity.
- Use try-with-resources for managing resources like streams and database connections to ensure proper cleanup.

## Hexagonal Architecture
- Separate code into three layers: Domain (core business logic), Application (use cases), and Infrastructure (adapters).
- Domain layer should be independent of any frameworks or external libraries.
- Application layer should orchestrate use cases and depend on domain interfaces.
- Infrastructure layer should implement the interfaces defined in the application layer and contain all framework-specific code (e.g., Spring annotations, database access).
- Use ports and adapters to decouple the layers and allow for easier testing and maintenance.
- Avoid circular dependencies between layers; dependencies should flow inward towards the domain layer.
- Use dependency injection to manage dependencies between layers, preferably through constructor injection.
- Keep controllers thin by delegating business logic to the application layer and domain layer, ensuring that controllers only handle HTTP requests and responses.

## Naming Conventions

- Domain entities should be named as nouns (e.g., `User`, `Order`).
- Service classes should be named with a verb followed by "Service" (e.g., `UserService`, `OrderProcessingService`).
- Repository interfaces should be named with the entity name followed by "Repository" (e.g., `UserRepository`, `OrderRepository`).
- DTO classes should be named with the entity name followed by "DTO" (e.g., `UserDTO`, `OrderDTO`).
- Mapper classes should be named with the entity name followed by "Mapper" (e.g., `UserMapper`, `OrderMapper`).
- Use clear and descriptive names for methods that indicate their purpose (e.g., `createUser`, `getUserById`, `updateOrderStatus`).
- Avoid abbreviations and acronyms in names unless they are widely understood (e.g., `ID`, `URL`).
- Use consistent naming conventions across the codebase to improve readability and maintainability.


## Design Principles

- **Single Responsibility Principle**: Each class should have only one reason to change.
- **Open/Closed Principle**: Classes should be open for extension but closed for modification.
- **Liskov Substitution Principle**: Subtypes must be substitutable for their base types.
- **Interface Segregation Principle**: Clients should not be forced to depend on interfaces they do not use.
- **Dependency Inversion Principle**: High-level modules should not depend on low-level modules; both should depend on abstractions.
- **Don't Repeat Yourself (DRY)**: Avoid code duplication by abstracting common functionality.
- **Keep It Simple, Stupid (KISS)**: Prefer simple solutions over complex ones.
- **YAGNI (You Aren't Gonna Need It)**: Don't implement functionality until it's necessary.
- **Law of Demeter**: A class should only interact with its immediate collaborators and not with the internals of other classes.
- **Composition over Inheritance**: Favor composition of objects over class inheritance to achieve code reuse and flexibility.

## Design Patterns

- Use design patterns where appropriate to solve common problems and improve code maintainability.
- **Factory Pattern**: For creating complex objects or encapsulating object creation logic.
- **Strategy Pattern**: For defining a family of algorithms and making them interchangeable.
- **Repository Pattern**: For abstracting data access and providing a collection-like interface for accessing domain objects.
- **Adapter Pattern**: For converting the interface of a class into another interface that clients expect, especially when integrating with external systems or libraries.
- **Observer Pattern**: For implementing event-driven architectures and allowing objects to be notified of changes in other objects.
- **Decorator Pattern**: For adding behavior to objects dynamically without affecting other objects of the same class.
- **Template Method Pattern**: For defining the skeleton of an algorithm in a method, deferring some steps to subclasses.
- Avoid overusing design patterns; use them when they provide clear benefits in terms of code clarity and maintainability, and avoid using them just for the sake of using patterns.
- When using design patterns, ensure that they are implemented correctly and consistently across the codebase to avoid confusion and maintain readability.

## Java Style

- Use UTF-8 encoding.
- Use descriptive names for classes, methods, and variables.
- Avoid `var` keyword, prefer explicit types.
- All method parameters should be `final`.
- All variables should be declared as `final` where possible.
- Preference for immutability:
- Avoid mutations of objects, specially when using for-each loops or Stream API using `forEach()`.
- Avoid magic numbers and strings; use constants instead.
- Check emptiness and nullness before operations on collections and strings.
- Avoid methods using `throws` clause; prefer unchecked exceptions.

- Avoid comments.
- Comments could be applied for: cron expressions, Regex patterns, TODOs or given/when/then separation in tests.
- Use `@Override` annotation when overriding methods.
- Avoid Objects.*isNull() and Objects.*nonNull() for one or two variables; prefer direct null checks for better performance.
- Wrap multiple conditions in a boolean variable for better readibility
- Prefer early returns.
- Avoid else statements when not necessary and try early returns.

## Lombok Annotations

- Use `@RequiredArgsConstructor` from Lombok for dependency injection via constructor.
- Use `@Slf4j` from Lombok for logging.
- Use `@Builder(setterPrefix = "with"))` for complex object creation.
- Avoid `@Data` annotation; prefer `@Getter` and `@Setter` for granular control.

## Annotations

- **`@Service`**: For business logic classes.
- **`@Repository`**: For data access classes that extend JPA repositories or interact with the database.
- **`@RestController`**: For web controllers.
- **`@Component`**: For generic Spring components.
- **`@Configuration`**: For Spring configuration classes.
- **`@Autowired`**: Prefer constructor injection for production code and field injection only for tests.
- **`@ConfigurationProperties`**: For binding related properties avoid multiple `@Value` annotations. From more than 2 properties, consider using this annotation.
- **`@Transactional`**: Only Service classes should be annotated with @Transactional at class level to avoid transaction management in each method.
- **`@Validated`**: To enable Bean Validation in method parameters or classes.
- **`@PreAuthorize`**: at the controller layer when using Spring Security to enforce method-level security.
- Circular dependencies should be avoided. Avoid `@Order` annotation for dependency resolution.

## Mappers(As a development team choose MapStruct or strictly static Mappers)

**Use MapStruct**

- MapFor mapping between DTOs and entities.
- Define mapper interfaces with `@Mapper` annotation.
- Use `@Mapping` annotation for custom field mappings.
- Use `componentModel = "spring"` to allow Spring to manage mapper instances.
- Mapper should have as suffix `Mapper` (e.g., `UserMapper`).
- Name mapper methods clearly (e.g., `toDto`, `toEntity`).
- Example Mapper Interface:

  ```java
  @Mapper(componentModel = "spring")
  public interface UserMapper {
      @Mapping(source = "email", target = "emailAddress")
      UserDTO toDto(User user);
      @Mapping(source = "emailAddress", target = "email")
      User toEntity(UserDTO userDto);
  }
  ```

- For testing mappers, use `Mappers.getMapper(UserMapper.class)` to get an instance of the mapper.

**Use Static Mappers**

- Define a private constructor to prevent instantiation with `UnsupportedOperationException("This class should never be instantiated")`.
- Use static methods for mapping between DTOs and entities.
- Name mapper methods clearly (e.g., `toDto`, `toEntity`).
- Example Static Mapper Class:

  ```java
  public class UserMapper {
      private UserMapper() {
          throw new UnsupportedOperationException("This class should never be instantiated");
      }
      public static UserDTO toDto(final User user) {
          if (user == null) {
              return null;
          }
          return UserDTO.builder()
              .withId(user.getId())
              .withEmailAddress(user.getEmail())
              .build();
      }
      public static User toEntity(final UserDTO userDto) {
          if (userDto == null) {
              return null;
          }
          return User.builder()
              .withId(userDto.getId())
              .withEmail(userDto.getEmailAddress())
              .build();
      }
  }
  ```

## Exception Handling

- Custom Exceptions: Create custom domain exception classes extending `RuntimeException`.
- Global Exception Handler: Use `@ControllerAdvice` and `@ExceptionHandler` to handle exceptions globally.
- HTTP Status Codes: Map exceptions to appropriate HTTP status codes in REST controllers.
- Error Response Structure: Define a consistent error response structure

## Testing

- Use JUnit 5 for unit and integration testing.
- Use Mockito for mocking dependencies in unit tests.
- Use `@WebMvcTest(ControllerClass.class)` for testing Spring MVC controllers.
- Use `@SpringBootTest` for integration tests that require the Spring context.
- Use `given/when/then` structure in test methods for clarity.
- Method naming could follow snake_case or camelCaset convention for test methods (e.g., `get_user_by_id_ok`, `get_user_by_id_not_found_ko`).
- Avoid reflection in tests.
- Avoid business logic in tests; focus on behavior verification.

## Logging

- Use `@Slf4j` annotation from Lombok for logging to avoid boilerplate code with Logger instances.
- Log at appropriate levels: `DEBUG`, `INFO`, `WARN`, `ERROR`.
- Include contextual information in logs (e.g., request IDs, user IDs).
- Avoid logging sensitive information.
- Use structured logging for better log management.
- Format log messages with placeholders (e.g., `{}`) instead of string concatenation.
- Logging info code could follow this template: log.info("[MicroserviceName/ModuleName] - API-CALL/METHOD/ACTION: response: {}, userId: {}", body, userId);
- Logging error code could follow this template: log.error("[MicroserviceName/ModuleName] - API-CALL/METHOD/ACTION: errorMessage: {}, userId: {}", errorMessage, userId);