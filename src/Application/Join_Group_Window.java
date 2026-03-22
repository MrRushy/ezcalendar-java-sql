package Application;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Join_Group_Window {

    public JFrame frame;
    private JTextField groupCodeTF;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Join_Group_Window window = new Join_Group_Window();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Join_Group_Window() {
        initialize();
    }

    void initialize() {
        Database.connect();
        frame = new JFrame();
        frame.setTitle("Join a Group");
        frame.setBounds(100, 100, 290, 280);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        UI_Styler.styleFrame(frame);

        createGroupCodeLBL();
        theCodeTF();
        joinGroupButton();
        back();
    }

    public void createGroupCodeLBL() {
        JLabel label = new JLabel("Group Code");
        label.setBounds(88, 47, 120, 18);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        UI_Styler.styleLabel(label);
        frame.getContentPane().add(label);
    }

    public void theCodeTF() {
        groupCodeTF = new JTextField();
        groupCodeTF.setBounds(55, 82, 174, 28);
        UI_Styler.styleTextField(groupCodeTF);
        frame.getContentPane().add(groupCodeTF);
    }

    public void joinGroupButton() {
        JButton button = new JButton("Join Group");
        button.setBounds(68, 135, 150, 30);
        UI_Styler.styleButton(button);
        frame.getContentPane().add(button);

        button.addActionListener(e -> {
            String code = groupCodeTF.getText().trim();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a group code.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = Ez_Calendar_Controller.joinGroup(code);

            if (status.equals("Success")) {
                JOptionPane.showMessageDialog(frame, "Group joined successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                Main_Menu_Window MMW = new Main_Menu_Window();
                MMW.initialize();
                MMW.frame.setLocationRelativeTo(null);
                MMW.frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, status, "Join Group Failed", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void back() {
        JButton backButton = new JButton("Back");
        backButton.setBounds(100, 185, 85, 21);
        UI_Styler.styleButton(backButton);
        frame.getContentPane().add(backButton);
        backButton.addActionListener(e -> {
            frame.dispose();
            Main_Menu_Window MMW = new Main_Menu_Window();
            MMW.initialize();
            MMW.frame.setLocationRelativeTo(null);
            MMW.frame.setVisible(true);
        });
    }
}
