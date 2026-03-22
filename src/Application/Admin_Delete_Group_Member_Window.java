// Admin_Delete_Group_Member_Window.java
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
 * Admin_Delete_Group_Member_Window.java
 *
 * Allows an admin to remove a member from a specific group.
 */
public class Admin_Delete_Group_Member_Window {
    public JFrame frame;
    private JTextField ownerTF;
    private JTextField groupIdTF;
    private JTextField memberTF;

    /**
     * Launch for testing.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();
                Admin_Delete_Group_Member_Window window =
                  new Admin_Delete_Group_Member_Window();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor: builds the form.
     */
    public Admin_Delete_Group_Member_Window() {
        initialize();
    }

    /**
     * Sets up frame and input fields/buttons.
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle("Remove Member from Group");
        frame.setBounds(100, 100, 400, 260);
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

        JLabel lblMember = new JLabel("Member Username:");
        lblMember.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMember.setBounds(20, 100, 180, 25);
        frame.getContentPane().add(lblMember);

        memberTF = new JTextField();
        memberTF.setBounds(200, 100, 160, 25);
        frame.getContentPane().add(memberTF);

        JButton btnRemove = new JButton("Remove Member");
        btnRemove.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnRemove.setBounds(110, 140, 160, 30);
        btnRemove.addActionListener(e -> {
            String owner = ownerTF.getText().trim().toLowerCase();
            String member = memberTF.getText().trim().toLowerCase();
            String gidStr = groupIdTF.getText().trim();
            if (owner.isEmpty() || member.isEmpty() || gidStr.isEmpty()) {
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
                // Check member exists
                PreparedStatement mChk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ez_user WHERE user_name = ?"
                );
                mChk.setString(1, member);
                ResultSet mrs = mChk.executeQuery();
                if (mrs.next() && mrs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(frame,
                        "Member not found.","Error",JOptionPane.ERROR_MESSAGE);
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
            Ez_Calendar_Controller.adminDeleteGroupMember(owner, member);
            JOptionPane.showMessageDialog(frame,
                "Operation complete (check console).",
                "Remove Member", JOptionPane.INFORMATION_MESSAGE);
        });
        frame.getContentPane().add(btnRemove);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBack.setBounds(150, 190, 100, 30);
        btnBack.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnBack);
    }
}
