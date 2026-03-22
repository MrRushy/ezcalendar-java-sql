# EzCalendar

A Java + SQL calendar management application designed to simplify personal scheduling and group event coordination. The system allows users to manage personal events, create and join groups, and receive group events through a shared calendar workflow.

## My Contribution (Rushil Shanmugam)
For this four-person group project, I contributed to:
- Admin-side functionality for deleting users
- Removing events from user calendars
- Managing and deleting groups within the system
- User interface work for the core program and admin features
- Presentation materials, diagrams, and the final project report

## Features
- User sign-up and sign-in
- Personal calendar management
- Group creation and group joining
- Group event creation and event viewing
- Admin controls for managing users, groups, and calendar data

## Tech Stack
- Java
- SQL / MySQL
- IntelliJ IDEA / VS Code
- JDBC
- External library dependencies in `lib/`

## Project Structure
- `src/` — Java source code
- `resources/` — project resources
- `lib/` — required dependency jars
- `sql/` — database schema/setup scripts

## Screenshots

### Sign Up / Sign In
![EZ Calendar Sign Up](assets/EzCalenderSignup.png)

### Main Menu
![EZ Calendar Home](assets/EzCalenderHome.png)

### Personal Calendar
![EZ Calendar Personal Calendar](assets/EzCalenderPersonal.png)

### Admin Panel
![EZ Calendar Admin Panel](assets/EzCalenderAdmin.png)

## Database Design
![EZ Calendar Schema](assets/EzCalenderSchema.png)

## Database Setup
1. Make sure MySQL Server is installed and running.
2. Open MySQL Workbench or another MySQL client.
3. Run the SQL schema file in the `sql/` folder to create the database and required tables.
4. Confirm that the database name is:
   - `ez_calendar`

## Database Configuration
This project connects to a local MySQL instance. For a public GitHub repo, credentials are not included.

Update the database username and password in the database connection class before running locally.

Use placeholders in the public repo:
- DB user: `YOUR_DB_USER`
- DB password: `YOUR_DB_PASSWORD`

The project uses a JDBC connection similar to:
- `jdbc:mysql://localhost:3306/ez_calendar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

## Dependencies
Required dependency jars are included in the `lib/` folder.

This project uses external libraries for:
- MySQL JDBC connection
- table/result-set handling
- password-related utilities

## How to Run
1. Clone the repository
2. Open the project in IntelliJ or VS Code with Java support
3. Ensure MySQL Server is running
4. Run the SQL setup script in the `sql/` folder
5. Update the database credentials locally in the database connection class
6. Run the application from:
   - `Ez_Calendar_Window` (contains `public static void main`)

## Notes
- This project was completed as a four-person group project.
- The section in the beginning highlights my individual contributions.
- The repo includes the required local dependency jars in `lib/` for easier setup.
- For public GitHub safety, real database credentials should not be committed.

## Summary
EzCalendar is a group-oriented calendar application that combines personal scheduling, shared group planning, and administrative controls into one Java + SQL desktop application.
