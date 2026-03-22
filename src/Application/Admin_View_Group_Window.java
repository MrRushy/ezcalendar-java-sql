// Admin_View_Group_Window.java
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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

/**
 * Admin_View_Group_Window.java
 *
 * Displays rows from a specific group table (owner_username_group)
 * filtered by a given group_id, including both member_id and member_name
 * so admins can see which user corresponds to each ID.
 */
public class Admin_View_Group_Window {
    public JFrame frame;
    private JTable groupTable;
    private String ownerUsername;
    private int groupId;

    /**
     * Launch example (for testing).
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();
                Admin_View_Group_Window window =
                  new Admin_View_Group_Window("alice", 5);
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor: specify the owner and group_id to view.
     */
    public Admin_View_Group_Window(String ownerUsername, int groupId) {
        this.ownerUsername = ownerUsername.toLowerCase();
        this.groupId = groupId;
        initialize();
        populateGroup();
    }

    /**
     * Sets up the UI (frame, labels, table, back button).
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle(ownerUsername + "'s Group " + groupId);
        frame.setBounds(100, 100, 650, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

        ImageIcon img = new ImageIcon("resources/calendar-icon.png");
        frame.setIconImage(img.getImage());

        JLabel lblTitle = new JLabel("Group " + groupId + " for " + ownerUsername);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBounds(200, 10, 300, 30);
        frame.getContentPane().add(lblTitle);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 50, 600, 260);
        frame.getContentPane().add(scrollPane);

        groupTable = new JTable();
        scrollPane.setViewportView(groupTable);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(270, 325, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }

    /**
     * Queries the owner's group table joining ez_user to fetch member names.
     * Validates owner and table existence, and emptiness.
     */
    void populateGroup() {
        try {
            // ── Line ~89 ── Ensure our shared connection is still open
            if (Database.connection == null || Database.connection.isClosed()) {
                Database.connect();
            }
            Connection conn = Database.connection;

            // Validate owner exists
            PreparedStatement oChk = conn.prepareStatement(
                "SELECT COUNT(*) FROM ez_user WHERE user_name = ?"
            );
            oChk.setString(1, ownerUsername);
            ResultSet ors = oChk.executeQuery();
            if (ors.next() && ors.getInt(1) == 0) {
                JOptionPane.showMessageDialog(frame,
                    "Owner '" + ownerUsername + "' not found.",
                    "Not Found", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
                return;
            }

            // Validate group table exists
            String tableName = ownerUsername + "_group";
            PreparedStatement tChk = conn.prepareStatement(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
              + "WHERE TABLE_SCHEMA = 'ez_calendar' AND TABLE_NAME = ?"
            );
            tChk.setString(1, tableName);
            ResultSet trs = tChk.executeQuery();
            if (trs.next() && trs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(frame,
                    "Group table does not exist for owner.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
                return;
            }

            // ── Line ~115 ── Query with both MemberID and MemberName
            String sql =
                "SELECT g.group_id AS GroupID, " +
                "       g.group_name AS GroupName, " +
                "       g.group_creation_date AS CreatedOn, " +
                "       g.owner_id AS OwnerID, " +
                "       ou.user_name   AS OwnerName, " +
                "       g.member_id AS MemberID, " +
                "       mu.user_name AS MemberName " +
                "FROM " + tableName + " g " +
                "  LEFT JOIN ez_user ou ON g.owner_id = ou.user_id " +
                "  LEFT JOIN ez_user mu ON g.member_id = mu.user_id " +
                "WHERE g.group_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, groupId);
            ResultSet rs = stm.executeQuery();

            groupTable.setModel(DbUtils.resultSetToTableModel(rs));

            // Notify if no rows returned
            if (groupTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame,
                    "No such group (ID " + groupId + ") for owner '" + ownerUsername + "'.",
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error loading group: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
