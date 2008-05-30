SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

SET @dbname:='ambit2';

DROP DATABASE IF EXISTS `ambit2` ;
CREATE SCHEMA IF NOT EXISTS `ambit2` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE ambit2;

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
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `format` enum('SDF','CML') collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  `type_structure` enum('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental') collate utf8_bin NOT NULL default 'NA',
  PRIMARY KEY  (`idstructure`),
  KEY `FK_structure_2` (`user_name`),
  KEY `idchemical` USING BTREE (`idchemical`),
  CONSTRAINT `fk_idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_structure_2` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100205 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DELIMITER $
CREATE TRIGGER copy_history BEFORE UPDATE ON STRUCTURE
  FOR EACH ROW BEGIN
   INSERT INTO HISTORY (idstructure,structure,format,updated,user_name,type_structure)
        SELECT idstructure,structure,format,updated,user_name,type_structure FROM structure
        WHERE structure.idstructure = OLD.idstructure;
  END $
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
  UNIQUE KEY `ddictionary_name` USING BTREE (`name`,`idreference`),
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
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`iddescriptor`,`idstructure`),
  KEY `dvalues_struc` (`idstructure`),
  KEY `dvalues_value` (`value`),
  KEY `FK_dvalues_3` (`user_name`),
  CONSTRAINT `FK_dvalues_3` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`),
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
  `format` enum('SDF','CML') collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  `type_structure` enum('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental') collate utf8_bin NOT NULL default 'NA',
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
  `idreference` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`),
  KEY `FK_src_dataset_1` (`user_name`),
  KEY `FK_src_dataset_2` (`idreference`),
  CONSTRAINT `FK_src_dataset_2` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE,
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
  `content` blob NOT NULL,
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
  `idchemical` int(10) unsigned NOT NULL,  
  `idstructure` int(11) unsigned NOT NULL,
  `selected` tinyint(1) NOT NULL default '1',
  `metric` float(10,6) NOT NULL default '1.000000',
  PRIMARY KEY  (`idquery`,`idchemical`,`idstructure`),
  KEY `FK_query_results_2` (`idstructure`),
  KEY `FK_query_results_3` (`idchemical`),
  CONSTRAINT `FK_query_results_1` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_3` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
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
  `idchemical` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idfuncgroup`,`idchemical`),
  KEY `FK_struc_fgroups_1` (`idchemical`),
  CONSTRAINT `FK_struc_fgroups_2` FOREIGN KEY (`idfuncgroup`) REFERENCES `funcgroups` (`idfuncgroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_struc_fgroups_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
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


-- ------------------------------------------------------------
-- template list
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE template (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_bin default NULL,
  `template` blob NOT NULL,
  PRIMARY KEY  (`idtemplate`),
  KEY `template_list_index4157` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ------------------------------------------------------------
-- Template study_fieldnames - fields defined per study
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `study_fieldnames`;
CREATE TABLE study_fieldnames (
  `id_fieldname` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_bin default NULL,
  `units` varchar(16) collate utf8_bin default NULL,
  `fieldtype` enum('string','numeric') collate utf8_bin default 'string',
  `fieldmode` enum('result','condition') collate utf8_bin default 'condition',
  PRIMARY KEY  (`id_fieldname`),
  UNIQUE KEY `study_fieldnames_index4255` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ------------------------------------------------------------
-- Template definition - field id and template id
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `template_def`;
CREATE TABLE  `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idtemplate`,`id_fieldname`),
  KEY `template_def_index4168` (`id_fieldname`),
  CONSTRAINT `template_def_ibfk_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `template_def_ibfk_2` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `study` Study definition
-- -----------------------------------------------------
DROP TABLE IF EXISTS `study`;
CREATE TABLE  `study` (
  `idstudy` int(10) unsigned NOT NULL auto_increment,
  `idtemplate` int(10) unsigned NOT NULL,
  `name` varchar(64) collate utf8_bin default NULL,
  PRIMARY KEY  (`idstudy`),
  KEY `study_index4290` (`name`),
  KEY `idtemplate` (`idtemplate`),
  CONSTRAINT `study_ibfk_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `experiment` Experimental data
-- -----------------------------------------------------
DROP TABLE IF EXISTS `experiment`;
CREATE TABLE  `experiment` (
  `idexperiment` int(10) unsigned NOT NULL auto_increment,
  `idstudy` int(10) unsigned NOT NULL,
  `idreference` int(11) unsigned NOT NULL,
  `idstructure` int(11) unsigned NOT NULL,
  `structure_type` enum('tested','parent','metabolite') collate utf8_bin NOT NULL default 'tested',
  `idstructure_parent` int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idexperiment`),
  UNIQUE KEY `idstudy` (`idstudy`,`idreference`,`idstructure`),
  KEY `experiment_index4302` (`idstructure_parent`),
  KEY `idstructure` (`idstructure`),
  KEY `experiment_ibfk_2` (`idreference`),
  CONSTRAINT `experiment_ibfk_2` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE,
  CONSTRAINT `experiment_ibfk_3` FOREIGN KEY (`idstudy`) REFERENCES `study` (`idstudy`) ON UPDATE CASCADE,
  CONSTRAINT `experiment_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `study_conditions` Pre-defined conditions of a study (test setup)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `study_conditions`;
CREATE TABLE study_conditions (
  `idstudy` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  `value` varchar(64) collate utf8_bin default NULL,
  PRIMARY KEY  (`idstudy`,`id_fieldname`),
  KEY `study_conditions_index4299` (`value`),
  KEY `study_conditions_index4353` (`id_fieldname`),
  CONSTRAINT `study_conditions_ibfk_1` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `study_conditions_ibfk_2` FOREIGN KEY (`idstudy`) REFERENCES `study` (`idstudy`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--fieldnames

-- -----------------------------------------------------
-- Table `study_results` Results of a study
-- -----------------------------------------------------
DROP TABLE IF EXISTS `study_results`;
CREATE TABLE study_results (
  `idexperiment` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  `value` varchar(64) collate utf8_bin default NULL,
  `value_num` float(10,4) default NULL,
  PRIMARY KEY  (`idexperiment`,`id_fieldname`),
  KEY `study_results_index4205` (`value`),
  KEY `study_results_index4206` (`value_num`),
  KEY `study_results_index4355` (`id_fieldname`),
  CONSTRAINT `study_results_ibfk_1` FOREIGN KEY (`idexperiment`) REFERENCES `experiment` (`idexperiment`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `study_results_ibfk_2` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON UPDATE CASCADE
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

-- -----------------------------------------------------
-- default users (guest, admin)
-- -----------------------------------------------------

insert into roles (role_name) values ("ambit_guest");
insert into roles (role_name) values ("ambit_admin");
insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("guest","084e0343a0486ff05530df6c705c8bb4","guest","Default guest user",now(),"confirmed","guest","http://ambit.acad.bg");
insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("admin","21232f297a57a5a743894a0e4a801fc3","admin","Default admin user",now(),"confirmed","admin","http://ambit.acad.bg");
insert into user_roles (user_name,role_name) values ("guest","ambit_guest");
insert into user_roles (user_name,role_name) values ("admin","ambit_admin");

-- -----------------------------------------------------
-- default dictionary entries
-- -----------------------------------------------------
insert into catalog_references (idreference,title,url) values (1,"CAS Registry Number","http://www.cas.org");
insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"CAS",1);

insert into catalog_references (idreference,title,url) values (2,"IUPAC name","http://www.iupac.org");
insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"IUPAC Name",2);

-- Grants for ambit2@localhost
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'admin'@'localhost';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'guest'@'localhost';
GRANT USAGE ON ambit2.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
GRANT ALL PRIVILEGES ON ambit2.* TO 'admin'@'localhost' WITH GRANT OPTION;
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON ambit2.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;