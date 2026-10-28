package bank.management.system;

import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Screen providing inter-account fund transfer with atomic database transactions.
 */
public class Transfer extends JFrame implements ActionListener {

    private final String sourceAccountNumber;
    private final JTextField destField, amountField, remarksField;
    private final JButton transferBtn, backBtn;
    private final AccountService accountService;

    public Transfer(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
        this.accountService = new AccountService();

        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 700, 700);
        add(background);

        JLabel title = new JLabel("INTER-ACCOUNT FUND TRANSFER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("System", Font.BOLD, 14));
        title.setBounds(135, 215, 300, 25);
        background.add(title);

        JLabel l1 = new JLabel("Target A/C No:");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 12));
        l1.setBounds(135, 250, 110, 22);
        background.add(l1);

        destField = new JTextField();
        destField.setFont(new Font("Raleway", Font.BOLD, 14));
        destField.setBounds(245, 250, 145, 24);
        background.add(destField);

        JLabel l2 = new JLabel("Amount (Rs):");
        l2.setForeground(Color.WHITE);
        l2.setFont(new Font("System", Font.BOLD, 12));
        l2.setBounds(135, 280, 110, 22);
        background.add(l2);

        amountField = new JTextField();
        amountField.setFont(new Font("Raleway", Font.BOLD, 14));
        amountField.setBounds(245, 280, 145, 24);
        background.add(amountField);

        JLabel l3 = new JLabel("Remarks:");
        l3.setForeground(Color.WHITE);
        l3.setFont(new Font("System", Font.BOLD, 12));
        l3.setBounds(135, 310, 110, 22);
        background.add(l3);

        remarksField = new JTextField();
        remarksField.setFont(new Font("Raleway", Font.BOLD, 14));
        remarksField.setBounds(245, 310, 145, 24);
        background.add(remarksField);

        transferBtn = new JButton("TRANSFER");
        transferBtn.setBounds(245, 355, 145, 26);
        background.add(transferBtn);

        backBtn = new JButton("BACK");
        backBtn.setBounds(245, 390, 145, 26);
        background.add(backBtn);

        transferBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setSize(700, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == transferBtn) {
            String targetAcc = destField.getText().trim();
            String amountStr = amountField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (targetAcc.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Target Account and Amount are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                accountService.transfer(sourceAccountNumber, targetAcc, amount, remarks.isEmpty() ? null : remarks);
                double balance = accountService.getBalance(sourceAccountNumber);

                JOptionPane.showMessageDialog(this, String.format("Transfer of Rs. %.2f to Account %s completed successfully.\nUpdated Balance: Rs. %.2f",
                        amount, targetAcc, balance), "Transfer Successful", JOptionPane.INFORMATION_MESSAGE);

                setVisible(false);
                new Transactions(sourceAccountNumber).setVisible(true);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Transfer Failed", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "System error during transfer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == backBtn) {
            setVisible(false);
            new Transactions(sourceAccountNumber).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Transfer("").setVisible(true));
    }
}
