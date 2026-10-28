package bank.management.system;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Backward-compatible database connector.
 * Delegates to DatabaseHelper using SQLite for zero-configuration local persistence.
 */
public class Conn implements AutoCloseable {

    public Connection c;
    public Statement s;

    public Conn() {
        try {
            this.c = DatabaseHelper.getConnection();
            this.s = this.c.createStatement();
        } catch (Exception e) {
            System.err.println("Conn Error: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (s != null && !s.isClosed()) {
                s.close();
            }
        } catch (Exception ignored) {}

        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (Exception ignored) {}
    }
}
