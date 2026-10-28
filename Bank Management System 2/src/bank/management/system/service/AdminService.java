package bank.management.system.service;

import bank.management.system.DatabaseHelper;
import bank.management.system.SecurityUtil;
import bank.management.system.model.Account;
import bank.management.system.model.AdminUser;
import bank.management.system.model.TransactionRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for administrative operations, user audits, account freezing/unfreezing,
 * and system oversight.
 */
public class AdminService {

    /**
     * Authenticates an administrator using constant-time hash verification.
     */
    public AdminUser authenticate(String username, String plainPassword) throws SQLException {
        if (username == null || plainPassword == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be empty.");
        }

        String sql = "SELECT * FROM administrators WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SecurityException("Invalid administrator credentials.");
                }

                String storedHash = rs.getString("password_hash");
                String storedSalt = rs.getString("password_salt");

                if (!SecurityUtil.verify(plainPassword, storedSalt, storedHash)) {
                    throw new SecurityException("Invalid administrator credentials.");
                }

                AdminUser user = new AdminUser();
                user.setAdminId(rs.getInt("admin_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        }
    }

    /**
     * Retrieves all accounts in the system with owner details.
     */
    public List<Account> getAllAccounts() throws SQLException {
        return searchAccounts(null);
    }

    /**
     * Searches accounts by keyword matching account number, card number, or customer name.
     */
    public List<Account> searchAccounts(String keyword) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT a.*, c.name AS customer_name
            FROM accounts a
            JOIN customers c ON a.customer_id = c.customer_id
        """);

        boolean hasFilter = keyword != null && !keyword.trim().isEmpty();
        if (hasFilter) {
            sql.append("""
                WHERE a.account_number LIKE ?
                   OR a.card_number LIKE ?
                   OR c.name LIKE ?
            """);
        }
        sql.append(" ORDER BY a.account_id DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (hasFilter) {
                String pattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Account a = new Account();
                    a.setAccountId(rs.getInt("account_id"));
                    a.setAccountNumber(rs.getString("account_number"));
                    a.setCardNumber(rs.getString("card_number"));
                    a.setAccountType(rs.getString("account_type"));
                    a.setBalance(rs.getDouble("balance"));
                    a.setStatus(rs.getString("status"));
                    a.setFacilities(rs.getString("facilities"));
                    a.setCustomerId(rs.getInt("customer_id"));
                    a.setCreatedAt(rs.getString("created_at"));
                    a.setCustomerName(rs.getString("customer_name"));
                    accounts.add(a);
                }
            }
        }
        return accounts;
    }

    /**
     * Changes an account status (e.g. FROZEN, ACTIVE, CLOSED).
     */
    public void setAccountStatus(String accountNumber, String status) throws SQLException {
        if (!"ACTIVE".equalsIgnoreCase(status) && !"FROZEN".equalsIgnoreCase(status) && !"CLOSED".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toUpperCase());
            pstmt.setString(2, accountNumber);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Account not found: " + accountNumber);
            }
        }
    }

    /**
     * Retrieves all transaction audit entries for a specific account.
     */
    public List<TransactionRecord> getAuditTransactions(String accountNumber) throws SQLException {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions
            WHERE account_number = ?
            ORDER BY transaction_id DESC
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
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
}
