# Feign Client Without Load Balancer

## Why Load Balancer Was Needed

By default, Spring Cloud OpenFeign uses **Spring Cloud LoadBalancer** as the HTTP client implementation. Even when you specify a direct URL like `url = "http://localhost:8080"`, Feign still routes the request through the load balancer infrastructure.

### The Load Balancer Flow:
```
FeignClient → LoadBalancer Client → HTTP Request → Target Service
```

Without the load balancer dependency, Feign cannot create the HTTP client, resulting in:
```
BeanCreationException: Error creating bean with name 'UserClient'
```

## Solution: Use OkHttp Client Instead

Instead of adding the heavy load balancer dependency, we can use a lightweight HTTP client like **OkHttp** or **Apache HttpClient**.

### What Was Changed:

#### 1. Added OkHttp Dependency (`build.gradle`)
```groovy
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'io.github.openfeign:feign-okhttp'  // <-- ADDED
    // ... other dependencies
}
```

#### 2. Enabled OkHttp in Configuration (`application.yml`)
```yaml
feign:
  okhttp:
    enabled: true
```

### The New Flow:
```
FeignClient → OkHttp Client → HTTP Request → Target Service
```

## Benefits of Using OkHttp

✅ **No Load Balancer Required** - Lightweight solution  
✅ **Better Performance** - OkHttp is optimized for HTTP/2  
✅ **Connection Pooling** - Reuses connections efficiently  
✅ **Simpler** - No need for service discovery infrastructure  

## Alternative: Use Apache HttpClient

If you prefer Apache HttpClient over OkHttp:

**build.gradle:**
```groovy
implementation 'io.github.openfeign:feign-httpclient'
```

**application.yml:**
```yaml
feign:
  httpclient:
    enabled: true
```

## When You SHOULD Use Load Balancer

You need `spring-cloud-starter-loadbalancer` when:

1. **Service Discovery** - Using Eureka, Consul, or Kubernetes service discovery
2. **Multiple Instances** - Load balancing between multiple service instances
3. **Client-Side Load Balancing** - Need custom load balancing strategies
4. **Dynamic URLs** - Service URLs change at runtime

For simple microservice-to-microservice calls with hardcoded URLs, **OkHttp is sufficient**.

## Configuration Summary

### UserClient
```java
@FeignClient(name = "ms-user", url = "${client.ms-user.url}")
public interface UserClient {
    @GetMapping("/v1/users/{id}")
    UserResponseDto getUserById(@PathVariable("id") Integer id);
}
```

### application.yml
```yaml
client:
  ms-user:
    url: http://localhost:8080

feign:
  okhttp:
    enabled: true
```

### build.gradle
```groovy
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
implementation 'io.github.openfeign:feign-okhttp'
```

## Testing

After rebuilding, start your application:
```bash
./gradlew bootRun
```

The application should start without the bean creation error, and the UserClient will work using OkHttp.

## Comparison

| Approach | Pros | Cons | Use Case |
|----------|------|------|----------|
| **LoadBalancer** | Full Spring Cloud integration, service discovery | Heavy, more complex | Microservices with discovery |
| **OkHttp** | Lightweight, fast, simple | No load balancing features | Direct service-to-service calls |
| **HttpClient** | Stable, well-tested | Slightly slower than OkHttp | Enterprise apps, legacy systems |

## Summary

✅ **Removed**: `spring-cloud-starter-loadbalancer`  
✅ **Added**: `feign-okhttp`  
✅ **Configured**: `feign.okhttp.enabled=true`  
✅ **Result**: Feign client works without load balancer dependency  

Your application is now lighter and simpler while maintaining the same Feign client functionality!

