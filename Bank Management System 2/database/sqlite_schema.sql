-- SQLite Schema for Banking Management System
-- Auto-generated and maintained by DatabaseHelper

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS customers (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    form_no TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    father_name TEXT,
    dob TEXT,
    gender TEXT,
    email TEXT,
    marital_status TEXT,
    address TEXT,
    city TEXT,
    pincode TEXT,
    state TEXT,
    religion TEXT,
    category TEXT,
    income TEXT,
    education TEXT,
    occupation TEXT,
    pan TEXT,
    aadhar TEXT,
    is_senior_citizen TEXT,
    has_existing_account TEXT,
    created_at TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS accounts (
    account_id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number TEXT UNIQUE NOT NULL,
    card_number TEXT UNIQUE NOT NULL,
    pin_hash TEXT NOT NULL,
    pin_salt TEXT NOT NULL,
    account_type TEXT NOT NULL,
    balance REAL NOT NULL DEFAULT 0.0 CHECK(balance >= 0),
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    facilities TEXT,
    customer_id INTEGER NOT NULL REFERENCES customers(customer_id),
    created_at TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number TEXT NOT NULL REFERENCES accounts(account_number),
    transaction_type TEXT NOT NULL,
    amount REAL NOT NULL CHECK(amount > 0),
    balance_after REAL NOT NULL,
    description TEXT,
    related_account TEXT,
    transaction_date TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS administrators (
    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    password_salt TEXT NOT NULL,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'ADMIN',
    created_at TEXT DEFAULT (datetime('now', 'localtime'))
);

CREATE INDEX IF NOT EXISTS idx_accounts_card ON accounts(card_number);
CREATE INDEX IF NOT EXISTS idx_accounts_num ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_trans_acc ON transactions(account_number);
