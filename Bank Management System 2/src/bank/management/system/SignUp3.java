package bank.management.system;

import bank.management.system.model.Account;
import bank.management.system.model.Customer;
import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Step 3 of the registration wizard: Account Configuration and Credential Issuance.
 */
public class SignUp3 extends JFrame implements ActionListener {

    private final Customer customer;
    private final JRadioButton r1, r2, r3, r4;
    private final JCheckBox c1, c2, c3, c4, c5, c6, c7;
    private final JTextField pinField;
    private final JButton b1, b2;
    private final AccountService accountService;

    public SignUp3(Customer customer) {
        this.customer = customer != null ? customer : new Customer();
        this.accountService = new AccountService();

        setTitle("NEW ACCOUNT APPLICATION FORM - STEP 3");
        setLayout(null);

        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/logo.jpg"));
            Image i2 = i1.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(i2));
            label.setBounds(25, 10, 90, 90);
            add(label);
        } catch (Exception ignored) {}

        JLabel l1 = new JLabel("Page 3: Account & Credential Details");
        l1.setFont(new Font("Raleway", Font.BOLD, 22));
        l1.setBounds(250, 40, 450, 30);
        add(l1);

        // Account Type
        JLabel l2 = new JLabel("Account Type:");
        l2.setFont(new Font("Raleway", Font.BOLD, 16));
        l2.setBounds(100, 110, 200, 25);
        add(l2);

        r1 = new JRadioButton("Saving Account");
        r1.setFont(new Font("Raleway", Font.PLAIN, 14));
        r1.setBackground(Color.WHITE);
        r1.setBounds(100, 140, 160, 25);
        add(r1);

        r2 = new JRadioButton("Fixed Deposit Account");
        r2.setFont(new Font("Raleway", Font.PLAIN, 14));
        r2.setBackground(Color.WHITE);
        r2.setBounds(300, 140, 200, 25);
        add(r2);

        r3 = new JRadioButton("Current Account");
        r3.setFont(new Font("Raleway", Font.PLAIN, 14));
        r3.setBackground(Color.WHITE);
        r3.setBounds(100, 170, 160, 25);
        add(r3);

        r4 = new JRadioButton("Recurring Deposit Account");
        r4.setFont(new Font("Raleway", Font.PLAIN, 14));
        r4.setBackground(Color.WHITE);
        r4.setBounds(300, 170, 220, 25);
        add(r4);

        ButtonGroup acheck = new ButtonGroup();
        acheck.add(r1);
        acheck.add(r2);
        acheck.add(r3);
        acheck.add(r4);
        r1.setSelected(true);

        // Card Info
        JLabel l3 = new JLabel("Card Number:");
        l3.setFont(new Font("Raleway", Font.BOLD, 16));
        l3.setBounds(100, 220, 200, 25);
        add(l3);

        JLabel l4 = new JLabel("XXXX-XXXX-XXXX-(Auto-Generated 16 Digits)");
        l4.setFont(new Font("Raleway", Font.BOLD, 14));
        l4.setForeground(Color.DARK_GRAY);
        l4.setBounds(300, 220, 380, 25);
        add(l4);

        // PIN Setup
        JLabel l6 = new JLabel("Set 4-Digit ATM PIN:");
        l6.setFont(new Font("Raleway", Font.BOLD, 16));
        l6.setBounds(100, 260, 200, 25);
        add(l6);

        Random ran = new Random();
        int defaultPin = 1000 + ran.nextInt(9000);
        pinField = new JTextField(String.valueOf(defaultPin));
        pinField.setFont(new Font("Raleway", Font.BOLD, 16));
        pinField.setBounds(300, 260, 100, 28);
        add(pinField);

        JLabel pinHint = new JLabel("(4-digit numerical security PIN)");
        pinHint.setFont(new Font("Raleway", Font.ITALIC, 12));
        pinHint.setForeground(Color.GRAY);
        pinHint.setBounds(415, 260, 250, 28);
        add(pinHint);

        // Services
        JLabel l9 = new JLabel("Services Required:");
        l9.setFont(new Font("Raleway", Font.BOLD, 16));
        l9.setBounds(100, 310, 200, 25);
        add(l9);

        c1 = new JCheckBox("ATM / Debit CARD", true);
        c1.setBackground(Color.WHITE);
        c1.setFont(new Font("Raleway", Font.PLAIN, 13));
        c1.setBounds(100, 340, 180, 25);
        add(c1);

        c2 = new JCheckBox("Internet Banking", true);
        c2.setBackground(Color.WHITE);
        c2.setFont(new Font("Raleway", Font.PLAIN, 13));
        c2.setBounds(300, 340, 180, 25);
        add(c2);

        c3 = new JCheckBox("Mobile Banking", true);
        c3.setBackground(Color.WHITE);
        c3.setFont(new Font("Raleway", Font.PLAIN, 13));
        c3.setBounds(100, 370, 180, 25);
        add(c3);

        c4 = new JCheckBox("EMAIL Alerts", true);
        c4.setBackground(Color.WHITE);
        c4.setFont(new Font("Raleway", Font.PLAIN, 13));
        c4.setBounds(300, 370, 180, 25);
        add(c4);

        c5 = new JCheckBox("Cheque Book");
        c5.setBackground(Color.WHITE);
        c5.setFont(new Font("Raleway", Font.PLAIN, 13));
        c5.setBounds(100, 400, 180, 25);
        add(c5);

        c6 = new JCheckBox("E-Statement", true);
        c6.setBackground(Color.WHITE);
        c6.setFont(new Font("Raleway", Font.PLAIN, 13));
        c6.setBounds(300, 400, 180, 25);
        add(c6);

        // Declaration
        c7 = new JCheckBox("I hereby declare that the information provided is true and accurate.", true);
        c7.setBackground(Color.WHITE);
        c7.setFont(new Font("Raleway", Font.BOLD, 12));
        c7.setBounds(100, 450, 580, 25);
        add(c7);

        // Buttons
        b1 = new JButton("SUBMIT & CREATE ACCOUNT");
        b1.setFont(new Font("Raleway", Font.BOLD, 13));
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.setBounds(200, 510, 240, 35);
        add(b1);

        b2 = new JButton("CANCEL");
        b2.setFont(new Font("Raleway", Font.BOLD, 13));
        b2.setBackground(Color.GRAY);
        b2.setForeground(Color.WHITE);
        b2.setBounds(460, 510, 120, 35);
        add(b2);

        b1.addActionListener(this);
        b2.addActionListener(this);

        getContentPane().setBackground(Color.WHITE);
        setSize(780, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public SignUp3(String formno) {
        this(new Customer(formno, "", "", "", "", "", "", "", "", "", ""));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b2) {
            setVisible(false);
            new Login().setVisible(true);
            return;
        }

        if (!c7.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please accept the declaration to proceed.", "Declaration Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String atype = r1.isSelected() ? "Saving Account"
                : (r2.isSelected() ? "Fixed Deposit Account"
                : (r3.isSelected() ? "Current Account" : "Recurring Deposit Account"));

        String pin = pinField.getText().trim();
        if (!pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "PIN must be exactly 4 numeric digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder facilities = new StringBuilder();
        if (c1.isSelected()) facilities.append("ATM Card, ");
        if (c2.isSelected()) facilities.append("Internet Banking, ");
        if (c3.isSelected()) facilities.append("Mobile Banking, ");
        if (c4.isSelected()) facilities.append("EMAIL Alerts, ");
        if (c5.isSelected()) facilities.append("Cheque Book, ");
        if (c6.isSelected()) facilities.append("E-Statement, ");
        String facStr = facilities.length() > 2 ? facilities.substring(0, facilities.length() - 2) : "Standard";

        try {
            Account account = accountService.createAccount(customer, atype, facStr, pin);

            String msg = String.format("""
                Account Created Successfully!
                
                ======================================
                Account Number:  %s
                Card Number:     %s
                4-Digit PIN:     %s
                Account Type:    %s
                ======================================
                
                Please record these credentials securely.
                You can now log in to the ATM using your Card Number and PIN.
                """, account.getAccountNumber(), account.getCardNumber(), pin, account.getAccountType());

            JOptionPane.showMessageDialog(this, msg, "Account Created", JOptionPane.INFORMATION_MESSAGE);

            setVisible(false);
            new Deposit(account.getAccountNumber()).setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to create account: " + e.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp3(new Customer()).setVisible(true));
    }
}
