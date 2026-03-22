package Application;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.sql.*;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import Application.Table_Styler.CalendarTheme;
import net.proteanit.sql.DbUtils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

	public class View_Group_Calendar_Window {
		
		public JFrame frame;
		private JTable calendarEventsTable;
		private JScrollPane scrollPane;
		private JComboBox<String> themeSelector;
		
		private String groupTable;

		
		public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						View_Group_Calendar_Window window = new View_Group_Calendar_Window();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		// Default constructor (for backwards compatibility)
		public View_Group_Calendar_Window() {
		    this.groupTable = Ez_Calendar_Controller.currentGroupName;
		    initialize();
		}

		// Constructor for selecting a specific group
		public View_Group_Calendar_Window(String groupTable) {
		    this.groupTable = groupTable;
		    initialize();
		}



		void initialize() {
			frame = new JFrame();
			frame.setTitle("Group Calendar");
			frame.setBounds(100, 100, 1000, 400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);

			UI_Styler.styleFrame(frame);
			
		    if (Ez_Calendar_Controller.isCurrentUserGroupOwner(groupTable)) {
		        createEvent();
		        getGroupCode();
		        editEvent();
		        deleteEvent();
		        deleteGroup();
		        renameGroup();
		    }
		    
		    if (!Ez_Calendar_Controller.isCurrentUserGroupOwner(groupTable)) {
		    	leaveGroup();
		    }
			
		    groupCalendarTitleLBL();
		    
		    addThemeSelector();
		  
		    back();
		    
			createScrollPaneAndTable();
			populateTable();
		}
		
		// Creates Group Calendar Title label
		public void groupCalendarTitleLBL() {
			String displayName = Ez_Calendar_Controller.fetchGroupDisplayName(groupTable);
			JLabel groupCalendarTitleLBL = new JLabel(displayName + "'s Calendar");

		    groupCalendarTitleLBL.setBounds(19, 9, 300, 30); 
		    groupCalendarTitleLBL.setFont(new Font("SansSerif", Font.BOLD, 18));
		    UI_Styler.styleLabel(groupCalendarTitleLBL);
		    frame.getContentPane().add(groupCalendarTitleLBL);
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
							Create_Group_Event_Window CGEW = new Create_Group_Event_Window();
							CGEW.initialize();
							CGEW.frame.setLocationRelativeTo(null);
							CGEW.frame.setVisible(true);
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
							int eventId = (int) calendarEventsTable.getModel().getValueAt(modelRow, 0);

							frame.dispose();
							Edit_Group_Event_Window EGEW = new Edit_Group_Event_Window(eventId);
							EGEW.frame.setLocationRelativeTo(null);
							EGEW.frame.setVisible(true);
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
				
				// Creates createEvent button
				public void getGroupCode() {
					JButton getGroupCodeButton = new JButton("Get Group Code");
					getGroupCodeButton.setBounds(820, 280, 148, 28);
					UI_Styler.styleButton(getGroupCodeButton);
					frame.getContentPane().add(getGroupCodeButton);
					getGroupCodeButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							frame.dispose();
							Group_Code_Window GCW = new Group_Code_Window();
							GCW.initialize();
							GCW.frame.setLocationRelativeTo(null);
							GCW.frame.setVisible(true);
							}
						});
					}
	
	// Creates leave group button
		public void leaveGroup() {
			JButton leaveGroupButton = new JButton("Leave Group");
			leaveGroupButton.setBounds(820, 320, 148, 28);
			UI_Styler.styleButton(leaveGroupButton);
			frame.getContentPane().add(leaveGroupButton);
			leaveGroupButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int confirm = JOptionPane.showConfirmDialog(
				            frame,
				            "Are you sure you want to leave this group?",
				            "Confirm Leave",
				            JOptionPane.YES_NO_OPTION
				        );

				        if (confirm == JOptionPane.YES_OPTION) {
				        	Ez_Calendar_Controller.leaveGroup(groupTable);
				            JOptionPane.showMessageDialog(frame, "Group Left successfully.");
				            populateTable(); // Refresh table after deletion
				        }
				        
				        frame.dispose();
						Main_Menu_Window MMW = new Main_Menu_Window();
						MMW.initialize();
						MMW.frame.setLocationRelativeTo(null);
						MMW.frame.setVisible(true);
					}
					
				});
			}
		
		public void deleteGroup() {
			JButton deleteGroupButton = new JButton("Delete Group");
			deleteGroupButton.setBounds(325, 320, 148, 28);
			UI_Styler.styleButton(deleteGroupButton);
			frame.getContentPane().add(deleteGroupButton);
			deleteGroupButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int confirm = JOptionPane.showConfirmDialog(
				            frame,
				            "Are you sure you want to delete this group?",
				            "Confirm Deletion",
				            JOptionPane.YES_NO_OPTION
				        );

				        if (confirm == JOptionPane.YES_OPTION) {
				        	Ez_Calendar_Controller.groupLeaderDeleteOwnGroup();
				            JOptionPane.showMessageDialog(frame, "Group Deleted successfully.");
				            populateTable(); // Refresh table after deletion
				        }
				        
				        frame.dispose();
						Main_Menu_Window MMW = new Main_Menu_Window();
						MMW.initialize();
						MMW.frame.setLocationRelativeTo(null);
						MMW.frame.setVisible(true);
						
					}
					
				});
			}
		
		// Creates Rename Group button
		public void renameGroup() {
			JButton getGroupCodeButton = new JButton("Rename Group");
			getGroupCodeButton.setBounds(160, 320, 150, 28);
			UI_Styler.styleButton(getGroupCodeButton);
			frame.getContentPane().add(getGroupCodeButton);
			getGroupCodeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					Rename_Group_Window RGW = new Rename_Group_Window();
					RGW.initialize();
					RGW.frame.setLocationRelativeTo(null);
					RGW.frame.setVisible(true);
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
	
	// Creates the Scroll Pane and Table that the list of group events reside in
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

	        String groupEventTable = groupTable.replace("_group", "_group_event");

	        String eventQuery = "SELECT group_event_id AS 'ID',"
	                + "title AS 'Title', "
	                + "description AS 'Description', "
	                + "date AS 'Date', "
	                + "start_time AS 'Start Time', "
	                + "end_time AS 'End Time', "
	                + "location AS 'Location' FROM " + groupEventTable;

	        PreparedStatement stm = connection.prepareStatement(eventQuery);
	        ResultSet result = stm.executeQuery();

	        calendarEventsTable.setModel(DbUtils.resultSetToTableModel(result));
	        Table_Styler.CalendarTheme savedTheme = Theme_Manager.loadTheme();
	        Table_Styler.styleCalendarTable(calendarEventsTable, scrollPane, savedTheme);

	        calendarEventsTable.getColumnModel().getColumn(0).setMinWidth(0);
	        calendarEventsTable.getColumnModel().getColumn(0).setMaxWidth(0);
	        calendarEventsTable.getColumnModel().getColumn(0).setWidth(0);

	    } catch (Exception e) {
	        System.out.println("❌ Error populating group calendar: " + e.getMessage());
	    }
	}




}

