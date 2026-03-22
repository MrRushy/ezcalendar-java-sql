// Admin_Window.java
package Application;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

/**
 * Admin_Window.java
 * 
 * This class creates the main admin interface for EZ Calendar.
 * It provides buttons for viewing all users, viewing a user's calendar,
 * deleting a user, and signing out. Each button triggers the corresponding
 * admin methods in Ez_Calendar_Controller.
 */

public class Admin_Window {
    public JFrame frame;

    /**
     * Launch the admin panel.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Database.connect();

                Admin_Window window = new Admin_Window();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor: sets up UI
     */
    public Admin_Window() {
        initialize();
    }

    /**
     * Build frame and add all admin function buttons.
     * Validates each prompt before opening helper windows.
     */
    void initialize() {
        frame = new JFrame();
        frame.setTitle("Admin Panel");
        frame.setBounds(100, 100, 520, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);

       // UI_Styler.styleFrame(frame);
        
       // setLookAndFeel();

        JButton title = new JButton("Administrator Functions");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBounds(130, 20, 260, 30);
        title.setEnabled(false);
        frame.getContentPane().add(title);

        int y = 70, dy = 40;
        int btnWidth = 260, btnX = 130, btnHeight = 30;

        // View All Users
        JButton btnViewUsers = new JButton("View All Users");
        btnViewUsers.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnViewUsers.setBounds(btnX, y, btnWidth, btnHeight);
        btnViewUsers.addActionListener(e -> {
            Admin_View_Users_Window w = new Admin_View_Users_Window();
            w.frame.setLocationRelativeTo(frame);
            w.frame.setVisible(true);
        });
        frame.getContentPane().add(btnViewUsers);

        // View User Calendar
        y += dy;
        JButton btnViewUserCal = new JButton("View User Calendar");
        btnViewUserCal.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnViewUserCal.setBounds(btnX, y, btnWidth, btnHeight);
        btnViewUserCal.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(frame, "Enter username:");
            if (user == null || user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Username cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Admin_View_User_Calendar_Window w =
                new Admin_View_User_Calendar_Window(user.trim());
            w.frame.setLocationRelativeTo(frame);
            w.frame.setVisible(true);
        });
        frame.getContentPane().add(btnViewUserCal);

        // Manage User Calendar
        y += dy;
        JButton btnManageUserCal = new JButton("Manage User Calendar");
        btnManageUserCal.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnManageUserCal.setBounds(btnX, y, btnWidth, btnHeight);
        btnManageUserCal.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(frame, "Enter username for management:");
            if (user == null || user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Username cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Admin_Manage_User_Calendar_Window w =
                new Admin_Manage_User_Calendar_Window(user.trim());
            w.frame.setLocationRelativeTo(frame);
            w.frame.setVisible(true);
        });
        frame.getContentPane().add(btnManageUserCal);

        // View Group
        y += dy;
        JButton btnViewGroup = new JButton("View Group");
        btnViewGroup.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnViewGroup.setBounds(btnX, y, btnWidth, btnHeight);
        btnViewGroup.addActionListener(e -> {
            String groupTable = JOptionPane.showInputDialog(frame, "Enter group table name (e.g., username_group):");
            if (groupTable == null || groupTable.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Group name cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
            	Ez_Calendar_Controller.adminViewGroup(groupTable.trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "An error occurred while viewing the group.",
                    "View Group Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        frame.getContentPane().add(btnViewGroup);


        // Remove Group Member
        y += dy;
        JButton btnRemoveMember = new JButton("Remove Group Member");
        btnRemoveMember.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnRemoveMember.setBounds(btnX, y, btnWidth, btnHeight);
        btnRemoveMember.addActionListener(e -> {
            String groupTable = JOptionPane.showInputDialog(frame, "Enter group table name (e.g., jake_group):");
            if (groupTable == null || groupTable.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Group name cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String memberUsername = JOptionPane.showInputDialog(frame, "Enter member's username:");
            if (memberUsername == null || memberUsername.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Member username cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ez_Calendar_Controller.adminDeleteGroupMember(groupTable.trim(), memberUsername.trim());
            JOptionPane.showMessageDialog(frame,
                "Check console for removal result.",
                "Remove Group Member", JOptionPane.INFORMATION_MESSAGE);
        });
        frame.getContentPane().add(btnRemoveMember);


        // Delete Group
        y += dy;
        JButton btnDeleteGroup = new JButton("Delete Group");
        btnDeleteGroup.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnDeleteGroup.setBounds(btnX, y, btnWidth, btnHeight);
        btnDeleteGroup.addActionListener(e -> {
            String groupTable = JOptionPane.showInputDialog(frame, "Enter group table name (e.g., jake_group):");
            if (groupTable == null || groupTable.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Group name cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete group '" + groupTable + "' and all its events?",
                "Confirm Group Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Ez_Calendar_Controller.adminDeleteGroup(groupTable.trim());
                JOptionPane.showMessageDialog(frame,
                    "Group deletion attempted. Check console for results.",
                    "Delete Group", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.getContentPane().add(btnDeleteGroup);


        // Delete User
        y += dy;
        JButton btnDeleteUser = new JButton("Delete User");
        btnDeleteUser.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnDeleteUser.setBounds(btnX, y, btnWidth, btnHeight);
        btnDeleteUser.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(frame, "Enter username to delete:");
            if (user == null || user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Username cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete user '" + user + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Ez_Calendar_Controller.adminDeleteUser(user.trim());
                JOptionPane.showMessageDialog(frame,
                    "Deletion completed (check console).",
                    "Delete User", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.getContentPane().add(btnDeleteUser);

        // Sign Out button
        JButton btnSignOut = new JButton("Sign Out");
        btnSignOut.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnSignOut.setBounds(200, 360, 120, 30);
        btnSignOut.addActionListener(e -> {
            frame.dispose();
            Ez_Calendar_Controller.signOut();
            Sign_In_Window win = new Sign_In_Window();
            win.frame.setLocationRelativeTo(null);
            win.frame.setVisible(true);
        });
        frame.getContentPane().add(btnSignOut);
    }
    
 // Makes the UI look modern if the user is running on Windows
 	public void setLookAndFeel() {
 		try {
 			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
 			} catch (Exception e) { }
 		}
}
