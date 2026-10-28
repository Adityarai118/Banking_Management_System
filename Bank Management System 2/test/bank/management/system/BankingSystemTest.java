package bank.management.system;

import bank.management.system.model.Account;
import bank.management.system.model.Customer;
import bank.management.system.model.TransactionRecord;
import bank.management.system.service.AccountService;
import bank.management.system.service.AdminService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite verifying banking business logic, security hashing,
 * atomic transactions, and administrative controls.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankingSystemTest {

    private static AccountService accountService;
    private static AdminService adminService;

    private static Account testAccount1;
    private static Account testAccount2;

    @BeforeAll
    public static void setup() throws SQLException {
        DatabaseHelper.initializeDatabase();
        accountService = new AccountService();
        adminService = new AdminService();
    }

    @Test
    @Order(1)
    public void testDatabaseInitialization() {
        assertNotNull(DatabaseHelper.getDatabasePath(), "Database file path should not be null.");
        assertDoesNotThrow(() -> {
            try (var conn = DatabaseHelper.getConnection()) {
                assertNotNull(conn, "Connection should be valid and open.");
            }
        });
    }

    @Test
    @Order(2)
    public void testPinSecurityHashing() {
        String pin = "4589";
        String salt = SecurityUtil.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());

        String hash1 = SecurityUtil.hash(pin, salt);
        String hash2 = SecurityUtil.hash(pin, salt);

        assertEquals(hash1, hash2, "Hashing same PIN with same salt must yield deterministic output.");
        assertTrue(SecurityUtil.verify(pin, salt, hash1), "Verification should succeed for correct PIN.");
        assertFalse(SecurityUtil.verify("0000", salt, hash1), "Verification must fail for wrong PIN.");
        assertFalse(SecurityUtil.verify("4588", salt, hash1), "Verification must fail for off-by-one PIN.");
    }

    @Test
    @Order(3)
    public void testAccountCreation() throws SQLException {
        String uid = UUID.randomUUID().toString().substring(0, 8);
        Customer c1 = new Customer("FORM_" + uid + "_1", "Alice Smith", "Robert Smith", "1995-05-12",
                "Female", "alice." + uid + "@example.com", "Single", "123 Park Ave", "New York", "10001", "NY");
        testAccount1 = accountService.createAccount(c1, "Saving Account", "ATM Card, Internet Banking", "1234");

        assertNotNull(testAccount1);
        assertNotNull(testAccount1.getAccountNumber());
        assertEquals(10, testAccount1.getAccountNumber().length(), "Account number must be 10 digits.");
        assertEquals(16, testAccount1.getCardNumber().length(), "Card number must be 16 digits.");
        assertEquals(0.0, testAccount1.getBalance(), 0.001);
        assertEquals("ACTIVE", testAccount1.getStatus());

        Customer c2 = new Customer("FORM_" + uid + "_2", "Bob Jones", "David Jones", "1992-08-20",
                "Male", "bob." + uid + "@example.com", "Married", "456 Oak Lane", "Boston", "02108", "MA");
        testAccount2 = accountService.createAccount(c2, "Current Account", "ATM Card", "5678");

        assertNotNull(testAccount2);
        assertNotEquals(testAccount1.getAccountNumber(), testAccount2.getAccountNumber(), "Account numbers must be unique.");
        assertNotEquals(testAccount1.getCardNumber(), testAccount2.getCardNumber(), "Card numbers must be unique.");
    }

    @Test
    @Order(4)
    public void testAuthentication() throws SQLException {
        // Successful login
        Account authenticated = accountService.authenticate(testAccount1.getCardNumber(), "1234");
        assertNotNull(authenticated);
        assertEquals(testAccount1.getAccountNumber(), authenticated.getAccountNumber());

        // Wrong PIN
        assertThrows(SecurityException.class, () ->
                accountService.authenticate(testAccount1.getCardNumber(), "9999"));

        // Non-existent Card
        assertThrows(SecurityException.class, () ->
                accountService.authenticate("9999999999999999", "1234"));
    }

    @Test
    @Order(5)
    public void testDeposit() throws SQLException {
        double newBalance = accountService.deposit(testAccount1.getAccountNumber(), 5000.0, "Initial Deposit");
        assertEquals(5000.0, newBalance, 0.001);
        assertEquals(5000.0, accountService.getBalance(testAccount1.getAccountNumber()), 0.001);

        // Reject zero or negative deposit
        assertThrows(IllegalArgumentException.class, () ->
                accountService.deposit(testAccount1.getAccountNumber(), 0.0, "Invalid"));
        assertThrows(IllegalArgumentException.class, () ->
                accountService.deposit(testAccount1.getAccountNumber(), -500.0, "Invalid"));
    }

    @Test
    @Order(6)
    public void testWithdrawal() throws SQLException {
        double newBalance = accountService.withdraw(testAccount1.getAccountNumber(), 1500.0, "ATM Cash Withdrawal");
        assertEquals(3500.0, newBalance, 0.001);
        assertEquals(3500.0, accountService.getBalance(testAccount1.getAccountNumber()), 0.001);

        // Overdraft prevention
        assertThrows(IllegalStateException.class, () ->
                accountService.withdraw(testAccount1.getAccountNumber(), 10000.0, "Overdraft Attempt"));

        // Reject zero or negative
        assertThrows(IllegalArgumentException.class, () ->
                accountService.withdraw(testAccount1.getAccountNumber(), -200.0, "Invalid"));
    }

    @Test
    @Order(7)
    public void testAtomicTransfer() throws SQLException {
        double aliceInitial = accountService.getBalance(testAccount1.getAccountNumber()); // 3500
        double bobInitial = accountService.getBalance(testAccount2.getAccountNumber());   // 0

        // Transfer 1200 from Alice to Bob
        accountService.transfer(testAccount1.getAccountNumber(), testAccount2.getAccountNumber(), 1200.0, "Rent Payment");

        assertEquals(aliceInitial - 1200.0, accountService.getBalance(testAccount1.getAccountNumber()), 0.001);
        assertEquals(bobInitial + 1200.0, accountService.getBalance(testAccount2.getAccountNumber()), 0.001);

        // Insufficient funds for transfer
        assertThrows(IllegalStateException.class, () ->
                accountService.transfer(testAccount1.getAccountNumber(), testAccount2.getAccountNumber(), 50000.0, "Big Transfer"));

        // Same account transfer rejection
        assertThrows(IllegalArgumentException.class, () ->
                accountService.transfer(testAccount1.getAccountNumber(), testAccount1.getAccountNumber(), 100.0, "Self Transfer"));

        // Non-existent target account rejection
        assertThrows(IllegalArgumentException.class, () ->
                accountService.transfer(testAccount1.getAccountNumber(), "0000000000", 100.0, "Ghost Transfer"));
    }

    @Test
    @Order(8)
    public void testTransactionAuditHistory() throws SQLException {
        List<TransactionRecord> list = accountService.getTransactions(testAccount1.getAccountNumber(), 10);
        assertNotNull(list);
        assertTrue(list.size() >= 3, "Account 1 should have deposit, withdrawal, and transfer records.");

        // First item is most recent (TRANSFER_OUT)
        TransactionRecord mostRecent = list.get(0);
        assertEquals("TRANSFER_OUT", mostRecent.getTransactionType());
        assertEquals(1200.0, mostRecent.getAmount(), 0.001);
        assertEquals(testAccount2.getAccountNumber(), mostRecent.getRelatedAccount());
    }

    @Test
    @Order(9)
    public void testIsolatedPinChange() throws SQLException {
        // Change Alice's PIN from 1234 to 4321
        accountService.changePin(testAccount1.getAccountNumber(), "1234", "4321");

        // Old PIN must fail
        assertThrows(SecurityException.class, () ->
                accountService.authenticate(testAccount1.getCardNumber(), "1234"));

        // New PIN must succeed
        assertNotNull(accountService.authenticate(testAccount1.getCardNumber(), "4321"));

        // Verify Bob's PIN (5678) is completely unaffected
        assertNotNull(accountService.authenticate(testAccount2.getCardNumber(), "5678"));
    }

    @Test
    @Order(10)
    public void testAdminControlsAndFreezing() throws SQLException {
        // Admin authentication
        var admin = adminService.authenticate("admin", "admin123");
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());

        // Freeze Bob's account
        adminService.setAccountStatus(testAccount2.getAccountNumber(), "FROZEN");
        Account frozenBob = accountService.getAccountByNumber(testAccount2.getAccountNumber());
        assertTrue(frozenBob.isFrozen());

        // Frozen login attempt must be rejected
        assertThrows(IllegalStateException.class, () ->
                accountService.authenticate(testAccount2.getCardNumber(), "5678"));

        // Withdrawal from frozen account must be blocked
        assertThrows(IllegalStateException.class, () ->
                accountService.withdraw(testAccount2.getAccountNumber(), 100.0, "Blocked"));

        // Unfreeze account
        adminService.setAccountStatus(testAccount2.getAccountNumber(), "ACTIVE");
        Account activeBob = accountService.getAccountByNumber(testAccount2.getAccountNumber());
        assertTrue(activeBob.isActive());
        assertNotNull(accountService.authenticate(testAccount2.getCardNumber(), "5678"));
    }
}
