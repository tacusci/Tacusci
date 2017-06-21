CREATE schema IF NOT EXISTS $schema_name CHARACTER SET utf8 COLLATE utf8_unicode_ci;

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