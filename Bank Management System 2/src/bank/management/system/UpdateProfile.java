package bank.management.system;

import bank.management.system.model.Customer;
import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen allowing customers to view and update their contact details.
 */
public class UpdateProfile extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JTextField nameField, emailField, addrField, cityField, stateField, pinField;
    private final JButton updateBtn, backBtn;
    private final AccountService accountService;

    public UpdateProfile(String accountNumber) {
        this.accountNumber = accountNumber;
        this.accountService = new AccountService();

        setTitle("UPDATE CUSTOMER PROFILE");
        getContentPane().setBackground(Color.WHITE);
        setSize(550, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("CUSTOMER PROFILE DETAILS");
        title.setFont(new Font("Raleway", Font.BOLD, 18));
        title.setBounds(130, 20, 300, 30);
        add(title);

        JLabel accInfo = new JLabel("Account Number: " + accountNumber);
        accInfo.setFont(new Font("Raleway", Font.BOLD, 13));
        accInfo.setForeground(Color.DARK_GRAY);
        accInfo.setBounds(60, 60, 400, 20);
        add(accInfo);

        int y = 100;
        int gap = 45;

        // Name
        JLabel l1 = new JLabel("Full Name:");
        l1.setFont(new Font("Raleway", Font.BOLD, 13));
        l1.setBounds(60, y, 120, 25);
        add(l1);
        nameField = new JTextField();
        nameField.setBounds(180, y, 280, 25);
        add(nameField);

        // Email
        y += gap;
        JLabel l2 = new JLabel("Email Address:");
        l2.setFont(new Font("Raleway", Font.BOLD, 13));
        l2.setBounds(60, y, 120, 25);
        add(l2);
        emailField = new JTextField();
        emailField.setBounds(180, y, 280, 25);
        add(emailField);

        // Address
        y += gap;
        JLabel l3 = new JLabel("Address:");
        l3.setFont(new Font("Raleway", Font.BOLD, 13));
        l3.setBounds(60, y, 120, 25);
        add(l3);
        addrField = new JTextField();
        addrField.setBounds(180, y, 280, 25);
        add(addrField);

        // City
        y += gap;
        JLabel l4 = new JLabel("City:");
        l4.setFont(new Font("Raleway", Font.BOLD, 13));
        l4.setBounds(60, y, 120, 25);
        add(l4);
        cityField = new JTextField();
        cityField.setBounds(180, y, 280, 25);
        add(cityField);

        // State
        y += gap;
        JLabel l5 = new JLabel("State:");
        l5.setFont(new Font("Raleway", Font.BOLD, 13));
        l5.setBounds(60, y, 120, 25);
        add(l5);
        stateField = new JTextField();
        stateField.setBounds(180, y, 280, 25);
        add(stateField);

        // Pincode
        y += gap;
        JLabel l6 = new JLabel("PIN Code:");
        l6.setFont(new Font("Raleway", Font.BOLD, 13));
        l6.setBounds(60, y, 120, 25);
        add(l6);
        pinField = new JTextField();
        pinField.setBounds(180, y, 280, 25);
        add(pinField);

        // Load existing values
        try {
            Customer c = accountService.getCustomerByAccount(accountNumber);
            if (c != null) {
                nameField.setText(c.getName() != null ? c.getName() : "");
                emailField.setText(c.getEmail() != null ? c.getEmail() : "");
                addrField.setText(c.getAddress() != null ? c.getAddress() : "");
                cityField.setText(c.getCity() != null ? c.getCity() : "");
                stateField.setText(c.getState() != null ? c.getState() : "");
                pinField.setText(c.getPincode() != null ? c.getPincode() : "");
            }
        } catch (Exception e) {
            System.err.println("Error loading profile: " + e.getMessage());
        }

        y += 55;
        updateBtn = new JButton("SAVE CHANGES");
        updateBtn.setBackground(Color.BLACK);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Raleway", Font.BOLD, 12));
        updateBtn.setBounds(180, y, 135, 30);
        add(updateBtn);

        backBtn = new JButton("BACK");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Raleway", Font.BOLD, 12));
        backBtn.setBounds(325, y, 135, 30);
        add(backBtn);

        updateBtn.addActionListener(this);
        backBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateBtn) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String addr = addrField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String pin = pinField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || addr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Email, and Address are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                accountService.updateCustomerDetails(accountNumber, name, email, addr, city, state, pin);
                JOptionPane.showMessageDialog(this, "Customer details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new Transactions(accountNumber).setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to update profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == backBtn) {
            setVisible(false);
            new Transactions(accountNumber).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateProfile("").setVisible(true));
    }
}
