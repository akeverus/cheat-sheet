# API Дизайн и Протоколы

Комплексное руководство по разработке и интеграции API: REST, GraphQL, gRPC, WebSocket. Дизайн, безопасность, тестирование и best practices.

## REST API

### RESTful Дизайн
- **HTTP Methods** - GET, POST, PUT, DELETE, PATCH
- **Resource Modeling** - URI design, resource relationships
- **HTTP Status Codes** - 2xx, 4xx, 5xx responses
- **Content Negotiation** - Accept/Content-Type headers
- **HATEOAS** - Hypermedia as the Engine of Application State

**REST принципы:**
- Stateless communication
- Cacheable responses
- Uniform interface
- Layered system architecture
- Client-server separation

### REST API Patterns

#### Resource-Based URLs
```http
# Good REST URLs
GET    /api/v1/users          # List users
GET    /api/v1/users/123      # Get specific user
POST   /api/v1/users          # Create user
PUT    /api/v1/users/123      # Update user
DELETE /api/v1/users/123      # Delete user

# Nested resources
GET    /api/v1/users/123/orders     # User's orders
POST   /api/v1/users/123/orders     # Create order for user

# Filtering and pagination
GET    /api/v1/users?page=2&limit=10&sort=name&filter[status]=active
```

#### HTTP Methods Usage
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {

        Page<User> users = userService.getUsers(PageRequest.of(page, size, Sort.by(sort)));
        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(users.getTotalElements()))
            .body(users.getContent());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();

        return ResponseEntity.created(location).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

## GraphQL

### Schema Definition
- **Type System** - Object types, scalars, enums
- **Queries** - Read operations
- **Mutations** - Write operations
- **Subscriptions** - Real-time updates
- **Resolvers** - Data fetching logic

**GraphQL преимущества:**
- Single endpoint для всех операций
- Client-driven data fetching
- Strongly typed schema
- Introspective API
- Version-free API evolution

### GraphQL Schema Design

#### Schema Definition
```graphql
# GraphQL Schema
type User {
    id: ID!
    email: String!
    name: String!
    orders: [Order!]!
    createdAt: DateTime!
}

type Order {
    id: ID!
    user: User!
    items: [OrderItem!]!
    total: Float!
    status: OrderStatus!
}

type Query {
    users(limit: Int, offset: Int): [User!]!
    user(id: ID!): User
    orders(userId: ID!): [Order!]!
}

type Mutation {
    createUser(input: CreateUserInput!): User!
    updateUser(id: ID!, input: UpdateUserInput!): User!
    createOrder(userId: ID!, input: CreateOrderInput!): Order!
}

type Subscription {
    userCreated: User!
    orderStatusChanged(orderId: ID!): Order!
}

enum OrderStatus {
    PENDING
    CONFIRMED
    SHIPPED
    DELIVERED
    CANCELLED
}

input CreateUserInput {
    email: String!
    name: String!
    password: String!
}

input UpdateUserInput {
    name: String
    email: String
}
```

#### Spring GraphQL Implementation
```java
@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(UserService userService) {
        return wiringBuilder -> wiringBuilder
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("users", env -> {
                    Integer limit = env.getArgument("limit");
                    Integer offset = env.getArgument("offset");
                    return userService.getUsers(limit != null ? limit : 10,
                                              offset != null ? offset : 0);
                })
                .dataFetcher("user", env -> {
                    String id = env.getArgument("id");
                    return userService.getUserById(Long.valueOf(id));
                }))
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createUser", env -> {
                    Map<String, Object> input = env.getArgument("input");
                    return userService.createUser(
                        new CreateUserRequest(
                            (String) input.get("email"),
                            (String) input.get("name"),
                            (String) input.get("password")
                        ));
                }));
    }
}
```

## gRPC

### Protocol Buffers
- **Message Definition** - proto3 syntax
- **Service Definition** - RPC methods
- **Streaming** - Unary, server, client, bidirectional
- **Code Generation** - Multi-language support

**gRPC особенности:**
- HTTP/2 transport
- Binary serialization (Protocol Buffers)
- Bi-directional streaming
- Built-in authentication
- High performance

### gRPC Service Definition

#### Proto File
```protobuf
syntax = "proto3";

package ecommerce.v1;

option java_multiple_files = true;
option java_package = "com.example.ecommerce.v1";
option java_outer_classname = "EcommerceProto";

service UserService {
    rpc GetUser(GetUserRequest) returns (User);
    rpc ListUsers(ListUsersRequest) returns (stream User);
    rpc CreateUser(CreateUserRequest) returns (User);
    rpc UpdateUser(UpdateUserRequest) returns (User);
    rpc DeleteUser(DeleteUserRequest) returns (google.protobuf.Empty);

    // Bidirectional streaming
    rpc Chat(stream ChatMessage) returns (stream ChatMessage);
}

service OrderService {
    rpc CreateOrder(CreateOrderRequest) returns (Order);
    rpc GetOrder(GetOrderRequest) returns (Order);
    rpc UpdateOrderStatus(UpdateOrderStatusRequest) returns (Order);
    rpc StreamOrderUpdates(StreamOrderUpdatesRequest) returns (stream OrderUpdate);
}

message User {
    int64 id = 1;
    string email = 2;
    string name = 3;
    repeated Order orders = 4;
    google.protobuf.Timestamp created_at = 5;
}

message Order {
    int64 id = 1;
    int64 user_id = 2;
    repeated OrderItem items = 3;
    double total = 4;
    OrderStatus status = 5;
    google.protobuf.Timestamp created_at = 6;
}

enum OrderStatus {
    ORDER_STATUS_UNSPECIFIED = 0;
    ORDER_STATUS_PENDING = 1;
    ORDER_STATUS_CONFIRMED = 2;
    ORDER_STATUS_SHIPPED = 3;
    ORDER_STATUS_DELIVERED = 4;
    ORDER_STATUS_CANCELLED = 5;
}

message GetUserRequest {
    int64 user_id = 1;
}
```

#### gRPC Service Implementation
```java
@Service
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
        try {
            UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

            User user = User.newBuilder()
                .setId(userEntity.getId())
                .setEmail(userEntity.getEmail())
                .setName(userEntity.getName())
                .setCreatedAt(Timestamps.fromMillis(userEntity.getCreatedAt().toEpochMilli()))
                .build();

            responseObserver.onNext(user);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("User not found")
                .asRuntimeException());
        }
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<User> responseObserver) {
        userRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
            .forEach(userEntity -> {
                User user = convertToProto(userEntity);
                responseObserver.onNext(user);
            });
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                // Process incoming message
                ChatMessage response = ChatMessage.newBuilder()
                    .setUserId(message.getUserId())
                    .setMessage("Echo: " + message.getMessage())
                    .setTimestamp(Timestamps.now())
                    .build();

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
```

## WebSocket

### WebSocket Protocols
- **Basic WebSocket** - Full-duplex communication
- **STOMP over WebSocket** - Message-oriented protocol
- **Socket.IO** - Real-time framework
- **Connection Management** - Heartbeats, reconnection

**WebSocket use cases:**
- Real-time dashboards
- Chat applications
- Collaborative editing
- Live notifications
- Gaming

### Spring WebSocket Implementation

#### STOMP Configuration
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker для in-memory messaging
        config.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix
        config.setApplicationDestinationPrefixes("/app");

        // Set user destination prefix
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS(); // Fallback для браузеров без WebSocket

        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000");
    }
}
```

#### WebSocket Controller
```java
@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                             SimpMessageHeaderAccessor headerAccessor) {
        // Add username to WebSocket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload PrivateMessage privateMessage) {
        messagingTemplate.convertAndSendToUser(
            privateMessage.getReceiver(), "/queue/reply", privateMessage);
    }
}
```

## API Security

### Authentication Methods

#### JWT Authentication
```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

#### OAuth2 Implementation
```java
@Configuration
public class OAuth2Config {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter =
            new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
```

### Authorization Patterns

#### Role-Based Access Control
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public User assignRole(@PathVariable Long userId, @RequestBody RoleAssignment role) {
        return userService.assignRole(userId, role.getRoleName());
    }
}
```

#### Attribute-Based Access Control
```java
@Component
public class OrderSecurityService {

    public boolean canAccessOrder(User user, Order order) {
        // User can access their own orders
        if (order.getUserId().equals(user.getId())) {
            return true;
        }

        // Admin can access all orders
        if (user.getRoles().contains("ADMIN")) {
            return true;
        }

        // Support can access orders from last 30 days
        if (user.getRoles().contains("SUPPORT")) {
            return order.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30));
        }

        return false;
    }
}

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        User currentUser = getCurrentUser();

        if (!orderSecurityService.canAccessOrder(currentUser, order)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order);
    }
}
```

## API Testing

### REST API Testing

#### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("john@example.com", "John Doe");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andDo(print());
    }

    @Test
    public void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(print());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Jane Doe");

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andDo(print());
    }
}
```

### GraphQL Testing

#### GraphQL Test Client
```java
@SpringBootTest
public class GraphQLTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    public void testGetUsers() throws Exception {
        GraphQLResponse response = graphQLTestTemplate.postForResource("queries/getUsers.graphql");

        assertThat(response.isOk()).isTrue();
        assertThat(response.get("$.data.users")).isNotNull();
        assertThat(response.get("$.data.users[0].email")).isNotNull();
    }

    @Test
    public void testCreateUser() throws Exception {
        GraphQLResponse response = graphQLTestTemplate.perform(
            "mutation($input: CreateUserInput!) { createUser(input: $input) { id email name } }",
            Map.of("input", Map.of(
                "email", "test@example.com",
                "name", "Test User",
                "password", "password123"
            ))
        );

        assertThat(response.isOk()).isTrue();
        assertThat(response.get("$.data.createUser.email")).isEqualTo("test@example.com");
    }
}
```

### gRPC Testing

#### gRPC Integration Test
```java
@SpringBootTest
@Testcontainers
public class GrpcTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    @Test
    public void testGetUser() {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setUserId(1)
            .build();

        User response = userService.getUser(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getEmail()).isNotEmpty();
        assertThat(response.getName()).isNotEmpty();
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest request = CreateUserRequest.newBuilder()
            .setEmail("grpc@example.com")
            .setName("gRPC User")
            .build();

        User response = userService.createUser(request);

        assertThat(response.getId()).isGreaterThan(0);
        assertThat(response.getEmail()).isEqualTo("grpc@example.com");
    }
}
```

## API Documentation

### OpenAPI/Swagger

#### Spring Boot OpenAPI Configuration
```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-commerce API")
                .version("1.0.0")
                .description("REST API for E-commerce platform")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(Arrays.asList(
                new Server().url("http://localhost:8080").description("Development server"),
                new Server().url("https://api.example.com").description("Production server")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

#### REST Controller with OpenAPI
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<User>> getUsers(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        // Implementation
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully",
        content = @Content(schema = @Schema(implementation = User.class)))
    public ResponseEntity<User> createUser(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User data", required = true,
                content = @Content(schema = @Schema(implementation = CreateUserRequest.class)))
            CreateUserRequest request) {

        // Implementation
    }
}
```

### GraphQL Documentation

#### GraphQL Schema Documentation
```graphql
"""
Represents a user in the system.
Users can have multiple orders and can be authenticated.
"""
type User {
    "Unique identifier for the user"
    id: ID!

    "User's email address - must be unique"
    email: String!

    "User's full name"
    name: String!

    "List of orders placed by this user"
    orders: [Order!]!

    "When the user account was created"
    createdAt: DateTime!
}

"""
Input type for creating a new user.
All fields are required for user registration.
"""
input CreateUserInput {
    "Valid email address for the user"
    email: String!

    "Full name of the user"
    name: String!

    "Secure password (min 8 characters)"
    password: String!
}
```

## API Versioning

### URI Versioning
```java
@RestController
@RequestMapping("/api")
public class VersionedController {

    @GetMapping("/v1/users")
    public List<UserV1> getUsersV1() {
        return userService.getUsersV1();
    }

    @GetMapping("/v2/users")
    public List<UserV2> getUsersV2() {
        return userService.getUsersV2();
    }
}
```

### Header Versioning
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<?>> getUsers(
            @RequestHeader(value = "X-API-Version", defaultValue = "1") String version) {

        if ("2".equals(version)) {
            return ResponseEntity.ok(userService.getUsersV2());
        } else {
            return ResponseEntity.ok(userService.getUsersV1());
        }
    }
}
```

### Content Negotiation
```java
@Configuration
public class ContentNegotiationConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer
                    .favorParameter(true)
                    .parameterName("mediaType")
                    .ignoreAcceptHeader(false)
                    .useRegisteredExtensionsOnly(false)
                    .defaultContentType(MediaType.APPLICATION_JSON)
                    .mediaType("json", MediaType.APPLICATION_JSON)
                    .mediaType("xml", MediaType.APPLICATION_XML);
            }
        };
    }
}
```

## Performance Optimization

### Caching Strategies

#### HTTP Caching
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/{id}")
    @Cacheable(value = "products", key = "#id")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);

        // HTTP cache headers
        CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.MINUTES)
            .mustRevalidate();

        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .lastModified(product.getLastModified())
            .body(product);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "products", key = "#id")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {

        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
}
```

#### Database Query Optimization
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.status = :status")
    Page<User> findByStatus(@Param("status") Status status, Pageable pageable);

    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "50"))
    @Query("SELECT u FROM User u")
    Stream<User> findAllAsStream();
}
```

### Rate Limiting

#### Bucket4j Implementation
```java
@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket createNewBucket() {
        return Bucket4j.builder()
            .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1))) // 10 requests per minute
            .build();
    }
}

@RestController
@RequestMapping("/api")
public class RateLimitedController {

    @Autowired
    private Bucket bucket;

    @GetMapping("/limited")
    public ResponseEntity<String> limitedEndpoint() {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok("Request successful");
        } else {
            return ResponseEntity.status(429)
                .header("X-Rate-Limit-Retry-After-Seconds", "60")
                .body("Too many requests");
        }
    }
}
```

## Monitoring и Observability

### Metrics Collection

#### Micrometer Metrics
```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "api-gateway")
            .commonTags("version", "1.0.0");
    }
}

@RestController
@RequestMapping("/api/users")
@Timed("user.controller")
public class UserController {

    private final Counter userCreatedCounter;
    private final Timer userQueryTimer;

    public UserController(MeterRegistry registry) {
        this.userCreatedCounter = registry.counter("user.created");
        this.userQueryTimer = registry.timer("user.query");
    }

    @PostMapping
    @Timed(value = "user.create", description = "Time taken to create user")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        Timer.Sample sample = Timer.start();

        try {
            User user = userService.createUser(request);
            userCreatedCounter.increment();

            return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri()
            ).body(user);
        } finally {
            sample.stop(userQueryTimer);
        }
    }

    @GetMapping("/{id}")
    @Timed(value = "user.get", description = "Time taken to get user")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }
}
```

### Distributed Tracing

#### Spring Cloud Sleuth
```java
@Configuration
public class TracingConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

    @NewSpan("validateUser")
    public boolean validateUser(@SpanTag("userId") Long userId) {
        // This method will be traced
        return userRepository.existsById(userId);
    }

    @NewSpan("callExternalService")
    public ExternalUserData getExternalUserData(Long userId) {
        return webClient.get()
            .uri("http://external-service/users/{id}", userId)
            .retrieve()
            .bodyToMono(ExternalUserData.class)
            .block();
    }
}
```

## Best Practices

### API Design Principles

#### 1. Consistency
```java
// Consistent error response format
public class ErrorResponse {
    private String code;
    private String message;
    private List<String> details;
    private Instant timestamp;

    // Constructor, getters, setters
}

// Consistent pagination format
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    // Constructor, getters, setters
}
```

#### 2. Backward Compatibility
```java
// Versioned DTOs
public class UserV1 {
    private Long id;
    private String email;
    private String name;
    // Basic fields
}

public class UserV2 {
    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private List<String> roles; // New fields
}
```

#### 3. Error Handling
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            Collections.singletonList(ex.getResourceName())
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            ex.getErrors()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Security Best Practices

#### 4. Input Validation
```java
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email should not exceed 100 characters")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;

    // Getters, setters, constructor
}
```

#### 5. CORS Configuration
```java
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "https://myapp.com")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

### Performance Best Practices

#### 6. Response Compression
```java
@Configuration
public class CompressionConfig {

    @Bean
    public GzipFilterDeflateCompressionFilter compressionFilter() {
        return new GzipFilterDeflateCompressionFilter();
    }
}

// Or with Spring Boot properties
# application.yml
server:
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
    min-response-size: 1024
```

#### 7. Connection Pooling
```java
@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        // Configure connection pooling
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build();

        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }
}
```

---

**Категория:** API Design
**Протоколы:** HTTP/1.1, HTTP/2, WebSocket
**Форматы:** JSON, XML, Protocol Buffers
**Безопасность:** JWT, OAuth2, API Keys
**Уровень:** Продвинутый
