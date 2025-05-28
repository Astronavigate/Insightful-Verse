DROP DATABASE IF EXISTS IVIEP;
CREATE DATABASE IVIEP;
USE IVIEP;

CREATE TABLE Class (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL
) AUTO_INCREMENT = 10000000;

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(16) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    class_id INT,
    eagloxis_id INT,
    way_1_id VARCHAR(100),
    way_2_id VARCHAR(100),
    way_3_id VARCHAR(100),
    way_4_id VARCHAR(100),
    way_5_id VARCHAR(100),
    way_6_id VARCHAR(100),
    authority ENUM('normal', ' teacher ', 'admin', 'infinite') NOT NULL,
    FOREIGN KEY (class_id) REFERENCES Class(class_id)
) AUTO_INCREMENT = 1000000000;

CREATE TABLE Courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_info VARCHAR(300)
) AUTO_INCREMENT = 100000;

CREATE TABLE File (
    file_id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    course_id INT,
    remarks VARCHAR(999),
    upload_user INT,
    upload_date DATE,
    file_path VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id),
    FOREIGN KEY (upload_user) REFERENCES Users(user_id)
) AUTO_INCREMENT = 100000;

CREATE TABLE Test (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(100) NOT NULL,
    course_id INT NOT NULL,
    test_content TEXT(999999999) NOT NULL,
    test_answer TEXT(999999999) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id)
);

CREATE TABLE StuAssignments (
    submission_id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    user_id INT NOT NULL,
    submission_date DATETIME NOT NULL,
    grade INT DEFAULT(0),
    FOREIGN KEY (test_id) REFERENCES test(test_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE FileViewRecords (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    file_id INT NOT NULL,
    user_id INT NOT NULL,
    view_duration INT NOT NULL,
    view_date TIMESTAMP NOT NULL,
    FOREIGN KEY (file_id) REFERENCES File(file_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE IVVersion (
	commit_id CHAR(36) PRIMARY KEY,
	version VARCHAR(35) NOT NULL,
    update_info VARCHAR(9999) DEFAULT 'Fix Bugs'
);

INSERT INTO Users (username, email, phone, password, authority) VALUES ('Arnold Lopez', 'arnold-lopez@auet.onmicrosoft.com', '+14086414215', 'f586dda44df3da412755507f4b0bd4cc3926cf7aca65322f6f0d7197a9d2fdf81dbc04980654bbbbc2cfd223f8802c709f0b5fa0dbcdf627bbd894115c438990', 'infinite');
INSERT INTO Users (username, email, phone, password, authority) VALUES ('任政', 'renzheng@neusoft.edu.cn', '+8600000000000', 'a506a646a5a14e39925c05f7713daceccd97bc85238a04ad2c7b9a9cb3a18a58e64f0c16e085ca162b28bb981e3d85beb3212a451f0611eee90bc738c52ab8dd', 'admin');
INSERT INTO Users (username, email, phone, password, authority) VALUES ('谢文尧', '22008020114@neusoft.edu.cn', '+8615640993693', 'a506a646a5a14e39925c05f7713daceccd97bc85238a04ad2c7b9a9cb3a18a58e64f0c16e085ca162b28bb981e3d85beb3212a451f0611eee90bc738c52ab8dd', 'normal');

INSERT INTO Courses (course_name, course_info) VALUES ('OOSA&D', 'Object-Oriented System Analysis and Design');
INSERT INTO Courses (course_name, course_info) VALUES ('Computer Network', 'Computer networking refers to connected computing devices and an ever-expanding array of IoT devices that communicate with one another.');

INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks) VALUES ('Ferryman', 'pdf', 1000000000, './file/doc/Ferryman.pdf', '2013-03-01', 'Life, death, love - which would you choose?');
INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks, course_id) VALUES ('The Development and Prospects of Passive Optical Networks (Chinese)', 'mp4', 1000000000, './file/video/PON.mp4', '2024-05-12', 'PON, developed in the mid-1990s, was originally designed to allow Internet Service Providers (ISPs) to deliver broadband triple-play services (data, voice, and video) to residential users.', 100001);
INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks, course_id) VALUES ('RFC 9114', 'pdf', 1000000000, './file/doc/rfc9114.pdf', '2022-06-06', 'After 5 years, HTTP 3 was finally standardized as RFC 9114. A new chapter in the web will be opened with RFC 9204 (QPACK header compression) and RFC 9218 (Extensible Prioritization)!', 100001);
INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks, course_id) VALUES ('Adapter Example', 'java', 1000000000, './WEB-INF/classes/code/AdapterExample.java', '2024-05-18', 'The user has purchased a new three-phase socket and wants to use the newly purchased three-phase socket to use both three-phase and two-phase appliances.', 100000);
INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks, course_id) VALUES ('Command Example', 'java', 1000000000, './WEB-INF/classes/code/CommandExample.java', '2024-05-17', 'The customer asked the waiter to order Mutton shashlik or chicken, and the chef was responsible for the barbecue.', 100000);
INSERT INTO file (file_name, type, upload_user, file_path, upload_date, remarks, course_id) VALUES ('Singleton Example', 'java', 1000000000, './WEB-INF/classes/code/SingletonExample.java', '2024-05-16', 'The print pool is an application that manages print tasks, allowing a print pool user to delete, abort, or change the priority of the print tasks, only one print pool object can run in a system.', 100000);

INSERT INTO IVVersion (commit_id, version, update_info) VALUES (UUID(), '1.0.16.1000.alpha.0', 'Initial release');
INSERT INTO IVVersion (commit_id, version, update_info) VALUES (UUID(), '1.0.16.1000.alpha.1', 'Optimize code logic and add the function of modifying personal information');
INSERT INTO IVVersion (commit_id, version, update_info) VALUES (UUID(), '1.0.18.1100.alpha.2', 'Fix some known bugs');
INSERT INTO IVVersion (commit_id, version, update_info) VALUES (UUID(), '1.0.19.1000.alpha.3', 'Adjust the logout page logic and add account unregsiter');
INSERT INTO IVVersion (commit_id, version, update_info) VALUES (UUID(), '1.0.21.1000.alpha.4', 'Adjusting web page architecture');
DELETE FROM IVVersion WHERE commit_id = '8a0f864f-1c08-11ef-8092-005056c00001';