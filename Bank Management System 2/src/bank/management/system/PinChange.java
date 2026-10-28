package bank.management.system;

import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen allowing customer to securely update their 4-digit PIN.
 */
public class PinChange extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JPasswordField t0, t1, t2;
    private final JButton b1, b2;
    private final AccountService accountService;

    public PinChange(String accountNumber) {
        this.accountNumber = accountNumber;
        this.accountService = new AccountService();

        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 700, 700);
        add(background);

        JLabel l1 = new JLabel("CHANGE YOUR PIN");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 15));
        l1.setBounds(180, 220, 300, 25);
        background.add(l1);

        JLabel l0 = new JLabel("Current PIN:");
        l0.setForeground(Color.WHITE);
        l0.setFont(new Font("System", Font.BOLD, 13));
        l0.setBounds(135, 260, 140, 25);
        background.add(l0);

        t0 = new JPasswordField();
        t0.setFont(new Font("Raleway", Font.BOLD, 18));
        t0.setBounds(275, 260, 110, 25);
        background.add(t0);

        JLabel l2 = new JLabel("New PIN:");
        l2.setForeground(Color.WHITE);
        l2.setFont(new Font("System", Font.BOLD, 13));
        l2.setBounds(135, 295, 140, 25);
        background.add(l2);

        t1 = new JPasswordField();
        t1.setFont(new Font("Raleway", Font.BOLD, 18));
        t1.setBounds(275, 295, 110, 25);
        background.add(t1);

        JLabel l3 = new JLabel("Re-Enter PIN:");
        l3.setForeground(Color.WHITE);
        l3.setFont(new Font("System", Font.BOLD, 13));
        l3.setBounds(135, 330, 140, 25);
        background.add(l3);

        t2 = new JPasswordField();
        t2.setFont(new Font("Raleway", Font.BOLD, 18));
        t2.setBounds(275, 330, 110, 25);
        background.add(t2);

        b1 = new JButton("CHANGE");
        b1.setBounds(270, 370, 115, 25);
        background.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(270, 400, 115, 25);
        background.add(b2);

        b1.addActionListener(this);
        b2.addActionListener(this);

        setSize(700, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            String oldPin = new String(t0.getPassword()).trim();
            String npin = new String(t1.getPassword()).trim();
            String rpin = new String(t2.getPassword()).trim();

            if (oldPin.isEmpty() || npin.isEmpty() || rpin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all PIN fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!npin.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "New PIN must be exactly 4 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!npin.equals(rpin)) {
                JOptionPane.showMessageDialog(this, "New PIN and Confirmation do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                accountService.changePin(accountNumber, oldPin, npin);
                JOptionPane.showMessageDialog(this, "PIN changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new Transactions(accountNumber).setVisible(true);

            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == b2) {
            setVisible(false);
            new Transactions(accountNumber).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PinChange("").setVisible(true));
    }
}
