package bank.management.system;

import bank.management.system.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Step 2 of the registration wizard: Additional Background Details.
 */
public class SignUp2 extends JFrame implements ActionListener {

    private final Customer customer;
    private final JTextField t1, t2;
    private final JRadioButton r1, r2, r3, r4;
    private final JButton b, backBtn;
    private final JComboBox<String> c1, c2, c3, c4, c5;

    public SignUp2(Customer customer) {
        this.customer = customer != null ? customer : new Customer();

        setTitle("NEW ACCOUNT APPLICATION FORM - STEP 2");
        setLayout(null);

        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/logo.jpg"));
            Image i2 = i1.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(i2));
            label.setBounds(25, 10, 90, 90);
            add(label);
        } catch (Exception ignored) {}

        JLabel l1 = new JLabel("Page 2: Additional Details");
        l1.setFont(new Font("Raleway", Font.BOLD, 22));
        l1.setBounds(280, 50, 400, 30);
        add(l1);

        int y = 110;
        int gap = 45;

        // Religion
        JLabel l2 = new JLabel("Religion:");
        l2.setFont(new Font("Raleway", Font.BOLD, 15));
        l2.setBounds(100, y, 160, 25);
        add(l2);
        String[] religion = {"Hindu", "Muslim", "Sikh", "Christian", "Other"};
        c1 = new JComboBox<>(religion);
        c1.setBackground(Color.WHITE);
        c1.setBounds(290, y, 320, 28);
        add(c1);

        // Category
        y += gap;
        JLabel l3 = new JLabel("Category:");
        l3.setFont(new Font("Raleway", Font.BOLD, 15));
        l3.setBounds(100, y, 160, 25);
        add(l3);
        String[] category = {"General", "OBC", "SC", "ST", "Other"};
        c2 = new JComboBox<>(category);
        c2.setBackground(Color.WHITE);
        c2.setBounds(290, y, 320, 28);
        add(c2);

        // Income
        y += gap;
        JLabel l4 = new JLabel("Annual Income:");
        l4.setFont(new Font("Raleway", Font.BOLD, 15));
        l4.setBounds(100, y, 160, 25);
        add(l4);
        String[] income = {"Null", "< 1,50,000", "< 2,50,000", "< 5,00,000", "Upto 10,00,000", "Above 10,00,000"};
        c3 = new JComboBox<>(income);
        c3.setBackground(Color.WHITE);
        c3.setBounds(290, y, 320, 28);
        add(c3);

        // Education
        y += gap;
        JLabel l5 = new JLabel("Educational Qualification:");
        l5.setFont(new Font("Raleway", Font.BOLD, 15));
        l5.setBounds(100, y, 190, 25);
        add(l5);
        String[] education = {"Non-Graduate", "Graduate", "Post-Graduate", "Doctorate", "Others"};
        c4 = new JComboBox<>(education);
        c4.setBackground(Color.WHITE);
        c4.setBounds(290, y, 320, 28);
        add(c4);

        // Occupation
        y += gap;
        JLabel l7 = new JLabel("Occupation:");
        l7.setFont(new Font("Raleway", Font.BOLD, 15));
        l7.setBounds(100, y, 160, 25);
        add(l7);
        String[] occupation = {"Salaried", "Self-Employed", "Business", "Student", "Retired", "Others"};
        c5 = new JComboBox<>(occupation);
        c5.setBackground(Color.WHITE);
        c5.setBounds(290, y, 320, 28);
        add(c5);

        // PAN Number
        y += gap;
        JLabel l8 = new JLabel("PAN Number:");
        l8.setFont(new Font("Raleway", Font.BOLD, 15));
        l8.setBounds(100, y, 160, 25);
        add(l8);
        t1 = new JTextField();
        t1.setFont(new Font("Raleway", Font.PLAIN, 14));
        t1.setBounds(290, y, 320, 28);
        add(t1);

        // Aadhar Number
        y += gap;
        JLabel l9 = new JLabel("Aadhar Number:");
        l9.setFont(new Font("Raleway", Font.BOLD, 15));
        l9.setBounds(100, y, 160, 25);
        add(l9);
        t2 = new JTextField();
        t2.setFont(new Font("Raleway", Font.PLAIN, 14));
        t2.setBounds(290, y, 320, 28);
        add(t2);

        // Senior Citizen
        y += gap;
        JLabel l10 = new JLabel("Senior Citizen:");
        l10.setFont(new Font("Raleway", Font.BOLD, 15));
        l10.setBounds(100, y, 160, 25);
        add(l10);
        r1 = new JRadioButton("Yes");
        r1.setFont(new Font("Raleway", Font.PLAIN, 14));
        r1.setBackground(Color.WHITE);
        r1.setBounds(290, y, 70, 25);
        add(r1);
        r2 = new JRadioButton("No");
        r2.setFont(new Font("Raleway", Font.PLAIN, 14));
        r2.setBackground(Color.WHITE);
        r2.setBounds(380, y, 70, 25);
        add(r2);
        ButtonGroup check = new ButtonGroup();
        check.add(r1);
        check.add(r2);
        r2.setSelected(true);

        // Existing Account
        y += gap;
        JLabel l11 = new JLabel("Existing Account:");
        l11.setFont(new Font("Raleway", Font.BOLD, 15));
        l11.setBounds(100, y, 160, 25);
        add(l11);
        r3 = new JRadioButton("Yes");
        r3.setFont(new Font("Raleway", Font.PLAIN, 14));
        r3.setBackground(Color.WHITE);
        r3.setBounds(290, y, 70, 25);
        add(r3);
        r4 = new JRadioButton("No");
        r4.setFont(new Font("Raleway", Font.PLAIN, 14));
        r4.setBackground(Color.WHITE);
        r4.setBounds(380, y, 70, 25);
        add(r4);
        ButtonGroup check1 = new ButtonGroup();
        check1.add(r3);
        check1.add(r4);
        r4.setSelected(true);

        y += 50;
        b = new JButton("NEXT >>");
        b.setFont(new Font("Raleway", Font.BOLD, 13));
        b.setBackground(Color.BLACK);
        b.setForeground(Color.WHITE);
        b.setBounds(510, y, 100, 32);
        b.addActionListener(this);
        add(b);

        backBtn = new JButton("<< BACK");
        backBtn.setFont(new Font("Raleway", Font.BOLD, 13));
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBounds(390, y, 100, 32);
        backBtn.addActionListener(this);
        add(backBtn);

        getContentPane().setBackground(Color.WHITE);
        setSize(750, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public SignUp2(String formno) {
        this(new Customer(formno, "", "", "", "", "", "", "", "", "", ""));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backBtn) {
            setVisible(false);
            new SignUp1().setVisible(true);
            return;
        }

        String pan = t1.getText().trim();
        String aadhar = t2.getText().trim();

        if (pan.isEmpty() || aadhar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PAN and Aadhar numbers are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        customer.setReligion((String) c1.getSelectedItem());
        customer.setCategory((String) c2.getSelectedItem());
        customer.setIncome((String) c3.getSelectedItem());
        customer.setEducation((String) c4.getSelectedItem());
        customer.setOccupation((String) c5.getSelectedItem());
        customer.setPan(pan);
        customer.setAadhar(aadhar);
        customer.setSeniorCitizen(r1.isSelected() ? "Yes" : "No");
        customer.setExistingAccount(r3.isSelected() ? "Yes" : "No");

        setVisible(false);
        new SignUp3(customer).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp2(new Customer()).setVisible(true));
    }
}
