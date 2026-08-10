# 🛒 E-Card

A modern **Android E-Commerce application** built using **Kotlin** and **Jetpack Compose**. E-Card provides a complete shopping experience for users, along with an **Admin Panel** for managing categories, products, users, and orders.

The application uses **Firebase** for authentication and backend data management and follows modern Android development practices.

---

## 🚧 Project Status

**Active Development**

The core User Panel and Admin Panel features have been implemented. Additional improvements and features will be added as development continues.

---

## ✨ Features

### 👤 User Panel

* 🔐 User Login
* 📝 User Registration
* 🔥 Firebase Authentication
* 🏠 Home Screen
* 📂 Category Selection
* 🛍️ Category-wise Product Listing
* 📦 Product Details
* ❤️ Add to Wishlist
* 🛒 Add to Cart
* ➕ Increase/Decrease Cart Quantity
* 🗑️ Remove Products from Cart
* 💳 Checkout
* 💰 Payment Integration
* 📋 Order Management
* 👤 User Profile

---

### 👨‍💼 Admin Panel

* 🔐 Admin Authentication

* 📊 Admin Dashboard

* 📂 Category Management

  * View Categories
  * Add Categories
  * Edit Categories
  * Delete Categories
  * Category Image Management

* 🛍️ Product Management

  * View Products
  * Add Products
  * Edit Products
  * Delete Products
  * Upload Product Images
  * Category-wise Product Management
  * Price Management
  * Stock Management

* 📦 Order Management

  * View Orders
  * View Order Details
  * Update Order Status
  * Manage Customer Orders

* 👥 User Management

  * View Registered Users
  * View User Information

## 🔥 Firebase

Firebase is used as the backend for the application.

Current Firebase services include:

* Firebase Authentication
* Cloud Firestore
* Firebase Storage

Firebase is used for:

* User authentication
* Admin authentication
* Category data
* Product data
* Order data
* User data
* Product/category images

---

## 🛠 Tech Stack

### Android

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **Android Studio**
* **Jetpack Navigation**
* **Kotlin Coroutines**

### Backend

* **Firebase Authentication**
* **Cloud Firestore**
* **Firebase Storage**


## 📱 Application Modules

### User Flow

```text
Login / Sign Up
       ↓
Home
       ↓
Categories
       ↓
Category-wise Products
       ↓
Product Details
       ↓
Wishlist / Cart
       ↓
Checkout
       ↓
Payment
       ↓
Order
```

### Admin Flow

```text
Admin Login
     ↓
Admin Dashboard
     ├── Categories
     ├── Products
     ├── Orders
     └── Users
```

---

## 📂 Main Functional Areas

| Module                  | Status      |
| ----------------------- | ----------- |
| Login                   | ✅ Completed |
| Sign Up                 | ✅ Completed |
| Firebase Authentication | ✅ Completed |
| Home Screen             | ✅ Completed |
| Categories              | ✅ Completed |
| Category-wise Products  | ✅ Completed |
| Product Details         | ✅ Completed |
| Wishlist                | ✅ Completed |
| Shopping Cart           | ✅ Completed |
| Checkout                | ✅ Completed |
| Payment Integration     | ✅ Completed |
| User Profile            | ✅ Completed |
| Admin Dashboard         | ✅ Completed |
| Admin Categories        | ✅ Completed |
| Admin Products          | ✅ Completed |
| Admin Orders            | ✅ Completed |
| Admin Users             | ✅ Completed |

---

## 📸 Screenshots

Screenshots will be added as development progresses.

Planned screenshots:

* Login Screen
* Sign Up Screen
* Home Screen
* Categories
* Product Listing
* Product Details
* Wishlist
* Shopping Cart
* Checkout
* User Profile


---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ADARSHS993/E-card.git
```

### 2. Open in Android Studio

Open the cloned project in **Android Studio**.

### 3. Configure Firebase

Connect the project to your Firebase project and add the required:

```text
google-services.json
```

Enable the required Firebase services:

* Authentication
* Cloud Firestore
* Firebase Cloud Messaging

### 4. Sync Gradle

Allow Android Studio to sync all Gradle dependencies.

### 5. Run the Application

Connect an Android device or start an emulator and run the application.

---

## 📈 Development Progress

### Completed

* ✅ Project Setup
* ✅ Material 3 UI
* ✅ Login Screen
* ✅ Sign Up Screen
* ✅ Firebase Authentication
* ✅ Home Screen
* ✅ Category Selection
* ✅ Category-wise Product Listing
* ✅ Product Details
* ✅ Wishlist
* ✅ Shopping Cart
* ✅ Checkout
* ✅ Payment Integration
* ✅ User Profile
* ✅ Admin Dashboard
* ✅ Admin Category Management
* ✅ Admin Product Management
* ✅ Admin User Management
* ✅ Admin Order Management

### Future Improvements

* 🔔 Advanced Push Notifications
* 📊 Advanced Admin Analytics
* 🔎 Improved Product Search
* 🏷️ Discount and Coupon System
* 📦 Improved Inventory Management
* ⭐ Product Reviews and Ratings
* 🚚 Enhanced Order Tracking
* 🎨 UI/UX Improvements
* ⚡ Performance Optimization

---

## 🔐 Security

The application uses Firebase Authentication and Firebase Security Rules to protect user and administrative data.

Admin functionality is restricted to authorized administrators.

Sensitive Firebase credentials and private server-side keys should never be stored directly in the Android source code.

---

## 👨‍💻 Developer

**Adarsh Patel**

GitHub: [ADARSHS993](https://github.com/ADARSHS993)

---

## ⭐ Support

If you like this project, consider giving the repository a ⭐ on GitHub.

Contributions, suggestions, and feedback are welcome!
