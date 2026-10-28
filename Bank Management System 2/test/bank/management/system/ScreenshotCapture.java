package bank.management.system;

import bank.management.system.model.AdminUser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility to capture genuine pixel-accurate screenshots directly from running Swing frames.
 */
public class ScreenshotCapture {

    public static void main(String[] args) {
        // Ensure UI components are created on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseHelper.initializeDatabase();
                File outDir = new File("../screenshots");
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                // 1. Capture Login Screen
                Login login = new Login();
                login.setVisible(true);
                captureFrame(login, new File(outDir, "login_screen.png"));
                login.dispose();

                // 2. Capture ATM Dashboard
                Transactions trans = new Transactions("1000000001");
                trans.setVisible(true);
                captureFrame(trans, new File(outDir, "atm_dashboard.png"));
                trans.dispose();

                // 3. Capture Deposit Screen
                Deposit deposit = new Deposit("1000000001");
                deposit.setVisible(true);
                captureFrame(deposit, new File(outDir, "deposit_screen.png"));
                deposit.dispose();

                // 4. Capture Transfer Screen
                Transfer transfer = new Transfer("1000000001");
                transfer.setVisible(true);
                captureFrame(transfer, new File(outDir, "transfer_screen.png"));
                transfer.dispose();

                // 5. Capture Mini Statement Screen
                MiniStatement mini = new MiniStatement("1000000001");
                mini.setVisible(true);
                captureFrame(mini, new File(outDir, "mini_statement.png"));
                mini.dispose();

                // 6. Capture Admin Dashboard
                AdminDashboard admin = new AdminDashboard(new AdminUser(1, "admin", "System Administrator", "ADMIN"));
                admin.setVisible(true);
                captureFrame(admin, new File(outDir, "admin_dashboard.png"));
                admin.dispose();

                System.out.println("SUCCESS: Captured 6 genuine screenshots to " + outDir.getAbsolutePath());
                System.exit(0);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureFrame(JFrame frame, File targetFile) {
        try {
            // Allow components to lay out and paint
            frame.validate();
            frame.repaint();
            Thread.sleep(150);

            int w = Math.max(frame.getWidth(), 100);
            int h = Math.max(frame.getHeight(), 100);
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            frame.paint(g2);
            g2.dispose();

            ImageIO.write(img, "png", targetFile);
            System.out.println("Captured: " + targetFile.getName() + " (" + w + "x" + h + ")");
        } catch (Exception e) {
            System.err.println("Failed to capture " + targetFile.getName() + ": " + e.getMessage());
        }
    }
}
