-- Flyway migration script for initial database schema
-- PostgreSQL syntax

-- Таблица nodelist_entry
CREATE TABLE nodelist_entry (
    id BIGSERIAL PRIMARY KEY,
    nodelist_year DATE NOT NULL,
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

CREATE TABLE node_history_entry (
    id BIGSERIAL PRIMARY KEY,
    zone INT NOT NULL,
    network INT NOT NULL,
    node INT NOT NULL,
    change_date DATE NOT NULL,
    nodelist_year SMALLINT NOT NULL CHECK (nodelist_year BETWEEN 1 AND 9999),
    day_of_year SMALLINT NOT NULL CHECK (day_of_year BETWEEN 1 AND 366),
    CONSTRAINT valid_day_of_year CHECK (
        day_of_year <= (
            CASE
                WHEN (nodelist_year % 4 = 0 AND nodelist_year % 100 != 0) OR (nodelist_year % 400 = 0)
                THEN 366
                ELSE 365
            END
        )
    ),
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
CREATE INDEX idx_zone_network_node_asc_change_date_desc
    ON node_history_entry (zone ASC, network ASC, node ASC, change_date DESC);
CREATE INDEX idx_change_date_desc ON node_history_entry (change_date DESC);
CREATE UNIQUE INDEX IF NOT EXISTS uk_node_history_entry_unique
    ON node_history_entry (zone, network, node, nodelist_year, day_of_year);