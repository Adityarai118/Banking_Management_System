package bank.management.system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages zero-configuration SQLite database connections, automatic schema initialization,
 * and database lifecycle for the Banking Management System.
 */
public class DatabaseHelper {

    private static final String DB_FILE_NAME = "banking_system.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_NAME;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: SQLite JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("CRITICAL: Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    /**
     * Obtains a new connection to the SQLite database with foreign keys enabled.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes all database tables and seeds the default administrator account.
     */
    public static synchronized void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // 1. Customers Table
            stmt.execute("""
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
            """);

            // 2. Accounts Table
            stmt.execute("""
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
            """);

            // 3. Transactions Table
            stmt.execute("""
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
            """);

            // 4. Administrators Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS administrators (
                    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    password_salt TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'ADMIN',
                    created_at TEXT DEFAULT (datetime('now', 'localtime'))
                );
            """);

            // Create Indexes for performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_accounts_card ON accounts(card_number);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_accounts_num ON accounts(account_number);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_trans_acc ON transactions(account_number);");

            // Seed default admin if table is empty
            seedDefaultAdmin(conn);
        }
    }

    private static void seedDefaultAdmin(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM administrators WHERE username = 'admin'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String salt = SecurityUtil.generateSalt();
                String hash = SecurityUtil.hash("admin123", salt);
                String insertSql = "INSERT INTO administrators (username, password_hash, password_salt, full_name, role) " +
                                   "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, hash);
                    pstmt.setString(3, salt);
                    pstmt.setString(4, "System Administrator");
                    pstmt.setString(5, "SUPER_ADMIN");
                    pstmt.executeUpdate();
                    System.out.println("INFO: Default administrator account initialized (username: admin / password: admin123).");
                }
            }
        } catch (SQLException e) {
            System.err.println("WARNING: Could not seed default admin: " + e.getMessage());
        }
    }

    /**
     * Returns the absolute path of the SQLite database file for debugging/logging.
     */
    public static String getDatabasePath() {
        return new File(DB_FILE_NAME).getAbsolutePath();
    }
}
