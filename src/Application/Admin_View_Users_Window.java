// Admin_View_Users_Window.java
package Application;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;

import net.proteanit.sql.DbUtils;

/**
 * Admin_View_Users_Window.java
 *
 * This class displays all users from the ez_user table in a JTable.
 * Admins can view user_id, user_name, full_name, and user_type.
 */
public class Admin_View_Users_Window {
    public JFrame frame;
    private JTable usersTable;

    /**
     * Launches the "View All Users" window.
     * Ensures the database connection is active before loading.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();
                Admin_View_Users_Window window = new Admin_View_Users_Window();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor: sets up UI and populates the table.
     */
    public Admin_View_Users_Window() {
        initialize();
        populateTable();
    }

    /**
     * Initializes the JFrame and UI components.
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle("View All Users");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

        ImageIcon img = new ImageIcon("resources/calendar-icon.png");
        frame.setIconImage(img.getImage());

        JLabel lblTitle = new JLabel("All Registered Users");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBounds(200, 10, 200, 30);
        frame.getContentPane().add(lblTitle);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 50, 550, 260);
        frame.getContentPane().add(scrollPane);

        usersTable = new JTable();
        scrollPane.setViewportView(usersTable);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(250, 325, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }

    /**
     * Executes the SQL query to fetch all users and populates the JTable.
     * Adds a check for empty result set.
     */
    void populateTable() {
        try {
            //  Ensure our shared connection is still open
            if (Database.connection == null || Database.connection.isClosed()) {
                Database.connect();
            }
            Connection conn = Database.connection;

            // Use the actual column name 'username' from your DDL
            String sql = "SELECT user_id AS ID, user_name AS Username, user_type AS Type FROM ez_user";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            // Populate table via DbUtils
            usersTable.setModel(DbUtils.resultSetToTableModel(rs));

            // Notify if no users found
            if (usersTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame,
                    "No registered users found.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error loading users: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
