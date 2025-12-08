CREATE UNIQUE INDEX IF NOT EXISTS uk_node_history_entry_unique
ON node_history_entry (zone, network, node, nodelist_year, nodelist_name);