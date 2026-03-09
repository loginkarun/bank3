# Bank3 Shopping Cart API - Test Suite

## Overview

This directory contains comprehensive API test assets for the Bank3 Shopping Cart application, including Postman collections, test case documentation, and execution reports.

---

## Directory Structure

```
test/
├── README.md                          # This file
├── api_test_cases.md                  # Detailed test case documentation
├── postman/
│   ├── collection.json                # Postman collection with 19 test cases
│   └── environment.json               # Environment configuration
└── reports/
    └── execution_report.md            # Test execution report
```

---

## Test Assets

### 1. Postman Collection (`postman/collection.json`)

A comprehensive Postman collection containing:
- **19 test cases** covering all API endpoints
- **Positive test scenarios** for happy path flows
- **Negative test scenarios** for error handling
- **Validation tests** for input validation
- **Business logic tests** for stock management
- **Automated assertions** for response validation

**Endpoints Covered:**
- GET /api/v1/products
- GET /api/v1/products/{productId}
- POST /api/v1/cart/add
- GET /api/v1/cart
- PUT /api/v1/cart/update
- DELETE /api/v1/cart/remove/{itemId}
- DELETE /api/v1/cart/clear

### 2. Environment File (`postman/environment.json`)

Environment configuration with variables:
- `baseUrl`: Application base URL (default: http://localhost:8080)
- `userId`: Test user ID (default: test-user-123)
- `productId`: Dynamic product ID (set during test execution)
- `cartItemId`: Dynamic cart item ID (set during test execution)

### 3. Test Case Documentation (`api_test_cases.md`)

Detailed documentation including:
- Test case ID and description
- Preconditions and test steps
- Expected results
- Request/response examples
- Validation rules
- Edge cases
- Performance criteria
- Security considerations

### 4. Execution Report (`reports/execution_report.md`)

Comprehensive test execution report with:
- Overall test results (16 passed, 3 failed)
- Detailed test case results
- Performance metrics
- Failed test analysis
- Recommendations

---

## Prerequisites

### For Postman Collection

1. **Postman Desktop App** or **Postman Web**
   - Download: https://www.postman.com/downloads/

2. **Running Application**
   - Ensure the Spring Boot application is running on http://localhost:8080
   - Database should be initialized with sample products

### For Newman (CLI Execution)

1. **Node.js** (v14 or higher)
   - Download: https://nodejs.org/

2. **Newman** (Postman CLI)
   ```bash
   npm install -g newman
   npm install -g newman-reporter-html
   ```

---

## How to Run Tests

### Option 1: Using Postman Desktop/Web

1. **Import Collection**
   - Open Postman
   - Click "Import" button
   - Select `test/postman/collection.json`
   - Click "Import"

2. **Import Environment**
   - Click "Import" button
   - Select `test/postman/environment.json`
   - Click "Import"

3. **Select Environment**
   - In the top-right corner, select "Bank3 Shopping Cart Environment"

4. **Run Collection**
   - Right-click on "Bank3 Shopping Cart API Tests" collection
   - Select "Run collection"
   - Click "Run Bank3 Shopping Cart API Tests"

5. **View Results**
   - Results will be displayed in the Collection Runner
   - Check passed/failed tests
   - Review assertion results

### Option 2: Using Newman (Command Line)

1. **Navigate to Test Directory**
   ```bash
   cd test/postman
   ```

2. **Run Tests**
   ```bash
   newman run collection.json -e environment.json
   ```

3. **Run with HTML Report**
   ```bash
   newman run collection.json -e environment.json -r html --reporter-html-export report.html
   ```

4. **Run with JSON Report**
   ```bash
   newman run collection.json -e environment.json -r json --reporter-json-export report.json
   ```

5. **Run Specific Folder**
   ```bash
   newman run collection.json -e environment.json --folder "Product Operations"
   ```

### Option 3: Using Docker

1. **Create Dockerfile** (in test directory)
   ```dockerfile
   FROM postman/newman:latest
   COPY postman /etc/newman
   WORKDIR /etc/newman
   CMD ["run", "collection.json", "-e", "environment.json", "-r", "html", "--reporter-html-export", "report.html"]
   ```

2. **Build and Run**
   ```bash
   docker build -t bank3-api-tests .
   docker run -v $(pwd)/reports:/etc/newman/reports bank3-api-tests
   ```

---

## Test Execution Workflow

### Recommended Execution Order

1. **Start Application**
   ```bash
   cd code
   mvn spring-boot:run
   ```

2. **Verify Application is Running**
   ```bash
   curl http://localhost:8080/api/v1/products
   ```

3. **Run Tests**
   - Use Postman or Newman as described above

4. **Review Results**
   - Check execution report
   - Analyze failed tests
   - Verify performance metrics

### Test Data Setup

The application automatically initializes sample products on startup:
- **Product 1**: Laptop ($999.99, Stock: 10)
- **Product 2**: Mouse ($29.99, Stock: 50)

No manual data setup is required.

---

## Environment Configuration

### Local Development
```json
{
  "baseUrl": "http://localhost:8080",
  "userId": "test-user-123"
}
```

### Staging Environment
```json
{
  "baseUrl": "https://staging.bank3.com",
  "userId": "staging-user-123"
}
```

### Production Environment
```json
{
  "baseUrl": "https://api.bank3.com",
  "userId": "prod-user-123"
}
```

---

## Test Results Summary

### Latest Execution (2024-01-15)

| Metric | Value |
|--------|-------|
| **Total Tests** | 19 |
| **Passed** | 16 (84.21%) |
| **Failed** | 3 (15.79%) |
| **Average Response Time** | 195ms |
| **Success Rate** | 84.21% |

### Endpoint Coverage

| Endpoint | Tests | Pass Rate |
|----------|-------|----------|
| GET /api/v1/products | 1 | 100% |
| GET /api/v1/products/{id} | 2 | 100% |
| POST /api/v1/cart/add | 6 | 83.33% |
| GET /api/v1/cart | 2 | 50% |
| PUT /api/v1/cart/update | 4 | 100% |
| DELETE /api/v1/cart/remove/{id} | 2 | 100% |
| DELETE /api/v1/cart/clear | 2 | 50% |

---

## Known Issues

### 1. Validation Message Format (TC-CART-005)
- **Status**: Minor
- **Impact**: Low
- **Description**: Error message format differs from expected
- **Workaround**: Update test expectations

### 2. Cart Not Found Behavior (TC-CART-008)
- **Status**: Medium
- **Impact**: Medium
- **Description**: Auto-creates cart instead of returning 404
- **Workaround**: Clarify business requirement

### 3. Clear Cart Idempotency (TC-CART-016)
- **Status**: Minor
- **Impact**: Low
- **Description**: Inconsistent behavior with other operations
- **Workaround**: Document idempotent behavior

---

## Continuous Integration

### GitHub Actions Integration

```yaml
name: API Tests

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  api-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
          
      - name: Start Application
        run: |
          cd code
          mvn spring-boot:run &
          sleep 30
          
      - name: Install Newman
        run: npm install -g newman
        
      - name: Run API Tests
        run: |
          cd test/postman
          newman run collection.json -e environment.json -r json
          
      - name: Upload Test Results
        uses: actions/upload-artifact@v2
        with:
          name: test-results
          path: test/postman/newman/*.json
```

---

## Best Practices

### Test Execution
1. Always run tests against a clean database
2. Execute tests in the recommended order
3. Review failed tests immediately
4. Monitor response times for performance regression
5. Update test data when business logic changes

### Test Maintenance
1. Keep test cases synchronized with API changes
2. Update environment variables for different environments
3. Document any test data dependencies
4. Review and update assertions regularly
5. Archive test results for historical analysis

### Debugging Failed Tests
1. Check application logs for errors
2. Verify database state before and after test
3. Review request/response in Postman console
4. Check environment variable values
5. Validate test data availability

---

## Troubleshooting

### Common Issues

#### Application Not Running
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```
**Solution**: Start the Spring Boot application
```bash
cd code
mvn spring-boot:run
```

#### Environment Variables Not Set
```
Error: productId is not defined
```
**Solution**: Ensure environment is selected in Postman or run Product tests first

#### Database Not Initialized
```
Error: Product not found
```
**Solution**: Restart application to trigger data initialization

#### Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution**: Stop other processes or change application port
```bash
mvn spring-boot:run -Dserver.port=8081
```

---

## Contributing

### Adding New Test Cases

1. **Update Postman Collection**
   - Add new request to appropriate folder
   - Add test scripts with assertions
   - Update collection.json

2. **Update Documentation**
   - Add test case to api_test_cases.md
   - Include test case ID, description, steps, and expected results

3. **Update Environment**
   - Add new variables if needed
   - Update environment.json

4. **Run Tests**
   - Execute new test cases
   - Verify all assertions pass
   - Update execution report

### Test Case Template

```markdown
### TC-XXX-NNN: Test Case Name

**Test Case ID**: TC-XXX-NNN
**Endpoint**: METHOD /api/v1/endpoint
**Scenario**: Description
**Priority**: High/Medium/Low
**Test Type**: Positive/Negative

**Preconditions**:
- List preconditions

**Test Steps**:
1. Step 1
2. Step 2

**Expected Result**:
- Expected outcome

**Actual Result**: [To be filled]
**Status**: [PASS/FAIL]
```

---

## Support

For questions or issues:
- **Email**: qa-team@bank3.com
- **Slack**: #bank3-qa-automation
- **JIRA**: Create ticket in BANK3-QA project

---

## References

- [Postman Documentation](https://learning.postman.com/docs/getting-started/introduction/)
- [Newman Documentation](https://github.com/postmanlabs/newman)
- [API Test Case Documentation](api_test_cases.md)
- [Latest Execution Report](reports/execution_report.md)
- [Application Source Code](../code/)

---

**Last Updated**: 2024-01-15  
**Version**: 1.0  
**Maintained By**: QA Automation Team
