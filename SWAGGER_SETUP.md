# Swagger Configuration Guide

## Current Setup

Swagger/OpenAPI has been added to your Loan Management API using SpringDoc OpenAPI.

## Access URLs

After starting the application on port 8081:

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/api-docs

## If You Get 500 Error

The 500 error when accessing `/api-docs` can be caused by several issues. Here are the solutions:

### Solution 1: Check Database Connection

Make sure PostgreSQL is running and the database exists:

```bash
# Check if PostgreSQL is running
psql -U postgres -c "SELECT version();"

# Create database if it doesn't exist
psql -U postgres -c "CREATE DATABASE loanEntity;"
```

The application needs a valid database connection to start properly, even though Swagger doesn't directly use the database.

### Solution 2: Verify Dependencies

Make sure the build completed successfully:

```bash
./gradlew clean build -x test
```

If there are any dependency issues, they will show up here.

### Solution 3: Check Application Logs

Start the application and check the logs for any errors:

```bash
./gradlew bootRun
```

Look for any exceptions in the console output, especially:
- Database connection errors
- Bean creation errors
- SpringDoc initialization errors

### Solution 4: Temporary Workaround

If the issue persists, you can temporarily disable JPA auto-configuration for testing Swagger:

Add this to `application.yml`:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

**Note**: This will disable database functionality, so only use it to test if Swagger works without the database.

### Solution 5: Update SpringDoc Version

If you're having compatibility issues with Spring Boot 4.0, try different SpringDoc versions in `build.gradle`:

```groovy
// Try version 2.7.0
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'

// Or version 2.6.0
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
```

Then rebuild:
```bash
./gradlew clean build -x test
```

## Configuration Files

### application.yml
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
```

### OpenApiConfig.java
Located at: `src/main/java/com/example/msloan/config/OpenApiConfig.java`

This file configures:
- API title and description
- Version information
- Contact details
- License information
- Server URLs

## Testing Swagger

### Step 1: Start the Application
```bash
./gradlew bootRun
```

### Step 2: Wait for Startup
Look for this line in the logs:
```
Started MsLoanApplication in X.XXX seconds
```

### Step 3: Access Swagger UI
Open browser: http://localhost:8081/swagger-ui/index.html

### Step 4: Test API Endpoint
You should see:
- **Loan Management** section
- All 6 endpoints listed
- Try the "GET /api/v1/loans" endpoint

## Common Issues and Solutions

### Issue: "Failed to load API definition"
**Cause**: Application not fully started or database connection issue
**Solution**: Check application logs for errors

### Issue: "Fetch error response status is 500"
**Cause**: Usually a database connection error or bean initialization failure
**Solution**: 
1. Verify PostgreSQL is running
2. Check database credentials in application.yml
3. Ensure database 'loanEntity' exists

### Issue: "404 Not Found"
**Cause**: Wrong URL or application not running
**Solution**: 
- Use http://localhost:8081/swagger-ui/index.html (not swagger-ui.html directly)
- Verify server.port in application.yml

### Issue: Endpoints not showing
**Cause**: Controller not being scanned
**Solution**: Ensure @RestController annotation is present on LoanController

## Verifying Setup

Run this command to verify the application starts correctly:

```bash
./gradlew bootRun 2>&1 | tee startup.log
```

Then check the log file for any errors.

## Alternative: Test with cURL

If Swagger UI doesn't work, you can still test if the API endpoints work:

```bash
# Test if application is running
curl http://localhost:8081/api/v1/loans

# Test OpenAPI JSON endpoint
curl http://localhost:8081/api-docs
```

If these work, the issue is specifically with Swagger UI rendering.

## Quick Diagnostic Commands

```bash
# Check if application is running
lsof -i :8081

# Check PostgreSQL is running
pg_isready

# Test database connection
psql -U postgres -d loanEntity -c "SELECT 1;"

# View recent application logs
tail -f build/logs/spring.log
```

## Need Help?

If the issue persists:
1. Share the full error stack trace from the application logs
2. Run: `./gradlew bootRun > app.log 2>&1` and share app.log
3. Check if the database 'loanEntity' exists and is accessible

