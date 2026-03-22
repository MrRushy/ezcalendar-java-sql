CREATE DATABASE ez_calendar;
USE ez_calendar;

-- Core setup tables required before running the program:
--   ez_user
--   all_calendars
--   group_codes
--
-- Additional test/demo tables:
--   test_calendar
--   test_group
--   test_group_event

CREATE TABLE ez_user (
    user_id INT AUTO_INCREMENT,
    user_name VARCHAR(255),
    user_password VARCHAR(255),
    user_email VARCHAR(255),
    user_join_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_type INT DEFAULT 0, -- 0 = regular user, 1 = admin
    PRIMARY KEY (user_id)
);

-- Default admin account
INSERT INTO ez_user (user_name, user_password, user_email, user_type)
VALUES ('Admin', 'password123', 'admin@gmail.com', 1);

-- Default test user account
INSERT INTO ez_user (user_name, user_password, user_email)
VALUES ('Test', 'password123', 'test@gmail.com');

-- Stores calendars owned by users
CREATE TABLE all_calendars (
    owner_id INT,
    calendar_name VARCHAR(255) PRIMARY KEY,
    FOREIGN KEY (owner_id) REFERENCES ez_user(user_id)
);

-- Stores group join codes
CREATE TABLE group_codes (
    code INT UNIQUE PRIMARY KEY,
    group_name VARCHAR(255)
);

-- Test calendar events linked to a user
CREATE TABLE test_calendar (
    event_id INT AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    start_time VARCHAR(255),
    end_time VARCHAR(255),
    location VARCHAR(255),
    calendar_owner_id INT,
    PRIMARY KEY (event_id),
    FOREIGN KEY (calendar_owner_id) REFERENCES ez_user(user_id)
);

-- Test groups with owner/member references
CREATE TABLE test_group (
    group_id INT AUTO_INCREMENT,
    group_name VARCHAR(255),
    group_creation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    owner_id INT,
    member_id INT,
    PRIMARY KEY (group_id),
    FOREIGN KEY (owner_id) REFERENCES ez_user(user_id),
    FOREIGN KEY (member_id) REFERENCES ez_user(user_id)
);

-- Test group events linked to a group
CREATE TABLE test_group_event (
    group_event_id INT AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    event_date DATE,
    start_time VARCHAR(255),
    end_time VARCHAR(255),
    location VARCHAR(255),
    group_id INT,
    PRIMARY KEY (group_event_id),
    FOREIGN KEY (group_id) REFERENCES test_group(group_id)
);