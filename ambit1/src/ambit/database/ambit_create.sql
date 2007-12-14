CREATE TABLE `ambituser` (
  `idambituser` int(10) unsigned NOT NULL auto_increment,
  `mysqluser` varchar(16) NOT NULL default '',
  `mysqlhost` varchar(60) NOT NULL default '%',
  `title` varchar(6) NOT NULL default '',
  `firstname` varchar(128) NOT NULL default '',
  `lastname` varchar(128) NOT NULL default '',
  `email` varchar(128) NOT NULL default '',
  `password` varchar(128) NOT NULL default '',
  `webpage` varchar(255) NOT NULL default '',
  `affiliation` varchar(128) NOT NULL default '',
  `address` varchar(255) NOT NULL default '',
  `city` varchar(128) NOT NULL default '',
  `country` varchar(128) NOT NULL default '',
  `usertype` enum('admin','guest','pro') NOT NULL default 'guest',
  `regstatus` enum('commenced','verification sent','verified') NOT NULL default 'commenced',
  `tregistered` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`idambituser`),
  UNIQUE KEY `ambituser_userhost` (`mysqluser`,`mysqlhost`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into ambituser (mysqluser,mysqlhost,usertype,regstatus) values ("root","localhost","admin","commenced");


--stype
CREATE TABLE `stype` (
  `stype_id` int(5) unsigned NOT NULL auto_increment,
  `substance_type` varchar(16) NOT NULL default '',
  PRIMARY KEY  (`stype_id`),
  UNIQUE KEY `stype_subst` (`substance_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert ignore into stype (substance_type)  values ("organic");
insert ignore into stype (substance_type)  values ("inorganic");
insert ignore into stype (substance_type)  values ("organometalic");
insert ignore into stype (substance_type)  values ("mixture/unknown");

--substance
CREATE TABLE `substance` (
  `idsubstance` int(10) unsigned NOT NULL auto_increment,
  `stype_id` int(5) unsigned NOT NULL default '1',
  `formula` varchar(64) character set latin1 collate latin1_bin NOT NULL default '',
  `molweight` float(8,3) NOT NULL default '0.000',
  `smiles` text character set latin1 collate latin1_bin,
  `time_elapsed` int(11) unsigned default NULL,
  `usmiles` tinyint(1) unsigned default NULL,
  PRIMARY KEY  (`idsubstance`),
  KEY `substance_formula` (`formula`),
  KEY `ssmiles` (`smiles`(760)),
  KEY `substance_stype` (`stype_id`),
  KEY `time_elapsed` (`time_elapsed`),
  CONSTRAINT `substance_ibfk_1` FOREIGN KEY (`stype_id`) REFERENCES `stype` (`stype_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--structure
CREATE TABLE `structure` (
  `idstructure` int(10) unsigned NOT NULL auto_increment,
  `idsubstance` int(10) unsigned NOT NULL default '0',
  `structure` blob NOT NULL,
  `type_structure` enum('SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental') NOT NULL default '2D with H',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `energy` float(8,4) NOT NULL default '0.0000',
  `remark` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idstructure`),
  KEY `structure_substance` (`idsubstance`),
  KEY `structure_type` (`type_structure`),
  CONSTRAINT `structure_ibfk_1` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--src_dataset
CREATE TABLE `src_dataset` (
  `id_srcdataset` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default 'default',
  `idref` int(10) unsigned default NULL,
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--struc_dataset
CREATE TABLE `struc_dataset` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `id_srcdataset` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--struc_user
CREATE TABLE `struc_user` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`idambituser`),
  KEY `idambituser` (`idambituser`),
  CONSTRAINT `struc_user_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--fp1024
CREATE TABLE `fp1024` (
  `idsubstance` int(10) unsigned NOT NULL default '0',
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
  `status` int(2) NOT NULL default '0',
  PRIMARY KEY  (`idsubstance`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `fp1024_ibfk_1` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--fpae
CREATE TABLE `fpae` (
  `idfpae` int(10) unsigned NOT NULL auto_increment,
  `atom` varchar(16) NOT NULL default '',
  `ae` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idfpae`),
  UNIQUE KEY `ae` (`ae`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--fpaeid
CREATE TABLE `fpaeid` (
  `idsubstance` int(10) unsigned NOT NULL default '0',
  `idfpae` int(10) unsigned NOT NULL default '0',
  `freq` int(10) default '1',
  `level` int(3) default '3',
  `time_elapsed` int(11) unsigned default NULL,
  `status` enum('valid','invalid','error') collate latin1_bin NOT NULL default 'valid',
  PRIMARY KEY  (`idsubstance`,`idfpae`),
  KEY `fpae` (`idfpae`),
  KEY `time` (`time_elapsed`),
  KEY `freq_index` (`freq`),
  KEY `freq` (`idsubstance`,`freq`),
  CONSTRAINT `fpae_1` FOREIGN KEY (`idfpae`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `substance_1` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;


--alias
CREATE TABLE `alias` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `alias` varchar(255) NOT NULL default 'NA',
  `alias_type` varchar(16) NOT NULL default 'ID',
  PRIMARY KEY  (`idstructure`,`alias`,`alias_type`),
  KEY `alias_type` (`alias_type`),
  KEY `alias_name` (`alias`),
  CONSTRAINT `alias_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--cas
CREATE TABLE `cas` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `casno` char(11) NOT NULL default '',
  `isregistry` tinyint(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`casno`),
  KEY `cas_casno` (`casno`),
  CONSTRAINT `cas_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--name
CREATE TABLE `name` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idstructure`,`name`),
  KEY `name_name` (`name`),
  CONSTRAINT `name_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- literature tables
--literature author
CREATE TABLE `author` (
  `idauthor` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idauthor`),
  UNIQUE KEY `Authors_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--literature journal
CREATE TABLE `journal` (
  `idjournal` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `publisher` varchar(128) default NULL,
  `city` varchar(64) default NULL,
  `abbreviation` varchar(64) default NULL,
  PRIMARY KEY  (`idjournal`),
  UNIQUE KEY `journal_name` (`name`),
  UNIQUE KEY `journal_abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--literature
CREATE TABLE `literature` (
  `idref` int(10) unsigned NOT NULL auto_increment,
  `reference` varchar(255) default NULL,
  `url` text NOT NULL,
  `volume` varchar(32) default NULL,
  `pages` varchar(32) default NULL,
  `year_pub` int(10) unsigned default NULL,
  `idjournal` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idref`),
  UNIQUE KEY `literature_all` (`reference`,`idjournal`,`volume`,`pages`,`year_pub`),
  KEY `journal` (`idjournal`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--literature ref_authors
CREATE TABLE `ref_authors` (
  `idref` int(10) unsigned NOT NULL default '0',
  `idauthor` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idref`,`idauthor`),
  KEY `idauthor` (`idauthor`),
  CONSTRAINT `ref_authors_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ref_authors_ibfk_2` FOREIGN KEY (`idauthor`) REFERENCES `author` (`idauthor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--descriptors ddictionary
CREATE TABLE `ddictionary` (
  `iddescriptor` int(10) unsigned NOT NULL auto_increment,
  `idref` int(10) unsigned NOT NULL default '0',
  `name` varchar(128) NOT NULL default '',
  `units` varchar(16) NOT NULL default '',
  `error` float(10,4) NOT NULL default '0.0000',
  `comments` varchar(128) NOT NULL default '',
  `islocal` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`),
  UNIQUE KEY `ddictionary_name` (`name`),
  KEY `ddictionary_idref` (`idref`),
  CONSTRAINT `ddictionary_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--localdvalues
CREATE TABLE `localdvalues` (
  `iddescriptor` int(10) unsigned NOT NULL auto_increment,
  `idstructure` int(10) unsigned NOT NULL default '0',
  `localid` int(10) unsigned NOT NULL default '0',
  `value` float(10,4) default '0.0000',
  PRIMARY KEY  (`iddescriptor`),
  KEY `localdvalues_struc` (`idstructure`),
  CONSTRAINT `localdvalues_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `localdvalues_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE ON DELETE CASCADE 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--dvalues
CREATE TABLE `dvalues` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  `value` float(10,4) default '0.0000',
  `error` float(10,4) default '0.0000',
  `status` enum('OK','ERROR') NOT NULL default 'OK',
  PRIMARY KEY  (`iddescriptor`,`idstructure`),
  KEY `dvalues_struc` (`idstructure`),
  KEY `dvalues_value` (`value`),
  CONSTRAINT `dvalues_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON UPDATE CASCADE ON DELETE CASCADE ,
  CONSTRAINT `dvalues_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE ON DELETE CASCADE 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--dgroup
CREATE TABLE `dgroup` (
  `iddgroup` int(10) unsigned NOT NULL auto_increment,
  `groupname` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`iddgroup`),
  UNIQUE KEY `dgroup_name` (`groupname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--descrgroups
CREATE TABLE `descrgroups` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `iddgroup` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`,`iddgroup`),
  KEY `descrgroups1` (`iddgroup`),
  CONSTRAINT `descrgroups_ibfk_1` FOREIGN KEY (`iddgroup`) REFERENCES `dgroup` (`iddgroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `descrgroups_ibfk_2` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--dict_user
CREATE TABLE `dict_user` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`,`idambituser`),
  KEY `dict_user1` (`idambituser`),
  CONSTRAINT `dict_user_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `dict_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--dsname
CREATE TABLE `dsname` (
  `iddsname` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(32) default NULL,
  `idambituser` int(10) unsigned NOT NULL default '0',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`iddsname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--datasets
CREATE TABLE `datasets` (
  `iddsname` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddsname`,`idstructure`),
  KEY `datasets_struc` (`idstructure`),
  CONSTRAINT `datasets_ibfk_1` FOREIGN KEY (`iddsname`) REFERENCES `dsname` (`iddsname`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `datasets_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE ON DELETE CASCADE 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- qsar tables

-- ------------------------------------------------------------
-- template list
-- ------------------------------------------------------------

CREATE TABLE template (
  idtemplate INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) NULL,
  template blob NOT NULL,
  PRIMARY KEY(idtemplate),
  INDEX template_list_index4157(name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE study_fieldnames (
  id_fieldname INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) NULL,
  units VARCHAR(16) NULL,
  fieldtype ENUM("string","numeric") NULL default "string",
  fieldmode ENUM("result","condition") NULL default "condition",
  PRIMARY KEY(id_fieldname),
  UNIQUE INDEX study_fieldnames_index4255(name)
)
TYPE=InnoDB;

-- ------------------------------------------------------------
-- Template definition - field id and template id
-- ------------------------------------------------------------

CREATE TABLE template_def (
  idtemplate INTEGER UNSIGNED NOT NULL,
  id_fieldname INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY(idtemplate, id_fieldname),
  INDEX template_def_index4168(id_fieldname),
  FOREIGN KEY(idtemplate)
    REFERENCES template(idtemplate)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(id_fieldname)
    REFERENCES study_fieldnames(id_fieldname)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
TYPE=InnoDB;

--study
CREATE TABLE study (
  idstudy INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  idtemplate INTEGER UNSIGNED NOT NULL,
  name VARCHAR(64) NULL,
  PRIMARY KEY(idstudy),
  INDEX study_index4290(name),
  FOREIGN KEY(idtemplate)
    REFERENCES template(idtemplate)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
TYPE=InnoDB;


-- experimental data
CREATE TABLE experiment (
  idexperiment INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  idstudy INTEGER UNSIGNED NOT NULL,
  idref INTEGER(10) UNSIGNED NOT NULL,
  idstructure INTEGER(10) UNSIGNED NOT NULL,
  structure_type ENUM("tested","parent","metabolite") NOT NULL DEFAULT "tested",
  idstructure_parent INTEGER(10) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY(idexperiment),
  UNIQUE KEY(idstudy,idref,idstructure),
  INDEX experiment_index4302(idstructure_parent),
  FOREIGN KEY(idstructure)
    REFERENCES structure(idstructure)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(idref)
    REFERENCES literature(idref)
      ON DELETE NO ACTION
      ON UPDATE CASCADE,
  FOREIGN KEY(idstudy)
    REFERENCES study(idstudy)
      ON DELETE NO ACTION
      ON UPDATE CASCADE
)
TYPE=InnoDB;

--fields
CREATE TABLE hierarchy (
  idparent_field INTEGER UNSIGNED NOT NULL DEFAULT 0,
  id_fieldname INTEGER UNSIGNED NOT NULL,
  leaf BOOL NULL,
  PRIMARY KEY(idparent_field, id_fieldname),
  FOREIGN KEY(id_fieldname)
    REFERENCES study_fieldnames(id_fieldname)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
TYPE=InnoDB;


--conditions
CREATE TABLE study_conditions (
  idstudy INTEGER UNSIGNED NOT NULL,
  id_fieldname INTEGER UNSIGNED NOT NULL,
  value VARCHAR(64) NULL,
  PRIMARY KEY(idstudy, id_fieldname),
  INDEX study_conditions_index4299(value),
  INDEX study_conditions_index4353(id_fieldname),
  FOREIGN KEY(id_fieldname)
    REFERENCES study_fieldnames(id_fieldname)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
  FOREIGN KEY(idstudy)
    REFERENCES study(idstudy)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
TYPE=InnoDB;

--fieldnames

--study-results
CREATE TABLE study_results (
  idexperiment INTEGER UNSIGNED NOT NULL,
  id_fieldname INTEGER UNSIGNED NOT NULL,
  value VARCHAR(64) NULL default null,
  value_num float(10,4) NULL default null,
  PRIMARY KEY(idexperiment, id_fieldname),
  INDEX study_results_index4205(value),
  INDEX study_results_index4206(value_num),  
  INDEX study_results_index4355(id_fieldname),
  FOREIGN KEY(idexperiment)
    REFERENCES experiment(idexperiment)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(id_fieldname)
    REFERENCES study_fieldnames(id_fieldname)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
)
TYPE=InnoDB;



-- ------------------------------------------------------------
-- QSAR
-- ------------------------------------------------------------
CREATE TABLE `modeltype` (
  `idmodeltype` int(10) unsigned NOT NULL auto_increment,
  `modeltype` varchar(32) NOT NULL default '',
  PRIMARY KEY  (`idmodeltype`),
  UNIQUE KEY `modeltype_type` (`modeltype`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--qsars
CREATE TABLE `qsars` (
  `idqsar` int(10) unsigned NOT NULL auto_increment,
  `idmodeltype` int(10) unsigned NOT NULL default '0',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name` varchar(64) NOT NULL default '',
  `idref` int(10) unsigned NOT NULL default '0',
  `note` varchar(255) NOT NULL default '',
  `keyw` varchar(128) NOT NULL default '',
  `stat_N` int(10) unsigned default NULL,
  `stat_Ndescriptors` int(10) unsigned default NULL,
  `stat_R2` float default NULL,
  `stat_R` float default NULL,
  `stat_R2adj` float default NULL,
  `stat_F` float default NULL,
  `stat_P` float default NULL,
  `stat_Q` float default NULL,
  `stat_Q2` float default NULL,
  `stat_RMSEC` float default NULL,
  `stat_RMSECV` float default NULL,
  `stat_s` float default NULL,
  `stat_SE` float default NULL,
  `stat_SD` float default NULL,
  `model` blob,
  PRIMARY KEY  (`idqsar`),
  UNIQUE KEY `qsars_all` (`name`,`idmodeltype`,`idref`),
  KEY `qsars_keyword` (`keyw`),
  KEY `idref` (`idref`),
  KEY `idmodeltype` (`idmodeltype`),
  CONSTRAINT `qsars_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `qsars_ibfk_2` FOREIGN KEY (`idmodeltype`) REFERENCES `modeltype` (`idmodeltype`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--qsar_user
CREATE TABLE `qsar_user` (
  `idqsar` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idqsar`,`idambituser`),
  KEY `qsar_user1` (`idambituser`),
  CONSTRAINT `qsar_user_ibfk_1` FOREIGN KEY (`idqsar`) REFERENCES `qsars` (`idqsar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `qsar_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--qsardata
CREATE TABLE `qsardata` (
  `idqsar` int(10) unsigned NOT NULL default '0',
  `idexperiment` int(10) unsigned NOT NULL default '0',
  `idpoint` int(10) unsigned NOT NULL default '0',
  `ypredicted` float NOT NULL default '0',
  `yexperimental` float NOT NULL default '0',
  `pointType` enum('Training','Validation','Unknown') NOT NULL default 'Unknown',
  PRIMARY KEY  (`idqsar`,`idexperiment`),
  KEY `qsardata_struc` (`idexperiment`),
  KEY `qsardata_type` (`pointType`),
  KEY `qsardata_point` (`idpoint`),
  CONSTRAINT `qsardata_ibfk_1` FOREIGN KEY (`idqsar`) REFERENCES `qsars` (`idqsar`) ON UPDATE CASCADE,
  CONSTRAINT `qsardata_ibfk_3` FOREIGN KEY (`idexperiment`) REFERENCES `experiment` (`idexperiment`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--qsardescriptors
CREATE TABLE `qsardescriptors` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `idqsar` int(10) unsigned NOT NULL default '0',
  `morder` int(10) unsigned NOT NULL default '0',
  `minvalue` float default NULL,
  `maxvalue` float default NULL,
  `meanvalue` float default NULL,
  PRIMARY KEY  (`iddescriptor`,`idqsar`),
  KEY `qsardescriptors1` (`idqsar`),
  CONSTRAINT `qsardescriptors_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON UPDATE CASCADE,
  CONSTRAINT `qsardescriptors_ibfk_2` FOREIGN KEY (`idqsar`) REFERENCES `qsars` (`idqsar`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



--timings

CREATE TABLE `timings` (
  `idsubstance` int(10) unsigned NOT NULL default '0',
  `time` int(11) unsigned default NULL,
  PRIMARY KEY  (`idsubstance`),
  KEY `time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- version
CREATE TABLE `version` (
  `id` int(5) unsigned NOT NULL auto_increment,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into version (id) values (6);

--atom distance
CREATE TABLE `atom_distance` (
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  `atom1` varchar(2) NOT NULL default 'C',
  `atom2` varchar(2) NOT NULL default 'C',
  `distance` int(10) NOT NULL default '0.000',  
  PRIMARY KEY  (`iddistance`),  
  UNIQUE KEY  (`atom1`,`atom2`,`distance`),
  KEY `distance` (`distance`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--atom structure
CREATE TABLE `atom_structure` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `iddistance` int(10) unsigned NOT NULL auto_increment,  
  PRIMARY KEY  (`iddistance`,`idstructure`),
  KEY `adistance` (`idstructure`),
  CONSTRAINT `atom_distance_fk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `atom_distance_fk_2` FOREIGN KEY (`iddistance`) REFERENCES `atom_distance` (`iddistance`) ON DELETE CASCADE ON UPDATE CASCADE  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Reactions. blob holds CML reaction
CREATE TABLE reaction (
  idreaction int(10) unsigned NOT NULL auto_increment,
  name varchar(64) NOT NULL,
  idref int(10) unsigned NOT NULL,
  reaction blob NOT NULL,
  PRIMARY KEY  (idreaction),
  KEY reaction_name (name),
  KEY idref(idref),
  CONSTRAINT `reaction_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- to store metaboliets and other reaction products
create table products (
  idsubstance int(10) unsigned NOT NULL,
  idproduct int(10) unsigned NOT NULL,
  idreaction int(10) unsigned NOT NULL,
  quantity int(6) unsigned NOT NULL default 1,
  PRIMARY KEY (idsubstance,idproduct,idreaction),
  KEY key_reaction(idreaction),
  KEY key_metabolite(idproduct),
  CONSTRAINT `transformation_1` FOREIGN KEY (`idreaction`) REFERENCES `reaction` (`idreaction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `substance_ibfk_2` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE,  
  CONSTRAINT `substance_ibfk_3` FOREIGN KEY (`idproduct`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE    
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;