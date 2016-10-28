CREATE SCHEMA `tvf` ;

CREATE TABLE `tvf`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `salt` CHAR(64) NOT NULL,
  `hash` CHAR(64) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
