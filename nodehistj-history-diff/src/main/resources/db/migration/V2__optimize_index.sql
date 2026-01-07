DROP INDEX idx_nodelist_year_name_desc;
CREATE INDEX idx_nodelist_year_name_asc ON nodelist_entry (nodelist_year, day_of_year);