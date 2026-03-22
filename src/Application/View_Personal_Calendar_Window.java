package Application;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import Application.Table_Styler.CalendarTheme;
import net.proteanit.sql.DbUtils;



	public class View_Personal_Calendar_Window {
		
		public JFrame frame;
		private JTable calendarEventsTable;
		private JScrollPane scrollPane;
		private JComboBox<String> themeSelector;

		
		public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						View_Personal_Calendar_Window window = new View_Personal_Calendar_Window();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		public View_Personal_Calendar_Window() { initialize(); }

		void initialize() {
			frame = new JFrame();
			frame.setTitle("Personal Calendar");
			frame.setBounds(100, 100, 1000, 400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			
			UI_Styler.styleFrame(frame);
			
		    calendarTitleLBL();
		    
		    addThemeSelector();
		    
		    editEvent();
		    deleteEvent();
		    createEvent();
		    back();
		    
			createScrollPaneAndTable();
			populateTable();
		}
		
		
		// Creates Calendar Title label
		public void calendarTitleLBL() {
			JLabel calendarTitleLBL = new JLabel(Ez_Calendar_Controller.currentUsername + "'s Calendar");
			calendarTitleLBL.setBounds(19, 9, 300, 30); 
			calendarTitleLBL.setFont(new Font("SansSerif", Font.BOLD, 18));
			UI_Styler.styleLabel(calendarTitleLBL);
			frame.getContentPane().add(calendarTitleLBL);
		}
		
		// Creates createEvent button
		public void createEvent() {
			JButton createEventButton = new JButton("Create Event");
			createEventButton.setBounds(820, 320, 148, 28);
			UI_Styler.styleButton(createEventButton);
			frame.getContentPane().add(createEventButton);
			createEventButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					Create_Event_Window CEW = new Create_Event_Window();
					CEW.initialize();
					CEW.frame.setLocationRelativeTo(null);
					CEW.frame.setVisible(true);
					}
				});
			}
		
		// Creates createEvent button
		public void deleteEvent() {
			JButton deleteEventButton = new JButton("Delete Event");				// Sets the text within a button, in this case it's 'Delete Event'
			deleteEventButton.setBounds(655, 320, 148, 28);					// Sets the bounds of the button (x, y, width, height)
			UI_Styler.styleButton(deleteEventButton);
			frame.getContentPane().add(deleteEventButton);
			deleteEventButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int selectedRow = calendarEventsTable.getSelectedRow();
					
			        if (selectedRow == -1) {
			            JOptionPane.showMessageDialog(frame, "Please select an event to delete.");
			            return;
			        }

			        int modelRow = calendarEventsTable.convertRowIndexToModel(selectedRow);
			        int eventId = (int) calendarEventsTable.getModel().getValueAt(modelRow, 0); // column 0 is event_id
			        Object groupEventObj = calendarEventsTable.getModel().getValueAt(modelRow, 1); // Column 1 = group_event_id
			        System.out.println("groupEventObj = " + groupEventObj);

			        if (groupEventObj != null) {
			        
			            if (Ez_Calendar_Controller.isCurrentUserGroupOwner()) {
			                JOptionPane.showMessageDialog(frame, "Please navigate to View Group to delete this event.");
			            } else {
			                JOptionPane.showMessageDialog(frame, "You do not have permission to delete this event.");
			            }
			            return;
			        }

			        int confirm = JOptionPane.showConfirmDialog(
			            frame,
			            "Are you sure you want to delete this event?",
			            "Confirm Deletion",
			            JOptionPane.YES_NO_OPTION
			        );

			        if (confirm == JOptionPane.YES_OPTION) {
			            Ez_Calendar_Controller.deleteUserCalendarEvent(eventId);
			            JOptionPane.showMessageDialog(frame, "Event deleted successfully.");
			            populateTable(); // Refresh table after deletion
			        }
				}
			});				
		}
		
		// Creates editEvent button
		public void editEvent() {
			JButton createEventButton = new JButton("Edit Event");
			createEventButton.setBounds(490, 320, 148, 28);
			UI_Styler.styleButton(createEventButton);
			frame.getContentPane().add(createEventButton);
			createEventButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int selectedRow = calendarEventsTable.getSelectedRow();
					if (selectedRow == -1) {
					    JOptionPane.showMessageDialog(frame, "Please select an event to edit.");
					    return;
					}
					
					int modelRow = calendarEventsTable.convertRowIndexToModel(selectedRow);
					int eventId = (int) calendarEventsTable.getModel().getValueAt(modelRow, 0); // column 0 is event_id
					Object groupEventObj = calendarEventsTable.getModel().getValueAt(modelRow, 1);
					System.out.println("groupEventObj = " + groupEventObj);

					if (groupEventObj != null) {
					    if (Ez_Calendar_Controller.isCurrentUserGroupOwner()) {
					        JOptionPane.showMessageDialog(frame, "Please navigate to View Group to edit this event.");
					    } else {
					        JOptionPane.showMessageDialog(frame, "You do not have permission to edit this event.");
					    }
					    return;
					}

					frame.dispose();
					Edit_Event_Window EEW = new Edit_Event_Window(eventId);
					EEW.frame.setLocationRelativeTo(null);
					EEW.frame.setVisible(true);
					}
				});
			}
		
		// Creates back button
				public void back() {
					JButton backButton = new JButton("Back");
					backButton.setBounds(10, 320, 85, 28);
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
				

		public void addThemeSelector() {
			String[] themes = { "Blue & Grey", "Red & White", "Brown & Cream", "Default" };
			JComboBox<String> themeSelector = new JComboBox<>(themes);
			themeSelector.setBounds(800, 10, 150, 25);
			UI_Styler.styleComboBox(themeSelector);
			frame.getContentPane().add(themeSelector);

			// Set default selected item based on current saved theme
			themeSelector.setSelectedIndex(Theme_Manager.loadTheme().ordinal());

			themeSelector.addActionListener(e -> {
			int selected = themeSelector.getSelectedIndex();
			CalendarTheme newTheme = CalendarTheme.values()[selected];
			Theme_Manager.saveTheme(newTheme);
			Table_Styler.styleCalendarTable(calendarEventsTable, scrollPane, newTheme);
			calendarEventsTable.repaint();
			});
		}

		
		public void createScrollPaneAndTable() {
			// Create Scroll Pane
			scrollPane = new JScrollPane();
			scrollPane.setBounds(19, 44, 950, 220);
			frame.getContentPane().add(scrollPane);
			
			// Create Table Inside of Scroll Pane
			calendarEventsTable = new JTable();
			scrollPane.setViewportView(calendarEventsTable);
		}
		
		// Performs a 'SELECT' query and populates the Table in the frame with the resulting data
		public void populateTable() {
			try {
				Connection connection = Database.connection;

				String query = "SELECT event_id AS 'ID',"
			             + "group_event_id AS 'GroupEventID',"
			             + "title AS 'Title', "
			             + "description AS 'Description', "
			             + "date AS 'Date', "
			             + "start_time AS 'Start Time', "
			             + "end_time AS 'End Time', "
			             + "location AS 'Location' FROM " + Ez_Calendar_Controller.currentUsername + "_calendar";

				PreparedStatement stm = connection.prepareStatement(query);
				ResultSet result = stm.executeQuery();
				
					calendarEventsTable.setModel(DbUtils.resultSetToTableModel(result));
					Table_Styler.CalendarTheme savedTheme = Theme_Manager.loadTheme();
					Table_Styler.styleCalendarTable(calendarEventsTable, scrollPane, savedTheme);
 
					calendarEventsTable.getColumnModel().getColumn(0).setMinWidth(0);
					calendarEventsTable.getColumnModel().getColumn(0).setMaxWidth(0);
					calendarEventsTable.getColumnModel().getColumn(0).setWidth(0);
					
					calendarEventsTable.getColumnModel().getColumn(1).setMinWidth(0);
					calendarEventsTable.getColumnModel().getColumn(1).setMaxWidth(0);
					calendarEventsTable.getColumnModel().getColumn(1).setWidth(0);
					
					calendarEventsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Title
					calendarEventsTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Description
					calendarEventsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
					calendarEventsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Start
					calendarEventsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // End
					calendarEventsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Location
					
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
}

