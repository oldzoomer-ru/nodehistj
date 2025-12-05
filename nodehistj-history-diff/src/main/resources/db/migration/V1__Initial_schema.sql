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
    FOREIGN KEY (nodelist_entry_id) REFERENCES nodelist_entry(id)
);

-- Индекс для node_entry
CREATE INDEX idx_zone_network_node_asc ON node_entry (zone, network, node);

CREATE TABLE node_history_entry (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    zone INT NOT NULL,
    network INT NOT NULL,
    node INT NOT NULL,
    change_date DATE NOT NULL,
    nodelist_year INT NOT NULL,
    nodelist_name VARCHAR(255) NOT NULL,
    change_type VARCHAR(8) NOT NULL CHECK (change_type IN ('ADDED', 'REMOVED', 'MODIFIED')),
    keywords VARCHAR(255),
    node_name VARCHAR(255),
    location VARCHAR(255),
    sys_op_name VARCHAR(255),
    phone VARCHAR(255),
    baud_rate INT,
    flags VARCHAR(255)[],
    prev_keywords VARCHAR(255),
    prev_node_name VARCHAR(255),
    prev_location VARCHAR(255),
    prev_sys_op_name VARCHAR(255),
    prev_phone VARCHAR(255),
    prev_baud_rate INT,
    prev_flags VARCHAR(255)[]
);

-- Индексы для node_history_entry
CREATE INDEX idx_zone_network_node_change_date_desc ON node_history_entry (zone, network, node, change_date DESC);
CREATE INDEX idx_change_date_desc ON node_history_entry (change_date DESC);
CREATE INDEX idx_change_type ON node_history_entry (change_type);