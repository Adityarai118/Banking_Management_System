package bank.management.system;

import bank.management.system.model.Customer;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Step 1 of the new account registration wizard: Personal Details.
 */
public class SignUp1 extends JFrame implements ActionListener {

    private final JLabel l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12;
    private final JTextField t1, t2, t3, t4, t5, t6, t7;
    private final JRadioButton r1, r2;
    private final JRadioButton r3, r4, r5;
    private final JButton b, backBtn;
    private final JDateChooser dateChooser;
    private final String formno;

    public SignUp1() {
        Random ran = new Random();
        long first4 = Math.abs((ran.nextLong() % 9000L) + 1000L);
        this.formno = String.valueOf(first4);

        setTitle("NEW ACCOUNT APPLICATION FORM - STEP 1");
        setLayout(null);

        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/logo.jpg"));
            Image i2 = i1.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(i2));
            label.setBounds(25, 10, 90, 90);
            add(label);
        } catch (Exception ignored) {}

        l1 = new JLabel("APPLICATION FORM NO. " + formno);
        l1.setFont(new Font("Raleway", Font.BOLD, 30));
        l1.setBounds(130, 25, 600, 35);
        add(l1);

        l2 = new JLabel("Page 1: Personal Details");
        l2.setFont(new Font("Raleway", Font.BOLD, 20));
        l2.setBounds(290, 75, 400, 30);
        add(l2);

        int y = 125;
        int gap = 45;

        // Name
        l3 = new JLabel("Full Name:");
        l3.setFont(new Font("Raleway", Font.BOLD, 15));
        l3.setBounds(100, y, 180, 25);
        add(l3);
        t1 = new JTextField();
        t1.setFont(new Font("Raleway", Font.PLAIN, 14));
        t1.setBounds(290, y, 320, 28);
        add(t1);

        // Father's Name
        y += gap;
        l4 = new JLabel("Father's Name:");
        l4.setFont(new Font("Raleway", Font.BOLD, 15));
        l4.setBounds(100, y, 180, 25);
        add(l4);
        t2 = new JTextField();
        t2.setFont(new Font("Raleway", Font.PLAIN, 14));
        t2.setBounds(290, y, 320, 28);
        add(t2);

        // DOB
        y += gap;
        l5 = new JLabel("Date of Birth:");
        l5.setFont(new Font("Raleway", Font.BOLD, 15));
        l5.setBounds(100, y, 180, 25);
        add(l5);
        dateChooser = new JDateChooser();
        dateChooser.setBounds(290, y, 320, 28);
        add(dateChooser);

        // Gender
        y += gap;
        l6 = new JLabel("Gender:");
        l6.setFont(new Font("Raleway", Font.BOLD, 15));
        l6.setBounds(100, y, 180, 25);
        add(l6);
        r1 = new JRadioButton("Male");
        r1.setFont(new Font("Raleway", Font.PLAIN, 14));
        r1.setBackground(Color.WHITE);
        r1.setBounds(290, y, 80, 25);
        add(r1);
        r2 = new JRadioButton("Female");
        r2.setFont(new Font("Raleway", Font.PLAIN, 14));
        r2.setBackground(Color.WHITE);
        r2.setBounds(390, y, 100, 25);
        add(r2);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(r1);
        genderGroup.add(r2);
        r1.setSelected(true);

        // Email
        y += gap;
        l7 = new JLabel("Email Address:");
        l7.setFont(new Font("Raleway", Font.BOLD, 15));
        l7.setBounds(100, y, 180, 25);
        add(l7);
        t3 = new JTextField();
        t3.setFont(new Font("Raleway", Font.PLAIN, 14));
        t3.setBounds(290, y, 320, 28);
        add(t3);

        // Marital Status
        y += gap;
        l8 = new JLabel("Marital Status:");
        l8.setFont(new Font("Raleway", Font.BOLD, 15));
        l8.setBounds(100, y, 180, 25);
        add(l8);
        r3 = new JRadioButton("Married");
        r3.setFont(new Font("Raleway", Font.PLAIN, 14));
        r3.setBackground(Color.WHITE);
        r3.setBounds(290, y, 90, 25);
        add(r3);
        r4 = new JRadioButton("Unmarried");
        r4.setFont(new Font("Raleway", Font.PLAIN, 14));
        r4.setBackground(Color.WHITE);
        r4.setBounds(390, y, 110, 25);
        add(r4);
        r5 = new JRadioButton("Other");
        r5.setFont(new Font("Raleway", Font.PLAIN, 14));
        r5.setBackground(Color.WHITE);
        r5.setBounds(510, y, 80, 25);
        add(r5);
        ButtonGroup maritalGroup = new ButtonGroup();
        maritalGroup.add(r3);
        maritalGroup.add(r4);
        maritalGroup.add(r5);
        r4.setSelected(true);

        // Address
        y += gap;
        l9 = new JLabel("Address:");
        l9.setFont(new Font("Raleway", Font.BOLD, 15));
        l9.setBounds(100, y, 180, 25);
        add(l9);
        t4 = new JTextField();
        t4.setFont(new Font("Raleway", Font.PLAIN, 14));
        t4.setBounds(290, y, 320, 28);
        add(t4);

        // City
        y += gap;
        l10 = new JLabel("City:");
        l10.setFont(new Font("Raleway", Font.BOLD, 15));
        l10.setBounds(100, y, 180, 25);
        add(l10);
        t5 = new JTextField();
        t5.setFont(new Font("Raleway", Font.PLAIN, 14));
        t5.setBounds(290, y, 320, 28);
        add(t5);

        // Pin code
        y += gap;
        l11 = new JLabel("PIN Code:");
        l11.setFont(new Font("Raleway", Font.BOLD, 15));
        l11.setBounds(100, y, 180, 25);
        add(l11);
        t6 = new JTextField();
        t6.setFont(new Font("Raleway", Font.PLAIN, 14));
        t6.setBounds(290, y, 320, 28);
        add(t6);

        // State
        y += gap;
        l12 = new JLabel("State:");
        l12.setFont(new Font("Raleway", Font.BOLD, 15));
        l12.setBounds(100, y, 180, 25);
        add(l12);
        t7 = new JTextField();
        t7.setFont(new Font("Raleway", Font.PLAIN, 14));
        t7.setBounds(290, y, 320, 28);
        add(t7);

        y += 50;
        b = new JButton("NEXT >>");
        b.setFont(new Font("Raleway", Font.BOLD, 13));
        b.setBackground(Color.BLACK);
        b.setForeground(Color.WHITE);
        b.setBounds(510, y, 100, 32);
        b.addActionListener(this);
        add(b);

        backBtn = new JButton("CANCEL");
        backBtn.setFont(new Font("Raleway", Font.BOLD, 13));
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBounds(390, y, 100, 32);
        backBtn.addActionListener(this);
        add(backBtn);

        getContentPane().setBackground(Color.WHITE);
        setSize(750, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backBtn) {
            setVisible(false);
            new Login().setVisible(true);
            return;
        }

        String name = t1.getText().trim();
        String fname = t2.getText().trim();
        String dob = "";
        try {
            dob = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText().trim();
        } catch (Exception ignored) {}

        String gender = r1.isSelected() ? "Male" : "Female";
        String email = t3.getText().trim();
        String marital = r3.isSelected() ? "Married" : (r4.isSelected() ? "Unmarried" : "Other");
        String address = t4.getText().trim();
        String city = t5.getText().trim();
        String pincode = t6.getText().trim();
        String state = t7.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (address.isEmpty() || city.isEmpty() || state.isEmpty() || pincode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address, City, State, and PIN Code are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer customer = new Customer(formno, name, fname, dob, gender, email, marital, address, city, pincode, state);
        setVisible(false);
        new SignUp2(customer).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp1().setVisible(true));
    }
}
