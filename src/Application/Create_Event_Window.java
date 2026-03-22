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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Create_Event_Window {

	public JFrame frame;				// Necessary as we're using frames
	private JTextField eventTitleTF;	// Creates a text field from JTextField
	private JTextField eventDescTF;
	private JTextField eventDateTF;
	private JTextField eventStartTF;
	private JTextField eventEndTF;
	private JTextField eventLocationTF;
	
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
					Create_Event_Window window = new Create_Event_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// Create the application
	public Create_Event_Window() { initialize(); }

	void initialize() {
		Database.connect(); 									// Connects to the database
		frame = new JFrame(); 									// Creates a new frame
		frame.setTitle("Create Event");							// Sets the title of the frame as 'Create Event'
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
		
		addEventButton(); 
		back();
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
		eventTitleTF.setBounds(150, 37, 223, 32);
		UI_Styler.styleTextField(eventTitleTF);
		frame.getContentPane().add(eventTitleTF);
	}
		
	// Creates Password text field
	public void eventDescTF() {
		eventDescTF = new JTextField();
		eventDescTF.setBounds(150, 79, 223, 32);
		UI_Styler.styleTextField(eventDescTF);
		frame.getContentPane().add(eventDescTF);
	}
	
	// Creates User Email text field
	public void eventDateTF() {
		eventDateTF = new JTextField();
		eventDateTF.setBounds(150, 121, 223, 32);
		UI_Styler.styleTextField(eventDateTF);
		
		eventDateTF.setText("yyyy-MM-dd");		// Creates text in the text-field to display
		eventDateTF.setForeground(Color.GRAY);	// Changes the color of the text to gray

		/**
		 * The two methods are for the date format located within the text-field for event date:
		 * 
		 * focuseGained is for when a user clicks within the text-field, the text 'yyyy-MM-dd', disappears
		 * focusLost is for when a user clicks out of the text-field without typing anything new in
		 */
		eventDateTF.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
		public void focusGained(java.awt.event.FocusEvent e) {
			if (eventDateTF.getText().equals("yyyy-MM-dd")) {
				eventDateTF.setText("");
				eventDateTF.setForeground(Color.BLACK);
			}
		}

		@Override
		public void focusLost(java.awt.event.FocusEvent e) {
			if (eventDateTF.getText().isEmpty()) {
				eventDateTF.setText("yyyy-MM-dd");
				eventDateTF.setForeground(Color.GRAY);
			}
		}
		});

		
		frame.getContentPane().add(eventDateTF);
	}
	
	// Creates User Name text field
		public void eventStartTF() {
			eventStartTF = new JTextField();
			eventStartTF.setBounds(555, 37, 223, 32);
			UI_Styler.styleTextField(eventStartTF);
			frame.getContentPane().add(eventStartTF);
		}
			
		// Creates Password text field
		public void eventEndTF() {
			eventEndTF = new JTextField();
			eventEndTF.setBounds(555, 79, 223, 32);
			UI_Styler.styleTextField(eventEndTF);
			frame.getContentPane().add(eventEndTF);
		}
		
		// Creates User Email text field
		public void eventLocationTF() {
			eventLocationTF = new JTextField();
			eventLocationTF.setBounds(555, 121, 223, 32);
			UI_Styler.styleTextField(eventLocationTF);
			frame.getContentPane().add(eventLocationTF);
		}
		
		// Creates the Add Event button
		public void addEventButton() {
			JButton addEventButton = new JButton("Add Event");				// Sets the text within a button, in this case it's 'Add Event'
			addEventButton.setBounds(625, 180, 117, 29);					// Sets the bounds of the button (x, y, width, height)
			addEventButton.setFont(new Font("SansSerif",Font.PLAIN, 15));
			UI_Styler.styleButton(addEventButton);
			frame.getContentPane().add(addEventButton);
			addEventButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String eventTitle = eventTitleTF.getText().trim();
					String eventDesc = eventDescTF.getText().trim();
					String eventDate = eventDateTF.getText().trim();
					String eventStart = eventStartTF.getText().trim();
					String eventEnd = eventEndTF.getText().trim();
					String eventLocation = eventLocationTF.getText().trim();

					if (eventTitle.isEmpty() || eventDesc.isEmpty() || eventDate.isEmpty() || eventStart.isEmpty() || eventEnd.isEmpty() || eventLocation.isEmpty()) {
						JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// Validate date format
					try {
						LocalDate parsedDate = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					} catch (DateTimeParseException ex) {
						JOptionPane.showMessageDialog(frame, "Date must be in the format yyyy-MM-dd (e.g., 2024-12-31).", "Invalid Date", JOptionPane.WARNING_MESSAGE);
						return;
					}

					Ez_Calendar_Controller.createUserEvent(eventTitle, eventDesc, eventDate, eventStart, eventEnd, eventLocation);

					JOptionPane.showMessageDialog(frame, "Event added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
					frame.dispose(); 
					View_Personal_Calendar_Window PCW = new View_Personal_Calendar_Window();
					PCW.initialize();
					PCW.frame.setLocationRelativeTo(null);
					PCW.populateTable();
					PCW.frame.setVisible(true);
				}
			});


		}
		
		
		// Creates back button
		public void back() {
			JButton backButton = new JButton("Back");
			backButton.setBounds(45, 180, 85, 21);
			backButton.setFont(new Font("SansSerif",Font.PLAIN, 15));
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