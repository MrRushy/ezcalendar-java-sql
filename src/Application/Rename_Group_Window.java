package Application;

import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.sql.*;
import javax.swing.JTextField;
import javax.swing.UIManager;

import net.proteanit.sql.DbUtils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Rename_Group_Window {
		
	public JFrame frame;			// Necessary as we're using frames
	private JTextField groupNameTF;	// Creates a text field from JTextField
		
	/**
	 * This class is the 'Create Group Window', it pops up once the 'Create Group' button in 
	 * 'Main Menu' is clicked
	 * 
	 * First is the label, next the text-field, and finally the button
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Rename_Group_Window window = new Rename_Group_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
	// Create the application
	public Rename_Group_Window() { initialize(); }

	void initialize() {
		Database.connect();										// Connects to the database
		frame = new JFrame();									// Creates a new frame
		frame.setTitle("Rename a Group");						// Sets the title of the frame as 'Create a Group'
		frame.setBounds(100, 100, 290, 280);					// Sets the bounds of the frame in the following format (x, y, width, height)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Makes it so once the X button is clicked, the frame closes
		frame.getContentPane().setLayout(null);					// Tells Swing to not use Layout Manager, as we're manually placing every component

		UI_Styler.styleFrame(frame);
			
		groupNameLBL();
		groupNameTF();
		    
		renameGroup();
		back();
	}
		
	// Creates Group Name label
	public void groupNameLBL() {
		JLabel createGroupNameLBL = new JLabel("Group Name");				// The text that's dispalyed, in this case it's 'Group Name:'
		createGroupNameLBL.setBounds(88, 47, 120, 18);						/** The bounds for the Label, so it's 50 along the x-axis, 46 along the y-axis,
			 																and is able to be sized up to a width of 120 and a height of 18**/
		createGroupNameLBL.setFont(new Font("SansSerif",Font.BOLD, 18));	// Sets the font, style of font, and size of text
		UI_Styler.styleLabel(createGroupNameLBL);
		frame.getContentPane().add(createGroupNameLBL);
	}

	// Creates Group Name text-field
	public void groupNameTF() {
		groupNameTF = new JTextField();
		groupNameTF.setBounds(55, 82, 174, 28);
		UI_Styler.styleTextField(groupNameTF);
		frame.getContentPane().add(groupNameTF);
	}

	// Creates Create Group button
	public void renameGroup() {
		JButton renameGroupButton = new JButton("Rename Group");				// Sets the text within a button, in this case it's 'Rename Group'
		renameGroupButton.setBounds(68, 135, 150, 30);						// Sets the bounds of the button (x, y, width, height)
		UI_Styler.styleButton(renameGroupButton);
		frame.getContentPane().add(renameGroupButton);

		renameGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int groupId = Ez_Calendar_Controller.currentGroupID;
				String groupName = groupNameTF.getText().trim();

				if (groupName.isEmpty()) {
				    JOptionPane.showMessageDialog(frame, "Please enter a group name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
				    return;
				}

				Ez_Calendar_Controller.groupLeaderRenameGroup(groupId, groupName);

				// Shows the user a success message
				JOptionPane.showMessageDialog(frame, "Group Renamed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

				frame.dispose();
				View_Group_Calendar_Window VGCW = new View_Group_Calendar_Window(Ez_Calendar_Controller.currentGroupName);
				VGCW.frame.setLocationRelativeTo(null);
				VGCW.populateTable();
				VGCW.frame.setVisible(true);
			}
		});

	}
		
	// Creates back button
	public void back() {
		JButton backButton = new JButton("Back");
		backButton.setBounds(100, 185, 85, 21);
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
}


