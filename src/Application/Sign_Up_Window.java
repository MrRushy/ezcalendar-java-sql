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
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Sign_Up_Window {

	public JFrame frame;			// Necessary as we're using frames
	private JTextField userNameTF;	// Creates a text-field from JTextField
	private JTextField passwordTF;
	private JTextField userEmailTF;
	
	/**
	 * This class is the 'Sign Up Window', it pops up once the 'Sign Up' button in 
	 * 'Ez Calendar Window' is clicked
	 * 
	 * First are three labels, then three text-fields
	 * Then the button: 'Sign Up'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sign_Up_Window window = new Sign_Up_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Create the application
	public Sign_Up_Window() { initialize(); }

	void initialize() {
		Database.connect();
		frame = new JFrame();
		frame.setTitle("Sign Up");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		UI_Styler.styleFrame(frame);
		
		userNameLBL();
		passwordLBL();
		userEmailLBL();
		
		userNameTF(); 
		passwordTF(); 
		userEmailTF(); 
		
		signUpButton(); 
		back();
	}
	
	// Creates User Name label
	public void userNameLBL() {
		JLabel userNameLBL = new JLabel("Username:");
		userNameLBL.setBounds(40, 44, 120, 18);
		userNameLBL.setFont(new Font("SansSerif",Font.BOLD, 17));
		UI_Styler.styleLabel(userNameLBL);
		frame.getContentPane().add(userNameLBL);
	}
	
	// Creates Password label
	public void passwordLBL() {
		JLabel passwordLBL = new JLabel("Password:");
		passwordLBL.setBounds(42, 84, 120, 18);
		passwordLBL.setFont(new Font("SansSerif",Font.BOLD, 17));
		UI_Styler.styleLabel(passwordLBL);
		frame.getContentPane().add(passwordLBL);
	}
	
	// Creates User Email label
	public void userEmailLBL() {
		JLabel emailLBL = new JLabel("Email:");
		emailLBL.setBounds(78, 126, 69, 18); 
		emailLBL.setFont(new Font("SansSerif",Font.BOLD, 17));
		UI_Styler.styleLabel(emailLBL);
		frame.getContentPane().add(emailLBL);
	}
	
	// Creates User Name text field
	public void userNameTF() {
		userNameTF = new JTextField();
		userNameTF.setBounds(150, 37, 223, 32);
		UI_Styler.styleTextField(userNameTF);
		frame.getContentPane().add(userNameTF);
	}
		
	// Creates Password text field
	public void passwordTF() {
		passwordTF = new JTextField();
		passwordTF.setBounds(150, 79, 223, 32);
		UI_Styler.styleTextField(passwordTF);
		frame.getContentPane().add(passwordTF);
	}
	
	// Creates User Email text field
	public void userEmailTF() {
		userEmailTF = new JTextField();
		userEmailTF.setBounds(150, 121, 223, 32);
		UI_Styler.styleTextField(userEmailTF);
		frame.getContentPane().add(userEmailTF);
	}
	
	// Creates the Sign Up button
	public void signUpButton() {
		JButton signUpButton = new JButton("Sign Up");
		signUpButton.setBounds(202, 180, 117, 29);
		UI_Styler.styleButton(signUpButton);
		frame.getContentPane().add(signUpButton);
		signUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = userNameTF.getText().trim();	// Trims the text entered in the text fields above, and stores it in a variable
				String password = passwordTF.getText().trim();
				String email = userEmailTF.getText().trim();

				// If any of the text fields are empty, a message is displayed telling the user to fill everything in
				if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (username.contains(" ")) {
					JOptionPane.showMessageDialog(frame, "No spaces allowed in the username.", "Invalid Username", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Connect to DB if not already connected
				if (Database.connection == null) {
					Database.connect();
				}

				// Calls the 'signUp' method from Ez_Calendar_Controller
				Ez_Calendar_Controller.signUp(username, password, email);

				JOptionPane.showMessageDialog(frame, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

				frame.dispose(); 
				Sign_In_Window SIW = new Sign_In_Window();
				SIW.initialize();
				SIW.frame.setLocationRelativeTo(null);
				SIW.frame.setVisible(true);
			}
		});
	}
	
	// Creates Back button
		public void back() {
			JButton backButton = new JButton("Back");
			backButton.setBounds(220, 220, 85, 25);
			UI_Styler.styleButton(backButton);
			frame.getContentPane().add(backButton);
			backButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					Ez_Calendar_Window ECW = new Ez_Calendar_Window();
					ECW.initialize();
					ECW.frame.setLocationRelativeTo(null);
					ECW.frame.setVisible(true);
				}
			});
		}
	
}