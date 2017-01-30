CREATE schema IF NOT EXISTS tvf_testing;

CREATE TABLE IF NOT EXISTS `tvf_testing`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `rootadmin` BIT(1) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `authhash` CHAR(72) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  `banned`  BIT(1) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));


CREATE TABLE IF NOT EXISTS  `tvf_testing`.`groups` (
  `idgroups` INT NOT NULL AUTO_INCREMENT,
  `groupname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idgroups`),
  UNIQUE INDEX `idgroups_UNIQUE` (`idgroups` ASC),
  UNIQUE INDEX `groupname_UNIQUE` (`groupname` ASC));


CREATE TABLE IF NOT EXISTS `tvf_testing`.`user2group` (
  `idusers` INT NOT NULL,
  `idgroups` INT NOT NULL);