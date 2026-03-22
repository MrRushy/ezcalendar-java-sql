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
import java.text.SimpleDateFormat;

import javax.swing.JTextField;
import javax.swing.UIManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Edit_Event_Window {

	public JFrame frame;				// Necessary as we're using frames
	private JTextField eventTitleTF;	// Creates a text field from JTextField
	private JTextField eventDescTF; //Should make JTextArea
	private JTextField eventDateTF;
	private JTextField eventStartTF;
	private JTextField eventEndTF;
	private JTextField eventLocationTF;
	private int eventIdToEdit;
	
	/**
	 * This class is the 'Create Event Window', it pops up once the 'Create Event' button in 
	 * 'View Personal / Group Calendar Window' is clicked
	 * 
	 * First are all the labels and then after are all the text fields
	 * Then at the end are the two buttons in this window, 'Create Event' and 'Back'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Edit_Event_Window window = new Edit_Event_Window(1);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// Create the application
	public Edit_Event_Window(int eventID) { this.eventIdToEdit = eventID; 
	initialize(); }

	void initialize() {
		Database.connect(); 									// Connects to the database
		frame = new JFrame(); 									// Creates a new frame
		frame.setTitle("Edit Event");							// Sets the title of the frame as 'Edit Event'
		frame.setBounds(100, 100, 900, 300);					// Sets the bounds of the frame in the following format (x, y, width, height)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Makes it so once the X button is clicked, the frame closes
		frame.getContentPane().setLayout(null);					// Tells Swing to not use Layout Manager, as we're manually placing every component
		
		UI_Styler.styleFrame(frame);
		
		eventTitleLBL();
		eventDescLBL();
		eventDateLBL();
		eventStartLBL();
		eventEndLBL();
		eventLocationLBL();
		
		eventTitleTF();
		eventDescTF();
		eventDateTF();
		eventStartTF();
		eventEndTF();
		eventLocationTF();
		
		populateFields();
		
		saveChangesButton(); 
		back();
	}
	
	
	private void populateFields() {
		try {
			Connection connection = Database.connection;
			String table = Ez_Calendar_Controller.currentUsername.toLowerCase() + "_calendar";
			String query = "SELECT * FROM " + table + " WHERE event_id = ?";
			PreparedStatement stm = connection.prepareStatement(query);
			stm.setInt(1, eventIdToEdit);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				eventTitleTF.setText(rs.getString("title"));
				eventDescTF.setText(rs.getString("description"));
				
				String date = rs.getString("date");
				
				if (date != null && !date.isEmpty()) {
				    eventDateTF.setText(date);
				    eventDateTF.setForeground(Color.BLACK);
				} else {
				    eventDateTF.setText("yyyy-MM-dd");
				    eventDateTF.setForeground(Color.GRAY);
				}


				eventStartTF.setText(rs.getString("start_time"));
				eventEndTF.setText(rs.getString("end_time"));
				eventLocationTF.setText(rs.getString("location"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Creates Event Title label
	public void eventTitleLBL() {
		JLabel eventTitleLBL = new JLabel("Event Title:");				// The text that's displayed, in this case it's 'Event Title'
		eventTitleLBL.setBounds(50, 44, 120, 18);						/** The bounds for the Label, so it's 50 along the x-axis, 44 along the y-axis, 
																		and is able to be sized up to a width of 120 and a heigh of 18 **/
		eventTitleLBL.setFont(new Font("SansSerif",Font.BOLD, 15));		// Sets the font, style of font, and size of text
		UI_Styler.styleLabel(eventTitleLBL);
		frame.getContentPane().add(eventTitleLBL);
	}
	
	// Creates Event Description label
	public void eventDescLBL() {
		JLabel eventDescLBL = new JLabel("Event Desc:");
		eventDescLBL.setBounds(50, 84, 120, 18);
		eventDescLBL.setFont(new Font("SansSerif",Font.BOLD, 15));
		UI_Styler.styleLabel(eventDescLBL);
		frame.getContentPane().add(eventDescLBL);
	}
	
	// Creates Event Date label
	public void eventDateLBL() {
		JLabel eventDateLBL = new JLabel("Event Date:");
		eventDateLBL.setBounds(50, 126, 120, 18);
		eventDateLBL.setFont(new Font("SansSerif",Font.BOLD, 15));
		UI_Styler.styleLabel(eventDateLBL);
		frame.getContentPane().add(eventDateLBL);
	}
	
	// Creates Event Start label
	public void eventStartLBL() {
		JLabel eventStartLBL = new JLabel("Start Time:");
		eventStartLBL.setBounds(450, 46, 120, 18);
		eventStartLBL.setFont(new Font("SansSerif",Font.BOLD, 15));
		UI_Styler.styleLabel(eventStartLBL);
		frame.getContentPane().add(eventStartLBL);
	}
		
	// Creates Event End label
	public void eventEndLBL() {
		JLabel eventEndLBL = new JLabel("End Time:");
		eventEndLBL.setBounds(450, 88, 120, 18);
		eventEndLBL.setFont(new Font("SansSerif",Font.BOLD, 15));
		UI_Styler.styleLabel(eventEndLBL);
		frame.getContentPane().add(eventEndLBL);
	}
		
	// Creates Event Location label
	public void eventLocationLBL() {
		JLabel eventLocationLBL = new JLabel("Location:");
		eventLocationLBL.setBounds(450, 130, 120, 18);
		eventLocationLBL.setFont(new Font("SansSerif",Font.BOLD, 15));
		UI_Styler.styleLabel(eventLocationLBL);
		frame.getContentPane().add(eventLocationLBL);
	}
	
	// Creates User Name text field
	public void eventTitleTF() {
		eventTitleTF = new JTextField();
		eventTitleTF.setColumns(10);	// Makes it so the text-field is able to fit 10 characters, therefore: setColumns(10)
		eventTitleTF.setBounds(150, 37, 223, 32);
		eventTitleTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
		frame.getContentPane().add(eventTitleTF);
	}
		
	// Creates Password text field
	public void eventDescTF() {
		eventDescTF = new JTextField();
		eventDescTF.setColumns(10);
		eventDescTF.setBounds(150, 79, 223, 32);
		eventDescTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
		frame.getContentPane().add(eventDescTF);
	}
	
	// Creates User Email text field
	public void eventDateTF() {
		eventDateTF = new JTextField();
		eventDateTF.setColumns(10);
		eventDateTF.setBounds(150, 121, 223, 32);
		eventDateTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
		frame.getContentPane().add(eventDateTF);
	}
	
	// Creates User Name text field
		public void eventStartTF() {
			eventStartTF = new JTextField();
			eventStartTF.setColumns(10);
			eventStartTF.setBounds(555, 37, 223, 32);
			eventStartTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
			frame.getContentPane().add(eventStartTF);
		}
			
		// Creates Password text field
		public void eventEndTF() {
			eventEndTF = new JTextField();
			eventEndTF.setColumns(10);
			eventEndTF.setBounds(555, 79, 223, 32);
			eventEndTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
			frame.getContentPane().add(eventEndTF);
		}
		
		// Creates User Email text field
		public void eventLocationTF() {
			eventLocationTF = new JTextField();
			eventLocationTF.setColumns(10);
			eventLocationTF.setBounds(555, 121, 223, 32);
			eventLocationTF.setFont(new Font("SansSerif",Font.PLAIN, 15));
			frame.getContentPane().add(eventLocationTF);
		}
		
		// Creates the Add Event button
		public void saveChangesButton() {
			JButton addEventButton = new JButton("Save Changes");				// Sets the text within a button, in this case it's 'Add Event'
			addEventButton.setBounds(680, 180, 180, 29);					// Sets the bounds of the button (x, y, width, height)
			UI_Styler.styleButton(addEventButton);
			frame.getContentPane().add(addEventButton);
			addEventButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("[DEBUG] Save Changes clicked");

					String eventTitle = eventTitleTF.getText().trim();		// Trims the text entered in the text fields above, and stores it in a variable
					String eventDesc = eventDescTF.getText().trim();
					String eventDate = eventDateTF.getText().trim();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					formatter.setLenient(false); // This ensures strict validation (e.g., Feb 30 is invalid)

					try {
					    java.util.Date parsedDate = formatter.parse(eventDate);
					    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
					    
					    // You can now use sqlDate.toString() to safely pass to your SQL query
					    System.out.println("Valid Date: " + sqlDate);
					    
					} catch (ParseException ex) {
					    JOptionPane.showMessageDialog(frame, "Please enter a valid date in the format yyyy-MM-dd.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
					    return;
					}

					String eventStart = eventStartTF.getText().trim();
					String eventEnd = eventEndTF.getText().trim();
					String eventLocation = eventLocationTF.getText().trim();
					
					System.out.println("[DEBUG] Collected Values:");
					System.out.println("Title: " + eventTitle);
					System.out.println("Date: " + eventDate);
					System.out.println("Start: " + eventStart);
					System.out.println("End: " + eventEnd);
					System.out.println("Location: " + eventLocation);
					System.out.println("Desc: " + eventDesc);

					// If any of the text fields are empty, a message is displayed telling the user to fill everything in
					if (eventTitle.isEmpty() || eventDesc.isEmpty() || eventDate.isEmpty() || eventStart.isEmpty() || eventEnd.isEmpty() || eventLocation.isEmpty()) {
						JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
						return;
					}
					System.out.println("[DEBUG] About to call editUserCalendarEvent with ID: " + eventIdToEdit);

					// Calls the 'editUserCalendarEvent' method from Ez_Calendar_Controller
					Ez_Calendar_Controller.editUserCalendarEvent(eventIdToEdit, eventTitle, eventDesc, eventDate, eventStart, eventEnd, eventLocation);
					

					JOptionPane.showMessageDialog(frame, "Event Edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
					
					frame.dispose(); 						// Disposes of the current frame after the button is clicked and everthing is processed 
					View_Personal_Calendar_Window PCW = new View_Personal_Calendar_Window();	// Necessary for the following steps
					PCW.initialize();						// Initializes the window that was written in the previous line
					PCW.frame.setLocationRelativeTo(null);	// Sets the location of the frame to the center of the screen *IMPORTANT*
					PCW.populateTable();					// This populates the persons group and or personal calendar with the event added
					PCW.frame.setVisible(true);				// Sets the new frame that was initialized as visible, displaying the frame

				}
			});

		}
		
		
		// Creates back button
		public void back() {
			JButton backButton = new JButton("Back");
			backButton.setBounds(45, 180, 85, 21);
			UI_Styler.styleButton(backButton);
			frame.getContentPane().add(backButton);
			backButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					View_Personal_Calendar_Window PCW = new View_Personal_Calendar_Window();
					PCW.initialize();
					PCW.frame.setLocationRelativeTo(null);
					PCW.frame.setVisible(true);
					}
				});
			}
	
}