package bank.management.system.model;

/**
 * Model representing a bank account with associated security details.
 */
public class Account {

    private int accountId;
    private String accountNumber;
    private String cardNumber;
    private String pinHash;
    private String pinSalt;
    private String accountType;
    private double balance;
    private String status; // ACTIVE, FROZEN, CLOSED
    private String facilities;
    private int customerId;
    private String createdAt;

    // Display / helper field
    private String customerName;

    public Account() {}

    public Account(String accountNumber, String cardNumber, String pinHash, String pinSalt,
                   String accountType, double balance, String status, String facilities, int customerId) {
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.pinHash = pinHash;
        this.pinSalt = pinSalt;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.facilities = facilities;
        this.customerId = customerId;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public String getPinSalt() { return pinSalt; }
    public void setPinSalt(String pinSalt) { this.pinSalt = pinSalt; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isFrozen() {
        return "FROZEN".equalsIgnoreCase(status);
    }
}
