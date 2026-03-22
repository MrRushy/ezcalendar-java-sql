package Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class View_List_Of_Groups_Window {

    public JFrame frame;
    private JList<String> groupList;
    private DefaultListModel<String> listModel;
    private JButton viewGroupButton;
    private String groupTable;
    
    public View_List_Of_Groups_Window() {
        initialize();
    }

    public View_List_Of_Groups_Window(String groupTable) {
	    this.groupTable = groupTable;
	    initialize();
	}
    
    public void initialize() {
        frame = new JFrame("Your Groups");
        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        UI_Styler.styleFrame(frame);

        JLabel lbl = new JLabel("List of Groups:");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setBounds(20, 15, 200, 30);
        UI_Styler.styleLabel(lbl);
        frame.getContentPane().add(lbl);

        listModel = new DefaultListModel<>();
        groupList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(groupList);
        scrollPane.setBounds(20, 60, 340, 130);
        frame.getContentPane().add(scrollPane);
        
        back();

        // View Group button
        viewGroupButton = new JButton("View Group");
        viewGroupButton.setBounds(216, 210, 140, 26);
        UI_Styler.styleButton(viewGroupButton);
        frame.getContentPane().add(viewGroupButton);
        viewGroupButton.addActionListener(e -> handleGroupSelection());

        populateGroupList();
    }
    
 // Creates back button
 		public void back() {
 			JButton backButton = new JButton("Back");
 			backButton.setBounds(20, 210, 85, 26);
 			UI_Styler.styleButton(backButton);
 			frame.getContentPane().add(backButton);
 			backButton.addActionListener(new ActionListener() {
 				public void actionPerformed(ActionEvent e) {
 					frame.dispose();
 					Main_Menu_Window MMW = new Main_Menu_Window();
 					MMW.initialize();
 					MMW.frame.setLocationRelativeTo(null);
 					MMW.frame.setVisible(true);
 					}
 				});
 			}

    private void populateGroupList() {
        try {
            Connection conn = Database.connection;
            int currentUserID = Ez_Calendar_Controller.currentUserID;

            String query = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = 'ez_calendar' AND table_name LIKE ?";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, "%\\_group");
            ResultSet tables = stm.executeQuery();

            while (tables.next()) {
                String groupTable = tables.getString("table_name");

                String check = "SELECT group_id FROM " + groupTable + " WHERE member_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(check);
                checkStmt.setInt(1, currentUserID);
                ResultSet res = checkStmt.executeQuery();

                if (res.next()) {
                    listModel.addElement(groupTable); // e.g., jake_group
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGroupSelection() {
        String selectedGroup = groupList.getSelectedValue();
        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(frame, "Please select a group to view.", "No Group Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = Database.connection;
            String query = "SELECT group_id FROM " + selectedGroup + " WHERE member_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Ez_Calendar_Controller.currentUserID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int groupID = rs.getInt("group_id");
                Ez_Calendar_Controller.currentGroupID = groupID;
                Ez_Calendar_Controller.currentGroupName = selectedGroup;

                frame.dispose();
                View_Group_Calendar_Window gcw = new View_Group_Calendar_Window(selectedGroup);
                gcw.frame.setLocationRelativeTo(null);
                gcw.populateTable();
                gcw.frame.setVisible(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
