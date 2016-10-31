CREATE SCHEMA `tvf` ;

CREATE TABLE `tvf`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `authhash` CHAR(72) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
  UNIQUE INDEX `email_UNIQUE` (`email` ASC)
