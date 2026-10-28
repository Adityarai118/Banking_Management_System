package bank.management.system;

import bank.management.system.model.Account;
import bank.management.system.model.AdminUser;
import bank.management.system.model.TransactionRecord;
import bank.management.system.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Administrative console for managing accounts, freezing/unfreezing,
 * searching, and auditing transactions.
 */
public class AdminDashboard extends JFrame implements ActionListener {

    private final AdminUser adminUser;
    private final AdminService adminService;
    private final JTable accountTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn, resetBtn, freezeBtn, unfreezeBtn, auditBtn, refreshBtn, logoutBtn;

    public AdminDashboard(AdminUser adminUser) {
        this.adminUser = adminUser;
        this.adminService = new AdminService();

        setTitle("BANK MANAGEMENT SYSTEM - ADMINISTRATIVE CONSOLE");
        setSize(980, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. Top Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("BANK MANAGEMENT SYSTEM - ADMIN CONSOLE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Logged in as: " + adminUser.getUsername() + " (" + adminUser.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userLabel.setForeground(new Color(220, 220, 220));
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Search Bar Sub-panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search (Account / Card / Name):"));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchPanel.add(searchField);

        searchBtn = new JButton("Search");
        searchBtn.addActionListener(this);
        searchPanel.add(searchBtn);

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        searchPanel.add(resetBtn);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Accounts Table
        String[] columns = {"A/C Number", "Card Number", "Customer Name", "Type", "Balance (Rs.)", "Status", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        accountTable = new JTable(tableModel);
        accountTable.setRowHeight(24);
        accountTable.setFont(new Font("Arial", Font.PLAIN, 12));
        accountTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Action Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        freezeBtn = new JButton("Freeze Account");
        freezeBtn.setBackground(new Color(178, 34, 34));
        freezeBtn.setForeground(Color.WHITE);
        freezeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(freezeBtn);

        unfreezeBtn = new JButton("Unfreeze Account");
        unfreezeBtn.setBackground(new Color(46, 139, 87));
        unfreezeBtn.setForeground(Color.WHITE);
        unfreezeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(unfreezeBtn);

        auditBtn = new JButton("Audit Transactions");
        auditBtn.setBackground(new Color(70, 130, 180));
        auditBtn.setForeground(Color.WHITE);
        auditBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(auditBtn);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(refreshBtn);

        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.DARK_GRAY);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(logoutBtn);

        freezeBtn.addActionListener(this);
        unfreezeBtn.addActionListener(this);
        auditBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        add(bottomPanel, BorderLayout.SOUTH);

        // Initial Data Load
        loadAccounts(null);
    }

    private void loadAccounts(String query) {
        tableModel.setRowCount(0);
        try {
            List<Account> list = adminService.searchAccounts(query);
            for (Account a : list) {
                tableModel.addRow(new Object[]{
                        a.getAccountNumber(),
                        a.getCardNumber(),
                        a.getCustomerName() != null ? a.getCustomerName() : "N/A",
                        a.getAccountType(),
                        String.format("%.2f", a.getBalance()),
                        a.getStatus(),
                        a.getCreatedAt()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load accounts: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedAccountNumber() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) tableModel.getValueAt(selectedRow, 0);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchBtn) {
            loadAccounts(searchField.getText().trim());

        } else if (ae.getSource() == resetBtn) {
            searchField.setText("");
            loadAccounts(null);

        } else if (ae.getSource() == freezeBtn) {
            String acc = getSelectedAccountNumber();
            if (acc != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Freeze Account " + acc + "? Customer will not be able to withdraw or transfer.",
                        "Confirm Freeze", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        adminService.setAccountStatus(acc, "FROZEN");
                        JOptionPane.showMessageDialog(this, "Account " + acc + " has been FROZEN.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                        loadAccounts(searchField.getText().trim());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } else if (ae.getSource() == unfreezeBtn) {
            String acc = getSelectedAccountNumber();
            if (acc != null) {
                try {
                    adminService.setAccountStatus(acc, "ACTIVE");
                    JOptionPane.showMessageDialog(this, "Account " + acc + " has been restored to ACTIVE status.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                    loadAccounts(searchField.getText().trim());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }

        } else if (ae.getSource() == auditBtn) {
            String acc = getSelectedAccountNumber();
            if (acc != null) {
                showAuditDialog(acc);
            }

        } else if (ae.getSource() == refreshBtn) {
            loadAccounts(searchField.getText().trim());

        } else if (ae.getSource() == logoutBtn) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }

    private void showAuditDialog(String accountNumber) {
        JDialog dialog = new JDialog(this, "Transaction Audit Log - A/C " + accountNumber, true);
        dialog.setSize(750, 450);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Tx ID", "Type", "Amount (Rs)", "Balance After", "Description", "Related A/C", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        try {
            List<TransactionRecord> list = adminService.getAuditTransactions(accountNumber);
            for (TransactionRecord r : list) {
                model.addRow(new Object[]{
                        r.getTransactionId(),
                        r.getTransactionType(),
                        String.format("%.2f", r.getAmount()),
                        String.format("%.2f", r.getBalanceAfter()),
                        r.getDescription(),
                        r.getRelatedAccount() != null ? r.getRelatedAccount() : "-",
                        r.getTransactionDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage());
        }

        JTable table = new JTable(model);
        table.setRowHeight(22);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel p = new JPanel();
        p.add(close);
        dialog.add(p, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard(new AdminUser(1, "admin", "System Administrator", "ADMIN")).setVisible(true));
    }
}
