CREATE SCHEMA if NOT EXISTS inventory_stock;
USE inventory_stock;

CREATE TABLE locations(
id int AUTO_INCREMENT PRIMARY KEY,
name varchar(50) NOT NULL,
warehouse_id int NOT NULL,
active boolean NOT NULL
);

CREATE TABLE stock_entry(
id int PRIMARY KEY,
quantity int,
update_at timestamp,
product_id int NOT NULL,
warehouse_id int NOT NULL
);

CREATE TABLE stock_movements(
id int AUTO_INCREMENT PRIMARY KEY,
movement_type varchar(20) NOT NULL,
quantity int NOT NULL,
before_stock int NOT NULL,
after_stock int NOT NULL,
create_at timestamp NOT NULL,
note varchar(255),
location_id int NOT NULL,
stock_entry_id int NOT NULL
);

CREATE TABLE location_stock(
id int AUTO_INCREMENT PRIMARY KEY,
product_id int NOT NULL,
quantity int,
location_id int NOT NULL
);

ALTER TABLE stock_movements
ADD CONSTRAINT fk_movement_location
FOREIGN KEY (location_id)
REFERENCES locations(id);

ALTER TABLE stock_movements
ADD CONSTRAINT fk_movement_stock_entry
FOREIGN KEY (stock_entry_id)
REFERENCES stock_entry(id);

ALTER TABLE location_stock
ADD CONSTRAINT fk_location_stock_location
FOREIGN KEY (location_id)
REFERENCES locations(id);
