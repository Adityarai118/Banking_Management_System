# 🏦 Banking Management System & ATM Simulator

A robust, enterprise-structured Desktop Core Banking and ATM simulation application built with **Java (Swing / AWT)**, **Maven**, and **SQLite**. 

This system provides a full-featured ATM interface for bank customers alongside a privileged administrative portal for account oversight, fraud prevention (freezing/unfreezing), and transaction auditing.

---

## 🌟 Key Features

### 👤 Customer ATM Operations
* **Account Registration Wizard:** 3-step structured registration collecting personal, background, and account configuration details. Generates unique 10-digit account numbers and 16-digit card numbers.
* **Secure Authentication:** Card Number + 4-digit PIN verification powered by salted **PBKDF2-HMAC-SHA256** cryptographic password hashing with constant-time equality checks.
* **Real-Time Balance Enquiry:** Instant $O(1)$ balance check directly from account balances.
* **Cash Deposit:** Validated fund deposits with running balance updates and ledger logging.
* **Cash Withdrawal & Fast Cash:** Overdraft-protected withdrawals with instant balance deduction and preset quick-cash buttons (Rs. 100, 500, 1000, 2000, 5000, 10000).
* **Inter-Account Fund Transfer:** Atomic, ACID-compliant money transfers between accounts. Built using database transactions (`setAutoCommit(false)`, `commit()`, `rollback()`) guaranteeing zero partial transfers.
* **Mini-Statement & Transaction Ledger:** Visual transaction history displaying timestamped debits, credits, and closing balances with masked card numbers.
* **Isolated PIN Change:** Secure PIN updates modifying only the target customer's record with zero risk of collision.
* **Profile Management:** View and edit customer contact details (address, email, city, state, pincode).

### 🛡️ Administrator Portal
* **Dedicated Admin Authentication:** Role-based access control protecting administrative actions from regular ATM card logins.
* **Account Management Console:** Tabular overview of all accounts, cards, balances, and operational statuses.
* **Search & Filter:** Instant search by Account Number, Card Number, or Customer Name.
* **Account Freezing / Unfreezing:** One-click account suspension to immediately block withdrawals and transfers on compromised accounts.
* **Audit Trail Inspection:** Comprehensive transaction audit log viewer for compliance and record-keeping.

---

## 📸 Screenshots

### 1. ATM Welcome & Sign-In Screen
![ATM Login](screenshots/login_screen.png)

### 2. Main ATM Transaction Dashboard
![ATM Dashboard](screenshots/atm_dashboard.png)

### 3. Inter-Account Fund Transfer
![Fund Transfer](screenshots/transfer_screen.png)

### 4. Cash Deposit Interface
![Cash Deposit](screenshots/deposit_screen.png)

### 5. Mini-Statement & Transaction Audit
![Mini Statement](screenshots/mini_statement.png)

### 6. Administrator Management Console
![Admin Dashboard](screenshots/admin_dashboard.png)

---

## 🏗️ Architecture & OOP Design

The project is structured according to clean Software Engineering principles, separating presentation, business logic, and persistence layers:

```
src/bank/management/system/
├── Login.java                 # Customer & Admin login gateway
├── SignUp1.java               # Step 1: Personal Details Wizard
├── SignUp2.java               # Step 2: Additional Details Wizard
├── SignUp3.java               # Step 3: Account Config & Credential Setup
├── Transactions.java          # ATM Main Menu Dashboard
├── Deposit.java               # Cash Deposit Screen
├── Withdrawl.java             # Cash Withdrawal Screen
├── FastCash.java              # Preset Cash Dispensing Screen
├── BalanceEnquiry.java        # Balance Lookup Screen
├── MiniStatement.java         # Statement History Screen
├── Transfer.java              # Inter-Account Atomic Transfer Screen
├── UpdateProfile.java         # Customer Contact Update Screen
├── AdminLogin.java            # Admin Authentication Gateway
├── AdminDashboard.java        # Admin Account & Audit Management
├── Conn.java                  # Backward-compatible JDBC wrapper
├── DatabaseHelper.java        # SQLite lifecycle & auto-schema setup
├── SecurityUtil.java          # Cryptographic hashing & verification (PBKDF2)
├── model/                     # Domain Entities
│   ├── Customer.java          # Customer domain entity
│   ├── Account.java           # Bank account domain entity
│   ├── TransactionRecord.java # Financial ledger entity
│   └── AdminUser.java         # Administrator entity
└── service/                   # Business Logic & Transactions
    ├── AccountService.java    # Core banking transactions (ACID transfers)
    └── AdminService.java      # Admin search, status control, and auditing
```

---

## 🛠️ Technology Stack

| Component | Technology | Rationale |
| :--- | :--- | :--- |
| **Language** | Java 17+ (LTS) | Clean OOP, robust type safety, platform independent. |
| **GUI Framework** | Java Swing / AWT | Authentic desktop ATM bezel styling and native event handling. |
| **Build & Dependency Tool** | Apache Maven 3.9+ | Standardized lifecycle, portable builds across Mac, Linux, and Windows. |
| **Database** | SQLite (via `sqlite-jdbc`) | Zero-configuration, serverless, self-contained local persistence. |
| **Security / Crypto** | PBKDF2WithHmacSHA256 | Salted password and PIN hashing preventing rainbow-table attacks. |
| **Testing** | JUnit 5 Jupiter | Automated test suite validating banking operations and security. |

---

## 🚀 Prerequisites

1. **Java Development Kit (JDK 17 or higher):**
   ```bash
   java -version
   javac -version
   ```
2. **Apache Maven (v3.8+):**
   ```bash
   mvn -version
   ```

---

## 📦 Setup & Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/ppriyansh2503/Banking-Management-System.git
   cd Banking-Management-System
   ```

2. **Build the Project:**
   ```bash
   mvn clean compile
   ```

3. **Run Automated Unit Tests:**
   ```bash
   mvn test
   ```

4. **Package Executable Fat JAR:**
   ```bash
   mvn package -DskipTests
   ```

---

## 💻 How to Run

### Option 1: Using Maven Exec Plugin
From the project directory:
```bash
mvn exec:java -pl "Bank Management System 2" -Dexec.mainClass="bank.management.system.Login"
```
Or navigate into the module folder:
```bash
cd "Bank Management System 2"
mvn exec:java
```

### Option 2: Running the Standalone Executable JAR
```bash
java -jar "Bank Management System 2/target/bank-management-system-1.0.0.jar"
```

---

## 🔑 Default Credentials

### Default Administrator Credentials:
* **Username:** `admin`
* **Password:** `admin123`
*(Accessible by clicking the "ADMINISTRATOR PORTAL" button on the login screen)*

### Default Test Customer Credentials:
Created automatically upon running `mvn test` or via the **NEW USER SIGN UP** button:
* **Card Number:** Created during Sign Up (16 digits)
* **PIN:** 4 digits (e.g. `1234`)

---

## 🗄️ Database Schema & Integrity

The system automatically initializes `banking_system.db` on first startup if it does not exist:

* **`customers`:** Stores KYC details, form numbers, contact, and personal info.
* **`accounts`:** Stores unique account numbers, 16-digit card numbers, salted `pin_hash`, `pin_salt`, balance (`REAL CHECK(balance >= 0)`), and account status (`ACTIVE`, `FROZEN`, `CLOSED`).
* **`transactions`:** Immutable ledger storing timestamped debit, credit, transfer out, and transfer in entries with resulting balances.
* **`administrators`:** Secure credentials and access roles for system administrators.

---

## 🧪 Testing Suite

Automated unit and integration tests are located in `test/bank/management/system/BankingSystemTest.java`.

To execute all tests:
```bash
mvn test
```

### Tests Covered:
1. `testDatabaseInitialization`: Verifies SQLite connection and schema creation.
2. `testPinSecurityHashing`: Verifies salt generation, deterministic hashing, and constant-time verification.
3. `testAccountCreation`: Validates 10-digit account number and 16-digit card number generation.
4. `testAuthentication`: Validates login, rejects invalid cards and wrong PINs.
5. `testDeposit`: Tests balance updates and rejects zero or negative amounts.
6. `testWithdrawal`: Validates cash withdrawal and overdraft prevention.
7. `testAtomicTransfer`: Tests ACID transfer between accounts, verifies sender debit and recipient credit.
8. `testTransactionAuditHistory`: Validates ledger logging for transfers and withdrawals.
9. `testIsolatedPinChange`: Ensures changing one customer's PIN leaves other customers unaffected.
10. `testAdminControlsAndFreezing`: Tests account freezing, preventing frozen accounts from authenticating or withdrawing.

---

## ⚠️ Notes & Limitations

* **Single-Node Desktop Mode:** The application is built as a standalone desktop system using SQLite for local persistence. For multi-branch distributed deployments, the `DatabaseHelper` can be pointed to an enterprise RDBMS (e.g., PostgreSQL or MySQL).
* **Card Dispensing Simulation:** ATM card numbers and PINs are printed on screen during registration for demonstration and viva presentation purposes. In production banking, credentials are sent via secure physical mailers or SMS OTPs.

---

## 📜 License

This project is open-source under the MIT License. Developed for academic demonstration and software engineering portfolio.
