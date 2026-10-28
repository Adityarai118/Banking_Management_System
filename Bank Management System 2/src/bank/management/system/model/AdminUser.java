package bank.management.system.model;

/**
 * Model representing an administrative user.
 */
public class AdminUser {

    private int adminId;
    private String username;
    private String fullName;
    private String role;
    private String createdAt;

    public AdminUser() {}

    public AdminUser(int adminId, String username, String fullName, String role) {
        this.adminId = adminId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
