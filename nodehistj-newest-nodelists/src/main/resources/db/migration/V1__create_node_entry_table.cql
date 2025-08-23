-- CQL миграция для создания таблицы NodeEntry в сервисе nodehistj-newest-nodelists
-- Эта миграция создает основную таблицу для хранения последних версий узлов

-- Создание таблицы node_entry с оптимизированной структурой первичного ключа
CREATE TABLE IF NOT EXISTS nodehistj_newest.node_entry (
    zone int,
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
    nodelist_year int,
    nodelist_name text,
    PRIMARY KEY (zone, network, node)
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Таблица для хранения последних версий узлов с оптимизированным PK'
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

-- Создание вторичных индексов для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_node_entry_nodelist_year ON nodehistj_newest.node_entry (nodelist_year);
CREATE INDEX IF NOT EXISTS idx_node_entry_nodelist_name ON nodehistj_newest.node_entry (nodelist_name);
CREATE INDEX IF NOT EXISTS idx_node_entry_zone ON nodehistj_newest.node_entry (zone);
CREATE INDEX IF NOT EXISTS idx_node_entry_network ON nodehistj_newest.node_entry (network);
CREATE INDEX IF NOT EXISTS idx_node_entry_node ON nodehistj_newest.node_entry (node);