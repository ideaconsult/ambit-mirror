SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP DATABASE IF EXISTS ambit2 ;
CREATE SCHEMA IF NOT EXISTS `ambit2` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `ambit2`;

-- -----------------------------------------------------
-- Table `roles`  User roles
-- -----------------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE  `roles` (
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `users` Users
-- -----------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE  `users` (
  `user_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  `password` varchar(45) character set utf8 collate utf8_bin NOT NULL,
  `email` varchar(45) NOT NULL,
  `registration_status` enum('commenced','confirmed','deleted') NOT NULL default 'commenced',
  `registration_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `registration_id` blob,
  `title` varchar(6) character set utf8 collate utf8_bin NOT NULL default '""',
  `firstname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `lastname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `address` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `country` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `webpage` varchar(255) character set utf8 collate utf8_bin NOT NULL default '""',
  `affiliation` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `keywords` varchar(128) default '""',
  `reviewer` tinyint(1) NOT NULL default '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `roles` Roles assigned to users
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE  `user_roles` (
  `user_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`user_name`,`role_name`),
  KEY `FK_user_roles_2` (`role_name`),
  CONSTRAINT `FK_user_roles_2` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_roles_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `references`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `catalog_references`;
CREATE TABLE  `catalog_references` (
  `idreference` int(11) unsigned NOT NULL auto_increment,
  `title` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `url` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idreference`),
  UNIQUE KEY `Index_2` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `chemicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chemicals` ;

CREATE  TABLE IF NOT EXISTS `chemicals` (
  `idchemical` int(11) unsigned NOT NULL  AUTO_INCREMENT ,
  `inchi` text character set latin1 collate latin1_bin,
  `smiles` text character set latin1 collate latin1_bin,
  `formula` VARCHAR(64) NULL ,
  PRIMARY KEY (`idchemical`),
  KEY `sinchi` (`inchi`(760)),
  KEY `ssmiles` (`smiles`(760))
  );

CREATE INDEX idchemical ON `chemicals` (idchemical) ;

CREATE INDEX inchi ON `chemicals` (inchi(1024)) ;

CREATE INDEX formula ON `chemicals` (`formula` ASC) ;


-- -----------------------------------------------------
-- Table `structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `structure` ;

CREATE TABLE  `structure` (
  `idstructure` int(11) unsigned NOT NULL auto_increment,
  `idchemical` int(11) unsigned NOT NULL,
  `structure` blob NOT NULL,
  `format` varchar(16) collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`idstructure`),
  KEY `idchemical` (`idchemical`),
  KEY `FK_structure_2` (`user_name`),
  CONSTRAINT `FK_structure_2` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
   

DELIMITER //


CREATE TRIGGER copy_history BEFORE UPDATE ON STRUCTURE
  FOR EACH ROW BEGIN
   INSERT INTO HISTORY (idstructure,structure,format,updated,user_name)
        SELECT idstructure,structure,format,updated,user_name FROM structure
        WHERE structure.idstructure = OLD.idstructure;

  END//

DELIMITER ;

-- -----------------------------------------------------
-- Table `dictionary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE  `dictionary` (
  `iddictionary` int(11) unsigned NOT NULL auto_increment,
  `idparent` int(11) unsigned NOT NULL,
  `name` varchar(128) collate utf8_bin NOT NULL,
  `idreference` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`iddictionary`),
  KEY `Index_2` (`name`),
  KEY `Index_3` (`idparent`),
  KEY `FK_dictionary_1` (`idreference`),
  CONSTRAINT `FK_dictionary_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `field_names`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `field_names`;

CREATE TABLE  `field_names` (
  `idfieldname` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(128) collate utf8_bin NOT NULL default 'NAME',
  `iddictionary` int(10) unsigned default NULL,
  PRIMARY KEY  (`idfieldname`),
  UNIQUE KEY `Index_2` (`name`),
  KEY `FK_field_names_1` (`iddictionary`),
  CONSTRAINT `FK_field_names_1` FOREIGN KEY (`iddictionary`) REFERENCES `dictionary` (`iddictionary`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `structure_fields`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `structure_fields` ;

DROP TABLE IF EXISTS `structure_fields`;
CREATE TABLE  `structure_fields` (
  `idstructure` int(11) unsigned NOT NULL,
  `idfieldname` int(11) unsigned NOT NULL,
  `value` varchar(256) collate utf8_bin default NULL,
  PRIMARY KEY  (`idfieldname`,`idstructure`),
  KEY `idstructure` (`idstructure`),
  KEY `fk_structure` (`idstructure`),
  KEY `fk_field` (`idfieldname`),
  CONSTRAINT `fk_field3` FOREIGN KEY (`idfieldname`) REFERENCES `field_names` (`idfieldname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_structure3` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- -----------------------------------------------------
-- Table `descriptors`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `descriptors`;
CREATE TABLE  `descriptors` (
  `iddescriptor` int(11) unsigned NOT NULL auto_increment,
  `idreference` int(11) unsigned NOT NULL default '0',
  `name` varchar(128) collate utf8_bin NOT NULL default '',
  `units` varchar(16) collate utf8_bin NOT NULL default '',
  `error` float(10,4) NOT NULL default '0.0000',
  `comments` varchar(128) collate utf8_bin NOT NULL default '',
  `islocal` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`),
  UNIQUE KEY `ddictionary_name` (`name`),
  KEY `ddictionary_idref` (`idreference`),
  CONSTRAINT `ddictionary_ibfk_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `dvalues`  Descriptor values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dvalues`;
CREATE TABLE  `dvalues` (
  `iddescriptor` int(11) unsigned NOT NULL default '0',
  `idstructure` int(11) unsigned NOT NULL default '0',
  `value` float(10,4) default '0.0000',
  `error` float(10,4) default '0.0000',
  `status` enum('OK','ERROR') collate utf8_bin NOT NULL default 'OK',
  PRIMARY KEY  (`iddescriptor`,`idstructure`),
  KEY `dvalues_struc` (`idstructure`),
  KEY `dvalues_value` (`value`),
  CONSTRAINT `dvalues_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `descriptors` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `dvalues_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `history`;

CREATE TABLE  `history` (
  `version` int(11) NOT NULL auto_increment,
  `idstructure` int(11) unsigned NOT NULL,
  `structure` blob NOT NULL,
  `format` varchar(16) collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`version`),
  KEY `idstructure` (`idstructure`),
  KEY `f_idstructure` (`idstructure`),
  KEY `FK_history_1` (`user_name`),
  CONSTRAINT `FK_history_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DROP TABLE IF EXISTS `src_dataset`;

CREATE TABLE  `src_dataset` (
  `id_srcdataset` int(11) NOT NULL auto_increment,
  `name` varchar(255) collate utf8_bin NOT NULL default 'default',
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`),
  KEY `FK_src_dataset_1` (`user_name`),
  CONSTRAINT `FK_src_dataset_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `struc_dataset`;


CREATE TABLE IF NOT EXISTS  `struc_dataset` (
  `idstructure` int unsigned  NOT NULL ,
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


-- -----------------------------------------------------
-- Table `sessions` User sessions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE  `sessions` (
  `idsessions` int(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `started` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `completed` timestamp,
  PRIMARY KEY  (`idsessions`),
  KEY `FK_sessions_1` (`user_name`),
  CONSTRAINT `FK_sessions_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `query` User queries per session
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query`;
CREATE TABLE  `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `idsessions` int(10) unsigned NOT NULL,
  `name` varchar(45) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idquery`),
  KEY `FK_query_1` (`idsessions`),
  KEY `Index_3` (`name`),
  CONSTRAINT `FK_query_1` FOREIGN KEY (`idsessions`) REFERENCES `sessions` (`idsessions`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Structure queries';

-- -----------------------------------------------------
-- Table `query_results` Results of a user query
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query_results`;
CREATE TABLE  `query_results` (
  `idquery` int(10) unsigned NOT NULL,
  `idstructure` int(11) unsigned NOT NULL,
  `selected` tinyint(1) NOT NULL,
  PRIMARY KEY  (`idquery`,`idstructure`),
  KEY `FK_query_results_2` (`idstructure`),
  CONSTRAINT `FK_query_results_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_1` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `funcgroups` Functional groups
-- -----------------------------------------------------
DROP TABLE IF EXISTS `funcgroups`;
CREATE TABLE  `funcgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `name` varchar(45) collate utf8_bin NOT NULL,
  `smarts` blob NOT NULL,
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`idfuncgroup`),
  UNIQUE KEY `Index_2` (`name`),
  KEY `FK_funcgroups_1` (`user_name`),
  CONSTRAINT `FK_funcgroups_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `struc_fgroups` Functional groups per structure
-- -----------------------------------------------------
DROP TABLE IF EXISTS `struc_fgroups`;
CREATE TABLE  `struc_fgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `idstructure` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idfuncgroup`,`idstructure`),
  KEY `FK_struc_fgroups_2` (`idstructure`),
  CONSTRAINT `FK_struc_fgroups_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_struc_fgroups_1` FOREIGN KEY (`idfuncgroup`) REFERENCES `funcgroups` (`idfuncgroup`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `fp1024` 1024 length hashed fingerprints
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fp1024`;
CREATE TABLE  `fp1024` (
  `idchemical` int(10) unsigned NOT NULL default '0',
  `fp1` bigint(20) unsigned NOT NULL default '0',
  `fp2` bigint(20) unsigned NOT NULL default '0',
  `fp3` bigint(20) unsigned NOT NULL default '0',
  `fp4` bigint(20) unsigned NOT NULL default '0',
  `fp5` bigint(20) unsigned NOT NULL default '0',
  `fp6` bigint(20) unsigned NOT NULL default '0',
  `fp7` bigint(20) unsigned NOT NULL default '0',
  `fp8` bigint(20) unsigned NOT NULL default '0',
  `fp9` bigint(20) unsigned NOT NULL default '0',
  `fp10` bigint(20) unsigned NOT NULL default '0',
  `fp11` bigint(20) unsigned NOT NULL default '0',
  `fp12` bigint(20) unsigned NOT NULL default '0',
  `fp13` bigint(20) unsigned NOT NULL default '0',
  `fp14` bigint(20) unsigned NOT NULL default '0',
  `fp15` bigint(20) unsigned NOT NULL default '0',
  `fp16` bigint(20) unsigned NOT NULL default '0',
  `time` int(10) unsigned default '0',
  `bc` int(6) NOT NULL default '0',
  `status` enum('invalid','valid','error') collate utf8_bin NOT NULL default 'invalid',
  PRIMARY KEY  (`idchemical`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `fp1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `atom_distance`  Distance between atoms (discretized)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atom_distance`;
CREATE TABLE  `atom_distance` (
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  `atom1` varchar(2) collate utf8_bin NOT NULL default 'C',
  `atom2` varchar(2) collate utf8_bin NOT NULL default 'C',
  `distance` int(10) NOT NULL default '0',
  PRIMARY KEY  (`iddistance`),
  UNIQUE KEY `atom1` (`atom1`,`atom2`,`distance`),
  KEY `distance` (`distance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `atom_structure`  Distance between atoms (reference to atom_distance)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atom_structure`;
CREATE TABLE  `atom_structure` (
  `idstructure` int(11) unsigned NOT NULL default '0',
  `iddistance` int(11) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`iddistance`,`idstructure`),
  KEY `adistance` (`idstructure`),
  CONSTRAINT `atom_distance_fk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `atom_distance_fk_2` FOREIGN KEY (`iddistance`) REFERENCES `atom_distance` (`iddistance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `version` Version
-- -----------------------------------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE  `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,  
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `comment` varchar(45),
  PRIMARY KEY  (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
insert into version (idmajor,idminor,comment) values (2,0,"AMBIT2 schema");

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;