SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP DATABASE ambit_repository;
CREATE SCHEMA IF NOT EXISTS `ambit_repository` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `ambit_repository`;

-- -----------------------------------------------------
-- Table `ambit_repository`.`chemicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`chemicals` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`chemicals` (
  `idchemical` INT NOT NULL  AUTO_INCREMENT ,
  `inchi` TEXT NULL ,
  `formula` VARCHAR(64) NULL ,
  PRIMARY KEY (`idchemical`) );

CREATE INDEX idchemical ON `ambit_repository`.`chemicals` (idchemical) ;

CREATE INDEX inchi ON `ambit_repository`.`chemicals` (inchi(1024)) ;

CREATE INDEX formula ON `ambit_repository`.`chemicals` (`formula` ASC) ;


-- -----------------------------------------------------
-- Table `ambit_repository`.`structures`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`structures` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`structure` (
  `idstructure` INT NOT NULL AUTO_INCREMENT ,
  `idchemical` INT NOT NULL ,
  `structure` BLOB NOT NULL ,
  `format` VARCHAR(16) NOT NULL DEFAULT "CML" ,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`idstructure`) ,
  INDEX (`idchemical`) ,
  CONSTRAINT `fk_idchemical`
    FOREIGN KEY (`idchemical` )
    REFERENCES `ambit_repository`.`chemicals` (idchemical)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

DELIMITER //


CREATE TRIGGER copy_history AFTER UPDATE ON STRUCTURE

  FOR EACH ROW BEGIN
    INSERT INTO HISTORY (idstructure,structure,format,updated)
        SELECT idstructure,structure,format,updated FROM structure WHERE structure.idstructure = OLD.idstructure;

  END//

DELIMITER ;

-- -----------------------------------------------------
-- Table `ambit_repository`.`field_names`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`field_names` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`field_names` (
  `idfieldname` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(256) NOT NULL DEFAULT "NAME" ,
  `unique` BOOLEAN NOT NULL DEFAULT false ,
  PRIMARY KEY (`idfieldname`) );


-- -----------------------------------------------------
-- Table `ambit_repository`.`structure_fields`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`structure_fields` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`structure_fields` (
  `idstructure` INT NOT NULL ,
  `idfieldname` INT NOT NULL ,
  `value` VARCHAR(256) NULL ,
  PRIMARY KEY (`idfieldname`) ,
  INDEX (`idstructure`),
  CONSTRAINT `fk_structure`
    FOREIGN KEY (`idstructure` )
    REFERENCES `ambit_repository`.`structures` (idstructure)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_field`
    FOREIGN KEY (`idfieldname` )
    REFERENCES `ambit_repository`.`fieldnames` (idfieldname)
    ON DELETE CASCADE
    ON UPDATE CASCADE
  );

CREATE INDEX idchemical ON `ambit_repository`.`structure_fields` (`idstructure` ASC) ;

CREATE INDEX fk_structure ON `ambit_repository`.`structure_fields` (`idstructure` ASC) ;

CREATE INDEX fk_field ON `ambit_repository`.`structure_fields` (`idfieldname` ASC) ;


-- -----------------------------------------------------
-- Table `ambit_repository`.`history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`history` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`history` (
  `version` INT NOT NULL AUTO_INCREMENT ,
  `idstructure` INT NOT NULL ,
  `structure` BLOB NOT NULL ,
  `format` VARCHAR(16) NOT NULL DEFAULT "CML" ,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`version`) ,
  CONSTRAINT `f_idstructure`
    FOREIGN KEY (`idstructure` )
    REFERENCES `ambit_repository`.`structures` (`idstructure`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE INDEX f_idstructure ON `ambit_repository`.`history` (`idstructure` ASC) ;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

insert into chemicals (inchi,formula) values ("inchi","formula");
insert into structure (idchemical,structure,format) values (1,"structure","my");