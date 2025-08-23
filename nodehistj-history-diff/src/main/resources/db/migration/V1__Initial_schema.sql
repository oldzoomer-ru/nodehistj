-- Flyway migration script for initial database schema
-- PostgreSQL syntax

-- Таблица nodelist_entry
CREATE TABLE nodelist_entry (
    id BIGSERIAL PRIMARY KEY,
    nodelist_year INT NOT NULL,
    nodelist_name TEXT NOT NULL
);

-- Индекс для nodelist_entry
CREATE INDEX idx_nodelist_year_name_desc ON nodelist_entry (nodelist_year DESC, nodelist_name DESC);

-- Таблица node_entry
CREATE TABLE node_entry (
    id BIGSERIAL PRIMARY KEY,
    nodelist_entry_id BIGINT NOT NULL,
    zone INT,
    network INT,
    node INT,
    keywords VARCHAR(255),
    node_name TEXT,
    location TEXT,
    sys_op_name TEXT,
    phone TEXT,
    baud_rate INT,
    flags TEXT[],
    FOREIGN KEY (nodelist_entry_id) REFERENCES nodelist_entry(id)
);

-- Индекс для node_entry
CREATE INDEX idx_zone_network_node_asc ON node_entry (zone, network, node);

CREATE TABLE node_history_entry (
    id BIGSERIAL PRIMARY KEY,
    zone INT NOT NULL,
    network INT NOT NULL,
    node INT NOT NULL,
    change_date DATE NOT NULL,
    nodelist_year INT NOT NULL,
    nodelist_name TEXT NOT NULL,
    change_type VARCHAR(8) NOT NULL CHECK (change_type IN ('ADDED', 'REMOVED', 'MODIFIED')),
    keywords VARCHAR(255),
    node_name TEXT,
    location TEXT,
    sys_op_name TEXT,
    phone TEXT,
    baud_rate INT,
    flags TEXT[],
    prev_keywords VARCHAR(255),
    prev_node_name TEXT,
    prev_location TEXT,
    prev_sys_op_name TEXT,
    prev_phone TEXT,
    prev_baud_rate INT,
    prev_flags TEXT[]
);

-- Индексы для node_history_entry
CREATE INDEX idx_zone_network_node_change_date_desc ON node_history_entry (zone, network, node, change_date DESC);
CREATE INDEX idx_change_date_desc ON node_history_entry (change_date DESC);
CREATE INDEX idx_change_type ON node_history_entry (change_type);