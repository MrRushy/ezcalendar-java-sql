package Application;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class Ez_Calendar_Window {

	JFrame frame;	// Necessary as we're using frames

	/**
	 * This class is the 'Ez Calendar Window', it's the frame the program is initially run from
	 * It is absolutely necessary to run the program from this window first
	 * 
	 * First is the 'Welcome' label
	 * Then at the end are the two buttons in this window, 'Sign in' and 'Sign up'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ez_Calendar_Window window = new Ez_Calendar_Window();
					window.frame.setLocationRelativeTo(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Creates the application
	public Ez_Calendar_Window() {
		initialize();
	}
	
	void initialize() {
		Database.connect();										// Connects to the database
		frame = new JFrame();									// Creates a new frame
		frame.setTitle("EZ Calendar");							// Sets the title of the frame as 'EZ Calendar'
		frame.setBounds(100, 100, 450, 325);					// Sets the bounds of the frame in the following format (x, y, width, height)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Makes it so once the X button is clicked, the frame closes
		frame.getContentPane().setLayout(null);					// Tells Swing to not use Layout Manager, as we're manually placing every component
		
		UI_Styler.styleFrame(frame);
		
		// setLookAndFeel(); 
		
		welcomeLBL();
		
		signInButton();
		signUpButton();
		
		setupClosingDBConnection();
	}
		
	// Creates Welcome label
	public void welcomeLBL() {
		JLabel welcomeLBL = new JLabel("Welcome to EZ Calendar!");	// The text that's displayed, in this case it's 'Welcome to EZ Calendar!'
		welcomeLBL.setBounds(79, 35, 300, 50);						/** The bounds for the Label, so it's 83 along the x-axis, 35 along the y-axis, 
																	and is able to be sized up to a width of 300 and a heigh of 50 **/
		welcomeLBL.setFont(new Font("SansSerif",Font.BOLD, 24));
		UI_Styler.styleLabel(welcomeLBL);
		frame.getContentPane().add(welcomeLBL);
	}
	
	// Creates Sign In button
	public void signInButton() {
		JButton signInButton = new JButton("Sign In");				// Sets the text within a button, in this case it's 'Add Event'
		signInButton.setBounds(140, 125, 170, 29);					// Sets the bounds of the button (x, y, width, height)
		UI_Styler.styleButton(signInButton);
		frame.getContentPane().add(signInButton);
		signInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();			// Disposes of the current frame after the button is clicked and everthing is processed 
				Sign_In_Window SIW = new Sign_In_Window();	// Necessary for the following steps
				SIW.initialize();			// Initializes the window that was written in the previous line
				SIW.frame.setLocationRelativeTo(null);		// Sets the location of the frame to the center of the screen *IMPORTANT*
				SIW.frame.setVisible(true);	// Sets the new frame that was initialized as visible, displaying the frame
			}
		});
	}
	
	// Creates Sign Up button
	public void signUpButton() {
		JButton signUpButton = new JButton("Sign Up");
		signUpButton.setBounds(140, 166, 170, 29);
		UI_Styler.styleButton(signUpButton);
		frame.getContentPane().add(signUpButton);
		signUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Sign_Up_Window SUW = new Sign_Up_Window();
				SUW.initialize();
				SUW.frame.setLocationRelativeTo(null);
				SUW.frame.setVisible(true);
			}
		});
	}
		
	// Makes the UI look modern if the user is running on Windows
	public void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception e) { }
		}
				
	// Handles closing the database connection if the user closes the program
	public static void setupClosingDBConnection() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		public void run() {
			try { Database.connection.close(); System.out.println("Application Closed - DB Connection Closed");
					} catch (SQLException e) { e.printStackTrace(); }
			}
		}, "Shutdown-thread"));
	}
}
