package Application;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Group_Code_Window {
	
	public JFrame frame;	// Necessary as we're using frames
	
	/**
	 * This class is the 'Group Code Window', it pops up once the 'Group Code' button in 
	 * 'View Group Calendar Window' is clicked
	 * Only visible to Group Owners
	 * 
	 * First are two labels, then the two buttons: 'Copy Code' and 'Back'
	 * 
	 * The public static void main is found within every window, as it is necessary
	 * in order to have the window actually appear
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Group_Code_Window window = new Group_Code_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// Create the application
	public Group_Code_Window() { initialize(); }

	void initialize() {
		Database.connect();
		frame = new JFrame();
		frame.setTitle("Group Code");
		frame.setBounds(100, 100, 280, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		UI_Styler.styleFrame(frame);
		
	    String groupCode = Ez_Calendar_Controller.retrieveCode();
	    
	    groupCodeTitleLBL();
	    groupCodeLBL(groupCode);
	    
	    createCopyButton(groupCode);
	    back();
	  
	}
	
	// Creates Group Code Title label
	public void groupCodeTitleLBL() {
		JLabel groupCodeTitleLBL = new JLabel("Group Code:");
		groupCodeTitleLBL.setBounds(21, 9, 300, 30);
		groupCodeTitleLBL.setFont(new Font("SansSerif", Font.BOLD, 22));
		UI_Styler.styleLabel(groupCodeTitleLBL);
		frame.getContentPane().add(groupCodeTitleLBL);
	}
	
	// Creates Group Code label
	public void groupCodeLBL(String code) {
		JLabel groupCodeLBL = new JLabel(code); 
		groupCodeLBL.setBounds(95, 45, 200, 45); 
		groupCodeLBL.setFont(new Font("SansSerif", Font.BOLD, 20));
		UI_Styler.styleLabel(groupCodeLBL);
		frame.getContentPane().add(groupCodeLBL);
	}
	
	// Creates Back button
	public void back() {
		JButton backButton = new JButton("Back");
		backButton.setBounds(21, 120, 85, 28);
		UI_Styler.styleButton(backButton);
		frame.getContentPane().add(backButton);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				View_Group_Calendar_Window VGCW = new View_Group_Calendar_Window();
				VGCW.initialize();
				VGCW.frame.setLocationRelativeTo(null);
				VGCW.frame.setVisible(true);
			}
		});
	}
		
	// Creates Copy button
	public void createCopyButton(String code) {
	    JButton copyButton = new JButton("Copy Code");
	    copyButton.setBounds(140, 120, 110, 28);
	    UI_Styler.styleButton(copyButton);
	    frame.getContentPane().add(copyButton);

	    copyButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		java.awt.Toolkit.getDefaultToolkit()
		        	.getSystemClipboard()
		            .setContents(new java.awt.datatransfer.StringSelection(code), null);

		        javax.swing.JOptionPane.showMessageDialog(frame, "Group code copied to clipboard!");
	    	}
	    });
	}

}
