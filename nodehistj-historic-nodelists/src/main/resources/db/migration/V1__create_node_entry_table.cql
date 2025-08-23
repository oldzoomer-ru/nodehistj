-- CQL миграция для создания таблицы NodeEntry в сервисе nodehistj-historic-nodelists
-- Эта миграция создает основную таблицу для хранения узлов с историей

-- Создание таблицы node_entry с оптимизированной структурой первичного ключа
CREATE TABLE IF NOT EXISTS nodehistj_historic.node_entry (
    zone int,
    network int,
    node int,
    nodelist_year int,
    nodelist_name text,
    id uuid,
    keywords text,
    node_name text,
    location text,
    sys_op_name text,
    phone text,
    baud_rate int,
    flags list<text>,
    PRIMARY KEY ((zone, network, node), nodelist_year, nodelist_name)
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Таблица для хранения узлов с историей с оптимизированным PK'
    AND compaction = {
        'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy',
        'min_threshold': '4',
        'max_threshold': '32'
    }
    AND compression = {
        'chunk_length_in_kb': '16',
        'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
    }
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p'
    AND tombstone_compaction_interval = 86400
    AND tombstone_threshold = 0.05
    AND read_repair_chance = 0.1
    AND dclocal_read_repair_chance = 0.1;

-- Создание таблицы для запросов по zone, network (без node)
CREATE TABLE IF NOT EXISTS nodehistj_historic.node_entry_by_zone_network (
    zone int,
    network int,
    nodelist_year int,
    nodelist_name text,
    id uuid,
    keywords text,
    node_name text,
    location text,
    sys_op_name text,
    phone text,
    baud_rate int,
    flags list<text>,
    node int,
    PRIMARY KEY ((zone, network), nodelist_year, nodelist_name, node)
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Таблица для запросов по zone, network'
    AND compaction = {
        'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy',
        'min_threshold': '4',
        'max_threshold': '32'
    }
    AND compression = {
        'chunk_length_in_kb': '16',
        'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
    }
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p'
    AND tombstone_compaction_interval = 86400
    AND tombstone_threshold = 0.05
    AND read_repair_chance = 0.1
    AND dclocal_read_repair_chance = 0.1;

-- Создание таблицы для запросов по zone
CREATE TABLE IF NOT EXISTS nodehistj_historic.node_entry_by_zone (
    zone int,
    nodelist_year int,
    nodelist_name text,
    network int,
    node int,
    id uuid,
    keywords text,
    node_name text,
    location text,
    sys_op_name text,
    phone text,
    baud_rate int,
    flags list<text>,
    PRIMARY KEY ((zone), nodelist_year, nodelist_name, network, node)
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Таблица для запросов по zone'
    AND compaction = {
        'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy',
        'min_threshold': '4',
        'max_threshold': '32'
    }
    AND compression = {
        'chunk_length_in_kb': '16',
        'class': 'org.apache.cassandra.io.compress.LZ4Compressor'
    }
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p'
    AND tombstone_compaction_interval = 86400
    AND tombstone_threshold = 0.05
    AND read_repair_chance = 0.1
    AND dclocal_read_repair_chance = 0.1;

-- Создание материализованного представления для запросов по nodelist_year и nodelist_name
CREATE MATERIALIZED VIEW IF NOT EXISTS nodehistj_historic.node_entry_by_nodelist_year_name AS
SELECT nodelist_year,
       nodelist_name,
       zone,
       network,
       node,
       id,
       keywords,
       node_name,
       location,
       sys_op_name,
       phone,
       baud_rate,
       flags
FROM node_entry
WHERE nodelist_year IS NOT NULL AND nodelist_name IS NOT NULL
PRIMARY KEY (nodelist_year, nodelist_name, zone, network, node);

-- Создание материализованного представления для запросов по nodelist_year
CREATE MATERIALIZED VIEW IF NOT EXISTS nodehistj_historic.node_entry_by_nodelist_year AS
SELECT nodelist_year,
       zone,
       network,
       node,
       id,
       keywords,
       node_name,
       location,
       sys_op_name,
       phone,
       baud_rate,
       flags,
       nodelist_name
FROM node_entry
WHERE nodelist_year IS NOT NULL
PRIMARY KEY (nodelist_year, zone, network, node);