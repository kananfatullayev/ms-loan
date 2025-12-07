# Feign Client Bean Creation Error - FIXED

## Problem
```
Error creating bean with name 'com.example.msloan.client.UserClient': 
FactoryBean threw exception on object creation
```

## Root Cause
Spring Cloud OpenFeign requires a load balancer implementation to work properly. In Spring Boot 4.0 with Spring Cloud 2025.1.0, the `spring-cloud-starter-loadbalancer` dependency is required.

## Solution
Added the Spring Cloud LoadBalancer dependency to `build.gradle`:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'  // <-- ADDED
    // ... other dependencies
}
```

## What is Spring Cloud LoadBalancer?
Spring Cloud LoadBalancer provides client-side load balancing capabilities. Even when calling a single service with a hardcoded URL, Feign uses the load balancer infrastructure internally for:
- Service discovery resolution
- Client-side routing
- Request distribution (when multiple instances exist)

## How It Works
1. **UserClient** interface is annotated with `@FeignClient(name = "ms-user")`
2. Spring creates a dynamic proxy for the interface
3. The proxy uses **LoadBalancer** to resolve the service URL
4. LoadBalancer looks up the URL from configuration (`feign.client.config.ms-user.url`)
5. HTTP request is made to the resolved URL

## Configuration
The Feign client is configured in `application.yml`:

```yaml
feign:
  client:
    config:
      ms-user:
        url: http://localhost:8080  # Direct URL for ms-user service
```

## After the Fix
1. Rebuild the project:
   ```bash
   ./gradlew clean build -x test
   ```

2. Start the application:
   ```bash
   ./gradlew bootRun
   ```

3. The UserClient bean should now be created successfully without errors.

## Verifying the Fix
You can verify the Feign client is working by:

1. Check application startup logs - should not have any bean creation errors
2. Inject UserClient in a service and use it:
   ```java
   @Service
   @RequiredArgsConstructor
   public class SomeService {
       private final UserClient userClient;
       
       public void test() {
           UserResponseDto user = userClient.getUserById(1);
           log.info("Got user: {}", user.getName());
       }
   }
   ```

## Alternative: Disable LoadBalancer (Not Recommended)
If you don't want to use LoadBalancer, you can use `@FeignClient(url = "...")` directly:

```java
@FeignClient(name = "ms-user", url = "http://localhost:8080", path = "/v1/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") Integer id);
}
```

This bypasses the load balancer, but you lose flexibility and features like service discovery.

## Summary
✅ **Added**: `spring-cloud-starter-loadbalancer` dependency  
✅ **Result**: Feign client beans created successfully  
✅ **Status**: Application should start without errors  

The Feign client is now ready to call the ms-user service!

