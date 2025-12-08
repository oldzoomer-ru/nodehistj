DROP INDEX IF EXISTS "idx_zone_network_node_change_date_desc";
DROP INDEX IF EXISTS "idx_change_type";

CREATE INDEX idx_zone_network_node_asc_change_date_desc
ON node_history_entry (zone ASC, network ASC, node ASC, change_date DESC);