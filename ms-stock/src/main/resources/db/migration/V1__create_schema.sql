CREATE SCHEMA if NOT EXISTS inventory_stock;
USE inventory_stock;

CREATE TABLE locations(
id int AUTO_INCREMENT PRIMARY KEY,
name varchar(50) NOT NULL,
warehouse_id int NOT NULL
);

CREATE TABLE stock_entry(
id int AUTO_INCREMENT PRIMARY KEY,
quantity int,
update_at timestamp,
product_id int NOT NULL
);

CREATE TABLE stock_movement(
id int AUTO_INCREMENT PRIMARY KEY,
movement_type varchar(20) NOT NULL,
quantity int NOT NULL,
timestamp timestamp NOT NULL,
note varchar(255),
location_id int NOT NULL,
stock_entry_id int NOT NULL
);

ALTER TABLE stock_movement
ADD CONSTRAINT fk_movement_location
FOREIGN KEY (location_id)
REFERENCES locations(id);
