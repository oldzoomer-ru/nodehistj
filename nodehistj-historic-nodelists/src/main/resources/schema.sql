CREATE TABLE IF NOT EXISTS nodelist (
    id BIGSERIAL PRIMARY KEY,
    nodelist_year INTEGER,
    nodelist_name VARCHAR(255),
    zone INTEGER,
    network INTEGER,
    node INTEGER,
    keywords VARCHAR(255),
    node_name VARCHAR(255),
    location VARCHAR(255),
    sys_op_name VARCHAR(255),
    phone VARCHAR(255),
    baud_rate INTEGER,
    flags TEXT[]
);
