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

public class Create_Group_Window {
		
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
					Create_Group_Window window = new Create_Group_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
	// Create the application
	public Create_Group_Window() { initialize(); }

	void initialize() {
		Database.connect();										// Connects to the database
		frame = new JFrame();									// Creates a new frame
		frame.setTitle("Create a Group");						// Sets the title of the frame as 'Create a Group'
		frame.setBounds(100, 100, 290, 280);					// Sets the bounds of the frame in the following format (x, y, width, height)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Makes it so once the X button is clicked, the frame closes
		frame.getContentPane().setLayout(null);					// Tells Swing to not use Layout Manager, as we're manually placing every component

		UI_Styler.styleFrame(frame);
			
		groupNameLBL();
		groupNameTF();
		    
		createGroup();
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
	public void createGroup() {
		JButton createGroupButton = new JButton("Create Group");				// Sets the text within a button, in this case it's 'Create Group'
		createGroupButton.setBounds(68, 135, 150, 30);						// Sets the bounds of the button (x, y, width, height)
		UI_Styler.styleButton(createGroupButton);
		frame.getContentPane().add(createGroupButton);

		createGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
				String groupName = groupNameTF.getText().trim();	// Trims the text entered in the text-field above, and stores it in a variable
					
				// if no text is entered in the text-field, a message pops up for the user to enter a group name
				if (groupName.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please enter a group name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
					return;
				}
					
				// Calls the 'createGroup' method from Ez_Calendar_Controller
				Ez_Calendar_Controller.createGroup(groupName);
				// Shows the user a success message
				JOptionPane.showMessageDialog(frame, "Group created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
				
				frame.dispose();						// Disposes of the current frame after the button is clicked and everthing is processed
				Main_Menu_Window MMW = new Main_Menu_Window();	// Necessary for the following steps
				MMW.initialize();						// Initializes the window that was written in the previous line
				MMW.frame.setLocationRelativeTo(null);	// Sets the location of the frame to the center of the screen *IMPORTANT*
				MMW.frame.setVisible(true);				// Sets the new frame that was initialized as visible, displaying the frame
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

