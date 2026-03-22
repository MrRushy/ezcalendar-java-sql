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
import javax.swing.JPasswordField;

public class Sign_In_Window {

	public JFrame frame;				// Necessary as we're using frames
	private JTextField userNameTF;		// Creates a text-field from JTextField
	private JPasswordField passwordPF;	// Creates a password-field from JPasswordField
	
	/**
	 * This class is the 'Sign In Window', it pops up once the 'Sign In' button in 
	 * 'Ez Calendar Window' is clicked
	 * 
	 * First are two labels, then two text-fields
	 * Then the button: 'Sign In'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Database.connect();
					Sign_In_Window window = new Sign_In_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Creates the application
	public Sign_In_Window() { initialize(); }

	void initialize() {
		Database.connect();
		frame = new JFrame();
		frame.setTitle("Sign In");
		frame.setBounds(100, 100, 290, 340);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		UI_Styler.styleFrame(frame);
		
		userNameLBL(); 
		passwordLBL();
		
		userNameTF();	 
		passwordTF(); 
		
		signInButton();
		back();
	}
	
	// Creates User Name label
	public void userNameLBL() {
		JLabel userNameLBL = new JLabel("User Name");
		userNameLBL.setBounds(95, 47, 110, 16);
		userNameLBL.setFont(new Font("SansSerif",Font.BOLD, 18));
		UI_Styler.styleLabel(userNameLBL);
		frame.getContentPane().add(userNameLBL);
	}
	
	// Creates Password label
	public void passwordLBL() {
		JLabel passwordLBL = new JLabel("Password");
		passwordLBL.setBounds(95, 117, 92, 16);
		passwordLBL.setFont(new Font("SansSerif",Font.BOLD, 18));
		UI_Styler.styleLabel(passwordLBL);
		frame.getContentPane().add(passwordLBL);
	}
	
	// Creates User Name text field
	public void userNameTF() {
		userNameTF = new JTextField();
		userNameTF.setColumns(10);
		userNameTF.setBounds(50, 70, 174, 28);
		UI_Styler.styleTextField(userNameTF);
		frame.getContentPane().add(userNameTF);
	}
	
	// Creates Password text field
	public void passwordTF() {
		passwordPF = new JPasswordField();
		passwordPF.setColumns(10);
		passwordPF.setBounds(50, 140, 174, 28);
		UI_Styler.styleTextField(passwordPF);
		frame.getContentPane().add(passwordPF);
	}
	
	// Creates the Sign In button
	public void signInButton() {
		JButton signInButton = new JButton("Sign In");
		signInButton.setBounds(64, 195, 150, 30);
		UI_Styler.styleButton(signInButton);
		frame.getContentPane().add(signInButton);
		signInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = userNameTF.getText().trim();			// Trims the text entered in the text fields above, and stores it in a variable
				String password = new String(passwordPF.getPassword()); // Get password from JPasswordField

				// Calls the 'signIn' method from Ez_Calendar_Controller
				int userType = Ez_Calendar_Controller.signIn(username, password);

				if (userType == -1) {
					JOptionPane.showMessageDialog(frame, "Incorrect username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				} else if (userType == 0) {
					frame.dispose();
					Main_Menu_Window MMW = new Main_Menu_Window();
					MMW.initialize();
					MMW.frame.setLocationRelativeTo(null);
					MMW.frame.setVisible(true);
				} else if (userType == 1) {
					frame.dispose();
					Admin_Window AW = new Admin_Window();
					AW.initialize();
					AW.frame.setLocationRelativeTo(null);
					AW.frame.setVisible(true);
				}
			}
		});
	}
	
	// Creates back button
	public void back() {
		JButton backButton = new JButton("Back");
		backButton.setBounds(98, 245, 85, 21);
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