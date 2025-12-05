-- Flyway migration script for initial database schema
-- PostgreSQL syntax

-- Таблица nodelist_entry
CREATE TABLE nodelist_entry (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nodelist_year INT NOT NULL,
    nodelist_name VARCHAR(255) NOT NULL
);

-- Индекс для nodelist_entry
CREATE INDEX idx_nodelist_year_name_desc ON nodelist_entry (nodelist_year DESC, nodelist_name DESC);

-- Таблица node_entry
CREATE TABLE node_entry (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nodelist_entry_id BIGINT NOT NULL,
    zone INT,
    network INT,
    node INT,
    keywords VARCHAR(255),
    node_name VARCHAR(255),
    location VARCHAR(255),
    sys_op_name VARCHAR(255),
    phone VARCHAR(255),
    baud_rate INT,
    flags VARCHAR(255)[],
    CONSTRAINT fk_nodelist_entry FOREIGN KEY (nodelist_entry_id) REFERENCES nodelist_entry(id)
);

-- Индекс для node_entry
CREATE INDEX idx_zone_network_node_asc ON node_entry (zone, network, node);