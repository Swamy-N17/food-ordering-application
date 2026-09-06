# 🍴 FoodHub — Food Ordering Application

FoodHub is a full-stack food ordering application developed using Java and Spring Boot. It provides separate customer and restaurant workflows for restaurant discovery, menu management, cart, checkout, order processing, payment status, profile management, and password recovery.

## 🛠️ Tech Stack

**Backend:** Java, Spring Boot, Spring Data JPA, Hibernate, REST APIs  
**Frontend:** HTML5, CSS3, JavaScript, Fetch API  
**Database:** MySQL  
**Security & Email:** BCrypt, Spring Boot Mail, Gmail SMTP  
**Tools:** Maven, Eclipse IDE, Postman, Git, GitHub

## ✨ Key Features

### Customer

- Customer registration and role-based login.
- Browse restaurants and view restaurant ratings.
- View and search restaurant menus.
- Add menu items to a browser-based cart.
- Restrict cart items to a single restaurant.
- Checkout with delivery details and demo payment options.
- Place orders and view order history.
- Track order status and cancel eligible orders.
- Update profile and address information.

### Restaurant

- Restaurant registration and role-based login.
- Manage restaurant profile and address information.
- Add and update menu items.
- Manage menu item availability and pricing.
- View incoming customer orders.
- View customer delivery information and payment status.
- Update order status.
- View restaurant rating.

### 🔐 Authentication & Password Recovery

- Implemented role-based authentication for Customer and Restaurant accounts.
- Passwords are securely stored using BCrypt hashing.
- Implemented forgot-password functionality using Gmail SMTP.
- Generates password-reset tokens with expiry and one-time-use handling.
- Reset credentials are supplied through environment variables.
- Users can reset their password through a secure email link.

### 🛒 Cart & Order Flow

The cart is managed on the frontend using browser Local Storage instead of a separate backend Cart entity.

```text
Browse Restaurant
        ↓
View Menu
        ↓
Add Items to Cart
        ↓
Checkout
        ↓
Demo Payment
        ↓
Place Order
        ↓
Restaurant Receives Order
        ↓
Restaurant Updates Status
        ↓
Customer Tracks Order
```

### 💳 Payment

FoodHub includes a demo payment workflow supporting payment methods such as UPI, Credit Card, Debit Card, and Cash.

Payment status is maintained for orders, including refund status when an eligible order is cancelled.

### 🔌 REST APIs

The Spring Boot backend provides REST APIs for:

- Authentication
- Customers
- Restaurants
- Menu Items
- Orders
- Payments

The frontend communicates with these APIs using the JavaScript Fetch API.

### 🗄️ Database

MySQL is used as the relational database, with Spring Data JPA and Hibernate handling entity mapping and persistence.

The database manages customer accounts, restaurant accounts, addresses, menu items, orders, payments, and password-reset tokens.

### 📧 Email Configuration

Password-reset emails are sent through the **Brevo API**.

The Brevo API key and sender credentials are stored using environment variables:

```text
BREVO_API_KEY
BREVO_SENDER_EMAIL
BREVO_SENDER_NAME

## 🏗️ Architecture

The backend follows a layered Spring Boot architecture:

```text
Client / Frontend
       ↓
REST Controller
       ↓
Service Layer
       ↓
Repository Layer
       ↓
JPA / Hibernate
       ↓
MySQL
```

The frontend is served through the Spring Boot application's static resources.

## 🧪 Testing

The major authentication, password recovery, restaurant, menu, cart, checkout, payment, order, cancellation, profile, address, and restaurant-management workflows were tested during development.

REST APIs were tested using Postman.

## 📌 Project Highlights

- Full-stack food ordering application using Java and Spring Boot.
- REST API-based communication between frontend and backend.
- JPA/Hibernate persistence with MySQL.
- Role-based customer and restaurant authentication.
- BCrypt password hashing and Gmail-based password recovery.
- Restaurant menu and order management.
- Frontend cart implementation using Local Storage.
- Customer checkout and demo payment workflow.
- Responsive HTML, CSS, and JavaScript interface.
- 
## 🚀 Deployment

**Backend Hosting:** Render  
**Database Hosting:** Aiven MySQL  

The application uses environment variables for database and Gmail credentials.

**🌐 Deployed Application:** https://foodhub-7zkg.onrender.com/

## 👨‍💻 Developer

**Swamy N**

Java Full Stack Developer
