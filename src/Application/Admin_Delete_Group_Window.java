// Admin_Delete_Group_Window.java
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
import javax.swing.JTextField;

/**
 * Admin_Delete_Group_Window.java
 *
 * Allows an admin to delete an entire group (and its events).
 */
public class Admin_Delete_Group_Window {
    public JFrame frame;
    private JTextField ownerTF;
    private JTextField groupIdTF;

    /**
     * Launch for testing.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();
                Admin_Delete_Group_Window window =
                  new Admin_Delete_Group_Window();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor.
     */
    public Admin_Delete_Group_Window() {
        initialize();
    }

    /**
     * Sets up UI components.
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle("Delete Group");
        frame.setBounds(100, 100, 400, 220);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

        ImageIcon img = new ImageIcon("resources/calendar-icon.png");
        frame.setIconImage(img.getImage());

        JLabel lblOwner = new JLabel("Group Owner Username:");
        lblOwner.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblOwner.setBounds(20, 20, 180, 25);
        frame.getContentPane().add(lblOwner);

        ownerTF = new JTextField();
        ownerTF.setBounds(200, 20, 160, 25);
        frame.getContentPane().add(ownerTF);

        JLabel lblGroupId = new JLabel("Group ID:");
        lblGroupId.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblGroupId.setBounds(20, 60, 180, 25);
        frame.getContentPane().add(lblGroupId);

        groupIdTF = new JTextField();
        groupIdTF.setBounds(200, 60, 160, 25);
        frame.getContentPane().add(groupIdTF);

        JButton btnDelete = new JButton("Delete Group");
        btnDelete.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnDelete.setBounds(110, 100, 160, 30);
        btnDelete.addActionListener(e -> {
            String owner = ownerTF.getText().trim().toLowerCase();
            String gidStr = groupIdTF.getText().trim();
            if (owner.isEmpty() || gidStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Please fill out all fields.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int gid;
            try {
                gid = Integer.parseInt(gidStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Group ID must be an integer.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Connection conn = Database.connection;
                // Check owner exists
                PreparedStatement oChk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ez_user WHERE user_name = ?"
                );
                oChk.setString(1, owner);
                ResultSet ors = oChk.executeQuery();
                if (ors.next() && ors.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(frame,
                        "Owner not found.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Check group exists
                String tableName = owner + "_group";
                PreparedStatement gChk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                  + "WHERE TABLE_SCHEMA='ez_calendar' AND TABLE_NAME = ?"
                );
                gChk.setString(1, tableName);
                ResultSet grs = gChk.executeQuery();
                if (grs.next() && grs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(frame,
                        "Group table not found for owner.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                    "Error validating input: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete group " + gid + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Ez_Calendar_Controller.adminDeleteGroup(owner);
                JOptionPane.showMessageDialog(frame,
                    "Operation complete (check console).",
                    "Delete Group", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.getContentPane().add(btnDelete);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(150, 150, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }
}
