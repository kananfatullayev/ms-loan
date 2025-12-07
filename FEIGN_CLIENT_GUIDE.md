# Feign Client Configuration

## User Service Client

The loanEntity service now includes a Feign client to communicate with the User service (ms-user).

## Client Details

### UserClient Interface
**Location**: `src/main/java/com/example/msloan/client/UserClient.java`

```java
@FeignClient(name = "ms-user", path = "/v1/users")
public interface UserClient {
    
    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") Integer id);
}
```

### Configuration

**Service URL**: `http://localhost:8080`  
**Base Path**: `/v1/users`

The Feign client is configured in `application.yml`:

```yaml
feign:
  client:
    config:
      ms-user:
        url: http://localhost:8080
```

## User Response DTO

**Location**: `src/main/java/com/example/msloan/client/dto/UserResponseDto.java`

Fields:
- `id` - User ID
- `name` - User's first name
- `surname` - User's last name
- `email` - User's email address
- `phone` - User's phone number
- `createdAt` - User creation timestamp
- `updatedAt` - User last update timestamp

## Usage Example

To use the UserClient in your service:

```java
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    
    private final LoanRepository loanRepository;
    private final UserClient userClient;
    
    public LoanResponseDto createLoan(LoanRequestDto request) {
        // Validate user exists before creating loanEntity
        UserResponseDto user = userClient.getUserById(request.getUserId());
        
        // Create loanEntity
        Loan loanEntity = Loan.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .monthlyAmount(request.getMonthlyAmount())
                .duration(request.getDuration())
                .build();
        
        Loan savedLoanEntity = loanRepository.save(loanEntity);
        return mapToResponseDto(savedLoanEntity);
    }
}
```

## Testing the Client

### Prerequisites
1. Make sure ms-user service is running on port 8080
2. User with ID 1 should exist in ms-user service

### Test with cURL

**Call ms-user directly**:
```bash
curl -X 'GET' \
  'http://localhost:8080/v1/users/1' \
  -H 'accept: */*'
```

**Expected Response**:
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "createdAt": "2025-12-07T11:20:22.46302",
  "updatedAt": "2025-12-07T11:20:22.463062"
}
```

## Error Handling

You can add error handling for Feign client calls:

```java
try {
    UserResponseDto user = userClient.getUserById(userId);
    // Process user
} catch (FeignException.NotFound e) {
    throw new RuntimeException("User not found with id: " + userId);
} catch (FeignException e) {
    throw new RuntimeException("Error communicating with user service: " + e.getMessage());
}
```

## Configuration Options

You can customize Feign client behavior in `application.yml`:

```yaml
feign:
  client:
    config:
      ms-user:
        url: http://localhost:8080
        connectTimeout: 5000      # Connection timeout in milliseconds
        readTimeout: 5000         # Read timeout in milliseconds
        loggerLevel: full         # Logging level: none, basic, headers, full
```

## Service Discovery (Optional)

If using service discovery (Eureka, Consul, etc.), you can remove the URL and let it discover automatically:

```yaml
feign:
  client:
    config:
      ms-user:
        # url: http://localhost:8080  # Remove this line
        connectTimeout: 5000
        readTimeout: 5000
```

The Feign client will then use the service name "ms-user" to look up the service from the registry.

## Troubleshooting

### Issue: "Load balancer does not contain an instance for the service ms-user"
**Solution**: Make sure the URL is configured in application.yml or the service is registered in service discovery.

### Issue: "Connection refused"
**Solution**: Ensure ms-user service is running on the configured URL (http://localhost:8080).

### Issue: "404 Not Found"
**Solution**: Verify the path is correct. The full URL will be: `{url}{path}/{id}` = `http://localhost:8080/v1/users/1`

### Issue: "Feign client method not found"
**Solution**: Ensure `@EnableFeignClients` is present on the main application class.

