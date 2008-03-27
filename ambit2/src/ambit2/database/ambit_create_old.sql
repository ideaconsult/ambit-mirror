CREATE TABLE `ambituser` (
  `idambituser` int(10) unsigned NOT NULL auto_increment,
  `mysqluser` varchar(16) NOT NULL default '',
  `mysqlhost` varchar(60) NOT NULL default '%',
  `title` varchar(6) NOT NULL default '',
  `firstname` varchar(128) NOT NULL default '',
  `lastname` varchar(128) NOT NULL default '',
  `email` varchar(128) NOT NULL default '',
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
  `time_elapsed` int(11) unsigned default NULL,
  `status` enum('valid','invalid','error') NOT NULL default 'valid',
  PRIMARY KEY  (`idsubstance`,`idfpae`),
  KEY `fpae` (`idfpae`),
  KEY `time` (`time_elapsed`),
  CONSTRAINT `fpae_1` FOREIGN KEY (`idfpae`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `substance_1` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--gamut
CREATE TABLE `gamut` (
  `idsubstance` int(10) unsigned NOT NULL default '0',
  `C` int(10) unsigned NOT NULL default '0',
  `O` int(10) unsigned NOT NULL default '0',
  `N` int(10) unsigned NOT NULL default '0',
  `S` int(10) unsigned NOT NULL default '0',
  `P` int(10) unsigned NOT NULL default '0',
  `Cl` int(10) unsigned NOT NULL default '0',
  `F` int(10) unsigned NOT NULL default '0',
  `Br` int(10) unsigned NOT NULL default '0',
  `I` int(10) unsigned NOT NULL default '0',
  `Si` int(10) unsigned NOT NULL default '0',
  `B` int(10) unsigned NOT NULL default '0',
  `aa` int(10) unsigned NOT NULL default '0',
  `rno` int(10) unsigned NOT NULL default '0',
  `rmax` int(10) unsigned NOT NULL default '0',
  `b1` int(10) unsigned NOT NULL default '0',
  `bar` int(10) unsigned NOT NULL default '0',
  `b2` int(10) unsigned NOT NULL default '0',
  `b3` int(10) unsigned NOT NULL default '0',
  `ab` int(10) unsigned NOT NULL default '0',
  `cb` int(10) unsigned NOT NULL default '0',
  `st` int(10) unsigned NOT NULL default '0',
  `o1` int(10) unsigned NOT NULL default '0',
  `o2` int(10) unsigned NOT NULL default '0',
  `o3` int(10) unsigned NOT NULL default '0',
  `o4` int(10) unsigned NOT NULL default '0',
  `oo` int(10) unsigned NOT NULL default '0',
  `time` int(10) unsigned default NULL,
  PRIMARY KEY  (`idsubstance`),
  KEY `gamut_all` (`aa`,`C`,`O`,`N`,`S`,`P`,`Cl`,`F`,`Br`,`I`,`Si`,`B`),
  KEY `gamut_bonds` (`ab`,`cb`,`b1`,`bar`,`b2`,`b3`),
  KEY `gamut_o` (`o1`,`o2`,`o3`,`o4`,`oo`),
  KEY `cb` (`cb`),
  KEY `time` (`time`),
  CONSTRAINT `gamut_ibfk_1` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


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

--study tables
--species
CREATE TABLE `species` (
  `idspecies` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `sex` enum('M','F','NA') NOT NULL default 'NA',
  PRIMARY KEY  (`idspecies`),
  UNIQUE KEY `species_name` (`name`,`sex`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--study
CREATE TABLE `study` (
  `idres` int(10) unsigned NOT NULL auto_increment,
  `idspecies` int(10) unsigned NOT NULL default '0',
  `name` varchar(64) NOT NULL default '',
  `units` varchar(16) NOT NULL default '',
  `duration` varchar(64) NOT NULL default '',
  `study_type` varchar(64) NOT NULL default '',
  `vehicle` varchar(64) NOT NULL default '',
  PRIMARY KEY  (`idres`),
  UNIQUE KEY `study_all` (`name`,`duration`,`study_type`,`vehicle`,`idspecies`),
  KEY `res_spec` (`idspecies`),
  CONSTRAINT `study_ibfk_1` FOREIGN KEY (`idspecies`) REFERENCES `species` (`idspecies`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--testresults
CREATE TABLE `testresults` (
  `idtest` int(10) unsigned NOT NULL auto_increment,
  `idres` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  `idref` int(10) unsigned NOT NULL default '0',
  `result_num` float(10,4) NOT NULL default '0.0000',
  `exp_err` float(8,4) NOT NULL default '0.0000',
  `result_str` varchar(32) NOT NULL default '',
  `note` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idtest`),
  UNIQUE KEY `testresults_all` (`idstructure`,`idres`,`idref`),
  KEY `testresults_ref` (`idref`),
  KEY `testresults_res` (`idres`),
  CONSTRAINT `testresults_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON UPDATE CASCADE,
  CONSTRAINT `testresults_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `testresults_ibfk_3` FOREIGN KEY (`idres`) REFERENCES `study` (`idres`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--testedstrucs
CREATE TABLE `testedstrucs` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `idtest` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`idtest`),
  KEY `testedstrucs_test` (`idtest`),
  CONSTRAINT `testedstrucs_ibfk_1` FOREIGN KEY (`idtest`) REFERENCES `testresults` (`idtest`) ON UPDATE CASCADE,
  CONSTRAINT `testedstrucs_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- qsar tables
--modeltype
CREATE TABLE `modeltype` (
  `idmodeltype` int(10) unsigned NOT NULL auto_increment,
  `modeltype` varchar(32) NOT NULL default '',
  PRIMARY KEY  (`idmodeltype`),
  UNIQUE KEY `modeltype_type` (`modeltype`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--qsars
CREATE TABLE `qsars` (
  `idqsar` int(10) unsigned NOT NULL auto_increment,
  `idres` int(10) unsigned NOT NULL default '0',
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
  UNIQUE KEY `qsars_all` (`name`,`idmodeltype`,`idres`,`idref`),
  KEY `qsars_keyword` (`keyw`),
  KEY `idref` (`idref`),
  KEY `idmodeltype` (`idmodeltype`),
  KEY `idres` (`idres`),
  CONSTRAINT `qsars_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON UPDATE CASCADE,
  CONSTRAINT `qsars_ibfk_2` FOREIGN KEY (`idmodeltype`) REFERENCES `modeltype` (`idmodeltype`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `qsars_ibfk_3` FOREIGN KEY (`idres`) REFERENCES `study` (`idres`) ON DELETE CASCADE ON UPDATE CASCADE
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
  `idstructure` int(10) unsigned NOT NULL default '0',
  `idtest` int(10) unsigned NOT NULL default '0',
  `idpoint` int(10) unsigned NOT NULL default '0',
  `ypredicted` float NOT NULL default '0',
  `yexperimental` float NOT NULL default '0',
  `pointType` enum('Training','Validation') NOT NULL default 'Training',
  PRIMARY KEY  (`idqsar`,`idstructure`),
  KEY `qsardata_struc` (`idstructure`),
  KEY `qsardata_test` (`idtest`),
  KEY `qsardata_type` (`pointType`),
  KEY `qsardata_point` (`idpoint`),
  CONSTRAINT `qsardata_ibfk_1` FOREIGN KEY (`idqsar`) REFERENCES `qsars` (`idqsar`) ON UPDATE CASCADE,
  CONSTRAINT `qsardata_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE,
  CONSTRAINT `qsardata_ibfk_3` FOREIGN KEY (`idtest`) REFERENCES `testresults` (`idtest`) ON UPDATE CASCADE
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

--query
CREATE TABLE `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `casno` varchar(11) default NULL,
  `name` varchar(255) default NULL,
  `smiles` text,
  PRIMARY KEY  (`idquery`),
  KEY `query_index3707` (`casno`),
  KEY `query_index3708` (`name`),
  KEY `query_index3709` (`smiles`(760))
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
insert into version (id) values (1);

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

