package Application;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSeparator;

public class Main_Menu_Window {
	
	public JFrame frame;			// Necessary as we're using frames
	private JButton signOutButton;	// Creates a button from JButton

	/**
	 * This class is the 'Main Menu Window', it pops up once the 'Sign In' button in 
	 * 'Sign In Window' is clicked
	 * 
	 * First are two labels, then a separator, then two more labels
	 * Then there are the five buttons: 'Personal Calendar', 'View Group', 'Create Group', 'Join Group', and 'Sign Out'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main_Menu_Window window = new Main_Menu_Window();
					window.frame.setLocationRelativeTo(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Create the application
	public Main_Menu_Window() { initialize(); }
	
	void initialize() {
		frame = new JFrame();
		frame.setTitle("Main Menu");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		UI_Styler.styleFrame(frame);
		
		welcomeLBL();
		currentUserLBL();
		
		addThickSeparator();
		
		currentDateLabelLBL();
		currentDateLBL();
		
		viewPersonalCalendar();
		
		boolean userInGroup = Ez_Calendar_Controller.isUserInGroup();
		boolean userIsOwner = Ez_Calendar_Controller.doesUserOwnAGroup();
		
		if (userInGroup) {
			viewGroup();
		}
		
		if (!userIsOwner) {
			createGroup();
		} else {
			viewGroup();
		}
		
		joinGroup();
		
		changePassword();
		signOut();
	}
		
	// Creates Welcome label
	public void welcomeLBL() {
		JLabel welcomeLBL = new JLabel("Welcome,");
		welcomeLBL.setBounds(25, 15, 160, 50);
		welcomeLBL.setFont(new Font("SansSerif",Font.BOLD, 30));
		UI_Styler.styleLabel(welcomeLBL);
		frame.getContentPane().add(welcomeLBL);
	}
	// Creates Current User label
	public void currentUserLBL() {
		JLabel currentUserLBL = new JLabel(Ez_Calendar_Controller.currentUsername); 
		currentUserLBL.setBounds(25, 55, 160, 50);
		currentUserLBL.setFont(new Font("SansSerif",Font.BOLD, 30));
		UI_Styler.styleLabel(currentUserLBL);
		frame.getContentPane().add(currentUserLBL);
	}
	// Creates Bar in between Welcome message and current date
	public void addThickSeparator() {
		JPanel linePanel = new JPanel();
		linePanel.setBackground(Color.decode("#2F2F1E"));
		linePanel.setBounds(25, 130, 135, 3); 
		frame.getContentPane().add(linePanel);
	}
		
	// Creates Current Date Label label
	public void currentDateLabelLBL() {
		JLabel currentDateLBL = new JLabel("Current Date:");
		currentDateLBL.setBounds(25, 145, 160, 50);
		UI_Styler.styleLabel(currentDateLBL);
		currentDateLBL.setFont(new Font("SansSerif",Font.ITALIC, 22));
		frame.getContentPane().add(currentDateLBL);
	}
	// Creates Current Date label
	public void currentDateLBL() {
		LocalDate date = LocalDate.now();
		String dateString = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE"));
		JLabel currentDateLBL = new JLabel(dateString);
		currentDateLBL.setBounds(25, 152, 210, 90);
		currentDateLBL.setFont(new Font("SansSerif",Font.ITALIC, 16));
		UI_Styler.styleLabel(currentDateLBL);
		frame.getContentPane().add(currentDateLBL);
	}
		
	// Creates view Personal Calendar button
	public void viewPersonalCalendar() {
	JButton viewPersonalCButton = new JButton("Personal Calendar");
	viewPersonalCButton.setBounds(265, 25, 165, 30);
	UI_Styler.styleButton(viewPersonalCButton);
	frame.getContentPane().add(viewPersonalCButton);
	viewPersonalCButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
			View_Personal_Calendar_Window PCW = new View_Personal_Calendar_Window();
			PCW.initialize();
			PCW.frame.setLocationRelativeTo(null);
			PCW.frame.setVisible(true);
			}
		});
	}
	
	// Creates View Group button
	public void viewGroup() {
		JButton viewGroupButton = new JButton("View Group");
		viewGroupButton.setBounds(265, 70, 165, 30);
		UI_Styler.styleButton(viewGroupButton);
		frame.getContentPane().add(viewGroupButton);
		viewGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				View_List_Of_Groups_Window LGW = new View_List_Of_Groups_Window();
				LGW.initialize();
				LGW.frame.setLocationRelativeTo(null);
				LGW.frame.setVisible(true);
				}
			});
	}
	
	// Creates Create Group button
	public void createGroup() {
		JButton createGroupButton = new JButton("Create Group");
		
		  // Check if user is in a group — move the button down if true
	    int yPosition = Ez_Calendar_Controller.isUserInGroup() ? 115 : 70;
		
		createGroupButton.setBounds(265, yPosition, 165, 30);
		UI_Styler.styleButton(createGroupButton);
		frame.getContentPane().add(createGroupButton);
		createGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Create_Group_Window CGW = new Create_Group_Window();
				CGW.initialize();
				CGW.frame.setLocationRelativeTo(null);
				CGW.frame.setVisible(true);
				}
			});
		}
	
	//Creates Join Group button
	public void joinGroup() {
		JButton createGroupButton = new JButton("Join Group");    
		createGroupButton.setBounds(265, 115, 165, 30);
		UI_Styler.styleButton(createGroupButton);
		frame.getContentPane().add(createGroupButton);
		createGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Join_Group_Window JGW = new Join_Group_Window();
				JGW.initialize();
				JGW.frame.setLocationRelativeTo(null);
				JGW.frame.setVisible(true);
				}
			});
		}
		
	// Creates Sign Out button
	public void signOut() {
		signOutButton = new JButton("Sign Out");
		signOutButton.setBounds(302, 227, 95, 26);
		UI_Styler.styleButton(signOutButton);
		frame.getContentPane().add(signOutButton);
		signOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Ez_Calendar_Controller.signOut();
				Ez_Calendar_Window.main(null);
			}
		});
	}
	
	//Creates Join Group button
	public void changePassword() {
		JButton createGroupButton = new JButton("Change Password");
		createGroupButton.setBounds(25, 227, 165, 26);
		UI_Styler.styleButton(createGroupButton);
		frame.getContentPane().add(createGroupButton);
		createGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Change_Password_Window CPW = new Change_Password_Window();
				CPW.initialize();
				CPW.frame.setLocationRelativeTo(null);
				CPW.frame.setVisible(true);
				}
			});
		}
}