package bank.management.system;

import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen displaying the real-time balance of the current account.
 */
public class BalanceEnquiry extends JFrame implements ActionListener {

    private final String accountNumber;
    private final JButton b1;

    public BalanceEnquiry(String accountNumber) {
        this.accountNumber = accountNumber;

        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 700, 700);
        add(background);

        b1 = new JButton("BACK");
        b1.setBounds(270, 390, 110, 25);
        background.add(b1);
        b1.addActionListener(this);

        double balance = 0.0;
        try {
            AccountService service = new AccountService();
            balance = service.getBalance(accountNumber);
        } catch (Exception e) {
            System.err.println("Balance check error: " + e.getMessage());
        }

        JLabel l1 = new JLabel("Your Current Balance is:");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 15));
        l1.setBounds(135, 230, 300, 25);
        background.add(l1);

        JLabel l2 = new JLabel(String.format("Rs. %.2f", balance));
        l2.setForeground(new Color(144, 238, 144)); // Light green
        l2.setFont(new Font("System", Font.BOLD, 22));
        l2.setBounds(135, 265, 300, 35);
        background.add(l2);

        setSize(700, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new Transactions(accountNumber).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BalanceEnquiry("").setVisible(true));
    }
}
