# 🚗 TikiPark – Smart Android Parking App

> **A real-time Android parking assistant that helps users discover, reserve, and manage urban parking spots efficiently.**

TikiPark is a collaborative university engineering project that combines a native Android client with a RESTful PHP backend and a robust MySQL database. It features real-time availability tracking, offline-first data handling, and strict database integrity enforcement.

---

## 📌 System Overview

TikiPark follows a classic **Client–Server Architecture**:

```text
Android App (Java)
        |
        v
   PHP Backend
        |
        v
   MySQL Database
        ^
        |
    SQLite Cache
```

* **Frontend:** Native Android (Java) with Google Maps integration.
* **Backend:** PHP 8.x handling business logic.
* **Database:** MySQL with relational integrity and SQLite for local caching.

---

## 🚀 Core Features

### 🔐 Authentication & Security
* **Role-Based Access Control:** Distinct interfaces and permissions for **Admins** and **Standard Users**.
* **Secure Login:** Backend-enforced authorization rules.

### 📍 Smart Map Integration
* **Real-Time Visualization:** Google Maps API integration showing parking spots.
* **Dynamic Markers:** Color-coded markers indicating availability.
* **Live Updates:** Spots update via API polling and refresh triggers.

### 📡 Offline-First Architecture
* **Local Caching:** Uses **SQLite** to store data locally.
* **Auto-Sync:** Detects internet connection restoration and syncs data to reduce API load.
* **Conflict Resolution:** Conflict-safe data refresh strategies.

### 📊 Robust Backend Logic
* **RESTful API:** Structured JSON communication with standard HTTP status codes.
* **Input Validation:** Server-side validation to prevent malformed data.
* **Wallet-Ready:** Architecture designed to support payment gateway integration.

---

## 🧰 Technical Stack

| Component | Technologies Used |
| :--- | :--- |
| **Mobile Client** | Android SDK (Java), XML Layouts, SQLite, Google Maps API |
| **Backend** | PHP 8.x, RESTful API Architecture |
| **Database** | MySQL (InnoDB Engine) |
| **Tools** | Android Studio, VS Code, XAMPP (Apache + MySQL), Git |

---

## 🗄 Database Design & Integrity

The system relies on a **normalized relational schema** with enforced referential integrity.

### 🔗 Foreign Keys & Cascades
We utilize specific cascade strategies (`ON DELETE CASCADE`, `ON UPDATE CASCADE`) to ensure automatic cleanup of dependent records and prevent orphaned data.
* `users → reservations`
* `parking_spots → reservations`
* `admins → managed_parking_spots`

### 🔄 Transaction Management
Critical operations, such as creating a reservation, are wrapped in **Database Transactions** to ensure atomicity.
1. Begin Transaction
2. Validate Availability
3. Insert Reservation
4. Update Parking Status
5. **Commit** (or **Rollback** on failure)

### ⚡ Optimized Queries
* **Indexed Lookups:** Fast retrieval based on location and availability.
* **Aggregations:** Optimized `COUNT` and `SUM` queries for admin dashboards.
* **Security:** All inputs use **Parameterized Queries** to prevent SQL injection.

---

## 📊 API Documentation

The backend exposes endpoints communicating via JSON.

**Example: Get Available Spots**
`GET /api/parking/available?lat=40.6401&lng=22.9444`

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "spot_id": 12,
      "latitude": 40.6402,
      "longitude": 22.9446,
      "available": true
    }
  ]
}
```

---

## 📦 Project Structure

```text
TikiPark/
│
├── AndroidApp/       # Native Android Studio Project
├── Backend/          # PHP Scripts and API Logic
├── Database/         # SQL Import Files (tikipark.sql)
├── Analysis/         # PDF Documentation & UMLs
├── Mockups/          # UI/UX Designs
└── README.md         # Project Documentation
```

---

## 🛠 Installation Guide

### 1️⃣ Clone Repository
```bash
git clone [https://github.com/AngelosFikias0/TikiPark.git](https://github.com/AngelosFikias0/TikiPark.git)
cd TikiPark
```

### 2️⃣ Backend Setup (XAMPP)
1. Install **XAMPP** and start **Apache** and **MySQL**.
2. Copy the contents of the `Backend/` folder to your htdocs directory:
    * `C:\xampp\htdocs\TikiPark`
3. Open **phpMyAdmin**, create a database named `tikipark`, and import:
    * `Database/tikipark.sql`
4. Configure database credentials in:
    * `Backend/config.php`

### 3️⃣ Android Setup
1. Open **Android Studio**.
2. Select **"Open Existing Project"**.
3. Navigate to and select the `AndroidApp/` folder.
4. Build and Run on an Emulator or Physical Device.

---

## 👥 Collaboration Workflow

We follow a structured Git branching model:

* `main`: Stable production version.
* `feature/*`: Development of new features.

**Workflow:**
```bash
# Create a new feature branch
git checkout -b feature-name

# Work, Add, and Commit
git add .
git commit -m "Add feature: detailed description"

# Push to origin
git push origin feature-name
```
*Pull Requests are reviewed before merging into main.*

---

## 📺 Demo

Check out the full demonstration of the application on YouTube:

[![TikiPark Demo](https://img.youtube.com/vi/mT_ZN3BbIjc/0.jpg)](https://www.youtube.com/watch?v=mT_ZN3BbIjc)

---

## 📈 Engineering Highlights & Future Improvements

**Highlights:**
* ✅ Full Client–Server Implementation
* ✅ Advanced Database Integrity (Cascades/Transactions)
* ✅ Offline-First Mobile Strategy

**Future Roadmap:**
* [ ] Migrate backend to Spring Boot or Node.js
* [ ] Implement JWT (JSON Web Token) Authentication
* [ ] Dockerize the backend services
* [ ] Add CI/CD pipelines for automated testing
* [ ] Implement Rate Limiting and API Monitoring

---

## 📄 Documentation

* **Main Analysis:** `Analysis/TikiPark - Main Deliverable.pdf`
* **User Manual:** `Analysis/TikiPark - User Manual.pdf`
