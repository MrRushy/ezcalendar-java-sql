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

public class Change_Password_Window {

	public JFrame frame;				// Necessary as we're using frames
	private JTextField oldPasswordTF;		// Creates a text-field from JTextField
	private JTextField newPasswordTF;	// Creates a password-field from JPasswordField
	
	/**
	 * This class is the 'Change Password Window', it pops up once the 'Change Password' button in 
	 * 'Main Menu Window ' is clicked
	 * 
	 * First are two labels, then two text-fields
	 * Then the button: 'Change Password'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Database.connect();
					Change_Password_Window window = new Change_Password_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Creates the application
	public Change_Password_Window() { initialize(); }

	void initialize() {
		Database.connect();
		frame = new JFrame();
		frame.setTitle("Sign In");
		frame.setBounds(100, 100, 290, 340);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		UI_Styler.styleFrame(frame);
		
		oldPasswordLBL(); 
		newPasswordLBL();
		
		oldPasswordTF();	 
		newPasswordTF(); 
		
		changePassword();
		back();
	}
	
	// Creates Old Password label
	public void oldPasswordLBL() {
		JLabel userNameLBL = new JLabel("Old Password");
		userNameLBL.setBounds(75, 47, 200, 26);
		userNameLBL.setFont(new Font("SansSerif",Font.BOLD, 18));
		UI_Styler.styleLabel(userNameLBL);
		frame.getContentPane().add(userNameLBL);
	}
	
	// Creates New Password label
	public void newPasswordLBL() {
		JLabel passwordLBL = new JLabel("New Password");
		passwordLBL.setBounds(75, 117, 200, 26);
		passwordLBL.setFont(new Font("SansSerif",Font.BOLD, 18));
		UI_Styler.styleLabel(passwordLBL);
		frame.getContentPane().add(passwordLBL);
	}
	
	// Creates Old Password text field
	public void oldPasswordTF() {
		oldPasswordTF = new JTextField();
		oldPasswordTF.setBounds(50, 70, 174, 28);
		UI_Styler.styleTextField(oldPasswordTF);
		frame.getContentPane().add(oldPasswordTF);
	}
	
	// Creates New Password text field
	public void newPasswordTF() {
		newPasswordTF = new JPasswordField();
		newPasswordTF.setBounds(50, 140, 174, 28);
		UI_Styler.styleTextField(newPasswordTF);
		frame.getContentPane().add(newPasswordTF);
	}
	
	// Creates the Change Password button
	public void changePassword() {
		JButton changePasswordButton = new JButton("Change Password");
		changePasswordButton.setBounds(46, 195, 180, 30);
		UI_Styler.styleButton(changePasswordButton);
		frame.getContentPane().add(changePasswordButton);
		changePasswordButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String oldPassword = oldPasswordTF.getText().trim();
		        String newPassword = newPasswordTF.getText().trim();

		        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
		            JOptionPane.showMessageDialog(frame, "Please fill out both fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
		            return;
		        }

		        boolean success = Ez_Calendar_Controller.changePassword(oldPassword, newPassword);

		        if (success) {
		            JOptionPane.showMessageDialog(frame, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		        } else {
		            JOptionPane.showMessageDialog(frame, "Incorrect old password.", "Error", JOptionPane.ERROR_MESSAGE);
		        }

		        frame.dispose();
		        Main_Menu_Window MMW = new Main_Menu_Window();
		        MMW.initialize();
		        MMW.frame.setLocationRelativeTo(null);
		        MMW.frame.setVisible(true);
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
				Main_Menu_Window MMW = new Main_Menu_Window();
				MMW.initialize();
				MMW.frame.setLocationRelativeTo(null);
				MMW.frame.setVisible(true);
				}
			});
		}
}
