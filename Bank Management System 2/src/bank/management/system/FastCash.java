package bank.management.system;

import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen providing one-click quick withdrawal presets.
 */
public class FastCash extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JButton b1, b2, b3, b4, b5, b6, b7;
    private final AccountService accountService;

    public FastCash(String accountNumber) {
        this.accountNumber = accountNumber;
        this.accountService = new AccountService();

        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 700, 700);
        add(background);

        JLabel l1 = new JLabel("SELECT WITHDRAWAL AMOUNT");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 15));
        l1.setBounds(135, 220, 300, 30);
        background.add(l1);

        b1 = new JButton("Rs 100");
        b1.setBounds(125, 280, 115, 25);
        background.add(b1);

        b2 = new JButton("Rs 500");
        b2.setBounds(250, 280, 135, 25);
        background.add(b2);

        b3 = new JButton("Rs 1000");
        b3.setBounds(125, 320, 115, 25);
        background.add(b3);

        b4 = new JButton("Rs 2000");
        b4.setBounds(250, 320, 135, 25);
        background.add(b4);

        b5 = new JButton("Rs 5000");
        b5.setBounds(125, 360, 115, 25);
        background.add(b5);

        b6 = new JButton("Rs 10000");
        b6.setBounds(250, 360, 135, 25);
        background.add(b6);

        b7 = new JButton("BACK");
        b7.setBounds(250, 400, 135, 25);
        background.add(b7);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);
        b7.addActionListener(this);

        setSize(700, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b7) {
            setVisible(false);
            new Transactions(accountNumber).setVisible(true);
            return;
        }

        String text = ((JButton) ae.getSource()).getText();
        double amount = Double.parseDouble(text.substring(3).trim());

        try {
            double newBalance = accountService.withdraw(accountNumber, amount, "Fast Cash - Rs. " + amount);
            JOptionPane.showMessageDialog(this, String.format("Rs. %.2f Debited Successfully.\nRemaining Balance: Rs. %.2f", amount, newBalance),
                    "Transaction Successful", JOptionPane.INFORMATION_MESSAGE);

            setVisible(false);
            new Transactions(accountNumber).setVisible(true);

        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Balance", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FastCash("").setVisible(true));
    }
}
