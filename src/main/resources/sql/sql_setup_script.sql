CREATE schema IF NOT EXISTS tacusci;

CREATE TABLE IF NOT EXISTS `tacusci`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `createdtime` LONG NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `authhash` CHAR(72) NOT NULL,
  `rootadmin` BIT(1) NOT NULL,
  `banned`  BIT(1) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));


CREATE TABLE IF NOT EXISTS  `tacusci`.`groups` (
  `idgroups` INT NOT NULL AUTO_INCREMENT,
  `createdtime` LONG NOT NULL,
  `groupname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idgroups`),
  UNIQUE INDEX `idgroups_UNIQUE` (`idgroups` ASC),
  UNIQUE INDEX `groupname_UNIQUE` (`groupname` ASC));


CREATE TABLE IF NOT EXISTS `tacusci`.`user2group` (
  `idusers` INT NOT NULL,
  `createdtime` LONG NOT NULL,
  `idgroups` INT NOT NULL);

CREATE TABLE IF NOT EXISTS `tacusci`.`routeentities` (
  `idrouteentities` INT NOT NULL,
  `parentid` INT,
  `name` VARCHAR(45) NOT NULL,
  `type` INT NOT NULL,
  `pageid` INT,
  PRIMARY KEY (`idrouteentities`),
  UNIQUE INDEX `idrouteentities_UNIQUE` (`idrouteentities` ASC));

CREATE TABLE IF NOT EXISTS `tacusci`.`resetpassword` (
  `idresetpasswords` INT NOT NULL AUTO_INCREMENT,
  `createdtime` LONG NOT NULL,
  `idusers` INT NOT NULL,
  `authhash` VARCHAR(72) NOT NULL,
  `expired` BIT(1) NOT NULL,
  PRIMARY KEY (`idresetpasswords`),
  UNIQUE INDEX `idresetpasswords_UNIQUE` (`idresetpasswords` ASC),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC));