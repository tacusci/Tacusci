CREATE schema IF NOT EXISTS $schema_name;

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

CREATE TABLE IF NOT EXISTS `$schema_name`.`routeentities` (
  `id_route_entities` INT NOT NULL,
  `parent_id` INT,
  `name` VARCHAR(45) NOT NULL,
  `type` INT NOT NULL,
  `id_page` INT,
  PRIMARY KEY (`id_route_entities`),
  UNIQUE INDEX `id_route_entities_UNIQUE` (`id_route_entities` ASC));

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
  `maintance_mode` BIT(1) NOT NULL,
  `public_and_live` BIT(1) NOT NULL,
  `author_user` INT NOT NULL,
  PRIMARY KEY (`id_page`),
  UNIQUE INDEX `id_page_UNIQUE` (`id_page` ASC),
  UNIQUE INDEX `page_route` (`page_route` ASC),
  UNIQUE INDEX `author_user` (`author_user` ASC));