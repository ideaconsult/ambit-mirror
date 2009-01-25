SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP DATABASE IF EXISTS `ambit-test` ;
CREATE SCHEMA IF NOT EXISTS `ambit-test` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `ambit-test`;

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
DROP TABLE IF EXISTS `chemicals`;
CREATE TABLE  `chemicals` (
  `idchemical` int(11) unsigned NOT NULL auto_increment,
  `inchi` text character set latin1 collate latin1_bin,
  `smiles` text character set latin1 collate latin1_bin,
  `formula` varchar(64) default NULL,
  `hashcode` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`idchemical`),
  KEY `sinchi` (`inchi`(760)),
  KEY `ssmiles` (`smiles`(760)),
  KEY `idchemical` (`idchemical`),
  KEY `inchi` (`inchi`(767)),
  KEY `formula` (`formula`),
  KEY `hashcode` USING BTREE (`hashcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
-- Table `properties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `properties`;
CREATE TABLE  `properties` (
  `idproperty` int(10) unsigned NOT NULL auto_increment,
  `idreference` int(11) unsigned NOT NULL default '0',
  `name` varchar(128) collate utf8_bin NOT NULL default '',
  `units` varchar(16) collate utf8_bin NOT NULL default '',
  `comments` varchar(128) collate utf8_bin NOT NULL default '',
  `islocal` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idproperty`),
  UNIQUE KEY `ddictionary_name` USING BTREE (`name`,`idreference`),
  KEY `ddictionary_idref` (`idreference`),
  CONSTRAINT `FK_properties_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_string` string values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_string`;
CREATE TABLE  `property_string` (
  `idvalue` int(10) unsigned NOT NULL auto_increment,
  `idtype` int(10) unsigned NOT NULL default '0',
  `value` varchar(200) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idvalue`),
  UNIQUE KEY `Index_3` (`value`),
  KEY `Index_2` (`idvalue`,`idtype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_number` numeric values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_number`;
CREATE TABLE  `property_number` (
  `idvalue` int(10) unsigned NOT NULL auto_increment,
  `idtype` int(10) unsigned NOT NULL default '1',
  `value` float(10,4) NOT NULL,
  PRIMARY KEY  (`idvalue`),
  UNIQUE KEY `Index_3` (`value`),
  KEY `Index_2` (`idvalue`,`idtype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `template` defines tuples
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE  `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_bin default NULL,
  PRIMARY KEY  (`idtemplate`),
  KEY `template_list_index4157` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `template_def` defines tuples
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template_def`;
CREATE TABLE  `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `order` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idtemplate`,`idproperty`),
  CONSTRAINT `FK_template_def_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `tuples` for non-scalar values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tuples`;
CREATE TABLE  `tuples` (
  `idtuple` int(10) unsigned NOT NULL auto_increment,
  `id_srcdataset` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`idtuple`),
  KEY `FK_tuples_1` (`id_srcdataset`),
  CONSTRAINT `FK_tuples_1` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_values` all values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_values`;
CREATE TABLE  `property_values` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idproperty` int(10) unsigned NOT NULL,
  `idstructure` int(10) unsigned NOT NULL,
  `idvalue` int(10) unsigned NOT NULL,
  `idtype` int(10) unsigned NOT NULL default '1',
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `status` enum('OK','ERROR') collate utf8_bin NOT NULL default 'OK',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `Index_2` (`idproperty`,`idstructure`,`idvalue`,`idtype`),
  KEY `FK_property_values_1` (`user_name`),
  KEY `FK_property_values_2` (`idstructure`),
  CONSTRAINT `FK_property_values_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_tuples` tuples of properties
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_tuples`;
CREATE TABLE  `property_tuples` (
  `idtuple` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`idtuple`,`id`),
  KEY `FK_property_tuples_2` (`id`),
  CONSTRAINT `FK_property_tuples_2` FOREIGN KEY (`id`) REFERENCES `property_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_tuple_1` FOREIGN KEY (`idtuple`) REFERENCES `tuples` (`idtuple`) ON DELETE CASCADE ON UPDATE CASCADE
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

-- -----------------------------------------------------
-- Table `src_dataset` datasets
-- -----------------------------------------------------
DROP TABLE IF EXISTS `src_dataset`;
CREATE TABLE  `src_dataset` (
  `id_srcdataset` int(11) unsigned NOT NULL auto_increment,
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

-- -----------------------------------------------------
-- Table `struc_dataset` structures per dataset
-- -----------------------------------------------------
DROP TABLE IF EXISTS `struc_dataset`;
CREATE TABLE IF NOT EXISTS  `struc_dataset` (
  `idstructure` int unsigned  NOT NULL ,
  `id_srcdataset` int unsigned NOT NULL ,
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
  KEY `Index_4` USING BTREE (`idquery`,`metric`),
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
insert into version (idmajor,idminor,comment) values (2,1,"AMBIT2 schema");

-- -----------------------------------------------------
-- numeric property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_number`;
create view `values_number` as
SELECT id,idproperty,idstructure,value,idvalue,status,user_name,idtype
FROM property_values join property_number using(idvalue,idtype);

-- -----------------------------------------------------
-- string property values
-- -----------------------------------------------------

DROP VIEW IF EXISTS `values_string`;
create view `values_string` as
SELECT id,idproperty,idstructure,value,idvalue,status,user_name,idtype
FROM property_values join property_string using(idvalue,idtype);

-- -----------------------------------------------------
-- templates
-- -----------------------------------------------------
create view `template_view` as
SELECT idtemplate,template.name as tname,idproperty,properties.name,idreference,units,comments 
FROM template join template_def using(idtemplate) join properties using(idproperty) 
order by template.idtemplate

-- -----------------------------------------------------
-- default users (guest, admin)
-- -----------------------------------------------------

-- insert into roles (role_name) values ("ambit_guest");
-- insert into roles (role_name) values ("ambit_admin");
-- insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("guest","084e0343a0486ff05530df6c705c8bb4","guest","Default guest user",now(),"confirmed","guest","http://ambit.acad.bg");
-- insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("admin","21232f297a57a5a743894a0e4a801fc3","admin","Default admin user",now(),"confirmed","admin","http://ambit.acad.bg");
-- insert into user_roles (user_name,role_name) values ("guest","ambit_guest");
-- insert into user_roles (user_name,role_name) values ("admin","ambit_admin");

-- -----------------------------------------------------
-- default dictionary entries
-- -----------------------------------------------------
insert into catalog_references (idreference,title,url) values (1,"CAS Registry Number","http://www.cas.org");
insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"CAS",1);

insert into catalog_references (idreference,title,url) values (2,"IUPAC name","http://www.iupac.org");
insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"IUPAC Name",2);


-- Grants for ambit-test@localhost
-- REVOKE ALL PRIVILEGES ON `ambit-test`.* FROM 'admin'@'localhost';
-- REVOKE ALL PRIVILEGES ON `ambit-test`.* FROM 'guest'@'localhost';
GRANT USAGE ON `ambit-test`.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
GRANT ALL PRIVILEGES ON `ambit-test`.* TO 'admin'@'localhost' WITH GRANT OPTION;
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `ambit-test`.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;