package Application;
import java.awt.Color;
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

import net.proteanit.sql.DbUtils;

import javax.swing.JOptionPane;


/**
 * Admin_View_User_Calendar_Window.java
 *
 * Shows a specific user's calendar events in a JTable.
 * Assumes calendar table name is <username>_calendar.
 */
public class Admin_View_User_Calendar_Window {
    public JFrame frame;
    private JTable calendarTable;
    private String targetUser;

    /**
     * Constructor accepts the username whose calendar to display.
     * @param username lower- or mixed-case input will be normalized.
     */
    public Admin_View_User_Calendar_Window(String username) {
        this.targetUser = username.toLowerCase();
        initialize();
        populateCalendar();
    }

    /**
     * Sets up frame, labels, table, and back button.
     */
    void initialize() {
        // Main frame
        frame = new JFrame();
        frame.setTitle(targetUser + "'s Calendar");
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // Icon
        ImageIcon img = new ImageIcon("resources/calendar-icon.png");
        frame.setIconImage(img.getImage());

        // Title label
        JLabel lblTitle = new JLabel(targetUser + "'s Calendar Events");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBounds(280, 10, 300, 30);
        frame.getContentPane().add(lblTitle);

        // Scroll pane + JTable
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 50, 750, 260);
        frame.getContentPane().add(scrollPane);

        calendarTable = new JTable();
        scrollPane.setViewportView(calendarTable);

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(350, 325, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }

    /**
     * Queries the user's calendar table and fills the JTable.
     */
    void populateCalendar() {
        try {
            // Ensure our shared connection is still open
            if (Database.connection == null || Database.connection.isClosed()) {
                Database.connect();
            }
            Connection conn = Database.connection;

            // Verify the calendar table actually exists
            String tableName = targetUser + "_calendar";
            java.sql.DatabaseMetaData md = conn.getMetaData();
            try (ResultSet tbls = md.getTables(null, null, tableName, null)) {
                if (!tbls.next()) {
                    JOptionPane.showMessageDialog(frame,
                        "Calendar table not found for user " + targetUser,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    frame.dispose();
                    return;
                }
            }

            //  Fetch and display events
            String sql = "SELECT event_id AS ID, title AS Title, description AS Description, "
                       + "date AS Date, start_time AS `Start Time`, end_time AS `End Time`, location AS Location "
                       + "FROM " + tableName;
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            calendarTable.setModel(DbUtils.resultSetToTableModel(rs));
            if (calendarTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame,
                    "No events to display.","Info",JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error loading calendar for " + targetUser + ": " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
