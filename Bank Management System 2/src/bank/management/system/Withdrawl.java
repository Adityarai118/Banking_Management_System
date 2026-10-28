package bank.management.system;

import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen for withdrawing cash with overdraft protection and balance validation.
 */
public class Withdrawl extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JTextField t1;
    private final JButton b1, b2;
    private final AccountService accountService;

    public Withdrawl(String accountNumber) {
        this.accountNumber = accountNumber;
        this.accountService = new AccountService();

        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 700, 700);
        add(background);

        JLabel l1 = new JLabel("ENTER AMOUNT TO WITHDRAW");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 15));
        l1.setBounds(135, 220, 300, 30);
        background.add(l1);

        t1 = new JTextField();
        t1.setFont(new Font("Raleway", Font.BOLD, 18));
        t1.setBounds(135, 260, 240, 30);
        background.add(t1);

        b1 = new JButton("WITHDRAW");
        b1.setBounds(245, 340, 130, 28);
        background.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(245, 375, 130, 28);
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
            String amountStr = t1.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the amount to withdraw.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Withdrawal amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double newBalance = accountService.withdraw(accountNumber, amount, "ATM Cash Withdrawal");
                JOptionPane.showMessageDialog(this, String.format("Rs. %.2f withdrawn successfully.\nRemaining Balance: Rs. %.2f", amount, newBalance),
                        "Withdrawal Successful", JOptionPane.INFORMATION_MESSAGE);

                setVisible(false);
                new Transactions(accountNumber).setVisible(true);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Balance", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Transaction Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == b2) {
            setVisible(false);
            new Transactions(accountNumber).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Withdrawl("").setVisible(true));
    }
}
