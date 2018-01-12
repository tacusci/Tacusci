CREATE SCHEMA IF NOT EXISTS $schema_name;

CREATE SEQUENCE $schema_name.users_seq;

CREATE TABLE IF NOT EXISTS $schema_name.users (
    id_users INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.users_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    full_name VARCHAR(45) NOT NULL,
    username VARCHAR(45) UNIQUE NOT NULL,
    email VARCHAR(45) UNIQUE NOT NULL,
    auth_hash CHAR(72) NOT NULL,
    root_admin BOOLEAN NOT NULL,
    banned  BOOLEAN NOT NULL,
    banned_date_time TEXT
);


CREATE SEQUENCE $schema_name.groups_seq;

CREATE TABLE IF NOT EXISTS  $schema_name.groups (
    id_groups INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.groups_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    group_name VARCHAR(45) UNIQUE NOT NULL,
    id_parent_group INT,
    default_group BOOLEAN NOT NULL,
    hidden BOOLEAN NOT NULL
);


CREATE TABLE IF NOT EXISTS $schema_name.user2group (
    id_users INT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    id_groups INT NOT NULL
);

CREATE SEQUENCE $schema_name.reset_password_seq;

CREATE TABLE IF NOT EXISTS $schema_name.reset_password (
    id_reset_passwords INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.reset_password_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    banned_date_time TEXT,
    id_users INT UNIQUE NOT NULL,
    auth_hash VARCHAR(100) NOT NULL,
    expired BOOLEAN NOT NULL
);

CREATE SEQUENCE $schema_name.pages_seq;

CREATE TABLE IF NOT EXISTS $schema_name.pages (
    id_page INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.pages_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    page_title VARCHAR(100) NOT NULL,
    page_route VARCHAR(200) UNIQUE NOT NULL,
    page_content TEXT NOT NULL,
    deleteable BOOLEAN NOT NULL,
    template_to_use_id INT,
    maintenance_mode BOOLEAN NOT NULL,
    author_user_id INT NOT NULL,
    page_type INT NOT NULL
);

CREATE SEQUENCE $schema_name.templates_seq;

CREATE TABLE IF NOT EXISTS $schema_name.templates (
    id_template INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.templates_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    template_title VARCHAR(100) NOT NULL UNIQUE,
    template_content TEXT NOT NULL,
    author_user_id INT NOT NULL
);

CREATE SEQUENCE $schema_name.includes_seq;

CREATE TABLE IF NOT EXISTS $schema_name.includes (
    id_include INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.includes_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    include_title VARCHAR(100) NOT NULL,
    include_content TEXT NOT NULL,
    author_user_id INT NOT NULL
);

CREATE SEQUENCE $schema_name.route_permissions_seq;

CREATE TABLE IF NOT EXISTS $schema_name.route_permissions (
    id_permission INT PRIMARY KEY UNIQUE NOT NULL DEFAULT NEXTVAL ('$schema_name.route_permissions_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    permission_title VARCHAR(100) NOT NULL UNIQUE,
    route VARCHAR(200) NOT NULL,
    id_groups INT NOT NULL
);