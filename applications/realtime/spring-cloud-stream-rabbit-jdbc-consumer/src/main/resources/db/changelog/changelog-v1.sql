-- liquibase formatted sql
-- changeset liquibaseuser:1
CREATE TABLE inv_orders (
    id text PRIMARY KEY,
    DATA JSONB
);
-- rollback drop table customer_orders;

create table products(
id text PRIMARY KEY,
DATA JSONB);
-- rollback drop table products;
