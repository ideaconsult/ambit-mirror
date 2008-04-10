SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP DATABASE IF EXISTS ambit_repository ;
CREATE SCHEMA IF NOT EXISTS `ambit_repository` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `ambit_repository`;


-- -----------------------------------------------------
-- Table `ambit_repository`.`chemicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`chemicals` ;

CREATE  TABLE IF NOT EXISTS `ambit_repository`.`chemicals` (
  `idchemical` INT NOT NULL  AUTO_INCREMENT ,
  `inchi` text character set latin1 collate latin1_bin,
  `smiles` text character set latin1 collate latin1_bin,
  `formula` VARCHAR(64) NULL ,
  PRIMARY KEY (`idchemical`),
  KEY `sinchi` (`inchi`(760)),
  KEY `ssmiles` (`smiles`(760))
  );

CREATE INDEX idchemical ON `ambit_repository`.`chemicals` (idchemical) ;

CREATE INDEX inchi ON `ambit_repository`.`chemicals` (inchi(1024)) ;

CREATE INDEX formula ON `ambit_repository`.`chemicals` (`formula` ASC) ;


-- -----------------------------------------------------
-- Table `ambit_repository`.`structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ambit_repository`.`structure` ;

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


CREATE TRIGGER copy_history BEFORE UPDATE ON STRUCTURE
  FOR EACH ROW BEGIN
   INSERT INTO HISTORY (idstructure,structure,format,updated)
        SELECT idstructure,structure,format,updated FROM structure
        WHERE structure.idstructure = OLD.idstructure;

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

DROP TABLE IF EXISTS `ambit_repository`.`structure_fields`;
CREATE TABLE  `ambit_repository`.`structure_fields` (
  `idstructure` int(11) NOT NULL,
  `idfieldname` int(11) NOT NULL,
  `value` varchar(256) collate utf8_bin default NULL,
  PRIMARY KEY  USING BTREE (`idfieldname`,`idstructure`),
  KEY `idstructure` (`idstructure`),
  KEY `fk_structure` (`idstructure`),
  KEY `fk_field` (`idfieldname`),
  CONSTRAINT `fk_field` FOREIGN KEY (`idfieldname`) REFERENCES `field_names` (`idfieldname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_structure` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
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
  KEY (`idstructure`)
)
;

CREATE INDEX f_idstructure ON `ambit_repository`.`history` (`idstructure` ASC) ;

DROP TABLE IF EXISTS `ambit_repository`.`src_dataset`;


CREATE TABLE IF NOT EXISTS  `ambit_repository`.`src_dataset` (
  `id_srcdataset` int  NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default 'default',
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`)
) ;


DROP TABLE IF EXISTS `ambit_repository`.`struc_dataset`;


CREATE TABLE IF NOT EXISTS  `ambit_repository`.`struc_dataset` (
  `idstructure` int  NOT NULL ,
  `id_srcdataset` int  NOT NULL ,
  PRIMARY KEY  (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1`
    FOREIGN KEY (`idstructure`)
    REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2`
    FOREIGN KEY (`id_srcdataset`)
    REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
