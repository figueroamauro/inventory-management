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

-- FOR TESTING

INSERT INTO users(username, password, email,enabled,role)
VALUES("admin", "$2y$10$jEVxb8Mh8y0ppp9XcX.9QO/9S2N9OBKXIdY9MAYGrxtXLpd5f8.dy", "admin@mail.com", true, "ADMIN");