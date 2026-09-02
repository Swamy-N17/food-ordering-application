# FoodHub - Food Ordering Application

FoodHub is a simple full-stack food ordering project built with:

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- HTML
- CSS
- JavaScript

The frontend is intentionally simple and student-friendly. No React, Node.js,
JWT, payment gateway or other unnecessary framework is used.

## Main Flow

### Customer
Register -> Login -> Browse Restaurants -> View Menu -> Cart -> Checkout ->
Choose Payment Method -> Place Order -> My Orders -> Profile

### Restaurant
Register -> Login -> Dashboard -> Manage Menu -> View Incoming Orders ->
Update Order Status -> Profile

## Database

The project uses a fresh MySQL database named:

`food_ordering_db`

The JDBC URL contains `createDatabaseIfNotExist=true`, so MySQL can create
the database automatically when the application starts.

Update the username/password in:

`src/main/resources/application.properties`

## Run

1. Make sure MySQL is running.
2. Open the project in Eclipse.
3. Check the MySQL username/password in `application.properties`.
4. Run `FoodOrderingApplication.java`.
5. Open:

`http://localhost:8080/`

6. Register a customer or restaurant from the login page.

## Important Backend Changes

1. Customer and Restaurant passwords are accepted from JSON but are
   write-only in JSON responses.
2. Passwords are stored using BCrypt.
3. Added validation for registration fields.
4. Order placement validates customer, menu items, quantity and payment amount.
5. Online demo payments are marked SUCCESS; cash payments are PENDING.
6. Updating OrderItem quantity recalculates the Order total and Payment amount.
7. Removing an OrderItem recalculates the Order total and Payment amount.
8. Adding an OrderItem recalculates the Order total and Payment amount.
9. Restaurant rating is validated between 1 and 5 during updates.
10. Menu price updates correctly accept any JSON number type.
11. Existing JSON recursion protection is retained.

## Frontend Structure

`src/main/resources/static/`

- `index.html` - login and registration
- `customer-dashboard.html`
- `restaurant-menu.html`
- `cart.html`
- `checkout.html`
- `my-orders.html`
- `customer-profile.html`
- `restaurant-dashboard.html`
- `restaurant-profile.html`

CSS and JavaScript are separated into their respective folders.

## Food Images

No image upload is required.

The menu uses a small local emoji-based visual mapping from the menu item
name (for example, biryani -> 🍛 and dosa -> 🥞). This avoids random image
mismatches and keeps the project easy to understand.

## Demo Payment

This is a student project, so there is no real payment gateway.

- UPI -> SUCCESS
- Credit Card -> SUCCESS
- Debit Card -> SUCCESS
- Cash -> PENDING

No real money is charged.
