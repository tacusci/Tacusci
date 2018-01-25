ALTER TABLE $schema_name.pages ADD COLUMN IF NOT EXISTS disabled BOOLEAN NOT NULL DEFAULT false

CREATE SEQUENCE IF NOT EXISTS $schema_name.sql_query_collection_seq;

CREATE TABLE IF NOT EXISTS $schema_name.sql_query_collection (
    id_sql_query_collection INTEGER PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.sql_query_collection_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    query_name VARCHAR(45) UNIQUE NOT NULL,
    query_text TEXT
);