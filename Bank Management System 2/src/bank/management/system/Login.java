package bank.management.system;

import bank.management.system.model.Account;
import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application entry point for Customer and Admin login.
 */
public class Login extends JFrame implements ActionListener {

    private final JLabel l1, l2, l3;
    private final JTextField tf1;
    private final JPasswordField pf2;
    private final JButton b1, b2, b3, b4;
    private final AccountService accountService;

    public Login() {
        this.accountService = new AccountService();

        setTitle("AUTOMATED TELLER MACHINE");
        setLayout(null);

        // Bank Logo
        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/logo.jpg"));
            Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(i2));
            label.setBounds(70, 10, 100, 100);
            add(label);
        } catch (Exception ignored) {}

        l1 = new JLabel("WELCOME TO ATM");
        l1.setFont(new Font("Osward", Font.BOLD, 36));
        l1.setBounds(200, 40, 450, 40);
        add(l1);

        l2 = new JLabel("Card No:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));
        l2.setBounds(125, 140, 150, 30);
        add(l2);

        tf1 = new JTextField(16);
        tf1.setBounds(280, 140, 260, 30);
        tf1.setFont(new Font("Arial", Font.BOLD, 14));
        add(tf1);

        l3 = new JLabel("PIN:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));
        l3.setBounds(125, 190, 150, 30);
        add(l3);

        pf2 = new JPasswordField(15);
        pf2.setFont(new Font("Arial", Font.BOLD, 14));
        pf2.setBounds(280, 190, 260, 30);
        add(pf2);

        b1 = new JButton("SIGN IN");
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.setFont(new Font("Arial", Font.BOLD, 13));
        b1.setBounds(280, 250, 120, 30);
        add(b1);

        b2 = new JButton("CLEAR");
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        b2.setFont(new Font("Arial", Font.BOLD, 13));
        b2.setBounds(420, 250, 120, 30);
        add(b2);

        b3 = new JButton("NEW USER SIGN UP");
        b3.setBackground(Color.BLACK);
        b3.setForeground(Color.WHITE);
        b3.setFont(new Font("Arial", Font.BOLD, 13));
        b3.setBounds(280, 295, 260, 30);
        add(b3);

        b4 = new JButton("ADMINISTRATOR PORTAL");
        b4.setBackground(new Color(25, 25, 112));
        b4.setForeground(Color.WHITE);
        b4.setFont(new Font("Arial", Font.BOLD, 12));
        b4.setBounds(280, 340, 260, 30);
        add(b4);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);

        getContentPane().setBackground(Color.WHITE);
        setSize(750, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            String cardno = tf1.getText().trim();
            String pin = new String(pf2.getPassword()).trim();

            if (cardno.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Card Number and PIN.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Account account = accountService.authenticate(cardno, pin);
                setVisible(false);
                new Transactions(account.getAccountNumber()).setVisible(true);
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Access Denied", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == b2) {
            tf1.setText("");
            pf2.setText("");
        } else if (ae.getSource() == b3) {
            setVisible(false);
            new SignUp1().setVisible(true);
        } else if (ae.getSource() == b4) {
            setVisible(false);
            new AdminLogin().setVisible(true);
        }
    }

    public static void main(String[] args) {
        // Ensure UI displays on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
