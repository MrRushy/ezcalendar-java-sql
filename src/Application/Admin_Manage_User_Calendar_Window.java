// Admin_Manage_User_Calendar_Window.java
package Application;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

/**
 * Admin_Manage_User_Calendar_Window.java
 *
 * Shows a user's calendar and lets the admin delete selected events.
 */
public class Admin_Manage_User_Calendar_Window {
    public JFrame frame;
    private JTable calendarTable;
    private String targetUser;

    /**
     * Launch for testing.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();
                Admin_Manage_User_Calendar_Window window =
                    new Admin_Manage_User_Calendar_Window("alice");
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor: takes username to manage.
     */
    public Admin_Manage_User_Calendar_Window(String username) {
        this.targetUser = username.toLowerCase();
        initialize();
        populateCalendar();
    }

    /**
     * Sets up frame, table, and buttons.
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle(targetUser + "'s Calendar");
        frame.setBounds(100, 100, 800, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

        ImageIcon img = new ImageIcon("resources/calendar-icon.png");
        frame.setIconImage(img.getImage());

        JLabel lblTitle = new JLabel("Calendar for " + targetUser);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBounds(300, 10, 300, 30);
        frame.getContentPane().add(lblTitle);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 50, 750, 280);
        frame.getContentPane().add(scrollPane);

        calendarTable = new JTable();
        scrollPane.setViewportView(calendarTable);

        JButton btnDelete = new JButton("Delete Selected Event");
        btnDelete.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnDelete.setBounds(250, 350, 200, 30);
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = calendarTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(frame,
                        "Please select an event to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int eventId = (int) calendarTable.getModel().getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete event ID " + eventId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Ez_Calendar_Controller.adminDeleteUserCalendarEntry(targetUser, eventId);
                    populateCalendar();
                }
            }
        });
        frame.getContentPane().add(btnDelete);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(350, 390, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }

    /**
     * Populates the table from the user's calendar table.
     * Validates user and table existence.
     */
    void populateCalendar() {
        try {
            // Ensure our shared connection is still open
            if (Database.connection == null || Database.connection.isClosed()) {
                Database.connect();
            }
            Connection conn = Database.connection;

            // Validate user
            PreparedStatement userChk = conn.prepareStatement(
                "SELECT COUNT(*) FROM ez_user WHERE user_name = ?"
            );
            userChk.setString(1, targetUser);
            ResultSet urs = userChk.executeQuery();
            if (urs.next() && urs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(frame,
                    "User not found.","Error",JOptionPane.ERROR_MESSAGE);
                frame.dispose(); return;
            }

            // Validate table
            String tableName = targetUser + "_calendar";
            PreparedStatement tChk = conn.prepareStatement(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
              + "WHERE TABLE_SCHEMA='ez_calendar' AND TABLE_NAME = ?"
            );
            tChk.setString(1, tableName);
            ResultSet trs = tChk.executeQuery();
            if (trs.next() && trs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(frame,
                  "Calendar table does not exist.","Error",JOptionPane.ERROR_MESSAGE);
                frame.dispose(); return;
            }

            // Fetch events
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
