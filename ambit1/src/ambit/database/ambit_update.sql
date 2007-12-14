drop table qsar_user;
drop table qsardata;
drop table qsardescriptors;
drop table qsars;
drop table testedstrucs;
drop table testresults;
drop table study;
drop table species;

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
CREATE TABLE study_fieldnames (
  id_fieldname INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) NULL,
  units VARCHAR(16) NULL,
  fieldtype ENUM("string","numeric") NULL,
  PRIMARY KEY(id_fieldname),
  UNIQUE INDEX study_fieldnames_index4255(name)
)
TYPE=InnoDB;

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
