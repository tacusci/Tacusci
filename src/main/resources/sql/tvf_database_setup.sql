CREATE SCHEMA `tvf` ;

CREATE TABLE `tvf`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `authhash` CHAR(72) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  `banned`  BIT(1) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))


CREATE TABLE `tvf`.`groups` (
  `idgroups` INT NOT NULL,
  `groupname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idgroups`),
  UNIQUE INDEX `idgroups_UNIQUE` (`idgroups` ASC),
  UNIQUE INDEX `groupname_UNIQUE` (`groupname` ASC));


CREATE TABLE `tvf`.`user2group` (
  `idusers` INT NOT NULL,
  `idgroups` INT NOT NULL,
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `idgroups_UNIQUE` (`idgroups` ASC));
