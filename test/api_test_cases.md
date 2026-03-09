# API Test Cases - Bank3 Shopping Cart Application

## Test Execution Information
- **Application**: Bank3 Shopping Cart API
- **Base URL**: http://localhost:8080/api
- **Test Environment**: Local Development
- **API Version**: v1
- **Date Generated**: 2024-01-15

---

## Table of Contents
1. [Product API Test Cases](#product-api-test-cases)
2. [Cart API Test Cases](#cart-api-test-cases)
3. [Test Data](#test-data)
4. [Validation Rules](#validation-rules)

---

## Product API Test Cases

### TC-PROD-001: Get All Products - Happy Path

**Test Case ID**: TC-PROD-001  
**Endpoint**: GET /api/v1/products  
**Scenario**: Retrieve all available products successfully  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running on http://localhost:8080
- Database contains sample products

**Test Steps**:
1. Send GET request to `/api/v1/products`
2. Verify response status code
3. Verify response structure
4. Verify product data fields

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Products retrieved successfully"
  - `data`: Array of product objects
- Each product contains:
  - `id`: String (UUID)
  - `name`: String
  - `price`: Number (BigDecimal)
  - `availableStock`: Integer
  - `description`: String

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-PROD-002: Get Product By ID - Happy Path

**Test Case ID**: TC-PROD-002  
**Endpoint**: GET /api/v1/products/{productId}  
**Scenario**: Retrieve a specific product by valid ID  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running
- Valid product ID exists in database
- Product ID obtained from TC-PROD-001

**Test Steps**:
1. Send GET request to `/api/v1/products/{validProductId}`
2. Verify response status code
3. Verify product details match the requested ID
4. Verify all required fields are present

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Product retrieved successfully"
  - `data`: Single product object with all fields
- Product ID matches the requested ID

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-PROD-003: Get Product By ID - Product Not Found

**Test Case ID**: TC-PROD-003  
**Endpoint**: GET /api/v1/products/{productId}  
**Scenario**: Attempt to retrieve product with invalid/non-existent ID  
**Priority**: Medium  
**Test Type**: Negative

**Preconditions**:
- Application is running
- Product ID does not exist in database

**Test Steps**:
1. Send GET request to `/api/v1/products/INVALID-ID-999`
2. Verify response status code
3. Verify error message
4. Verify error structure

**Expected Result**:
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "PRODUCT_NOT_FOUND"
  - `message`: "Product not found: INVALID-ID-999"
  - `timestamp`: ISO 8601 format
  - `traceId`: UUID

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

## Cart API Test Cases

### TC-CART-001: Add Item to Cart - Happy Path

**Test Case ID**: TC-CART-001  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Successfully add a valid product to cart  
**Priority**: Critical  
**Test Type**: Positive

**Preconditions**:
- Application is running
- Valid product ID exists
- Product has sufficient stock
- User ID header is provided

**Test Steps**:
1. Send POST request to `/api/v1/cart/add` with:
   - Header: `X-User-Id: test-user-123`
   - Body: `{"productId": "<validProductId>", "quantity": 2}`
2. Verify response status code
3. Verify cart is created/updated
4. Verify item is added with correct quantity
5. Verify total amount is calculated correctly

**Request Body**:
```json
{
  "productId": "<validProductId>",
  "quantity": 2
}
```

**Expected Result**:
- Status Code: 201 Created
- Response contains:
  - `success`: true
  - `message`: "Item added to cart successfully"
  - `data`: Cart object with:
    - `id`: String (UUID)
    - `userId`: "test-user-123"
    - `items`: Array containing the added item
    - `totalAmount`: Calculated total (price × quantity)
    - `totalItems`: Number of items in cart

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-002: Add Item to Cart - Product Not Found

**Test Case ID**: TC-CART-002  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Attempt to add non-existent product to cart  
**Priority**: High  
**Test Type**: Negative

**Preconditions**:
- Application is running
- Product ID does not exist

**Test Steps**:
1. Send POST request with invalid product ID
2. Verify error response
3. Verify cart is not modified

**Request Body**:
```json
{
  "productId": "INVALID-999",
  "quantity": 1
}
```

**Expected Result**:
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "PRODUCT_NOT_FOUND"
  - `message`: "Product not found: INVALID-999"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-003: Add Item to Cart - Invalid Quantity (Zero)

**Test Case ID**: TC-CART-003  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Attempt to add item with quantity = 0  
**Priority**: High  
**Test Type**: Negative - Validation

**Preconditions**:
- Application is running
- Valid product ID exists

**Test Steps**:
1. Send POST request with quantity = 0
2. Verify validation error response
3. Verify cart is not modified

**Request Body**:
```json
{
  "productId": "<validProductId>",
  "quantity": 0
}
```

**Expected Result**:
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Array with field error:
    - `field`: "quantity"
    - `issue`: "Quantity must be at least 1"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-004: Add Item to Cart - Invalid Quantity (Negative)

**Test Case ID**: TC-CART-004  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Attempt to add item with negative quantity  
**Priority**: High  
**Test Type**: Negative - Validation

**Preconditions**:
- Application is running
- Valid product ID exists

**Test Steps**:
1. Send POST request with quantity = -1
2. Verify validation error response

**Request Body**:
```json
{
  "productId": "<validProductId>",
  "quantity": -1
}
```

**Expected Result**:
- Status Code: 400 Bad Request
- Validation error for quantity field

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-005: Add Item to Cart - Missing Product ID

**Test Case ID**: TC-CART-005  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Attempt to add item without product ID  
**Priority**: High  
**Test Type**: Negative - Validation

**Preconditions**:
- Application is running

**Test Steps**:
1. Send POST request with missing productId field
2. Verify validation error response

**Request Body**:
```json
{
  "quantity": 2
}
```

**Expected Result**:
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: Array with field error:
    - `field`: "productId"
    - `issue`: "Product ID is required"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-006: Add Item to Cart - Insufficient Stock

**Test Case ID**: TC-CART-006  
**Endpoint**: POST /api/v1/cart/add  
**Scenario**: Attempt to add more items than available in stock  
**Priority**: Critical  
**Test Type**: Negative - Business Logic

**Preconditions**:
- Application is running
- Valid product ID exists
- Product has limited stock (e.g., 10 units)

**Test Steps**:
1. Send POST request with quantity exceeding available stock (e.g., 9999)
2. Verify insufficient stock error
3. Verify cart is not modified

**Request Body**:
```json
{
  "productId": "<validProductId>",
  "quantity": 9999
}
```

**Expected Result**:
- Status Code: 409 Conflict
- Response contains:
  - `errorCode`: "INSUFFICIENT_STOCK"
  - `message`: "Insufficient stock for product: <productId>"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-007: Get Cart - Happy Path

**Test Case ID**: TC-CART-007  
**Endpoint**: GET /api/v1/cart  
**Scenario**: Retrieve cart for existing user  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running
- User has items in cart (from TC-CART-001)
- User ID header is provided

**Test Steps**:
1. Send GET request to `/api/v1/cart` with header `X-User-Id: test-user-123`
2. Verify response status code
3. Verify cart contains previously added items
4. Verify total calculations

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Cart retrieved successfully"
  - `data`: Cart object with:
    - `id`: String
    - `userId`: "test-user-123"
    - `items`: Array of cart items
    - `totalAmount`: Sum of all item subtotals
    - `totalItems`: Count of items

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-008: Get Cart - Cart Not Found

**Test Case ID**: TC-CART-008  
**Endpoint**: GET /api/v1/cart  
**Scenario**: Attempt to retrieve cart for user without cart  
**Priority**: Medium  
**Test Type**: Negative

**Preconditions**:
- Application is running
- User ID does not have an existing cart

**Test Steps**:
1. Send GET request with new/non-existent user ID
2. Verify error response

**Expected Result**:
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "CART_NOT_FOUND"
  - `message`: "Cart not found for user: <userId>"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-009: Update Cart Item - Happy Path

**Test Case ID**: TC-CART-009  
**Endpoint**: PUT /api/v1/cart/update  
**Scenario**: Successfully update quantity of existing cart item  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running
- User has items in cart
- Product has sufficient stock for new quantity

**Test Steps**:
1. Send PUT request to `/api/v1/cart/update` with:
   - Header: `X-User-Id: test-user-123`
   - Body: `{"productId": "<existingProductId>", "quantity": 3}`
2. Verify response status code
3. Verify item quantity is updated
4. Verify total amount is recalculated

**Request Body**:
```json
{
  "productId": "<existingProductId>",
  "quantity": 3
}
```

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Cart item updated successfully"
  - `data`: Updated cart with new quantity and recalculated totals

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-010: Update Cart Item - Invalid Quantity

**Test Case ID**: TC-CART-010  
**Endpoint**: PUT /api/v1/cart/update  
**Scenario**: Attempt to update item with invalid quantity  
**Priority**: High  
**Test Type**: Negative - Validation

**Preconditions**:
- Application is running
- User has items in cart

**Test Steps**:
1. Send PUT request with quantity = -1
2. Verify validation error response

**Request Body**:
```json
{
  "productId": "<existingProductId>",
  "quantity": -1
}
```

**Expected Result**:
- Status Code: 400 Bad Request
- Validation error for quantity field

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-011: Update Cart Item - Product Not in Cart

**Test Case ID**: TC-CART-011  
**Endpoint**: PUT /api/v1/cart/update  
**Scenario**: Attempt to update quantity for product not in cart  
**Priority**: Medium  
**Test Type**: Negative

**Preconditions**:
- Application is running
- User has a cart
- Product ID is not in user's cart

**Test Steps**:
1. Send PUT request with product ID not in cart
2. Verify error response

**Request Body**:
```json
{
  "productId": "<productNotInCart>",
  "quantity": 2
}
```

**Expected Result**:
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "PRODUCT_NOT_FOUND"
  - `message`: "Item not found in cart: <productId>"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-012: Update Cart Item - Insufficient Stock

**Test Case ID**: TC-CART-012  
**Endpoint**: PUT /api/v1/cart/update  
**Scenario**: Attempt to update quantity beyond available stock  
**Priority**: High  
**Test Type**: Negative - Business Logic

**Preconditions**:
- Application is running
- User has items in cart
- Product has limited stock

**Test Steps**:
1. Send PUT request with quantity exceeding stock
2. Verify insufficient stock error

**Request Body**:
```json
{
  "productId": "<existingProductId>",
  "quantity": 9999
}
```

**Expected Result**:
- Status Code: 409 Conflict
- Response contains:
  - `errorCode`: "INSUFFICIENT_STOCK"
  - `message`: "Insufficient stock for product: <productId>"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-013: Remove Cart Item - Happy Path

**Test Case ID**: TC-CART-013  
**Endpoint**: DELETE /api/v1/cart/remove/{itemId}  
**Scenario**: Successfully remove an item from cart  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running
- User has items in cart
- Valid cart item ID exists

**Test Steps**:
1. Send DELETE request to `/api/v1/cart/remove/{validItemId}`
2. Verify response status code
3. Verify item is removed from cart
4. Verify total amount is recalculated

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Item removed from cart successfully"
  - `data`: Updated cart without the removed item
- Total amount is recalculated correctly

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-014: Remove Cart Item - Item Not Found

**Test Case ID**: TC-CART-014  
**Endpoint**: DELETE /api/v1/cart/remove/{itemId}  
**Scenario**: Attempt to remove non-existent item  
**Priority**: Medium  
**Test Type**: Negative

**Preconditions**:
- Application is running
- User has a cart

**Test Steps**:
1. Send DELETE request with invalid item ID
2. Verify error response

**Expected Result**:
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "PRODUCT_NOT_FOUND"
  - `message`: "Item not found: INVALID-ITEM-999"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-015: Clear Cart - Happy Path

**Test Case ID**: TC-CART-015  
**Endpoint**: DELETE /api/v1/cart/clear  
**Scenario**: Successfully clear all items from cart  
**Priority**: High  
**Test Type**: Positive

**Preconditions**:
- Application is running
- User has items in cart

**Test Steps**:
1. Send DELETE request to `/api/v1/cart/clear`
2. Verify response status code
3. Verify success message
4. Verify cart is empty (optional: call GET /cart to confirm)

**Expected Result**:
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `message`: "Cart cleared successfully"
  - `data`: "Cart has been cleared"

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

### TC-CART-016: Clear Cart - Cart Not Found

**Test Case ID**: TC-CART-016  
**Endpoint**: DELETE /api/v1/cart/clear  
**Scenario**: Attempt to clear cart for user without cart  
**Priority**: Low  
**Test Type**: Negative

**Preconditions**:
- Application is running
- User does not have a cart

**Test Steps**:
1. Send DELETE request with new user ID
2. Verify error response or successful no-op

**Expected Result**:
- Status Code: 404 Not Found OR 200 OK (depending on implementation)
- If 404: Error message indicating cart not found
- If 200: Success message (idempotent operation)

**Actual Result**: [To be filled during execution]

**Status**: [PASS/FAIL]

---

## Test Data

### Sample Products
```json
[
  {
    "id": "<generated-uuid>",
    "name": "Laptop",
    "price": 999.99,
    "availableStock": 10,
    "description": "High performance laptop"
  },
  {
    "id": "<generated-uuid>",
    "name": "Mouse",
    "price": 29.99,
    "availableStock": 50,
    "description": "Wireless mouse"
  }
]
```

### Test Users
- **User ID**: test-user-123
- **User ID**: test-user-456 (for multi-user scenarios)

---

## Validation Rules

### Request Validation

| Field | Validation Rule | Error Message |
|-------|----------------|---------------|
| productId | @NotBlank | "Product ID is required" |
| quantity | @Min(1) | "Quantity must be at least 1" |

### Business Logic Validation

| Scenario | Validation | Error Code | HTTP Status |
|----------|-----------|------------|-------------|
| Product not found | Product exists in catalog | PRODUCT_NOT_FOUND | 404 |
| Insufficient stock | Requested quantity ≤ available stock | INSUFFICIENT_STOCK | 409 |
| Cart not found | Cart exists for user | CART_NOT_FOUND | 404 |
| Item not in cart | Item exists in user's cart | PRODUCT_NOT_FOUND | 404 |

---

## Edge Cases

### TC-EDGE-001: Add Same Item Multiple Times
**Scenario**: Add the same product to cart multiple times  
**Expected**: Quantity should be accumulated, not create duplicate entries

### TC-EDGE-002: Update Item to Same Quantity
**Scenario**: Update item quantity to its current value  
**Expected**: Operation succeeds, no changes to cart

### TC-EDGE-003: Remove Last Item from Cart
**Scenario**: Remove the only item in cart  
**Expected**: Cart becomes empty, total amount = 0

### TC-EDGE-004: Concurrent Cart Operations
**Scenario**: Multiple simultaneous requests to modify same cart  
**Expected**: Operations are handled atomically, no data corruption

### TC-EDGE-005: Large Quantity Values
**Scenario**: Add item with maximum integer quantity  
**Expected**: System handles large numbers correctly or returns appropriate error

---

## Performance Test Cases

### TC-PERF-001: Response Time - Get All Products
**Acceptance Criteria**: Response time < 500ms for 100 products

### TC-PERF-002: Response Time - Add to Cart
**Acceptance Criteria**: Response time < 300ms

### TC-PERF-003: Concurrent Users
**Acceptance Criteria**: System handles 100 concurrent users without errors

---

## Security Test Cases

### TC-SEC-001: SQL Injection in Product ID
**Scenario**: Attempt SQL injection via productId parameter  
**Expected**: Input is sanitized, no SQL injection possible

### TC-SEC-002: XSS in Product Name
**Scenario**: Attempt XSS via product name field  
**Expected**: Input is escaped, no script execution

### TC-SEC-003: CORS Validation
**Scenario**: Request from unauthorized origin  
**Expected**: CORS policy blocks unauthorized origins

---

## Test Execution Summary Template

| Test Case ID | Test Name | Status | Execution Time | Notes |
|--------------|-----------|--------|----------------|-------|
| TC-PROD-001 | Get All Products - Happy Path | | | |
| TC-PROD-002 | Get Product By ID - Happy Path | | | |
| TC-PROD-003 | Get Product By ID - Not Found | | | |
| TC-CART-001 | Add Item to Cart - Happy Path | | | |
| TC-CART-002 | Add Item - Product Not Found | | | |
| TC-CART-003 | Add Item - Invalid Quantity (Zero) | | | |
| TC-CART-004 | Add Item - Invalid Quantity (Negative) | | | |
| TC-CART-005 | Add Item - Missing Product ID | | | |
| TC-CART-006 | Add Item - Insufficient Stock | | | |
| TC-CART-007 | Get Cart - Happy Path | | | |
| TC-CART-008 | Get Cart - Cart Not Found | | | |
| TC-CART-009 | Update Cart Item - Happy Path | | | |
| TC-CART-010 | Update Cart Item - Invalid Quantity | | | |
| TC-CART-011 | Update Cart Item - Not in Cart | | | |
| TC-CART-012 | Update Cart Item - Insufficient Stock | | | |
| TC-CART-013 | Remove Cart Item - Happy Path | | | |
| TC-CART-014 | Remove Cart Item - Not Found | | | |
| TC-CART-015 | Clear Cart - Happy Path | | | |
| TC-CART-016 | Clear Cart - Cart Not Found | | | |

**Total Test Cases**: 19  
**Passed**: [To be filled]  
**Failed**: [To be filled]  
**Blocked**: [To be filled]  
**Not Executed**: [To be filled]

---

## Notes

1. All test cases should be executed in the order listed to ensure proper test data setup
2. Environment variables should be configured before test execution
3. Database should be reset between test runs for consistent results
4. All timestamps should be validated for ISO 8601 format
5. All UUIDs should be validated for proper UUID format
6. Response times should be monitored and logged
7. All error responses should include traceId for debugging

---

**Document Version**: 1.0  
**Last Updated**: 2024-01-15  
**Author**: QA Automation Agent
