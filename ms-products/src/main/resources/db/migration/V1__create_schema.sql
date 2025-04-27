CREATE SCHEMA if NOT EXISTS inventory_products;
USE inventory_products;

CREATE TABLE products(
id int AUTO_INCREMENT PRIMARY KEY,
name varchar(50),
description varchar(300),
price decimal,
created_at date,
category_id int,
warehouse_id int
);

CREATE TABLE categories(
id int AUTO_INCREMENT PRIMARY KEY,
name varchar(30),
warehouse_id int
);

CREATE TABLE warehouses(
id int AUTO_INCREMENT PRIMARY KEY,
name varchar(50),
user_id int
);


ALTER TABLE products
ADD CONSTRAINT fk_products_category
FOREIGN KEY (category_id)
REFERENCES categories(id);

ALTER TABLE products
ADD CONSTRAINT fk_products_warehouse
FOREIGN KEY (warehouse_id)
REFERENCES warehouses(id);

ALTER TABLE categories
ADD CONSTRAINT fk_categories_warehouse
FOREIGN KEY (warehouse_id)
REFERENCES warehouses(id);

-- FOR TESTING
-- INSERT INTO warehouses(name, user_id) VALUES("deposito", 1);
-- INSERT INTO warehouses(name, user_id) VALUES("deposito", 2);
-- INSERT INTO warehouses(name, user_id) VALUES("deposito", 3);