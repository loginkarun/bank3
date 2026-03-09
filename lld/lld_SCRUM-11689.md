# Low-Level Design Document: SCRUM-11689

## 1. Objective

This requirement enables shoppers to add items to their cart from the product detail page. The cart must display item details (name, price, quantity, total cost) and persist across sessions for logged-in users, or in local storage for guests. The system must validate product existence and stock before adding, update cart in real-time, and ensure data integrity and security.

## 2. SpringBoot Backend Details

### 2.1. Controller Layer

#### 2.1.1. REST API Endpoints
| Operation         | REST Method | URL                        | Request Body                | Response Body                  |
|-------------------|-------------|----------------------------|-----------------------------|--------------------------------|
| Add item to cart  | POST        | /api/cart/add              | {"productId", "quantity"} | Cart DTO (items, totals)       |
| Get cart          | GET         | /api/cart                  | -                           | Cart DTO (items, totals)       |
| Update quantity   | PUT         | /api/cart/update           | {"productId", "quantity"} | Cart DTO (items, totals)       |
| Remove item       | DELETE      | /api/cart/remove/{id}      | -                           | Cart DTO (items, totals)       |
| Clear cart        | DELETE      | /api/cart/clear            | -                           | Success message                |

#### 2.1.2. Controller Classes
| Class Name           | Responsibility                                  | Methods                     |
|----------------------|-------------------------------------------------|-----------------------------|
| CartController       | Handle cart operations (add, get, update, remove)| addItem, getCart, updateItem, removeItem, clearCart |

#### 2.1.3. Exception Handlers
- CartExceptionHandler: Handles CartNotFoundException, ProductNotFoundException, InsufficientStockException, etc. Maps to appropriate HTTP status codes.

### 2.2. Service Layer

#### 2.2.1. Business Logic Implementation
- CartService: Implements cart operations, checks product catalog for existence and stock, manages cart persistence for logged-in users (DB) and guests (local storage abstraction).

#### 2.2.2. Service Layer Architecture
- CartServiceImpl: Implements CartService interface.
- Injects ProductService for product validation.
- Uses CartRepository for DB persistence.

#### 2.2.3. Dependency Injection Configuration
- @Service for CartServiceImpl
- @Autowired for CartRepository and ProductService

#### 2.2.4. Validation Rules
| Field Name   | Validation                       | Error Message                  | Annotation Used         |
|--------------|-----------------------------------|-------------------------------|------------------------|
| productId    | Exists in product catalog         | Product not found             | @ExistsInCatalog (custom)|
| quantity     | > 0, <= available stock           | Insufficient stock            | @Min(1), @StockCheck (custom)|

### 2.3. Repository/Data Access Layer

#### 2.3.1. Entity Models
| Entity      | Fields                                      | Constraints                  |
|-------------|---------------------------------------------|------------------------------|
| Cart        | id, userId, items (List<CartItem>), totals  | userId unique, items not null|
| CartItem    | productId, name, price, quantity, subtotal  | productId exists, quantity > 0|

#### 2.3.2. Repository Interfaces
- CartRepository: Extends JpaRepository<Cart, String>
- Custom query: findByUserId(String userId)

#### 2.3.3. Custom Queries
- findByUserId: Retrieve cart for logged-in user

### 2.4. Configuration

#### 2.4.1. Application Properties
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- cart.session.timeout=30m
- encryption.enabled=true

#### 2.4.2. Spring Configuration Classes
- CartConfig: Bean definitions for CartService, CartRepository

#### 2.4.3. Bean Definitions
- CartService bean
- ProductService bean

### 2.5. Security
- Authentication: JWT-based for logged-in users
- Authorization: Only authenticated users can persist cart in DB
- Token handling: JWT in Authorization header

### 2.6. Error Handling & Exceptions
- GlobalExceptionHandler (@ControllerAdvice)
- Custom exceptions: CartNotFoundException, ProductNotFoundException, InsufficientStockException
- HTTP Status codes: 404 (not found), 400 (bad request), 409 (conflict), 500 (internal error)

## 3. Database Details

### 3.1. ER Model
```mermaid
erDiagram
    USER ||--o{ CART : owns
    CART ||--|{ CARTITEM : contains
    CARTITEM }--|| PRODUCT : references
```

### 3.2. Table Schema
| Table Name | Columns                                 | Data Types         | Constraints                 |
|------------|------------------------------------------|--------------------|-----------------------------|
| cart       | id, user_id, totals                     | VARCHAR, VARCHAR, DECIMAL | PK(id), FK(user_id), totals >= 0 |
| cart_item  | id, cart_id, product_id, name, price, quantity, subtotal | VARCHAR, VARCHAR, VARCHAR, VARCHAR, DECIMAL, INT, DECIMAL | PK(id), FK(cart_id), FK(product_id), quantity > 0 |

### 3.3. Database Validations
- product_id must exist in product table
- quantity <= available stock
- cart_id must reference valid cart

## 4. Non-Functional Requirements

### 4.1. Performance Considerations
- Cart updates must complete within 500ms
- Backend must scale to 10,000 concurrent users

### 4.2. Security Requirements
- Data encrypted in transit (HTTPS)
- Data encrypted at rest (DB encryption)
- JWT authentication for cart persistence

### 4.3. Logging & Monitoring
- Log cart operations (add, update, remove)
- Monitor cart API latency and errors

## 5. Dependencies (Maven)
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- jjwt (JWT token handling)
- h2/postgresql/mysql (DB)

## 6. Assumptions
- Product catalog is available and exposes API for validation
- Guest cart persistence is handled by frontend (local storage abstraction)
- Cart is unique per user; guest cart is not persisted in backend
- Only logged-in users can checkout

---

**LLD Filenames:**
- lld/lld_SCRUM-11689.md
