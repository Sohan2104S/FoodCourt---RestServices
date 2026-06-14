# Food Ordering RBAC Demo (Spring Boot, Maven)

This project implements the take‑home assignment:

- View restaurants and menu items
- Create orders (add food items)
- Place orders (checkout & pay)
- Cancel orders
- Update payment method
- Enforce role‑based access control (RBAC) for **Admin / Manager / Member**
- Bonus: Managers and Members are limited to data from **their own country** (India / America)

## Data storage

**There is no database in this project yet.**

All data lives **in memory** inside:

`src/main/java/com/example/restservice/service/DataStore.java`

| Data | Storage |
|------|---------|
| Users | `Map<Long, User> users` |
| Restaurants & menus | `Map<Long, Restaurant> restaurants` |
| Orders | `Map<Long, Order> orders` |
| Payment methods | `Map<Long, PaymentMethod> paymentMethods` |

- Data is **seeded on startup** in `seedUsers()`, `seedRestaurants()`, `seedPaymentMethods()`.
- When you **restart the app**, all orders are **lost** (fresh seed data).
- For production you would add **PostgreSQL/MySQL + JPA** and replace `DataStore` with repositories.

---

## Razorpay TEST mode setup (step-by-step)

### Step 1 — Create a Razorpay test account

1. Go to [https://razorpay.com](https://razorpay.com) and sign up.
2. Open **Dashboard → Test Mode** (toggle at top).
3. Go to **Account & Settings → API Keys**.
4. Click **Generate Test Key** and copy:
   - **Key ID** → `rzp_test_...`
   - **Key Secret** → keep private

### Step 2 — Add keys to your project (local only)

1. Copy the example file:

```powershell
cd complete
copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
```

2. Edit `application-local.properties`:

```properties
razorpay.enabled=true
razorpay.key-id=rzp_test_YOUR_KEY_ID
razorpay.key-secret=YOUR_KEY_SECRET
razorpay.currency=INR
```

3. **Never commit** `application-local.properties` (it is in `.gitignore`).

Spring Boot auto-loads `application-local.properties` when present.

### Step 3 — Run the app

```powershell
cd complete
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080/`. If Razorpay is configured, you will see toast: **"Razorpay TEST mode active"**.

### Step 4 — Test payment flow

1. Login as **Captain Marvel** (Manager, India) or **Nick Fury** (Admin).
2. Add items from an **India** restaurant → **Create Order**.
3. Go to **Orders** → click **Review & Pay**.
4. Razorpay checkout popup opens (not the demo modal).
5. Use Razorpay **test card**:
   - Card: `4111 1111 1111 1111`
   - Expiry: any future date
   - CVV: any 3 digits
6. On success:
   - Order status → `PLACED`
   - Payment ref → `RZP-pay_...`
   - Receipt shown on page
   - Email receipt **logged in server console** (see `EmailService`)

### Step 5 — API endpoints (for Postman)

| Method | URL | Purpose |
|--------|-----|---------|
| GET | `/payments/config` | Check if Razorpay is enabled |
| POST | `/payments/razorpay/orders/{id}/create` | Create Razorpay order |
| POST | `/payments/razorpay/orders/{id}/verify` | Verify payment & place order |

Header required: `X-USER-ID: 1` (or 2–6)

Verify body:

```json
{
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "signature_xxx"
}
```

### Demo mode (no Razorpay keys)

If `razorpay.enabled=false` or keys are empty, the app uses **Demo Payment** (fake gateway + manual auth modal).

---

## Tech stack

- Java 17
- Spring Boot (REST API + static HTML)
- Maven (via project wrapper `mvnw.cmd` / `mvnw`)

## Project structure

- `src/main/java/com/example/restservice`
  - `RestServiceApplication` – Spring Boot entry point
  - Domain: `User`, `Role`, `Country`, `Restaurant`, `MenuItem`, `Order`, `OrderItem`, `OrderStatus`, `PaymentMethod`
  - RBAC: `Action`, `AccessControlService`, `AccessDeniedException`
  - Data: `DataStore` (in‑memory data for users, restaurants, orders, payment methods)
  - Request context: `CurrentUserResolver` (reads `X-USER-ID` header)
  - Controllers:
    - `RestaurantController` – `/restaurants`
    - `OrderController` – `/orders`
    - `PaymentController` – `/payments`
- `src/main/resources/static/index.html`
  - Simple web UI (no framework) to:
    - Switch current user
    - View restaurants & menus
    - Add items to cart and create orders
    - List and place/cancel orders
    - View / update payment method

## Users & roles

Seeded in `DataStore`:

1. `1` – **Nick Fury** – `ADMIN`
2. `2` – **Captain Marvel** – `MANAGER`, `INDIA`
3. `3` – **Captain America** – `MANAGER`, `AMERICA`
4. `4` – **Thanos** – `MEMBER`, `INDIA`
5. `5` – **Thor** – `MEMBER`, `INDIA`
6. `6` – **Travis** – `MEMBER`, `AMERICA`

The UI lets you pick these users from a dropdown; APIs read the user via the `X-USER-ID` HTTP header.

## Running the app (Maven)

From the project root of this module:

```bash
cd complete
.\mvnw spring-boot:run   # on Windows PowerShell / CMD
# or
./mvnw spring-boot:run   # on macOS / Linux
```

The app will start on `http://localhost:8080`.

## Using the web UI

Open in your browser:

- `http://localhost:8080/`

Steps:

1. **Select user** (Nick, Captain Marvel, etc.) from the dropdown.
2. Click **“Load Restaurants”**.
   - Admin sees all restaurants (India + America).
   - Managers/Members see only restaurants in their own country.
3. Use **“Add”** buttons to add menu items to the cart.
4. Click **“Create Order”** to create an order.
5. Click **“Load Orders”**:
   - Admin: sees all orders (all countries).
   - Manager: all orders in their country.
   - Member: only their own orders in their country.
6. Use **“Place”** / **“Cancel”** order buttons:
   - Place / cancel is allowed only for Admin and Managers (RBAC).
7. In **Payment Method** panel:
   - Any user can view their payment method.
   - Only **Admin** can update the payment method (RBAC).

## RBAC matrix

Implemented as `AccessControlService`:

| Function                         | ADMIN | MANAGER | MEMBER |
|----------------------------------|:-----:|:-------:|:------:|
| View restaurants & menu items    |  yes  |   yes   |  yes   |
| Create order (add food items)    |  yes  |   yes   |  yes   |
| Place order (checkout & pay)     |  yes  |   yes   |   no   |
| Cancel order                     |  yes  |   yes   |   no   |
| Update payment method            |  yes  |   no    |   no   |

Bonus (country restriction):

- Managers and Members can only:
  - See restaurants in their own country.
  - Create / view orders in their own country.
  - Place / cancel orders only within their own country.
- Admin can see and act on data in **all countries**.

## API overview

All APIs expect the header `X-USER-ID` with one of the user IDs shown above.

- `GET /restaurants`
  - Returns restaurants + menu items, filtered by country for non‑admin users.
- `POST /orders`
  - Create a new order (cart).
  - Body: `{ "restaurantId": 1, "items": [ { "menuItemId": 1, "quantity": 2 } ] }`
- `GET /orders`
  - List orders visible to the current user, filtered by role and country.
- `PATCH /orders/{id}/place`
  - Place order (Admin/Manager only).
  - Body: `{ "paymentMethodId": 1 }` (optional).
- `PATCH /orders/{id}/cancel`
  - Cancel order (Admin/Manager only).
- `GET /payments`
  - List payment methods of the current user.
- `PATCH /payments`
  - Update current user’s payment method (Admin only).
  - Body: `{ "methodType": "CARD", "details": "**** **** **** 4242" }`

