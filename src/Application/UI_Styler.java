package Application;

import javax.swing.*;
import java.awt.*;

public class UI_Styler {
	
    public static void styleButton(JButton button) {
        button.setBackground(Color.decode("#1E1E2F")); // #5175ae // #1E1E2F
        button.setForeground(Color.WHITE);              
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(Color.decode("#E9F5EC")); // #3B82F6
        ImageIcon img = new ImageIcon("resources/calendar-icon.png");	// Sets the icon in the top left of the frame to a calendar icon
        frame.setIconImage(img.getImage());
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(Color.decode("#1B4332")); // #191927
    }
    
    public static void styleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textField.setCaretColor(Color.BLACK); // Cursor color
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        textField.setColumns(10);
    }
    
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.decode("#1B4332"));
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        comboBox.setFocusable(false);
    }

}
