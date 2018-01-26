CREATE schema IF NOT EXISTS $schema_name CHARACTER SET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `$schema_name`.`tacusci_info` (
  `id_tacusci_info` INT NOT NULL DEFAULT 1,
  `version_number_major` INT NOT NULL $VERSION_MAJOR,
  `version_number_minor` INT NOT NULL $VERSION_MINOR,
  `version_number_revision` INT NOT NULL $VERSION_REVISION,
  PRIMARY KEY (`id_tacusci_info`),
  UNIQUE INDEX `id_tacusci_info_UNIQUE` (`id_tacusci_info` ASC));

CREATE TABLE IF NOT EXISTS `$schema_name`.`users` (
  `id_users` INT NOT NULL AUTO_INCREMENT,
  `created_date_time` LONG NOT NULL,
  `last_updated_date_time` LONG NOT NULL,
  `full_name` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `auth_hash` CHAR(72) NOT NULL,
  `root_admin` BIT(1) NOT NULL,
  `banned`  BIT(1) NOT NULL,
  `banned_date_time` LONG,
  PRIMARY KEY (`id_users`),
  UNIQUE INDEX `id_users_UNIQUE` (`id_users` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));


CREATE TABLE IF NOT EXISTS  `$schema_name`.`groups` (
  `id_groups` INT NOT NULL AUTO_INCREMENT,
  `created_date_time` LONG NOT NULL,
  `last_updated_date_time` LONG NOT NULL,
  `group_name` VARCHAR(45) NOT NULL,
  `id_parent_group` INT,
  `default_group` BIT(1) NOT NULL,
  `hidden` BIT(1) NOT NULL,
  PRIMARY KEY (`id_groups`),
  UNIQUE INDEX `id_groups_UNIQUE` (`id_groups` ASC),
  UNIQUE INDEX `group_name_UNIQUE` (`group_name` ASC));


CREATE TABLE IF NOT EXISTS `$schema_name`.`user2group` (
  `id_users` INT NOT NULL,
  `last_updated_date_time` LONG NOT NULL,
  `id_groups` INT NOT NULL);

CREATE TABLE IF NOT EXISTS `$schema_name`.`reset_password` (
  `id_reset_passwords` INT NOT NULL AUTO_INCREMENT,
  `created_date_time` LONG NOT NULL,
  `last_updated_date_time` LONG NOT NULL,
  `banned_date_time` LONG,
  `id_users` INT NOT NULL,
  `auth_hash` VARCHAR(100) NOT NULL,
  `expired` BIT(1) NOT NULL,
  PRIMARY KEY (`id_reset_passwords`),
  UNIQUE INDEX `id_reset_passwords_UNIQUE` (`id_reset_passwords` ASC),
  UNIQUE INDEX `id_users_UNIQUE` (`id_users` ASC));

CREATE TABLE IF NOT EXISTS `$schema_name`.`pages` (
  `id_page` INT NOT NULL AUTO_INCREMENT,
  `created_date_time` LONG NOT NULL,
  `last_updated_date_time` LONG NOT NULL,
  `page_title` VARCHAR(100) NOT NULL,
  `page_route` VARCHAR(200) NOT NULL,
  `page_content` LONGTEXT NOT NULL,
  `deleteable` BIT(1) NOT NULL,
  `disabled` BIT(1) NOT NULL,
  `template_to_use_id` INT,
  `maintenance_mode` BIT(1) NOT NULL,
  `author_user_id` INT NOT NULL,
  `page_type` INT NOT NULL,
  PRIMARY KEY (`id_page`),
  UNIQUE INDEX `id_page_UNIQUE` (`id_page` ASC),
  UNIQUE INDEX `page_route` (`page_route` ASC));

CREATE TABLE IF NOT EXISTS `$schema_name`.`templates` (
    `id_template` INT NOT NULL AUTO_INCREMENT,
    `created_date_time` LONG NOT NULL,
    `last_updated_date_time` LONG NOT NULL,
    `template_title` VARCHAR(100) NOT NULL UNIQUE,
    `template_content` LONGTEXT NOT NULL,
    `author_user_id` INT NOT NULL,
    PRIMARY KEY (`id_template`),
    UNIQUE INDEX `id_page_UNIQUE` (`id_template` ASC));

CREATE TABLE IF NOT EXISTS `$schema_name`.`includes` (
    `id_include` INT NOT NULL AUTO_INCREMENT,
    `created_date_time` LONG NOT NULL,
    `last_updated_date_time` LONG NOT NULL,
    `include_title` VARCHAR(100) NOT NULL,
    `include_content` LONGTEXT NOT NULL,
    `author_user_id` INT NOT NULL,
    PRIMARY KEY (`id_include`),
    UNIQUE INDEX `id_include_UNIQUE` (`id_include` ASC));

CREATE TABLE IF NOT EXISTS `$schema_name`.`route_permissions` (
    `id_permission` INT NOT NULL AUTO_INCREMENT,
    `created_date_time` LONG NOT NULL,
    `last_updated_date_time` LONG NOT NULL,
    `permission_title` VARCHAR(100) NOT NULL UNIQUE,
    `route` VARCHAR(200) NOT NULL,
    `id_groups` INT NOT NULL,
    PRIMARY KEY (`id_permission`));

CREATE TABLE IF NOT EXISTS `$schema_name`.`sql_queries` (
    `id_sql_queries` INT NOT NULL AUTO_INCREMENT,
    `created_date_time` TEXT NOT NULL,
    `last_updated_date_time` TEXT NOT NULL,
    `query_label` VARCHAR(45) UNIQUE NOT NULL,
    `query_name` VARCHAR(45) UNIQUE NOT NULL,
    `query_text` TEXT NOT NULL,
    PRIMARY KEY (`id_sql_queries`));