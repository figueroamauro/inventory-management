create schema if not exists inventory;
use inventory;

create table users(
id int auto_increment primary key,
username varchar(50),
password varchar(300),
email varchar(255),
enabled boolean,
role varchar(20)
);