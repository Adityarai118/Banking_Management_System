package bank.management.system;

import bank.management.system.model.AdminUser;
import bank.management.system.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Administrative login gateway with role-based access control.
 */
public class AdminLogin extends JFrame implements ActionListener {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginBtn, backBtn;
    private final AdminService adminService;

    public AdminLogin() {
        this.adminService = new AdminService();

        setTitle("ADMINISTRATOR CONTROL CONSOLE - LOGIN");
        getContentPane().setBackground(Color.WHITE);
        setSize(550, 400);
        setLocationRelativeTo(null);
        setLayout(null);

        try {
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/icons/logo.jpg"));
            Image i2 = i1.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(i2));
            label.setBounds(50, 20, 70, 70);
            add(label);
        } catch (Exception ignored) {}

        JLabel title = new JLabel("BANK ADMINISTRATION");
        title.setFont(new Font("Osward", Font.BOLD, 22));
        title.setBounds(140, 25, 350, 30);
        add(title);

        JLabel subtitle = new JLabel("Authorized Management Personnel Only");
        subtitle.setFont(new Font("Raleway", Font.PLAIN, 12));
        subtitle.setForeground(Color.RED);
        subtitle.setBounds(140, 55, 350, 20);
        add(subtitle);

        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(new Font("Raleway", Font.BOLD, 13));
        userLabel.setBounds(70, 130, 140, 25);
        add(userLabel);

        usernameField = new JTextField("admin");
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameField.setBounds(210, 130, 230, 28);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Raleway", Font.BOLD, 13));
        passLabel.setBounds(70, 180, 140, 25);
        add(passLabel);

        passwordField = new JPasswordField("admin123");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBounds(210, 180, 230, 28);
        add(passwordField);

        loginBtn = new JButton("ADMIN LOGIN");
        loginBtn.setBackground(new Color(25, 25, 112));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 13));
        loginBtn.setBounds(210, 240, 130, 32);
        add(loginBtn);

        backBtn = new JButton("BACK");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 13));
        backBtn.setBounds(350, 240, 90, 32);
        add(backBtn);

        JLabel hint = new JLabel("(Default credentials: admin / admin123)");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(210, 285, 250, 20);
        add(hint);

        loginBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginBtn) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                AdminUser admin = adminService.authenticate(username, password);
                setVisible(false);
                new AdminDashboard(admin).setVisible(true);

            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Authentication Denied", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == backBtn) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}
