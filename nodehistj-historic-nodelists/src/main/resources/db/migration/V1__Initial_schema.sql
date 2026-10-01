-- Flyway migration script for initial database schema
-- PostgreSQL syntax

-- Таблица nodelist_entry
CREATE TABLE nodelist_entry (
    id BIGSERIAL PRIMARY KEY,
    nodelist_year SMALLINT NOT NULL CHECK (nodelist_year BETWEEN 1984 AND 2038),
    day_of_year SMALLINT NOT NULL CHECK (day_of_year BETWEEN 1 AND 366)
);

-- Индекс для nodelist_entry
CREATE INDEX idx_nodelist_year_name_desc ON nodelist_entry (nodelist_year DESC, day_of_year DESC);

-- Таблица node_entry
CREATE TABLE node_entry (
    node_id BIGSERIAL PRIMARY KEY,
    nodelist_entry_id BIGSERIAL NOT NULL REFERENCES nodelist_entry(id) ON DELETE CASCADE,
    zone INT,
    network INT,
    node INT,
    keywords VARCHAR(255),
    node_name TEXT,
    location TEXT,
    sys_op_name TEXT,
    phone TEXT,
    baud_rate INT,
    flags TEXT[]
);

-- Индекс для node_entry
CREATE INDEX idx_zone_network_node_asc ON node_entry (zone, network, node);
CREATE INDEX idx_node_entry_nodelist_entry_id ON node_entry(nodelist_entry_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_nodelist_entry_unique
    ON nodelist_entry (nodelist_year, day_of_year);