package bank.management.system.service;

import bank.management.system.DatabaseHelper;
import bank.management.system.SecurityUtil;
import bank.management.system.model.Account;
import bank.management.system.model.Customer;
import bank.management.system.model.TransactionRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core business service handling account creation, authentication,
 * balance inquiries, deposits, withdrawals, and inter-account atomic transfers.
 */
public class AccountService {

    private static final Random RANDOM = new Random();

    /**
     * Creates a new customer record and associated bank account with unique credentials.
     */
    public Account createAccount(Customer customer, String accountType, String facilities, String plainPin) throws SQLException {
        if (plainPin == null || !plainPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }

        String accountNumber = generateUniqueAccountNumber();
        String cardNumber = generateUniqueCardNumber();
        String salt = SecurityUtil.generateSalt();
        String pinHash = SecurityUtil.hash(plainPin, salt);

        String insertCustomerSql = """
            INSERT INTO customers (
                form_no, name, father_name, dob, gender, email, marital_status,
                address, city, pincode, state, religion, category, income,
                education, occupation, pan, aadhar, is_senior_citizen, has_existing_account
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String insertAccountSql = """
            INSERT INTO accounts (
                account_number, card_number, pin_hash, pin_salt,
                account_type, balance, status, facilities, customer_id
            ) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', ?, ?)
        """;

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int customerId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, customer.getFormNo());
                    pstmt.setString(2, customer.getName());
                    pstmt.setString(3, customer.getFatherName());
                    pstmt.setString(4, customer.getDob());
                    pstmt.setString(5, customer.getGender());
                    pstmt.setString(6, customer.getEmail());
                    pstmt.setString(7, customer.getMaritalStatus());
                    pstmt.setString(8, customer.getAddress());
                    pstmt.setString(9, customer.getCity());
                    pstmt.setString(10, customer.getPincode());
                    pstmt.setString(11, customer.getState());
                    pstmt.setString(12, customer.getReligion());
                    pstmt.setString(13, customer.getCategory());
                    pstmt.setString(14, customer.getIncome());
                    pstmt.setString(15, customer.getEducation());
                    pstmt.setString(16, customer.getOccupation());
                    pstmt.setString(17, customer.getPan());
                    pstmt.setString(18, customer.getAadhar());
                    pstmt.setString(19, customer.getSeniorCitizen());
                    pstmt.setString(20, customer.getExistingAccount());
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            customerId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to obtain generated customer ID.");
                        }
                    }
                }

                Account account = new Account(
                        accountNumber, cardNumber, pinHash, salt,
                        accountType, 0.0, "ACTIVE", facilities, customerId
                );

                try (PreparedStatement pstmt = conn.prepareStatement(insertAccountSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setString(2, cardNumber);
                    pstmt.setString(3, pinHash);
                    pstmt.setString(4, salt);
                    pstmt.setString(5, accountType);
                    pstmt.setDouble(6, 0.0);
                    pstmt.setString(7, facilities);
                    pstmt.setInt(8, customerId);
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            account.setAccountId(rs.getInt(1));
                        }
                    }
                }

                conn.commit();
                account.setCustomerName(customer.getName());
                return account;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Authenticates a user by card number and plain PIN using constant-time hash comparison.
     */
    public Account authenticate(String cardNumber, String plainPin) throws SQLException {
        if (cardNumber == null || cardNumber.trim().isEmpty() || plainPin == null || plainPin.trim().isEmpty()) {
            throw new IllegalArgumentException("Card Number and PIN must not be empty.");
        }

        String sql = """
            SELECT a.*, c.name AS customer_name
            FROM accounts a
            JOIN customers c ON a.customer_id = c.customer_id
            WHERE a.card_number = ?
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SecurityException("Incorrect Card Number or PIN.");
                }

                String status = rs.getString("status");
                if ("FROZEN".equalsIgnoreCase(status)) {
                    throw new IllegalStateException("Your account is FROZEN by bank administration. Please contact your branch.");
                }
                if ("CLOSED".equalsIgnoreCase(status)) {
                    throw new IllegalStateException("This account is closed.");
                }

                String storedHash = rs.getString("pin_hash");
                String storedSalt = rs.getString("pin_salt");

                if (!SecurityUtil.verify(plainPin.trim(), storedSalt, storedHash)) {
                    throw new SecurityException("Incorrect Card Number or PIN.");
                }

                return mapAccount(rs);
            }
        }
    }

    /**
     * Deposits funds into an account and writes an immutable audit record.
     */
    public double deposit(String accountNumber, double amount, String description) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account account = getAccountForUpdate(conn, accountNumber);
                if (account == null) {
                    throw new IllegalArgumentException("Account not found: " + accountNumber);
                }
                if (!account.isActive()) {
                    throw new IllegalStateException("Account is not active.");
                }

                double newBalance = account.getBalance() + amount;

                // Update account balance
                String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, newBalance);
                    pstmt.setString(2, accountNumber);
                    pstmt.executeUpdate();
                }

                // Insert transaction record
                String transSql = """
                    INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description)
                    VALUES (?, 'DEPOSIT', ?, ?, ?)
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(transSql)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setDouble(2, amount);
                    pstmt.setDouble(3, newBalance);
                    pstmt.setString(4, description != null ? description : "Cash Deposit");
                    pstmt.executeUpdate();
                }

                conn.commit();
                return newBalance;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Withdraws funds from an account after verifying sufficient balance.
     */
    public double withdraw(String accountNumber, double amount, String description) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account account = getAccountForUpdate(conn, accountNumber);
                if (account == null) {
                    throw new IllegalArgumentException("Account not found: " + accountNumber);
                }
                if (!account.isActive()) {
                    throw new IllegalStateException("Account is not active.");
                }
                if (account.getBalance() < amount) {
                    throw new IllegalStateException(String.format("Insufficient balance. Current balance is Rs. %.2f", account.getBalance()));
                }

                double newBalance = account.getBalance() - amount;

                String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, newBalance);
                    pstmt.setString(2, accountNumber);
                    pstmt.executeUpdate();
                }

                String transSql = """
                    INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description)
                    VALUES (?, 'WITHDRAWAL', ?, ?, ?)
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(transSql)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setDouble(2, amount);
                    pstmt.setDouble(3, newBalance);
                    pstmt.setString(4, description != null ? description : "Cash Withdrawal");
                    pstmt.executeUpdate();
                }

                conn.commit();
                return newBalance;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Atomically transfers money between two accounts.
     * Guaranteed ACID execution: both accounts update together or neither does.
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount, String remarks) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (fromAccountNumber == null || toAccountNumber == null || fromAccountNumber.trim().equalsIgnoreCase(toAccountNumber.trim())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account fromAccount = getAccountForUpdate(conn, fromAccountNumber);
                if (fromAccount == null) {
                    throw new IllegalArgumentException("Source account not found: " + fromAccountNumber);
                }
                if (!fromAccount.isActive()) {
                    throw new IllegalStateException("Source account is not active.");
                }

                Account toAccount = getAccountForUpdate(conn, toAccountNumber);
                if (toAccount == null) {
                    throw new IllegalArgumentException("Destination account not found: " + toAccountNumber);
                }
                if (!toAccount.isActive()) {
                    throw new IllegalStateException("Destination account is not active or is frozen.");
                }

                if (fromAccount.getBalance() < amount) {
                    throw new IllegalStateException(String.format("Insufficient balance. Current balance: Rs. %.2f", fromAccount.getBalance()));
                }

                double fromNewBalance = fromAccount.getBalance() - amount;
                double toNewBalance = toAccount.getBalance() + amount;

                // 1. Deduct from source
                String deductSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deductSql)) {
                    pstmt.setDouble(1, fromNewBalance);
                    pstmt.setString(2, fromAccountNumber);
                    pstmt.executeUpdate();
                }

                // 2. Add to destination
                String creditSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(creditSql)) {
                    pstmt.setDouble(1, toNewBalance);
                    pstmt.setString(2, toAccountNumber);
                    pstmt.executeUpdate();
                }

                // 3. Record TRANSFER_OUT for source
                String transOutSql = """
                    INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description, related_account)
                    VALUES (?, 'TRANSFER_OUT', ?, ?, ?, ?)
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(transOutSql)) {
                    pstmt.setString(1, fromAccountNumber);
                    pstmt.setDouble(2, amount);
                    pstmt.setDouble(3, fromNewBalance);
                    pstmt.setString(4, remarks != null ? remarks : "Fund Transfer to " + toAccountNumber);
                    pstmt.setString(5, toAccountNumber);
                    pstmt.executeUpdate();
                }

                // 4. Record TRANSFER_IN for destination
                String transInSql = """
                    INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description, related_account)
                    VALUES (?, 'TRANSFER_IN', ?, ?, ?, ?)
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(transInSql)) {
                    pstmt.setString(1, toAccountNumber);
                    pstmt.setDouble(2, amount);
                    pstmt.setDouble(3, toNewBalance);
                    pstmt.setString(4, remarks != null ? remarks : "Fund Transfer from " + fromAccountNumber);
                    pstmt.setString(5, fromAccountNumber);
                    pstmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Changes the PIN of a specific account without modifying any other customer's credentials.
     */
    public void changePin(String accountNumber, String oldPin, String newPin) throws SQLException {
        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("New PIN must be exactly 4 digits.");
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            Account account = getAccountByNumber(accountNumber);
            if (account == null) {
                throw new IllegalArgumentException("Account not found.");
            }

            if (!SecurityUtil.verify(oldPin, account.getPinSalt(), account.getPinHash())) {
                throw new SecurityException("Current PIN is incorrect.");
            }

            String newSalt = SecurityUtil.generateSalt();
            String newHash = SecurityUtil.hash(newPin, newSalt);

            String updateSql = "UPDATE accounts SET pin_hash = ?, pin_salt = ? WHERE account_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, newHash);
                pstmt.setString(2, newSalt);
                pstmt.setString(3, accountNumber);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves the current balance for an account.
     */
    public double getBalance(String accountNumber) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        throw new IllegalArgumentException("Account not found: " + accountNumber);
    }

    /**
     * Retrieves the recent transaction history for an account.
     */
    public List<TransactionRecord> getTransactions(String accountNumber, int limit) throws SQLException {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions
            WHERE account_number = ?
            ORDER BY transaction_id DESC
            LIMIT ?
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransactionRecord tr = new TransactionRecord();
                    tr.setTransactionId(rs.getInt("transaction_id"));
                    tr.setAccountNumber(rs.getString("account_number"));
                    tr.setTransactionType(rs.getString("transaction_type"));
                    tr.setAmount(rs.getDouble("amount"));
                    tr.setBalanceAfter(rs.getDouble("balance_after"));
                    tr.setDescription(rs.getString("description"));
                    tr.setRelatedAccount(rs.getString("related_account"));
                    tr.setTransactionDate(rs.getString("transaction_date"));
                    list.add(tr);
                }
            }
        }
        return list;
    }

    /**
     * Updates permitted customer details.
     */
    public void updateCustomerDetails(String accountNumber, String name, String email, String address,
                                      String city, String state, String pincode) throws SQLException {
        String sql = """
            UPDATE customers SET name = ?, email = ?, address = ?, city = ?, state = ?, pincode = ?
            WHERE customer_id = (SELECT customer_id FROM accounts WHERE account_number = ?)
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, address);
            pstmt.setString(4, city);
            pstmt.setString(5, state);
            pstmt.setString(6, pincode);
            pstmt.setString(7, accountNumber);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves customer profile information by account number.
     */
    public Customer getCustomerByAccount(String accountNumber) throws SQLException {
        String sql = """
            SELECT c.* FROM customers c
            JOIN accounts a ON c.customer_id = a.customer_id
            WHERE a.account_number = ?
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerId(rs.getInt("customer_id"));
                    c.setFormNo(rs.getString("form_no"));
                    c.setName(rs.getString("name"));
                    c.setFatherName(rs.getString("father_name"));
                    c.setDob(rs.getString("dob"));
                    c.setGender(rs.getString("gender"));
                    c.setEmail(rs.getString("email"));
                    c.setMaritalStatus(rs.getString("marital_status"));
                    c.setAddress(rs.getString("address"));
                    c.setCity(rs.getString("city"));
                    c.setPincode(rs.getString("pincode"));
                    c.setState(rs.getString("state"));
                    return c;
                }
            }
        }
        return null;
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = """
            SELECT a.*, c.name AS customer_name
            FROM accounts a
            JOIN customers c ON a.customer_id = c.customer_id
            WHERE a.account_number = ?
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    public Account getAccountByCard(String cardNumber) throws SQLException {
        String sql = """
            SELECT a.*, c.name AS customer_name
            FROM accounts a
            JOIN customers c ON a.customer_id = c.customer_id
            WHERE a.card_number = ?
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    private Account getAccountForUpdate(Connection conn, String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Account a = new Account();
                    a.setAccountId(rs.getInt("account_id"));
                    a.setAccountNumber(rs.getString("account_number"));
                    a.setCardNumber(rs.getString("card_number"));
                    a.setPinHash(rs.getString("pin_hash"));
                    a.setPinSalt(rs.getString("pin_salt"));
                    a.setAccountType(rs.getString("account_type"));
                    a.setBalance(rs.getDouble("balance"));
                    a.setStatus(rs.getString("status"));
                    a.setFacilities(rs.getString("facilities"));
                    a.setCustomerId(rs.getInt("customer_id"));
                    return a;
                }
            }
        }
        return null;
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setCardNumber(rs.getString("card_number"));
        account.setPinHash(rs.getString("pin_hash"));
        account.setPinSalt(rs.getString("pin_salt"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));
        account.setStatus(rs.getString("status"));
        account.setFacilities(rs.getString("facilities"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setCreatedAt(rs.getString("created_at"));
        try {
            account.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException ignored) {}
        return account;
    }

    private String generateUniqueAccountNumber() throws SQLException {
        String num;
        do {
            long val = 1000000000L + (long)(RANDOM.nextDouble() * 8999999999L);
            num = String.valueOf(val);
        } while (getAccountByNumber(num) != null);
        return num;
    }

    private String generateUniqueCardNumber() throws SQLException {
        String card;
        do {
            long val = 5040936000000000L + (long)(RANDOM.nextDouble() * 99999999L);
            card = String.valueOf(val);
        } while (getAccountByCard(card) != null);
        return card;
    }
}
