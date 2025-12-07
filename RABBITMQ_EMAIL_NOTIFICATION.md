# RabbitMQ Email Notification System

## Overview

The loanEntity service now sends email notifications via RabbitMQ after successful loanEntity creation. When a loanEntity is created, the system:
1. Fetches user details from ms-user service
2. Creates the loanEntity in the database
3. Builds an HTML email template with user information
4. Sends the email event to RabbitMQ queue

## Components Created

### 1. Event DTOs

**RecipientDto** (`event/RecipientDto.java`)
```java
public class RecipientDto {
    private Boolean alias;
    private String address;
}
```

**SendEmailEvent** (`event/SendEmailEvent.java`)
```java
public class SendEmailEvent {
    private String from;
    private List<RecipientDto> to;
    private String subject;
    private String message;
}
```

### 2. RabbitMQ Configuration

**RabbitMQConfig** (`config/RabbitMQConfig.java`)
- Creates email queue with name: `email-notification-queue`
- Configures JSON message converter
- Sets up RabbitTemplate for sending messages

### 3. Email Producer

**EmailProducer** (`producer/EmailProducer.java`)
- Sends SendEmailEvent to RabbitMQ queue
- Logs all send operations
- Uses RabbitTemplate with JSON converter

### 4. Email Template Service

**EmailTemplateService** and **EmailTemplateServiceImpl**
- Builds HTML email template
- Populates user information (ID, name, phone)
- Returns formatted HTML string

### 5. Updated LoanServiceImpl

**Changes:**
- Injects `EmailProducer` and `EmailTemplateService`
- Calls `sendLoanCreationEmail()` after successful loanEntity creation
- Handles email failures gracefully (logs error but doesn't fail transaction)

## Configuration

### application.yml

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

rabbitmq:
  queue:
    email: email-notification-queue

email:
  notification:
    from: noreply@loanservice.com
    recipients:
      - alias: false
        address: admin@loanservice.com
      - alias: false
        address: loans@loanservice.com
    subject: New Loan Request Created
```

### Customization

**Change Email Recipients:**
```yaml
email:
  notification:
    recipients:
      - alias: false
        address: your-admin@company.com
      - alias: false
        address: your-team@company.com
```

**Change Queue Name:**
```yaml
rabbitmq:
  queue:
    email: your-custom-queue-name
```

**Change RabbitMQ Server:**
```yaml
spring:
  rabbitmq:
    host: your-rabbitmq-server.com
    port: 5672
    username: your-username
    password: your-password
```

## Email Template

The HTML email template includes:
- User ID
- Customer full name (name + surname)
- Mobile phone number

**Template:**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Loan request</title>
</head>
<body>
    <ul>
        <li>Id: 123</li>
        <li>Customer name: John Doe</li>
        <li>Mobile number: +1234567890</li>
    </ul>
</body>
</html>
```

## Message Flow

```
1. POST /api/v1/loans
   ↓
2. LoanController.createLoan()
   ↓
3. LoanServiceImpl.createLoan()
   ↓
4. Fetch user from ms-user service
   ↓
5. Save loanEntity to database
   ↓
6. EmailTemplateService.buildLoanRequestEmail()
   ↓
7. EmailProducer.sendEmailEvent()
   ↓
8. RabbitMQ Queue: email-notification-queue
   ↓
9. [Email Service Consumer picks up message]
   ↓
10. Email sent to recipients
```

## Prerequisites

### Install RabbitMQ

**macOS:**
```bash
brew install rabbitmq
brew services start rabbitmq
```

**Docker:**
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

**Ubuntu:**
```bash
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server
```

### Access RabbitMQ Management UI

- URL: http://localhost:15672
- Username: `guest`
- Password: `guest`

## Testing

### 1. Start RabbitMQ
```bash
# If using Docker
docker start rabbitmq

# If using brew (macOS)
brew services start rabbitmq
```

### 2. Start the Application
```bash
./gradlew bootRun
```

### 3. Create a Loan
```bash
curl -X POST http://localhost:8081/api/v1/loans \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 50000.00,
    "monthlyAmount": 2500.00,
    "duration": 24.00
  }'
```

### 4. Check RabbitMQ Queue

Go to http://localhost:15672 and navigate to:
- Queues → `email-notification-queue`
- Check message count

### 5. View Application Logs

Look for these log messages:
```
INFO  - Creating loanEntity for user: 1
INFO  - Loan created successfully with id: 1
INFO  - Sending email event to queue: email-notification-queue
INFO  - Email event sent successfully to queue: email-notification-queue
INFO  - Email notification sent for loanEntity creation - userId: 1
```

## Message Example

**Message sent to RabbitMQ:**
```json
{
  "from": "noreply@loanservice.com",
  "to": [
    {
      "alias": false,
      "address": "admin@loanservice.com"
    },
    {
      "alias": false,
      "address": "loans@loanservice.com"
    }
  ],
  "subject": "New Loan Request Created",
  "message": "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>Loan request</title>\n</head>\n<body>\n    <ul>\n        <li>Id: 1</li>\n        <li>Customer name: John Doe</li>\n        <li>Mobile number: +1234567890</li>\n    </ul>\n</body>\n</html>"
}
```

## Error Handling

Email sending failures are logged but don't affect loanEntity creation:

```java
try {
    emailProducer.sendEmailEvent(emailEvent);
    log.info("Email notification sent for loanEntity creation - userId: {}", userId);
} catch (Exception e) {
    log.error("Failed to send email notification for loanEntity creation - userId: {}", userId, e);
    // Loan creation continues successfully
}
```

**Benefits:**
- Loan creation is transactional and won't rollback on email failure
- Email issues are logged for debugging
- System remains resilient to RabbitMQ outages

## Troubleshooting

### Issue: Connection refused to RabbitMQ
**Solution:** Ensure RabbitMQ is running:
```bash
# Check if RabbitMQ is running
sudo systemctl status rabbitmq-server  # Linux
brew services list | grep rabbitmq     # macOS
docker ps | grep rabbitmq              # Docker
```

### Issue: Messages not appearing in queue
**Solution:** 
1. Check RabbitMQ logs
2. Verify queue name in configuration matches
3. Check if queue was created: http://localhost:15672

### Issue: Authentication failed
**Solution:** Update credentials in application.yml:
```yaml
spring:
  rabbitmq:
    username: your-username
    password: your-password
```

### Issue: JSON serialization error
**Solution:** Ensure Jackson dependencies are available (included with spring-boot-starter-amqp)

## Dependencies Added

```groovy
implementation 'org.springframework.boot:spring-boot-starter-amqp'
```

This includes:
- Spring AMQP
- RabbitMQ Java Client
- Jackson for JSON serialization

## Next Steps

To complete the email notification flow, you need:

1. **Create an Email Consumer Service** (separate microservice)
   - Consumes messages from `email-notification-queue`
   - Connects to SMTP server
   - Sends actual emails

2. **Add Dead Letter Queue** (optional but recommended)
   - Handle failed email deliveries
   - Retry mechanism

3. **Add Email Templates** (enhancement)
   - More sophisticated HTML templates
   - Template variables
   - Styling and branding

## Summary

✅ **Created**: RabbitMQ producer for email notifications  
✅ **Created**: Email template service for HTML generation  
✅ **Integrated**: Email sending after loanEntity creation  
✅ **Configured**: RabbitMQ connection and queue setup  
✅ **Error Handling**: Graceful failure handling  

The system now sends email notifications to RabbitMQ queue after every successful loanEntity creation!

