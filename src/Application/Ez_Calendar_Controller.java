package Application;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import java.util.ArrayList;

import java.sql.DriverManager;


class Database {
	public static Connection connection;
	
	public static void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ez_calendar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				"YOUR_DB_USER",
				"YOUR_DB_PASSWORD"
			);
			System.out.println("Database connected successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class Ez_Calendar_Controller {
	
	public static void main(String[] args) {
		Database.connect();
		
		boolean running = true;
		while(running == true)
		{
			
			/*
			============================================================================
			 USER ACTION FLOW SUMMARY:
			============================================================================
			 1. ALL USERS:
			    - signIn(username, password): Authenticates user and sets session data.
			    - signUp(username, password, email): Registers user and creates personal calendar.
			    - signOut(): Clears session.
			    - changePassword(oldPassword, newPassword): Allows the user to change their password.
			    - isUserInGroup(): Checks if user belongs to any group.
			    - isCurrentUserGroupOwner(): Checks if current user owns a group.
			    - getGroupOwnerUsername(): Gets the owner's username from group table.
			    - fetchGroupDisplayName(): Gets display name for group calendar titles.

			 2. REGULAR USERS (user_type = 0):
			    - createCalendar(user_id, username): Called during sign-up to make user's calendar.
			    - createUserEvent(...): Add an event to the user's personal calendar.
			    - editUserCalendarEvent(...): Edit an event in the user's personal calendar.
			    - deleteUserCalendarEvent(eventId): Delete an event in the user's personal calendar.
			    
			    - createGroup(groupName): Group owner creates a new group and group event table.
			    - createGroupEvent(...): Group owner creates an event shared with all group members.
			    - groupLeaderEditGroupEvent(...): Group owner edits a group event and updates member calendars.
			    - groupLeaderDeleteOwnGroup(): Group owner deletes group and all associated data.
			    - groupLeaderRenameGroup(groupId, newGroupName): Group owner renames their group.

			    - joinGroup(code): Join an existing group using a code.
			    - leaveGroup(): Leave the currently joined group.

			    - viewGroup(): Displays members and info of user's group.
			    - retrieveCode(): Retrieves the group join code for the user's group.

			 3. ADMINS (user_type = 1):
			    - adminViewAllUsers(): Displays all registered users.
			    - adminViewUserCalendar(username): Views any user's personal calendar.
			    - adminDeleteUserCalendarEntry(username, event_id): Deletes an event from a user's calendar.
			    - adminViewGroup(groupOwnerUsername, groupId): Views detailed data for a specific group.
			    - adminDeleteGroupMember(groupOwnerUsername, groupId, memberUsername): Removes a member from a group.
			    - adminDeleteGroup(groupOwnerUsername, groupId): Deletes a group and its associated events.
			    - adminDeleteUser(username): Deletes a user and all related data (calendar, group, membership, etc.).
			*/		
			
		}
		
		setupClosingDBConnection();
	}
	
	// =========== CURRENT USER INFO =========== 
	
		public static int currentUserID;
		public static int currentUserType;
		public static String currentUsername;
		public static int currentGroupID;
		public static String currentGroupName;
		
	// ================================= START OF ALL USERS SECTION =================================

	// Method for Sign in
		public static int signIn(String username, String password) {
			try {
				Connection connection = Database.connection;
				String query = "SELECT * FROM ez_user WHERE user_name = ? AND user_password = ?";
				PreparedStatement stm = connection.prepareStatement(query);
				stm.setString(1, username);
				stm.setString(2, password);
				ResultSet result = stm.executeQuery();

				if (result.next()) {
					currentUserID = result.getInt("user_id");
					currentUserType = result.getInt("user_type");
					currentUsername = result.getString("user_name");

					return currentUserType; // return 0 or 1 depending on user type
				} else {
					// Login failed, user/pass not found
					return -1;
				}
			} catch (Exception e) {
				System.out.println("Login error: " + e.getMessage());
				return -1;
			}
		}
				
	// Method for Sign up
		public static void signUp(String username, String password, String email) {
			try {
				Connection connection = Database.connection;
				String query = "INSERT INTO ez_user (user_name, user_password, user_email, user_type) VALUES (?, ?, ?, ?)";
				PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

				int user_type_default = 0; // Makes them a user by default
				stm.setString(1, username);
				stm.setString(2, password);
				stm.setString(3, email);
				stm.setInt(4, user_type_default);

				stm.executeUpdate();
				
				ResultSet generatedKeys = stm.getGeneratedKeys();
				int user_id = 0;
				if (generatedKeys.next()) {
					user_id = generatedKeys.getInt(1);
				}
						
				createCalendar(user_id, username);

				connection.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}



	// Method for Sign out
	public static void signOut()
	{
		//should return the program back to sign in
		//resets current user info
		currentUserType = 0;
		currentUserID = 0;
		currentUsername = "";
	}
	
	// Method for Changing Password
	public static boolean changePassword(String oldPassword, String newPassword) {
	    try {
	        Connection connection = Database.connection;

	        String checkQuery = "SELECT user_password FROM ez_user WHERE user_id = ?";
	        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
	        checkStmt.setInt(1, currentUserID);
	        ResultSet rs = checkStmt.executeQuery();

	        if (rs.next()) {
	            String currentPassword = rs.getString("user_password");
	            if (!currentPassword.equals(oldPassword)) {
	                return false; // Old password doesn't match
	            }
	        }

	        String updateQuery = "UPDATE ez_user SET user_password=? WHERE user_id=?";
	        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
	        updateStmt.setString(1, newPassword);
	        updateStmt.setInt(2, currentUserID);
	        updateStmt.executeUpdate();

	        return true;
	    } catch (Exception e) {
	        System.out.println(e);
	        return false;
	    }
	}
	
	// Method for Is User In Group
	public static boolean isUserInGroup() {
	    try {
	        Connection connection = Database.connection;

	        String query = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
	                       "WHERE table_schema = 'ez_calendar' AND table_name LIKE ?";
	        PreparedStatement tableStmt = connection.prepareStatement(query);
	        tableStmt.setString(1, "%\\_group");
	        ResultSet tables = tableStmt.executeQuery();

	        while (tables.next()) {
	            String groupTable = tables.getString("table_name");

	            String checkQuery = "SELECT 1 FROM " + groupTable + " WHERE member_id = ? LIMIT 1";
	            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
	            checkStmt.setInt(1, currentUserID);
	            ResultSet result = checkStmt.executeQuery();

	            if (result.next()) {
	                return true;
	            }
	        }

	        return false;

	    } catch (SQLException e) {
	        System.out.println("❌ Group membership check failed: " + e.getMessage());
	        return false;
	    }
	}
		
		// Method for Is Current User Group Owner
	public static boolean isCurrentUserGroupOwner(String groupTable) {
	    try {
	        Connection connection = Database.connection;

	        String query = "SELECT * FROM " + groupTable + " WHERE owner_id = ?";
	        PreparedStatement stm = connection.prepareStatement(query);
	        stm.setInt(1, currentUserID);
	        ResultSet rs = stm.executeQuery();

	        return rs.next(); // true if current user is the owner of this specific group
	    } catch (Exception e) {
	        System.out.println("Owner check failed: " + e.getMessage());
	        return false;
	    }
	}

	public static boolean isCurrentUserGroupOwner() {
	    return isCurrentUserGroupOwner(currentGroupName);
	}
	
	public static boolean doesUserOwnAGroup() {
	    try {
	        Connection connection = Database.connection;
	        String query = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
	                       "WHERE table_schema = 'ez_calendar' AND table_name LIKE ?";
	        PreparedStatement stmt = connection.prepareStatement(query);
	        stmt.setString(1, "%\\_group");

	        ResultSet tables = stmt.executeQuery();

	        while (tables.next()) {
	            String groupTable = tables.getString("table_name");
	            String ownerCheck = "SELECT owner_id FROM " + groupTable + " WHERE owner_id = ?";
	            PreparedStatement ownerStmt = connection.prepareStatement(ownerCheck);
	            ownerStmt.setInt(1, currentUserID);
	            ResultSet rs = ownerStmt.executeQuery();

	            if (rs.next()) {
	                return true;
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("Owner check failed: " + e.getMessage());
	    }

	    return false;
	}

		
		// Method for Get Group Owner Username
		public static String getGroupOwnerUsername() {
		    try {
		        Connection connection = Database.connection;
		        
		        String query = "SELECT owner_id FROM " + currentGroupName + " WHERE group_id = ?";
		        PreparedStatement stm = connection.prepareStatement(query);
		        stm.setInt(1, currentGroupID);
		        ResultSet result = stm.executeQuery();
		        
		        if (result.next()) {
		            int ownerId = result.getInt("owner_id");

		            // Get owner's username
		            String userQuery = "SELECT user_name FROM ez_user WHERE user_id = ?";
		            PreparedStatement userStm = connection.prepareStatement(userQuery);
		            userStm.setInt(1, ownerId);
		            ResultSet userResult = userStm.executeQuery();
		            
		            if (userResult.next()) {
		                return userResult.getString("user_name").toLowerCase();
		            }
		        }
		    } catch (Exception e) {
		        System.out.println("❌ Error finding owner username: " + e.getMessage());
		    }
		    return null;
		}

		// Method for Fetch Group Display Name
		public static String fetchGroupDisplayName(String groupTable) {
		    try {
		        Connection connection = Database.connection;

		        String checkQuery = "SELECT group_name FROM " + groupTable + " WHERE member_id = ?";
		        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
		        checkStmt.setInt(1, currentUserID);
		        ResultSet result = checkStmt.executeQuery();

		        if (result.next()) {
		            return result.getString("group_name");
		        }

		        return "Unknown Group";

		    } catch (Exception e) {
		        System.out.println("⚠️ Group name fetch failed: " + e.getMessage());
		        return "Group Name Error";
		    }
		}

		
		// ================================= END OF ALL USERS SECTION =================================
		
		// ================================= START OF REGULAR USERS SECTION =================================

	// Method for Create Calendar
	public static void createCalendar(int user_id, String username)
	{
		// The name of the table will always be username_calendar
		try 
		{
			Connection connection = Database.connection;
			Statement statement = connection.createStatement();
			String safeUsername = username.toLowerCase();
			// SQL statement to create a new table
			String createTableSQL = "CREATE TABLE " + safeUsername + "_calendar (" +
		            "event_id INT AUTO_INCREMENT PRIMARY KEY, " +
					"group_event_id INT DEFAULT NULL, " +
		            "title VARCHAR(255), " +
		            "description VARCHAR(255), " +
		            "date DATE, " +
		            "start_time VARCHAR(255), " +
		            "end_time VARCHAR(255), " +
		            "location VARCHAR(255), " +
		            "calendar_owner_id INT " + 
		        ");";

			statement.executeUpdate(createTableSQL);
			
			String query = "INSERT INTO "+ safeUsername +"_calendar (owner_id) VALUES (?)";
			PreparedStatement stm = connection.prepareStatement(query);
			//sets the user_id as owner_id for the new accounts calendar
			stm.setInt(1, user_id);
			stm.executeUpdate(query);
			
			//adds new calendar owner and calendar name to all_calendars
			String query2 = "INSERT INTO all_calendars (owner_id, calendar_name) VALUES (?, ?)";
			PreparedStatement stm2 = connection.prepareStatement(query2);
			//sets the user_id as owner_id for the new accounts calendar
			stm2.setInt(1, user_id);
			stm2.setString(2, safeUsername + "_calendar");
			stm2.executeUpdate(query2);
			
		} 
		
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
	
	// Method for Create Event
	public static void createUserEvent(String eventTitle, String eventDesc, String eventDate, String eventStart, String eventEnd, String eventLocation) {
		try {
			Connection connection = Database.connection;

			String safeUsername = currentUsername.toLowerCase();
			String query = "INSERT INTO " + safeUsername + "_calendar (title, description, date, start_time, end_time, location) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stm = connection.prepareStatement(query);

			stm.setString(1, eventTitle);
			stm.setString(2, eventDesc);
			stm.setString(3, eventDate);
			stm.setString(4, eventStart);
			stm.setString(5, eventEnd);
			stm.setString(6, eventLocation);
			
			stm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Edit an event on your own calendar (corrected table naming)
    public static void editUserCalendarEvent(int eventId, String newTitle, String newDesc, String newDate, String newStart, String newEnd, String newLocation) {
        try {
            Connection connection = Database.connection;
            String safeUsername = currentUsername.toLowerCase();
            String query = "UPDATE " + safeUsername + "_calendar SET title=?, description=?, date=?, start_time=?, end_time=?, location=? WHERE event_id=?";
            PreparedStatement stm = connection.prepareStatement(query);

            // Set values in the correct order:
            stm.setString(1, newTitle);
            stm.setString(2, newDesc);
            stm.setString(3, newDate);      // This is the one MySQL cares about being a valid DATE
            stm.setString(4, newStart);
            stm.setString(5, newEnd);
            stm.setString(6, newLocation);
            stm.setInt(7, eventId);         // ID comes last in WHERE clause

            stm.executeUpdate();
            
            System.out.println("[DEBUG] Updating event: ID=" + eventId + " | Date=" + newDate);

            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Delete an event on your own calendar (table naming fixed)
    public static void deleteUserCalendarEvent(int eventId) {
        try {
            Connection connection = Database.connection;
            String query = "DELETE FROM " + currentUsername + "_calendar WHERE event_id=?";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setInt(1, eventId);
            stm.executeUpdate();
           
        } catch (Exception e) {
            System.out.println(e);
        }
    }
	
	// Method for Create Group
	public static void createGroup(String groupName)
	{
		// The name of the table will always be username_group (username being the group owner)
		try 
		{
			Connection connection = Database.connection;
			Statement statement = connection.createStatement();
			String safeUsername = currentUsername.toLowerCase();
			// SQL statement to create a table for the group and its owner and members
			String createTableSQL = "CREATE TABLE " + safeUsername + "_group (" +
                    "group_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "group_name VARCHAR(255), " +
                    "group_creation_date date default (current_date), " +
                    "owner_id int, " +
                    "member_id int " +
                    ");";
			statement.executeUpdate(createTableSQL);
			
			String query = "INSERT INTO " + safeUsername + "_group (group_name, owner_id, member_id) VALUES (?, ?, ?)";
			PreparedStatement stm = connection.prepareStatement(query);
			stm.setString(1, groupName);
			stm.setInt(2, currentUserID);
			stm.setInt(3, currentUserID);
			stm.executeUpdate();
			//result is a new group made with the table name being "username_group"
			//sets the owner_id to the person who made it			
			
			// SQL statement to create a table for the events of a group
			Statement statement2 = connection.createStatement();
			String createTableSQL2 = "CREATE TABLE " + safeUsername + "_group_event (" +
                    "group_event_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "description varchar(255), " +
                    "date DATE, " +
                    "start_time varchar (255), " +
                    "end_time varchar (255), " +
                    "location varchar (255), " +
                    "group_id int, " +
                    "FOREIGN KEY (group_id) REFERENCES "+safeUsername+"_group(group_id)" +
                    ");";
			statement2.executeUpdate(createTableSQL2);
			
			//retrieve the group id from the newly made group table
			String query4 = "SELECT group_id FROM "+safeUsername+"_group"; 
			PreparedStatement stm4 = connection.prepareStatement(query4); 
			ResultSet tempGroupID = stm4.executeQuery();
			int groupID = -1; // Initialize with a default value
			if (tempGroupID.next()) 
			{ // Move to the first row of the result set
			    groupID = tempGroupID.getInt("group_id"); // get the group_id
			}			
			
			//creates a code for a group (the password to join one you could say)
			String query2 = "INSERT INTO group_codes (code, group_name) VALUES (?, ?)";
			PreparedStatement stm2 = connection.prepareStatement(query2);
			//sets the user_id as owner_id for the new accounts calendar
			String uniqueCode = currentUserID+""+(int)(Math.random() * 1_000_000);
			int intcode = Integer.parseInt(uniqueCode);
			
			stm2.setInt(1, intcode);
			stm2.setString(2, ""+safeUsername+"_group");
			stm2.executeUpdate();
			
		} 
		
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
	
	// Method for Create Group Event
	public static void createGroupEvent(String eventTitle, String eventDesc, String eventDate, String eventStart, String eventEnd, String eventLocation) {
	    try {
	        Connection connection = Database.connection;

	        // 1. Insert into group event table
	        String groupEventTable = currentUsername + "_group_event";
	        String insertGroupEventQuery = "INSERT INTO " + groupEventTable + " (title, description, date, start_time, end_time, location) VALUES (?, ?, ?, ?, ?, ?)";
	        PreparedStatement insertGroupEvent = connection.prepareStatement(insertGroupEventQuery, Statement.RETURN_GENERATED_KEYS);
	        insertGroupEvent.setString(1, eventTitle);
	        insertGroupEvent.setString(2, eventDesc);
	        insertGroupEvent.setString(3, eventDate);
	        insertGroupEvent.setString(4, eventStart);
	        insertGroupEvent.setString(5, eventEnd);
	        insertGroupEvent.setString(6, eventLocation);
	        insertGroupEvent.executeUpdate();

	        // 2. Get generated group_event_id
	        ResultSet generatedKeys = insertGroupEvent.getGeneratedKeys();
	        int groupEventId = -1;
	        if (generatedKeys.next()) {
	            groupEventId = generatedKeys.getInt(1);
	        }

	        System.out.println("[DEBUG] Created group event with ID: " + groupEventId);

	        // 3. Send to each group member’s calendar with group_event_id
	        String recipientsQuery = "SELECT member_id FROM " + currentUsername + "_group";
	        Statement recipientStmt = connection.createStatement();
	        ResultSet members = recipientStmt.executeQuery(recipientsQuery);

	        String fetchUsernameQuery = "SELECT user_name FROM ez_user WHERE user_id = ?";
	        PreparedStatement getUsername = connection.prepareStatement(fetchUsernameQuery);

	        while (members.next()) {
	            int memberId = members.getInt("member_id");
	            getUsername.setInt(1, memberId);
	            ResultSet userResult = getUsername.executeQuery();
	            if (userResult.next()) {
	                String memberUsername = userResult.getString("user_name").toLowerCase();

	                String personalInsert = String.format(
	                    "INSERT INTO %s_calendar (title, description, date, start_time, end_time, location, group_event_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
	                    memberUsername
	                );

	                PreparedStatement insertIntoPersonal = connection.prepareStatement(personalInsert);
	                insertIntoPersonal.setString(1, eventTitle);
	                insertIntoPersonal.setString(2, eventDesc);
	                insertIntoPersonal.setString(3, eventDate);
	                insertIntoPersonal.setString(4, eventStart);
	                insertIntoPersonal.setString(5, eventEnd);
	                insertIntoPersonal.setString(6, eventLocation);
	                insertIntoPersonal.setInt(7, groupEventId); // key part
	                insertIntoPersonal.executeUpdate();

	                System.out.println("[DEBUG] Sent group event to " + memberUsername + "_calendar");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	
	// 3. Group leader edits a group event (correct table and resend logic)
	public static void groupLeaderEditGroupEvent(
		    int groupEventId, String newTitle, String newDesc, String newDate,
		    String newStartTime, String newEndTime, String newLocation,
		    String originalTitle, String originalDate
		) {
		    try {
		        Connection connection = Database.connection;

		        // 1. Update the central group event table
		        String groupEventTable = currentGroupName.replace("_group", "_group_event");
		        String updateGroupQuery = "UPDATE " + groupEventTable + 
		            " SET title=?, description=?, date=?, start_time=?, end_time=?, location=? WHERE group_event_id=?";
		        PreparedStatement stm = connection.prepareStatement(updateGroupQuery);
		        stm.setString(1, newTitle);
		        stm.setString(2, newDesc);
		        stm.setString(3, newDate);
		        stm.setString(4, newStartTime);
		        stm.setString(5, newEndTime);
		        stm.setString(6, newLocation);
		        stm.setInt(7, groupEventId);
		        stm.executeUpdate();

		        System.out.println("[DEBUG] Updated group event with ID: " + groupEventId);

		        // 2. Get all members from the group table
		        String getMembersQuery = "SELECT DISTINCT member_id FROM " + currentGroupName;
		        Statement memberStmt = connection.createStatement();
		        ResultSet members = memberStmt.executeQuery(getMembersQuery);

		        // 3. Prepare to fetch usernames
		        String fetchUsername = "SELECT user_name FROM ez_user WHERE user_id = ?";
		        PreparedStatement getUsername = connection.prepareStatement(fetchUsername);

		        while (members.next()) {
		            int memberId = members.getInt("member_id");
		            getUsername.setInt(1, memberId);
		            ResultSet userRs = getUsername.executeQuery();

		            if (userRs.next()) {
		                String memberUsername = userRs.getString("user_name").toLowerCase();
		                String personalCalendar = memberUsername + "_calendar";

		                // 4. Update personal calendar event via group_event_id
		                String updatePersonal = "UPDATE " + personalCalendar + 
		                	    " SET title = ?, description = ?, date = ?, start_time = ?, end_time = ?, location = ? " +
		                	    "WHERE group_event_id = ?";

		                	PreparedStatement update = connection.prepareStatement(updatePersonal);
		                	update.setString(1, newTitle);
		                	update.setString(2, newDesc);
		                	update.setString(3, newDate);
		                	update.setString(4, newStartTime);
		                	update.setString(5, newEndTime);
		                	update.setString(6, newLocation);
		                	update.setInt(7, groupEventId);

		                int rowsAffected = update.executeUpdate();

		                System.out.println("[DEBUG] Updated event for " + memberUsername + " | Rows: " + rowsAffected);
		            }
		        }

		    } catch (Exception e) {
		        System.out.println("[ERROR] Failed to update group event: " + e.getMessage());
		        e.printStackTrace();
		    }
		}


	// Group leader deletes their own group (includes group_codes cleanup)
	public static void groupLeaderDeleteOwnGroup() {
	    try {
	        Connection connection = Database.connection;
	        connection.setAutoCommit(false);

	        String groupTable = currentGroupName;                     // e.g. "jake_group"
	        String groupEventTable = groupTable.replace("_group", "_group_event");

	        // Step 1: Fetch all group_event_ids from the event table
	        ArrayList<Integer> eventIds = new ArrayList<>();
	        String eventQuery = "SELECT group_event_id FROM " + groupEventTable;
	        try (PreparedStatement eventStmt = connection.prepareStatement(eventQuery);
	             ResultSet rs = eventStmt.executeQuery()) {
	            while (rs.next()) {
	                eventIds.add(rs.getInt("group_event_id"));
	            }
	        }

	        // Step 2: Get all member IDs in the group
	        String memberQuery = "SELECT member_id FROM " + groupTable + " WHERE group_id = ?";
	        PreparedStatement memberStmt = connection.prepareStatement(memberQuery);
	        memberStmt.setInt(1, currentGroupID);
	        ResultSet members = memberStmt.executeQuery();

	        String fetchUsername = "SELECT user_name FROM ez_user WHERE user_id = ?";
	        PreparedStatement getUsername = connection.prepareStatement(fetchUsername);

	        // Step 3: Delete group events from each member's calendar
	        while (members.next()) {
	            int memberId = members.getInt("member_id");

	            getUsername.setInt(1, memberId);
	            ResultSet userRs = getUsername.executeQuery();
	            if (userRs.next()) {
	                String uname = userRs.getString("user_name").toLowerCase();
	                String personalCalendar = uname + "_calendar";

	                for (int groupEventId : eventIds) {
	                    String deletePersonal = "DELETE FROM " + personalCalendar + " WHERE group_event_id = ?";
	                    PreparedStatement deleteStmt = connection.prepareStatement(deletePersonal);
	                    deleteStmt.setInt(1, groupEventId);
	                    deleteStmt.executeUpdate();
	                }
	            }
	        }

	        // Step 4: Delete join code from group_codes
	        PreparedStatement deleteCode = connection.prepareStatement("DELETE FROM group_codes WHERE group_name = ?");
	        deleteCode.setString(1, groupTable);
	        deleteCode.executeUpdate();

	        // Step 5: Drop group event table and membership table
	        Statement dropStmt = connection.createStatement();
	        dropStmt.executeUpdate("DROP TABLE IF EXISTS " + groupEventTable);
	        dropStmt.executeUpdate("DROP TABLE IF EXISTS " + groupTable);

	        connection.commit();
	        System.out.println("[DEBUG] Group and all associated data successfully deleted.");

	        // Reset group state
	        currentGroupName = null;
	        currentGroupID = -1;

	    } catch (Exception e) {
	        System.out.println("[ERROR] Deleting group failed: " + e.getMessage());
	        try {
	            Database.connection.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }
	}

	
	// Method for Renaming Group
	public static void groupLeaderRenameGroup(int groupId, String newGroupName) {
	    try {
	        Connection connection = Database.connection;

	        String groupTable = currentGroupName; // e.g., "jake_group"
	        String query = "UPDATE " + groupTable + " SET group_name = ? WHERE group_id = ?";
	        PreparedStatement stm = connection.prepareStatement(query);
	        stm.setString(1, newGroupName);
	        stm.setInt(2, groupId);
	        stm.executeUpdate();

	        System.out.println("[DEBUG] Renamed group_id " + groupId + " in table " + groupTable + " to '" + newGroupName + "'");

	    } catch (Exception e) {
	        System.out.println("[ERROR] Failed to rename group: " + e.getMessage());
	    }
	}

	
	// Method for Join Group
		public static String joinGroup(String codeString) {
		    try {
		        int theCode = Integer.parseInt(codeString);
		        Connection connection = Database.connection;

		        // Step 1: Get group_name from group_codes using the code
		        String query = "SELECT group_name FROM group_codes WHERE code = ?";
		        PreparedStatement stm = connection.prepareStatement(query);
		        stm.setInt(1, theCode);
		        ResultSet result = stm.executeQuery();

		        if (!result.next()) {
		            return "Invalid group code.";
		        }

		        String groupTableName = result.getString("group_name");

		        // Step 2: Check if user is already a member
		        String checkQuery = "SELECT * FROM " + groupTableName + " WHERE member_id = ?";
		        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
		        checkStmt.setInt(1, currentUserID);
		        ResultSet checkResult = checkStmt.executeQuery();

		        if (checkResult.next()) {
		            return "You are already a member of this group.";
		        }

		        // Step 3: Fetch existing group_name and owner_id from existing row
		        String metaQuery = "SELECT group_name, owner_id, group_id FROM " + groupTableName + " LIMIT 1";
		        Statement metaStmt = connection.createStatement();
		        ResultSet metaResult = metaStmt.executeQuery(metaQuery);

		        String groupName = "Unnamed";
		        int ownerId = -1;
		        int groupId = -1;

		        if (metaResult.next()) {
		            groupName = metaResult.getString("group_name");
		            ownerId = metaResult.getInt("owner_id");
		            groupId = metaResult.getInt("group_id");
		        }

		        // Step 4: Insert full member data
		        String joinQuery = "INSERT INTO " + groupTableName + " (group_name, group_creation_date, owner_id, member_id) VALUES (?, CURRENT_DATE, ?, ?)";
		        PreparedStatement joinStmt = connection.prepareStatement(joinQuery);
		        joinStmt.setString(1, groupName);
		        joinStmt.setInt(2, ownerId);
		        joinStmt.setInt(3, currentUserID);
		        joinStmt.executeUpdate();

		        return "Success";

		    } catch (NumberFormatException e) {
		        return "Please enter a valid numeric group code.";
		    } catch (Exception e) {
		        e.printStackTrace();
		        return "An error occurred: " + e.getMessage();
		    }
		}
		
		// Method for leaving a group
		public static void leaveGroup(String groupTable) {
		    try {
		        Connection conn = Database.connection;
		        String query = "DELETE FROM " + groupTable + " WHERE member_id = ?";
		        PreparedStatement stm = conn.prepareStatement(query);
		        stm.setInt(1, currentUserID);
		        stm.executeUpdate();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		// Method for View Group
		public static void viewGroup()
		{
			try
			{
				Connection connection = Database.connection;
				
				//selects the table belonging to the currentUsername VERY IMPORTANT
				 String query = "SELECT * FROM " + currentUsername + "_group";
			     Statement stm = connection.createStatement();
			     ResultSet result = stm.executeQuery(query);

				
				while (result.next()) //quite sure this lets it chain down the table
				{
					//retrieves the username for a member_id
					int memberId = result.getInt("member_id");
					String query2 = "SELECT user_name FROM ez_user WHERE user_id = ?";
		            try (PreparedStatement stm2 = connection.prepareStatement(query2)) 
		            {
		                stm2.setInt(1, memberId); // Set the member_id parameter

		                try (ResultSet result2 = stm2.executeQuery()) 
		                {
		                    String memberName = null;
		                    if (result2.next()) 
		                    { // Check if a result is available
		                        memberName = result2.getString("user_name"); // Retrieve the user_name
		                    }
		                    
		                    String groupName = result.getString("group_name");
		                    String creationDate = result.getString("group_creation_date");
		                    String ownerName = result.getString("owner_id"); // Adjust column name if needed
		                    // Other fields if they exist (e.g., start_time, end_time, etc.)

		                    // Display or process the information as needed
		                    System.out.println("Group Name: " + groupName);
		                    System.out.println("Creation Date: " + creationDate);
		                    System.out.println("Owner ID: " + ownerName);
		                    System.out.println("Member Name: " + memberName);
		                }
		            }
				}
				connection.close();
			}
			
			catch (Exception e) 
			{
				System.out.println(e);
			}
		}
		
		// Method for Retrieve Code
		public static String retrieveCode() {
		    try {
		        Connection connection = Database.connection;

		        String query = "SELECT code FROM group_codes WHERE group_name = ?";
		        PreparedStatement stm = connection.prepareStatement(query);
		        stm.setString(1, currentUsername + "_group");
		        ResultSet result = stm.executeQuery();

		        if (result.next()) {
		            int groupCodeNumber = result.getInt("code");
		            return String.valueOf(groupCodeNumber);
		        } else {
		            return "No Code Found";
		        }
		    } catch (Exception e) {
		        System.out.println("Error retrieving code: " + e.getMessage());
		        return "Error";
		    }
		}
		
		
		public static List<String> getAllGroupsForUser() {
		    List<String> groupList = new ArrayList<>();
		    try {
		        Connection connection = Database.connection;
		        String query = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
		                       "WHERE table_schema = 'ez_calendar' AND table_name LIKE ?";

		        PreparedStatement stmt = connection.prepareStatement(query);
		        stmt.setString(1, "%\\_group");
		        ResultSet tables = stmt.executeQuery();

		        while (tables.next()) {
		            String groupTable = tables.getString("table_name");
		            String checkQuery = "SELECT * FROM " + groupTable + " WHERE member_id = ?";
		            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
		            checkStmt.setInt(1, currentUserID);
		            ResultSet result = checkStmt.executeQuery();
		            if (result.next()) {
		                groupList.add(groupTable);
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return groupList;
		}

	

	/********************************************************************
	ADMIN METHODS FOR MANAGING/DELETING USERS, CALENDARS, EVENTS AND GROUPS
	 ********************************************************************/

	/**
	 * adminViewAllUsers()
	 * 
	 * This method connects to the database and prints out every record 
	 * from the ez_user table. It helps the admin to see all registered users.
	 */
	public static void adminViewAllUsers() {
	    try {
	        Connection connection = Database.connection;
	        // SQL query to select all details from ez_user.
	        String query = "SELECT * FROM ez_user";
	        Statement stm = connection.createStatement();
	        ResultSet rs = stm.executeQuery(query);
	        
	        System.out.println("----- Users Table -----");
	        // Iterate through all records (rows) in the result set.
	        while(rs.next()){
	            int userId = rs.getInt("user_id");
	            String userName = rs.getString("user_name");
	            String fullName = rs.getString("full_name");
	            String password = rs.getString("user_password");
	            int userType = rs.getInt("user_type");
	            // Print details for each user in a readable way.
	            System.out.println("ID: " + userId 
	                + " | Username: " + userName 
	                + " | Full Name: " + fullName 
	                + " | Password: " + password 
	                + " | Type: " + userType);
	        }
	        connection.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * adminViewUserCalendar(String targetUsername)
	 * 
	 * This method retrieves and displays all events from the target user's
	 * calendar table. The table name is built as "targetUsername_calendar".
	 * If there are no events, nothing will be printed.
	 *
	 * @param targetUsername The username whose calendar we want to view.
	 */
	public static void adminViewUserCalendar(String targetUsername) {
	    try {
	        Connection connection = Database.connection;
	        // Build the table name dynamically.
	        String calendarTable = targetUsername + "_calendar";
	        // SQL to select all rows from the calendar table.
	        String query = "SELECT * FROM " + calendarTable;
	        Statement stm = connection.createStatement();
	        ResultSet rs = stm.executeQuery(query);
	        
	        System.out.println("----- " + targetUsername + "'s Calendar -----");
	        while(rs.next()){
	            int eventId = rs.getInt("event_id");
	            String title = rs.getString("title");
	            String date = rs.getString("date"); // Column name should match your schema.
	            String startTime = rs.getString("start_time");
	            String endTime = rs.getString("end_time");
	            String location = rs.getString("location");
	            String description = rs.getString("description");
	            // Display each event's details.
	            System.out.println("Event ID: " + eventId 
	                + " | Title: " + title 
	                + " | Date: " + date 
	                + " | Start: " + startTime 
	                + " | End: " + endTime 
	                + " | Location: " + location 
	                + " | Description: " + description);
	        }
	        connection.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * adminDeleteUserCalendarEntry(String targetUsername, int eventId)
	 * 
	 * Deletes a specific event from the target user's calendar table.
	 * Uses the event_id to remove the proper row. If no row matches,
	 * a message is printed to notify that nothing was deleted.
	 *
	 * @param targetUsername The username of the calendar's owner.
	 * @param eventId The ID of the event to delete.
	 */
	public static void adminDeleteUserCalendarEntry(String targetUsername, int eventId) {
	    try {
	        Connection connection = Database.connection;
	        // Build the dynamic table name for the user's calendar.
	        String calendarTable = targetUsername + "_calendar";
	        // SQL to delete the event with the given event_id.
	        String deleteQuery = "DELETE FROM " + calendarTable + " WHERE event_id = ?";
	        PreparedStatement stm = connection.prepareStatement(deleteQuery);
	        stm.setInt(1, eventId);
	        int rowsAffected = stm.executeUpdate();
	        // Check if any event was deleted.
	        if (rowsAffected > 0) {
	            System.out.println("Successfully deleted event with ID " + eventId 
	                + " from " + targetUsername + "'s calendar.");
	        } else {
	            System.out.println("No event found with ID " + eventId 
	                + " in " + targetUsername + "'s calendar.");
	        }
	        connection.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * adminViewGroup(String groupOwnerUsername)
	 * 
	 * This method is used by admins to view a group's details identified by group_id.
	 * The group table is assumed to be named as "groupOwnerUsername_group".
	 * I display the owner_id that indicates which user created the group.
	 *
	 * @param groupOwnerUsername The username of the group owner.
	 * @param groupId The identifier for the specific group.
	 */
	public static void adminViewGroup(String groupTable) {
	    try {
	        Connection connection = Database.connection;

	        // Get the group_id of the group (just the first one is fine)
	        String query = "SELECT group_id FROM `" + groupTable + "` LIMIT 1";
	        PreparedStatement stm = connection.prepareStatement(query);
	        ResultSet rs = stm.executeQuery();

	        if (!rs.next()) {
	            JOptionPane.showMessageDialog(null, "No group data found in: " + groupTable);
	            return;
	        }

	        int groupId = rs.getInt("group_id");

	        // Parse the owner username from the group table name
	        // (e.g., "jake_group" → "jake")
	        String ownerUsername = groupTable.replace("_group", "");

	        // 🪟 Open GUI window instead of printing to console
	        Admin_View_Group_Window window = new Admin_View_Group_Window(ownerUsername, groupId);
	        window.frame.setLocationRelativeTo(null);
	        window.frame.setVisible(true);

	    } catch (Exception e) {
	        System.out.println("❌ Error viewing group: " + e.getMessage());
	        e.printStackTrace();
	    }
	}


	/**
	 * adminDeleteGroupMember(String groupOwnerUsername, String memberUsername)
	 * 
	 * This method allows an admin to remove a specific member from a group.
	 * It first retrieves the user_id of the member via the ez_user table, then
	 * deletes any row in the group table (named "groupOwnerUsername_group") 
	 * that matches both the provided group_id and member_id.
	 *
	 * @param groupOwnerUsername The username of the group owner.
	 * @param memberUsername The username of the member to be removed.
	 * @param groupId The identifier for the specific group.
	 */
	public static void adminDeleteGroupMember(String groupTable, String memberUsername) {
	    try {
	        Connection connection = Database.connection;

	        // Step 1: Get the user_id for the member.
	        String getUserQuery = "SELECT user_id FROM ez_user WHERE user_name = ?";
	        int memberId = -1;
	        try (PreparedStatement stmGetUser = connection.prepareStatement(getUserQuery)) {
	            stmGetUser.setString(1, memberUsername);
	            ResultSet rs = stmGetUser.executeQuery();
	            if (rs.next()) {
	                memberId = rs.getInt("user_id");
	            } else {
	                System.out.println("❌ No such member: " + memberUsername);
	                return;
	            }
	        }

	        // Step 2: Delete the member from the group table based on their member_id.
	        String deleteQuery = "DELETE FROM `" + groupTable + "` WHERE member_id = ?";
	        try (PreparedStatement stmDelete = connection.prepareStatement(deleteQuery)) {
	            stmDelete.setInt(1, memberId);
	            int rowsAffected = stmDelete.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("✅ Member '" + memberUsername + "' removed from group '" + groupTable + "'.");
	            } else {
	                System.out.println("⚠️ Member '" + memberUsername + "' was not found in group '" + groupTable + "'.");
	            }
	        }

	        connection.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	/**
	 * adminDeleteGroup(String groupOwnerUsername, int groupId)
	 * 
	 * Deletes a specific group along with its associated events.
	 * Here, I assumed that the group information is stored as a row in the group table 
	 * (named "groupOwnerUsername_group"). I assumed this b/c if a user has multiple groups
	 *  they own then there would be a table with the all groups they own with each group having 
	 *  unique group_id. [IF THIS IS WRONG PLEASE LET ME KNOW AND I WILL FIX ASAP].
	 *  I also assumed group events are stored in the group events table
	 * (named "groupOwnerUsername_group_event") with a column that references group_id.
	 * Rather than dropping the entire table (which would remove all groups):
	 *   1. Delete rows in the group events table where group_id = ?.
	 *   2. Delete the group row from the group table where group_id = ?.
	 *   
	 * @param groupOwnerUsername The username of the group owner whose group will be deleted.
	 * @param groupId The identifier for the specific group to delete.
	 */
	public static void adminDeleteGroup(String groupTable) {
	    try {
	        Connection connection = Database.connection;
	        connection.setAutoCommit(false);

	        String groupEventTable = groupTable.replace("_group", "_group_event");

	        // Step 1: Delete from group_event table
	        String deleteEventsSQL = "DELETE FROM `" + groupEventTable + "`";
	        try (PreparedStatement stmDeleteEvents = connection.prepareStatement(deleteEventsSQL)) {
	            int eventsDeleted = stmDeleteEvents.executeUpdate();
	            System.out.println("✅ Deleted " + eventsDeleted + " event(s) from " + groupEventTable);
	        }

	        // Step 2: Delete all group membership rows
	        String deleteGroupSQL = "DELETE FROM `" + groupTable + "`";
	        try (PreparedStatement stmDeleteGroup = connection.prepareStatement(deleteGroupSQL)) {
	            int groupRowsDeleted = stmDeleteGroup.executeUpdate();
	            System.out.println("✅ Deleted " + groupRowsDeleted + " membership row(s) from " + groupTable);
	        }

	        connection.commit();

	        System.out.println("✅ Group '" + groupTable + "' and its events have been deleted successfully.");

	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            Database.connection.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }
	}

	
	/*
    adminDeleteUser(String targetUsername)
    
	Method to delete an existing user from the system while ensuring that all records referring to
    the user (from group memberships, groups owned, and the user's calendar) are removed first.
    
    @param targetUsername The username of the one who will be deleted from the system
    */
	
	public static void adminDeleteUser(String targetUsername) {
	    try {
	       
	        Connection connection = Database.connection;
	        
	        // Turn off auto-commit so that all statements are treated as one single transaction.
	        // If one statement fails, we can roll everything back to keep the data consistent.
	        connection.setAutoCommit(false);
	        
	        // 1. Retrieve the target user's ID from the ez_user table.
	        // This is important because other tables reference the user via their ID.
	        String getUserQuery = "SELECT user_id FROM ez_user WHERE user_name = ?";
	        int targetUserID = -1;
	        try (PreparedStatement stmGetUser = connection.prepareStatement(getUserQuery)) {
	            stmGetUser.setString(1, targetUsername);
	            try (ResultSet rs = stmGetUser.executeQuery()) {
	                if (rs.next()) {
	                    targetUserID = rs.getInt("user_id");
	                } else {
	                    // If the user isn't found, we print a message and exit the method.
	                    System.out.println("User not found: " + targetUsername);
	                    return;
	                }
	            }
	        }
	        
	        // 2. Process group tables:
	        // Remove the user from groups they are a member of,
	        // and if the user owns a group, delete that group (along with its associated events).
	        // We query INFORMATION_SCHEMA.TABLES to find all tables that end with "_group".
	        String getGroupTablesQuery = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES "
	        	    + "WHERE table_schema = 'ez_calendar' AND table_name LIKE ? ESCAPE '\\\\'";
	        try (PreparedStatement stmGroupTables = connection.prepareStatement(getGroupTablesQuery)) {
	            // The pattern '%\_group' finds any table ending with "_group".
	            stmGroupTables.setString(1, "%\\_group");
	            try (ResultSet rsGroup = stmGroupTables.executeQuery()) {
	                while (rsGroup.next()) {
	                    String groupTable = rsGroup.getString("table_name");
	                    
	                    // Check if this group table is the one owned by the target user.
	                    if (groupTable.equals(targetUsername + "_group")) {
	                        // If the user owns this group, we need to remove the group events first.
	                        String groupEventTable = targetUsername + "_group_event";
	                        try (Statement stmDropEvent = connection.createStatement()) {
	                            // Drop the table that holds the group's events.
	                        	String dropGroupEventSQL = "DROP TABLE IF EXISTS `" + groupEventTable + "`";
	                            stmDropEvent.executeUpdate(dropGroupEventSQL);
	                        }
	                        // Now drop the group table (which holds the group membership info).
	                        try (Statement stmDropGroup = connection.createStatement()) {
	                        	String dropGroupSQL = "DROP TABLE IF EXISTS `" + groupTable + "`";
	                            stmDropGroup.executeUpdate(dropGroupSQL);
	                        }
	                    } else {
	                        // Otherwise, the user is simply a member in this group.
	                        // We remove the user's membership by deleting rows where member_id equals the target user's ID.
	                        String deleteMembershipSQL = "DELETE FROM " + groupTable + " WHERE member_id = ?";
	                        try (PreparedStatement stmDeleteMembership = connection.prepareStatement(deleteMembershipSQL)) {
	                            stmDeleteMembership.setInt(1, targetUserID);
	                            stmDeleteMembership.executeUpdate();
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 3. Delete the user's personal calendar.
	        // This involves dropping the table that holds the user's calendar events.
	        String calendarTable = targetUsername + "_calendar";
	        try (Statement stmDropCalendar = connection.createStatement()) {
	        	String dropCalendarSQL = "DROP TABLE IF EXISTS `" + calendarTable + "`";
	            stmDropCalendar.executeUpdate(dropCalendarSQL);
	        }
	        
	        // Also remove the calendar record from the all_calendars table.
	        String deleteAllCalendarSQL = "DELETE FROM all_calendars WHERE calendar_name = ?";
	        try (PreparedStatement stmDeleteAllCal = connection.prepareStatement(deleteAllCalendarSQL)) {
	            stmDeleteAllCal.setString(1, calendarTable);
	            stmDeleteAllCal.executeUpdate();
	        }
	        
	        // 4. Finally, delete the user record itself from the ez_user table.
	        String deleteUserSQL = "DELETE FROM ez_user WHERE user_name = ?";
	        try (PreparedStatement stmDeleteUser = connection.prepareStatement(deleteUserSQL)) {
	            stmDeleteUser.setString(1, targetUsername);
	            stmDeleteUser.executeUpdate();
	        }
	        
	        // If all statements executed without errors, commit the transaction to make the changes permanent.
	        connection.commit();
	        System.out.println("User " + targetUsername + " and all associated records have been deleted.");
	        connection.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        // In case of an error, rollback the transaction so that none of the changes are saved.
	        try {
	            Database.connection.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }
	}
	
	public static void setupClosingDBConnection() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            try { Database.connection.close(); System.out.println("Application Closed - DB Connection Closed");
				} catch (SQLException e) { e.printStackTrace(); }
	        }
	    }, "Shutdown-thread"));
	}

}
