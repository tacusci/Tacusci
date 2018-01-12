CREATE schema IF NOT EXISTS $schema_name CHARACTER SET utf8 COLLATE utf8_unicode_ci;

CREATE SEQUENCE $schema_name.users_seq;

CREATE TABLE IF NOT EXISTS $schema_name.users (
  id_users INT NOT NULL DEFAULT NEXTVAL ('$schema_name.users_seq'),
  created_date_time TEXT NOT NULL,
  last_updated_date_time TEXT NOT NULL,
  full_name VARCHAR(45) NOT NULL,
  username VARCHAR(45) NOT NULL,
  email VARCHAR(45) NOT NULL,
  auth_hash CHAR(72) NOT NULL,
  root_admin BOOLEAN(1) NOT NULL,
  banned  BOOLEAN(1) NOT NULL,
  banned_date_time TEXT,
  PRIMARY KEY (id_users),
  CONSTRAINT id_users_UNIQUE UNIQUE  (id_users ASC),
  CONSTRAINT username_UNIQUE UNIQUE  (username ASC),
  CONSTRAINT email_UNIQUE UNIQUE  (email ASC));


CREATE SEQUENCE $schema_name.groups_seq;

CREATE TABLE IF NOT EXISTS  $schema_name.groups (
  id_groups INT NOT NULL DEFAULT NEXTVAL ('$schema_name.groups_seq'),
  created_date_time TEXT NOT NULL,
  last_updated_date_time TEXT NOT NULL,
  group_name VARCHAR(45) NOT NULL,
  id_parent_group INT,
  default_group BOOLEAN(1) NOT NULL,
  hidden BOOLEAN(1) NOT NULL,
  PRIMARY KEY (id_groups),
  CONSTRAINT id_groups_UNIQUE UNIQUE  (id_groups ASC),
  CONSTRAINT group_name_UNIQUE UNIQUE  (group_name ASC));


CREATE TABLE IF NOT EXISTS $schema_name.user2group (
  id_users INT NOT NULL,
  last_updated_date_time TEXT NOT NULL,
  id_groups INT NOT NULL);

CREATE SEQUENCE $schema_name.reset_password_seq;

CREATE TABLE IF NOT EXISTS $schema_name.reset_password (
  id_reset_passwords INT NOT NULL DEFAULT NEXTVAL ('$schema_name.reset_password_seq'),
  created_date_time TEXT NOT NULL,
  last_updated_date_time TEXT NOT NULL,
  banned_date_time TEXT,
  id_users INT NOT NULL,
  auth_hash VARCHAR(100) NOT NULL,
  expired BOOLEAN(1) NOT NULL,
  PRIMARY KEY (id_reset_passwords),
  CONSTRAINT id_reset_passwords_UNIQUE UNIQUE  (id_reset_passwords ASC),
  CONSTRAINT id_users_UNIQUE UNIQUE  (id_users ASC));

CREATE SEQUENCE $schema_name.pages_seq;

CREATE TABLE IF NOT EXISTS $schema_name.pages (
  id_page INT NOT NULL DEFAULT NEXTVAL ('$schema_name.pages_seq'),
  created_date_time TEXT NOT NULL,
  last_updated_date_time TEXT NOT NULL,
  page_title VARCHAR(100) NOT NULL,
  page_route VARCHAR(200) NOT NULL,
  page_content LONGTEXT NOT NULL,
  deleteable BOOLEAN(1) NOT NULL,
  template_to_use_id INT,
  maintenance_mode BOOLEAN(1) NOT NULL,
  author_user_id INT NOT NULL,
  page_type INT NOT NULL,
  PRIMARY KEY (id_page),
  CONSTRAINT id_page_UNIQUE UNIQUE  (id_page ASC),
  CONSTRAINT page_route UNIQUE  (page_route ASC));

CREATE SEQUENCE $schema_name.templates_seq;

CREATE TABLE IF NOT EXISTS $schema_name.templates (
    id_template INT NOT NULL DEFAULT NEXTVAL ('$schema_name.templates_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    template_title VARCHAR(100) NOT NULL UNIQUE,
    template_content LONGTEXT NOT NULL,
    author_user_id INT NOT NULL,
    PRIMARY KEY (id_template),
    CONSTRAINT id_page_UNIQUE UNIQUE  (id_template ASC));

CREATE SEQUENCE $schema_name.includes_seq;

CREATE TABLE IF NOT EXISTS $schema_name.includes (
    id_include INT NOT NULL DEFAULT NEXTVAL ('$schema_name.includes_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    include_title VARCHAR(100) NOT NULL,
    include_content LONGTEXT NOT NULL,
    author_user_id INT NOT NULL,
    PRIMARY KEY (id_include),
    CONSTRAINT id_include_UNIQUE UNIQUE  (id_include ASC));

CREATE SEQUENCE $schema_name.route_permissions_seq;

CREATE TABLE IF NOT EXISTS $schema_name.route_permissions (
    id_permission INT NOT NULL DEFAULT NEXTVAL ('$schema_name.route_permissions_seq'),
    created_date_time TEXT NOT NULL,
    last_updated_date_time TEXT NOT NULL,
    permission_title VARCHAR(100) NOT NULL UNIQUE,
    route VARCHAR(200) NOT NULL,
    id_groups INT NOT NULL,
    PRIMARY KEY (id_permission));