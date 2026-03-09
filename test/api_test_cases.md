# API Test Cases Documentation

## Project: Shopping Cart API
**Repository:** bank3  
**Branch:** main  
**Base URL:** http://localhost:8080/api  
**Generated:** 2024-01-15  

---

## Test Coverage Summary

| Category | Test Cases | Endpoints Covered |
|----------|------------|-------------------|
| Cart Operations | 15 | 5 |
| Product Operations | 4 | 2 |
| Validation Tests | 8 | All |
| Error Handling | 6 | All |
| **Total** | **33** | **7** |

---

## 1. Cart Operations Test Cases

### TC-CART-001: Add Item to Cart - Success
**Test Case ID:** TC-CART-001  
**Endpoint:** POST /api/cart/add  
**Scenario:** Add a valid product to cart with valid quantity  
**Priority:** High  

**Preconditions:**
- Application is running
- Product PROD-12345 exists in catalog
- Product has sufficient stock (>= 2)

**Test Steps:**
1. Send POST request to /api/cart/add
2. Include valid request body:
   ```json
   {
     "productId": "PROD-12345",
     "quantity": 2
   }
   ```
3. Verify response

**Expected Result:**
- Status Code: 201 Created
- Response contains:
  - Cart ID
  - User ID
  - Items array with added product
  - Correct totals calculation
  - Item subtotal = price * quantity

**Assertions:**
```javascript
pm.response.to.have.status(201);
pm.expect(response.id).to.exist;
pm.expect(response.items).to.be.an('array');
pm.expect(response.totals).to.be.a('number');
```

---

### TC-CART-002: Add Item to Cart - Product Not Found
**Test Case ID:** TC-CART-002  
**Endpoint:** POST /api/cart/add  
**Scenario:** Attempt to add non-existent product to cart  
**Priority:** High  

**Preconditions:**
- Application is running
- Product INVALID-PROD does not exist

**Test Steps:**
1. Send POST request to /api/cart/add
2. Include invalid product ID:
   ```json
   {
     "productId": "INVALID-PROD",
     "quantity": 1
   }
   ```

**Expected Result:**
- Status Code: 404 Not Found
- Error response contains:
  - errorCode: "PRODUCT_NOT_FOUND"
  - message describing the error
  - timestamp
  - traceId

**Assertions:**
```javascript
pm.response.to.have.status(404);
pm.expect(response.errorCode).to.equal('PRODUCT_NOT_FOUND');
pm.expect(response.message).to.include('Product not found');
```

---

### TC-CART-003: Add Item to Cart - Insufficient Stock
**Test Case ID:** TC-CART-003  
**Endpoint:** POST /api/cart/add  
**Scenario:** Attempt to add more items than available stock  
**Priority:** High  

**Preconditions:**
- Application is running
- Product exists with limited stock (e.g., 5 units)

**Test Steps:**
1. Send POST request to /api/cart/add
2. Request quantity exceeding available stock:
   ```json
   {
     "productId": "PROD-12345",
     "quantity": 1000
   }
   ```

**Expected Result:**
- Status Code: 409 Conflict
- Error response contains:
  - errorCode: "INSUFFICIENT_STOCK"
  - message: "Insufficient stock for product"

**Assertions:**
```javascript
pm.response.to.have.status(409);
pm.expect(response.errorCode).to.equal('INSUFFICIENT_STOCK');
```

---

### TC-CART-004: Add Item to Cart - Invalid Quantity (Zero)
**Test Case ID:** TC-CART-004  
**Endpoint:** POST /api/cart/add  
**Scenario:** Validation failure for quantity = 0  
**Priority:** Medium  

**Preconditions:**
- Application is running

**Test Steps:**
1. Send POST request with quantity = 0:
   ```json
   {
     "productId": "PROD-12345",
     "quantity": 0
   }
   ```

**Expected Result:**
- Status Code: 400 Bad Request
- Error response contains:
  - errorCode: "VALIDATION_ERROR"
  - details array with field "quantity"
  - message: "Quantity must be at least 1"

---

### TC-CART-005: Add Item to Cart - Missing Product ID
**Test Case ID:** TC-CART-005  
**Endpoint:** POST /api/cart/add  
**Scenario:** Validation failure for missing productId  
**Priority:** Medium  

**Test Steps:**
1. Send POST request with missing productId:
   ```json
   {
     "quantity": 2
   }
   ```

**Expected Result:**
- Status Code: 400 Bad Request
- Error response contains validation error for "productId"
- message: "Product ID is required"

---

### TC-CART-006: Add Item to Cart - Negative Quantity
**Test Case ID:** TC-CART-006  
**Endpoint:** POST /api/cart/add  
**Scenario:** Validation failure for negative quantity  
**Priority:** Medium  

**Test Steps:**
1. Send POST request with negative quantity:
   ```json
   {
     "productId": "PROD-12345",
     "quantity": -5
   }
   ```

**Expected Result:**
- Status Code: 400 Bad Request
- Validation error for quantity field

---

### TC-CART-007: Get Cart - Success
**Test Case ID:** TC-CART-007  
**Endpoint:** GET /api/cart  
**Scenario:** Retrieve existing cart for user  
**Priority:** High  

**Preconditions:**
- User has an existing cart with items

**Test Steps:**
1. Send GET request to /api/cart
2. Verify response structure

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - Cart ID
  - User ID
  - Items array (may be empty)
  - Totals (sum of all item subtotals)

**Assertions:**
```javascript
pm.response.to.have.status(200);
pm.expect(response.id).to.exist;
pm.expect(response.userId).to.exist;
pm.expect(response.items).to.be.an('array');
pm.expect(response.totals).to.be.a('number');
```

---

### TC-CART-008: Get Cart - Cart Not Found
**Test Case ID:** TC-CART-008  
**Endpoint:** GET /api/cart  
**Scenario:** Attempt to get cart for user without cart  
**Priority:** Medium  

**Preconditions:**
- User does not have an existing cart

**Test Steps:**
1. Send GET request to /api/cart with new user ID

**Expected Result:**
- Status Code: 404 Not Found
- Error response:
  - errorCode: "CART_NOT_FOUND"
  - message: "Cart not found for user"

---

### TC-CART-009: Update Cart Item - Success
**Test Case ID:** TC-CART-009  
**Endpoint:** PUT /api/cart/update  
**Scenario:** Update quantity of existing cart item  
**Priority:** High  

**Preconditions:**
- Cart exists with item PROD-12345
- Product has sufficient stock for new quantity

**Test Steps:**
1. Send PUT request to /api/cart/update:
   ```json
   {
     "productId": "PROD-12345",
     "quantity": 5
   }
   ```

**Expected Result:**
- Status Code: 200 OK
- Response shows updated quantity
- Subtotal recalculated correctly
- Cart totals updated

---

### TC-CART-010: Update Cart Item - Product Not in Cart
**Test Case ID:** TC-CART-010  
**Endpoint:** PUT /api/cart/update  
**Scenario:** Attempt to update item not in cart  
**Priority:** Medium  

**Test Steps:**
1. Send PUT request for product not in cart:
   ```json
   {
     "productId": "PROD-99999",
     "quantity": 3
   }
   ```

**Expected Result:**
- Status Code: 404 Not Found
- Error: "Product not found in cart"

---

### TC-CART-011: Update Cart Item - Insufficient Stock
**Test Case ID:** TC-CART-011  
**Endpoint:** PUT /api/cart/update  
**Scenario:** Update quantity exceeding available stock  
**Priority:** High  

**Test Steps:**
1. Send PUT request with quantity > available stock

**Expected Result:**
- Status Code: 409 Conflict
- Error: "INSUFFICIENT_STOCK"

---

### TC-CART-012: Remove Item from Cart - Success
**Test Case ID:** TC-CART-012  
**Endpoint:** DELETE /api/cart/remove/{id}  
**Scenario:** Remove existing item from cart  
**Priority:** High  

**Preconditions:**
- Cart contains item with productId PROD-12345

**Test Steps:**
1. Send DELETE request to /api/cart/remove/PROD-12345

**Expected Result:**
- Status Code: 200 OK
- Response shows cart without removed item
- Cart totals recalculated

---

### TC-CART-013: Remove Item from Cart - Item Not Found
**Test Case ID:** TC-CART-013  
**Endpoint:** DELETE /api/cart/remove/{id}  
**Scenario:** Attempt to remove non-existent item  
**Priority:** Medium  

**Test Steps:**
1. Send DELETE request for non-existent product

**Expected Result:**
- Status Code: 404 Not Found
- Error: "Product not found in cart"

---

### TC-CART-014: Clear Cart - Success
**Test Case ID:** TC-CART-014  
**Endpoint:** DELETE /api/cart/clear  
**Scenario:** Clear all items from cart  
**Priority:** High  

**Preconditions:**
- Cart exists with items

**Test Steps:**
1. Send DELETE request to /api/cart/clear

**Expected Result:**
- Status Code: 200 OK
- Success message: "Cart cleared successfully"
- Timestamp in response

---

### TC-CART-015: Clear Cart - Cart Not Found
**Test Case ID:** TC-CART-015  
**Endpoint:** DELETE /api/cart/clear  
**Scenario:** Attempt to clear non-existent cart  
**Priority:** Low  

**Test Steps:**
1. Send DELETE request for user without cart

**Expected Result:**
- Status Code: 404 Not Found
- Error: "Cart not found"

---

## 2. Product Operations Test Cases

### TC-PROD-001: Get All Products - Success
**Test Case ID:** TC-PROD-001  
**Endpoint:** GET /api/v1/products  
**Scenario:** Retrieve list of all available products  
**Priority:** High  

**Preconditions:**
- Application is running
- Product catalog is initialized

**Test Steps:**
1. Send GET request to /api/v1/products

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - success: true
  - message: "Products retrieved successfully"
  - data: array of products
- Each product has: id, name, price, availableStock, description

---

### TC-PROD-002: Get Product by ID - Success
**Test Case ID:** TC-PROD-002  
**Endpoint:** GET /api/v1/products/{productId}  
**Scenario:** Retrieve specific product details  
**Priority:** High  

**Test Steps:**
1. Send GET request to /api/v1/products/{validProductId}

**Expected Result:**
- Status Code: 200 OK
- Response contains product details
- All fields populated correctly

---

### TC-PROD-003: Get Product by ID - Not Found
**Test Case ID:** TC-PROD-003  
**Endpoint:** GET /api/v1/products/{productId}  
**Scenario:** Attempt to retrieve non-existent product  
**Priority:** Medium  

**Test Steps:**
1. Send GET request with invalid product ID

**Expected Result:**
- Status Code: 404 Not Found
- Error: "Product not found"

---

### TC-PROD-004: Get Product by ID - Invalid ID Format
**Test Case ID:** TC-PROD-004  
**Endpoint:** GET /api/v1/products/{productId}  
**Scenario:** Request with malformed product ID  
**Priority:** Low  

**Test Steps:**
1. Send GET request with special characters or SQL injection attempt

**Expected Result:**
- Status Code: 400 Bad Request or 404 Not Found
- Appropriate error message

---

## 3. Integration Test Scenarios

### TC-INT-001: Complete Shopping Flow
**Test Case ID:** TC-INT-001  
**Scenario:** End-to-end shopping cart workflow  
**Priority:** Critical  

**Test Steps:**
1. Get all products (GET /api/v1/products)
2. Add first product to cart (POST /api/cart/add)
3. Add second product to cart (POST /api/cart/add)
4. Get cart to verify items (GET /api/cart)
5. Update quantity of first item (PUT /api/cart/update)
6. Remove second item (DELETE /api/cart/remove/{id})
7. Get cart to verify changes (GET /api/cart)
8. Clear cart (DELETE /api/cart/clear)

**Expected Result:**
- All operations succeed
- Cart state consistent after each operation
- Totals calculated correctly throughout

---

### TC-INT-002: Concurrent Cart Operations
**Test Case ID:** TC-INT-002  
**Scenario:** Multiple operations on same cart  
**Priority:** High  

**Test Steps:**
1. Add item A to cart
2. Simultaneously add item B to cart
3. Update item A quantity
4. Verify cart consistency

**Expected Result:**
- No data loss
- All operations reflected correctly
- No race conditions

---

## 4. Performance Test Cases

### TC-PERF-001: Response Time - Add to Cart
**Test Case ID:** TC-PERF-001  
**Endpoint:** POST /api/cart/add  
**Scenario:** Measure response time for add operation  
**Priority:** Medium  

**Test Steps:**
1. Send 100 sequential requests to add items
2. Measure response time for each

**Expected Result:**
- 95th percentile response time < 500ms
- No timeouts
- All requests succeed

---

### TC-PERF-002: Concurrent Users
**Test Case ID:** TC-PERF-002  
**Scenario:** Simulate 100 concurrent users  
**Priority:** Medium  

**Test Steps:**
1. Simulate 100 users performing cart operations
2. Monitor system performance

**Expected Result:**
- System remains responsive
- No errors under load
- Response times within acceptable range

---

## 5. Security Test Cases

### TC-SEC-001: CORS Validation
**Test Case ID:** TC-SEC-001  
**Scenario:** Verify CORS headers for allowed origin  
**Priority:** High  

**Test Steps:**
1. Send request from http://localhost:4200
2. Check CORS headers in response

**Expected Result:**
- Access-Control-Allow-Origin: http://localhost:4200
- Access-Control-Allow-Credentials: true

---

### TC-SEC-002: SQL Injection Prevention
**Test Case ID:** TC-SEC-002  
**Scenario:** Attempt SQL injection in product ID  
**Priority:** High  

**Test Steps:**
1. Send request with SQL injection payload:
   ```json
   {
     "productId": "'; DROP TABLE cart; --",
     "quantity": 1
   }
   ```

**Expected Result:**
- Request handled safely
- No database corruption
- Appropriate error response

---

### TC-SEC-003: XSS Prevention
**Test Case ID:** TC-SEC-003  
**Scenario:** Attempt XSS in request payload  
**Priority:** Medium  

**Test Steps:**
1. Send request with XSS payload in product name

**Expected Result:**
- Payload sanitized or rejected
- No script execution

---

## 6. Edge Cases

### TC-EDGE-001: Empty Cart Operations
**Test Case ID:** TC-EDGE-001  
**Scenario:** Operations on empty cart  
**Priority:** Medium  

**Test Steps:**
1. Get empty cart
2. Attempt to update non-existent item
3. Attempt to remove non-existent item
4. Clear empty cart

**Expected Result:**
- Appropriate responses for each operation
- No system errors

---

### TC-EDGE-002: Maximum Quantity
**Test Case ID:** TC-EDGE-002  
**Scenario:** Add maximum integer quantity  
**Priority:** Low  

**Test Steps:**
1. Add item with quantity = Integer.MAX_VALUE

**Expected Result:**
- System handles gracefully
- Either accepts or rejects with validation error

---

### TC-EDGE-003: Special Characters in Product ID
**Test Case ID:** TC-EDGE-003  
**Scenario:** Product ID with special characters  
**Priority:** Low  

**Test Steps:**
1. Request product with ID containing: @#$%^&*()

**Expected Result:**
- Handled appropriately
- No system crash

---

## Test Execution Guidelines

### Prerequisites
1. Application running on http://localhost:8080
2. H2 database initialized
3. Sample products loaded
4. Postman or Newman installed

### Execution Order
1. Product Operations (TC-PROD-001 to TC-PROD-004)
2. Cart Operations - Positive (TC-CART-001, TC-CART-007, TC-CART-009, TC-CART-012, TC-CART-014)
3. Cart Operations - Negative (TC-CART-002, TC-CART-003, TC-CART-008, TC-CART-010, TC-CART-013)
4. Validation Tests (TC-CART-004, TC-CART-005, TC-CART-006)
5. Integration Tests (TC-INT-001, TC-INT-002)
6. Performance Tests (TC-PERF-001, TC-PERF-002)
7. Security Tests (TC-SEC-001, TC-SEC-002, TC-SEC-003)
8. Edge Cases (TC-EDGE-001, TC-EDGE-002, TC-EDGE-003)

### Test Data Requirements
- Valid Product IDs: PROD-12345, PROD-67890, PROD-11111
- Invalid Product ID: INVALID-PROD
- User IDs: user123, user456
- Test quantities: 1, 2, 5, 10, 100, 1000

### Environment Variables
- BASE_URL: http://localhost:8080/api
- USER_ID: guest-user (default)
- PRODUCT_ID_1: PROD-12345
- PRODUCT_ID_2: PROD-67890

---

## Test Coverage Matrix

| Endpoint | Positive Tests | Negative Tests | Validation Tests | Total |
|----------|----------------|----------------|------------------|-------|
| POST /api/cart/add | 1 | 2 | 3 | 6 |
| GET /api/cart | 1 | 1 | 0 | 2 |
| PUT /api/cart/update | 1 | 2 | 0 | 3 |
| DELETE /api/cart/remove/{id} | 1 | 1 | 0 | 2 |
| DELETE /api/cart/clear | 1 | 1 | 0 | 2 |
| GET /api/v1/products | 1 | 0 | 0 | 1 |
| GET /api/v1/products/{id} | 1 | 2 | 0 | 3 |
| **Total** | **7** | **9** | **3** | **19** |

---

## Defect Reporting Template

**Defect ID:** DEF-XXXX  
**Test Case ID:** TC-XXXX  
**Severity:** Critical/High/Medium/Low  
**Priority:** P1/P2/P3  

**Summary:** Brief description  

**Steps to Reproduce:**
1. Step 1
2. Step 2
3. Step 3

**Expected Result:** What should happen  
**Actual Result:** What actually happened  

**Environment:**
- OS: 
- Browser/Tool: 
- Application Version: 

**Attachments:** Screenshots, logs, etc.

---

**Document Version:** 1.0  
**Last Updated:** 2024-01-15  
**Author:** QA Automation Agent  
**Status:** Ready for Execution
