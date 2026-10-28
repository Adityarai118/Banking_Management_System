package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main ATM transaction dashboard providing access to all customer banking operations.
 */
public class Transactions extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JButton b1, b2, b3, b4, b5, b6, b7, b8, b9;

    public Transactions(String accountNumber) {
        this.accountNumber = accountNumber;

        setLayout(null);

        // ATM Bezel background
        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
            Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(i2));
            background.setBounds(0, 0, 700, 700);
            add(background);

            JLabel title = new JLabel("Please Select Your Transaction");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("System", Font.BOLD, 15));
            title.setBounds(135, 220, 400, 30);
            background.add(title);

            // Left Column
            b1 = new JButton("DEPOSIT");
            b1.setBounds(125, 270, 110, 25);
            background.add(b1);

            b3 = new JButton("FAST CASH");
            b3.setBounds(125, 305, 110, 25);
            background.add(b3);

            b5 = new JButton("PIN CHANGE");
            b5.setBounds(125, 340, 110, 25);
            background.add(b5);

            b7 = new JButton("TRANSFER");
            b7.setBounds(125, 375, 110, 25);
            background.add(b7);

            // Right Column
            b2 = new JButton("WITHDRAWAL");
            b2.setBounds(245, 270, 145, 25);
            background.add(b2);

            b4 = new JButton("MINI STATEMENT");
            b4.setBounds(245, 305, 145, 25);
            background.add(b4);

            b6 = new JButton("BALANCE");
            b6.setBounds(245, 340, 145, 25);
            background.add(b6);

            b8 = new JButton("MY PROFILE");
            b8.setBounds(245, 375, 145, 25);
            background.add(b8);

            // Exit / Logout
            b9 = new JButton("LOGOUT");
            b9.setBounds(185, 412, 120, 25);
            background.add(b9);

            b1.addActionListener(this);
            b2.addActionListener(this);
            b3.addActionListener(this);
            b4.addActionListener(this);
            b5.addActionListener(this);
            b6.addActionListener(this);
            b7.addActionListener(this);
            b8.addActionListener(this);
            b9.addActionListener(this);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ATM UI: " + e.getMessage(), e);
        }

        setSize(700, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            setVisible(false);
            new Deposit(accountNumber).setVisible(true);
        } else if (ae.getSource() == b2) {
            setVisible(false);
            new Withdrawl(accountNumber).setVisible(true);
        } else if (ae.getSource() == b3) {
            setVisible(false);
            new FastCash(accountNumber).setVisible(true);
        } else if (ae.getSource() == b4) {
            new MiniStatement(accountNumber).setVisible(true);
        } else if (ae.getSource() == b5) {
            setVisible(false);
            new PinChange(accountNumber).setVisible(true);
        } else if (ae.getSource() == b6) {
            setVisible(false);
            new BalanceEnquiry(accountNumber).setVisible(true);
        } else if (ae.getSource() == b7) {
            setVisible(false);
            new Transfer(accountNumber).setVisible(true);
        } else if (ae.getSource() == b8) {
            setVisible(false);
            new UpdateProfile(accountNumber).setVisible(true);
        } else if (ae.getSource() == b9) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Transactions("").setVisible(true));
    }
}
