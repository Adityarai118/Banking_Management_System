package bank.management.system;

import bank.management.system.model.Account;
import bank.management.system.model.TransactionRecord;
import bank.management.system.service.AccountService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Screen displaying mini-statement with recent transaction entries.
 */
public class MiniStatement extends JFrame {

    public MiniStatement(String accountNumber) {
        super("Mini Statement");

        getContentPane().setBackground(Color.WHITE);
        setSize(480, 620);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("INDIAN BANK");
        title.setFont(new Font("System", Font.BOLD, 18));
        title.setBounds(175, 20, 200, 25);
        add(title);

        JLabel subtitle = new JLabel("ATM Transaction History");
        subtitle.setFont(new Font("System", Font.PLAIN, 12));
        subtitle.setBounds(165, 45, 200, 20);
        add(subtitle);

        JLabel cardLabel = new JLabel("Card: Loading...");
        cardLabel.setFont(new Font("System", Font.BOLD, 12));
        cardLabel.setBounds(25, 80, 420, 20);
        add(cardLabel);

        JLabel accLabel = new JLabel("Account No: " + accountNumber);
        accLabel.setFont(new Font("System", Font.BOLD, 12));
        accLabel.setBounds(25, 105, 420, 20);
        add(accLabel);

        // Header for transactions
        JLabel colHeader = new JLabel(String.format("%-19s %-14s %10s", "Date/Time", "Type", "Amount (Rs)"));
        colHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        colHeader.setBounds(25, 135, 430, 20);
        add(colHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 155, 430, 2);
        add(sep);

        StringBuilder sb = new StringBuilder("<html><body style='font-family: monospace; font-size: 11px;'>");

        double currentBalance = 0.0;
        try {
            AccountService service = new AccountService();
            Account acc = service.getAccountByNumber(accountNumber);
            if (acc != null) {
                String card = acc.getCardNumber();
                String masked = card.length() >= 16
                        ? card.substring(0, 4) + "XXXXXXXX" + card.substring(12)
                        : card;
                cardLabel.setText("Card: " + masked + " (" + acc.getAccountType() + ")");
                currentBalance = acc.getBalance();
            }

            List<TransactionRecord> transactions = service.getTransactions(accountNumber, 8);
            if (transactions.isEmpty()) {
                sb.append("<p style='color: gray;'>No transactions found.</p>");
            } else {
                for (TransactionRecord t : transactions) {
                    String date = t.getTransactionDate() != null && t.getTransactionDate().length() >= 16
                            ? t.getTransactionDate().substring(0, 16)
                            : (t.getTransactionDate() != null ? t.getTransactionDate() : "-");
                    String type = t.getTransactionType();
                    String color = (type.contains("DEPOSIT") || type.contains("TRANSFER_IN")) ? "green" : "red";

                    sb.append(String.format(
                            "<div>%s &nbsp; <b style='color:%s;'>%-12s</b> &nbsp; Rs. %.2f</div><br>",
                            date, color, type, t.getAmount()
                    ));
                }
            }
        } catch (Exception e) {
            sb.append("<p style='color: red;'>Error retrieving statement: ").append(e.getMessage()).append("</p>");
        }
        sb.append("</body></html>");

        JLabel transLabel = new JLabel(sb.toString());
        transLabel.setVerticalAlignment(SwingConstants.TOP);
        transLabel.setBounds(25, 165, 430, 340);
        add(transLabel);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(25, 520, 430, 2);
        add(sep2);

        JLabel balanceLabel = new JLabel(String.format("Available Balance: Rs. %.2f", currentBalance));
        balanceLabel.setFont(new Font("System", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(0, 100, 0));
        balanceLabel.setBounds(25, 530, 400, 25);
        add(balanceLabel);

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setBounds(185, 560, 100, 25);
        closeBtn.addActionListener(e -> setVisible(false));
        add(closeBtn);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniStatement("").setVisible(true));
    }
}
