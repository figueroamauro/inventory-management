CREATE SCHEMA if NOT EXISTS inventory;
USE inventory;

CREATE TABLE users(
id int AUTO_INCREMENT PRIMARY KEY,
username varchar(50)  UNIQUE,
password varchar(300),
email varchar(255) UNIQUE,
enabled boolean,
role varchar(20)
);