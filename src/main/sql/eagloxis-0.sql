DROP DATABASE IF EXISTS EAGLOXIS;
CREATE DATABASE EAGLOXIS;
USE EAGLOXIS;

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(16) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    department VARCHAR(35),
    authority ENUM('normal', 'admin', 'infinite') NOT NULL
)

CREATE TABLE File (
    file_id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    remarks VARCHAR(9999),
    upload_user INT,
    upload_date DATE,
    file_path VARCHAR(512) NOT NULL UNIQUE,
    FOREIGN KEY (upload_user) REFERENCES Users(user_id)
) AUTO_INCREMENT = 10000000;

CREATE TABLE Products (
	product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(192) NOT NULL,
    remarks VARCHAR(9999),
    link VARCHAR(512)
) AUTO_INCREMENT = 1000000;

CREATE TABLE Version (
	commit_id CHAR(36) PRIMARY KEY,
	version VARCHAR(35) NOT NULL,
    type VARCHAR(25) NOT NULL,
    channel VARCHAR(25) DEFAULT 'Release',
    update_info VARCHAR(9999) DEFAULT 'Fix Bugs'
);

INSERT INTO Users (username, email, phone, password, authority) VALUES ('Arnold Lopez', 'arnold-lopez@auet.onmicrosoft.com', '+14086414215', 'f586dda44df3da412755507f4b0bd4cc3926cf7aca65322f6f0d7197a9d2fdf81dbc04980654bbbbc2cfd223f8802c709f0b5fa0dbcdf627bbd894115c438990', 'infinite');
INSERT INTO Users (username, email, phone, password, authority) VALUES ('谢文尧', '22008020114@neusoft.edu.cn', '+8615640993693', 'a506a646a5a14e39925c05f7713daceccd97bc85238a04ad2c7b9a9cb3a18a58e64f0c16e085ca162b28bb981e3d85beb3212a451f0611eee90bc738c52ab8dd', 'admin');
INSERT INTO Products (product_name, remarks, link) VALUES ('Insightful Verse Integrated Educating Platform','Learn something new and start a new chapter. Welcome to the Insightful Verse Integrated Educating Platform! You can log in, register, log out , read books, watch videos, write code, you can do almost everything.','InsightfulVerse.run');
INSERT INTO Version (commit_id, version, type, channel, update_info) VALUES (UUID(), '1.0.0.1000..0', 'Web', 'Zeta', 'The first version of Eagloxis Website is developed.');