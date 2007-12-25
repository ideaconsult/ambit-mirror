-- MySQL dump 10.11
--
-- Host: localhost    Database: ambit_qmrf
-- ------------------------------------------------------
-- Server version	5.0.37-community-nt-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `ambit_qmrf`
--

/*!40000 DROP DATABASE IF EXISTS `ambitqmrf`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ambit_qmrf` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `ambit_qmrf`;

--
-- Table structure for table `alias`
--

DROP TABLE IF EXISTS `alias`;
CREATE TABLE `alias` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `alias` varchar(255) NOT NULL default 'NA',
  `alias_type` varchar(16) NOT NULL default 'ID',
  PRIMARY KEY  (`idstructure`,`alias`,`alias_type`),
  KEY `alias_type` (`alias_type`),
  KEY `alias_name` (`alias`),
  CONSTRAINT `alias_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `alias`
--

LOCK TABLES `alias` WRITE;
/*!40000 ALTER TABLE `alias` DISABLE KEYS */;
/*!40000 ALTER TABLE `alias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ambituser`
--

DROP TABLE IF EXISTS `ambituser`;
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

--
-- Dumping data for table `ambituser`
--

LOCK TABLES `ambituser` WRITE;
/*!40000 ALTER TABLE `ambituser` DISABLE KEYS */;
INSERT INTO `ambituser` VALUES (1,'root','localhost','','','','','','','','','','','admin','commenced','2007-10-17 15:43:59'),(2,'lri_admin','localhost','','','','','','','','','','','pro','commenced','2007-10-17 15:43:59'),(3,'guest','%','','','','','','','','','','','guest','commenced','2007-10-17 15:43:59');
/*!40000 ALTER TABLE `ambituser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `atom_distance`
--

DROP TABLE IF EXISTS `atom_distance`;
CREATE TABLE `atom_distance` (
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  `atom1` varchar(2) NOT NULL default 'C',
  `atom2` varchar(2) NOT NULL default 'C',
  `distance` int(10) NOT NULL default '0',
  PRIMARY KEY  (`iddistance`),
  UNIQUE KEY `atom1` (`atom1`,`atom2`,`distance`),
  KEY `distance` (`distance`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `atom_distance`
--

LOCK TABLES `atom_distance` WRITE;
/*!40000 ALTER TABLE `atom_distance` DISABLE KEYS */;
/*!40000 ALTER TABLE `atom_distance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `atom_structure`
--

DROP TABLE IF EXISTS `atom_structure`;
CREATE TABLE `atom_structure` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`iddistance`,`idstructure`),
  KEY `adistance` (`idstructure`),
  CONSTRAINT `atom_distance_fk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `atom_distance_fk_2` FOREIGN KEY (`iddistance`) REFERENCES `atom_distance` (`iddistance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `atom_structure`
--

LOCK TABLES `atom_structure` WRITE;
/*!40000 ALTER TABLE `atom_structure` DISABLE KEYS */;
/*!40000 ALTER TABLE `atom_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `idauthor` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idauthor`),
  UNIQUE KEY `Authors_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `author`
--

LOCK TABLES `author` WRITE;
/*!40000 ALTER TABLE `author` DISABLE KEYS */;
/*!40000 ALTER TABLE `author` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cas`
--

DROP TABLE IF EXISTS `cas`;
CREATE TABLE `cas` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `casno` char(11) NOT NULL default '',
  `isregistry` tinyint(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`casno`),
  KEY `cas_casno` (`casno`),
  CONSTRAINT `cas_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cas`
--

LOCK TABLES `cas` WRITE;
/*!40000 ALTER TABLE `cas` DISABLE KEYS */;
/*!40000 ALTER TABLE `cas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datasets`
--

DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets` (
  `iddsname` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddsname`,`idstructure`),
  KEY `datasets_struc` (`idstructure`),
  CONSTRAINT `datasets_ibfk_1` FOREIGN KEY (`iddsname`) REFERENCES `dsname` (`iddsname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasets_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `datasets`
--

LOCK TABLES `datasets` WRITE;
/*!40000 ALTER TABLE `datasets` DISABLE KEYS */;
/*!40000 ALTER TABLE `datasets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ddictionary`
--

DROP TABLE IF EXISTS `ddictionary`;
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

--
-- Dumping data for table `ddictionary`
--

LOCK TABLES `ddictionary` WRITE;
/*!40000 ALTER TABLE `ddictionary` DISABLE KEYS */;
/*!40000 ALTER TABLE `ddictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `descrgroups`
--

DROP TABLE IF EXISTS `descrgroups`;
CREATE TABLE `descrgroups` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `iddgroup` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`,`iddgroup`),
  KEY `descrgroups1` (`iddgroup`),
  CONSTRAINT `descrgroups_ibfk_1` FOREIGN KEY (`iddgroup`) REFERENCES `dgroup` (`iddgroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `descrgroups_ibfk_2` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `descrgroups`
--

LOCK TABLES `descrgroups` WRITE;
/*!40000 ALTER TABLE `descrgroups` DISABLE KEYS */;
/*!40000 ALTER TABLE `descrgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dgroup`
--

DROP TABLE IF EXISTS `dgroup`;
CREATE TABLE `dgroup` (
  `iddgroup` int(10) unsigned NOT NULL auto_increment,
  `groupname` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`iddgroup`),
  UNIQUE KEY `dgroup_name` (`groupname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dgroup`
--

LOCK TABLES `dgroup` WRITE;
/*!40000 ALTER TABLE `dgroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `dgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dict_user`
--

DROP TABLE IF EXISTS `dict_user`;
CREATE TABLE `dict_user` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`iddescriptor`,`idambituser`),
  KEY `dict_user1` (`idambituser`),
  CONSTRAINT `dict_user_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `dict_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dict_user`
--

LOCK TABLES `dict_user` WRITE;
/*!40000 ALTER TABLE `dict_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `dict_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dsname`
--

DROP TABLE IF EXISTS `dsname`;
CREATE TABLE `dsname` (
  `iddsname` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(32) default NULL,
  `idambituser` int(10) unsigned NOT NULL default '0',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`iddsname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dsname`
--

LOCK TABLES `dsname` WRITE;
/*!40000 ALTER TABLE `dsname` DISABLE KEYS */;
/*!40000 ALTER TABLE `dsname` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dvalues`
--

DROP TABLE IF EXISTS `dvalues`;
CREATE TABLE `dvalues` (
  `iddescriptor` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  `value` float(10,4) default '0.0000',
  `error` float(10,4) default '0.0000',
  `status` enum('OK','ERROR') NOT NULL default 'OK',
  PRIMARY KEY  (`iddescriptor`,`idstructure`),
  KEY `dvalues_struc` (`idstructure`),
  KEY `dvalues_value` (`value`),
  CONSTRAINT `dvalues_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `dvalues_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dvalues`
--

LOCK TABLES `dvalues` WRITE;
/*!40000 ALTER TABLE `dvalues` DISABLE KEYS */;
/*!40000 ALTER TABLE `dvalues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `experiment`
--

DROP TABLE IF EXISTS `experiment`;
CREATE TABLE `experiment` (
  `idexperiment` int(10) unsigned NOT NULL auto_increment,
  `idstudy` int(10) unsigned NOT NULL,
  `idref` int(10) unsigned NOT NULL,
  `idstructure` int(10) unsigned NOT NULL,
  `structure_type` enum('tested','parent','metabolite') NOT NULL default 'tested',
  `idstructure_parent` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idexperiment`),
  UNIQUE KEY `idstudy` (`idstudy`,`idref`,`idstructure`),
  KEY `experiment_index4302` (`idstructure_parent`),
  KEY `idstructure` (`idstructure`),
  KEY `idref` (`idref`),
  CONSTRAINT `experiment_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `experiment_ibfk_2` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `experiment_ibfk_3` FOREIGN KEY (`idstudy`) REFERENCES `study` (`idstudy`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `experiment`
--

LOCK TABLES `experiment` WRITE;
/*!40000 ALTER TABLE `experiment` DISABLE KEYS */;
/*!40000 ALTER TABLE `experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fp1024`
--

DROP TABLE IF EXISTS `fp1024`;
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

--
-- Dumping data for table `fp1024`
--

LOCK TABLES `fp1024` WRITE;
/*!40000 ALTER TABLE `fp1024` DISABLE KEYS */;
/*!40000 ALTER TABLE `fp1024` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fpae`
--

DROP TABLE IF EXISTS `fpae`;
CREATE TABLE `fpae` (
  `idfpae` int(10) unsigned NOT NULL auto_increment,
  `atom` varchar(16) NOT NULL default '',
  `ae` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idfpae`),
  UNIQUE KEY `ae` (`ae`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fpae`
--

LOCK TABLES `fpae` WRITE;
/*!40000 ALTER TABLE `fpae` DISABLE KEYS */;
/*!40000 ALTER TABLE `fpae` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fpaeid`
--

DROP TABLE IF EXISTS `fpaeid`;
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

--
-- Dumping data for table `fpaeid`
--

LOCK TABLES `fpaeid` WRITE;
/*!40000 ALTER TABLE `fpaeid` DISABLE KEYS */;
/*!40000 ALTER TABLE `fpaeid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hierarchy`
--

DROP TABLE IF EXISTS `hierarchy`;
CREATE TABLE `hierarchy` (
  `idparent_field` int(10) unsigned NOT NULL default '0',
  `id_fieldname` int(10) unsigned NOT NULL,
  `leaf` tinyint(1) default NULL,
  PRIMARY KEY  (`idparent_field`,`id_fieldname`),
  KEY `id_fieldname` (`id_fieldname`),
  CONSTRAINT `hierarchy_ibfk_1` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hierarchy`
--

LOCK TABLES `hierarchy` WRITE;
/*!40000 ALTER TABLE `hierarchy` DISABLE KEYS */;
/*!40000 ALTER TABLE `hierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `journal`
--

DROP TABLE IF EXISTS `journal`;
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

--
-- Dumping data for table `journal`
--

LOCK TABLES `journal` WRITE;
/*!40000 ALTER TABLE `journal` DISABLE KEYS */;
/*!40000 ALTER TABLE `journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `literature`
--

DROP TABLE IF EXISTS `literature`;
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

--
-- Dumping data for table `literature`
--

LOCK TABLES `literature` WRITE;
/*!40000 ALTER TABLE `literature` DISABLE KEYS */;
/*!40000 ALTER TABLE `literature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localdvalues`
--

DROP TABLE IF EXISTS `localdvalues`;
CREATE TABLE `localdvalues` (
  `iddescriptor` int(10) unsigned NOT NULL auto_increment,
  `idstructure` int(10) unsigned NOT NULL default '0',
  `localid` int(10) unsigned NOT NULL default '0',
  `value` float(10,4) default '0.0000',
  PRIMARY KEY  (`iddescriptor`),
  KEY `localdvalues_struc` (`idstructure`),
  CONSTRAINT `localdvalues_ibfk_1` FOREIGN KEY (`iddescriptor`) REFERENCES `ddictionary` (`iddescriptor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `localdvalues_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `localdvalues`
--

LOCK TABLES `localdvalues` WRITE;
/*!40000 ALTER TABLE `localdvalues` DISABLE KEYS */;
/*!40000 ALTER TABLE `localdvalues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modeltype`
--

DROP TABLE IF EXISTS `modeltype`;
CREATE TABLE `modeltype` (
  `idmodeltype` int(10) unsigned NOT NULL auto_increment,
  `modeltype` varchar(32) NOT NULL default '',
  PRIMARY KEY  (`idmodeltype`),
  UNIQUE KEY `modeltype_type` (`modeltype`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `modeltype`
--

LOCK TABLES `modeltype` WRITE;
/*!40000 ALTER TABLE `modeltype` DISABLE KEYS */;
/*!40000 ALTER TABLE `modeltype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `name`
--

DROP TABLE IF EXISTS `name`;
CREATE TABLE `name` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`idstructure`,`name`),
  KEY `name_name` (`name`),
  CONSTRAINT `name_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `name`
--

LOCK TABLES `name` WRITE;
/*!40000 ALTER TABLE `name` DISABLE KEYS */;
/*!40000 ALTER TABLE `name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `idsubstance` int(10) unsigned NOT NULL,
  `idproduct` int(10) unsigned NOT NULL,
  `idreaction` int(10) unsigned NOT NULL,
  `quantity` int(6) unsigned NOT NULL default '1',
  PRIMARY KEY  (`idsubstance`,`idproduct`,`idreaction`),
  KEY `key_reaction` (`idreaction`),
  KEY `key_metabolite` (`idproduct`),
  CONSTRAINT `transformation_1` FOREIGN KEY (`idreaction`) REFERENCES `reaction` (`idreaction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `substance_ibfk_2` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `substance_ibfk_3` FOREIGN KEY (`idproduct`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qsar_user`
--

DROP TABLE IF EXISTS `qsar_user`;
CREATE TABLE `qsar_user` (
  `idqsar` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idqsar`,`idambituser`),
  KEY `qsar_user1` (`idambituser`),
  CONSTRAINT `qsar_user_ibfk_1` FOREIGN KEY (`idqsar`) REFERENCES `qsars` (`idqsar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `qsar_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `qsar_user`
--

LOCK TABLES `qsar_user` WRITE;
/*!40000 ALTER TABLE `qsar_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qsar_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qsardata`
--

DROP TABLE IF EXISTS `qsardata`;
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

--
-- Dumping data for table `qsardata`
--

LOCK TABLES `qsardata` WRITE;
/*!40000 ALTER TABLE `qsardata` DISABLE KEYS */;
/*!40000 ALTER TABLE `qsardata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qsardescriptors`
--

DROP TABLE IF EXISTS `qsardescriptors`;
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

--
-- Dumping data for table `qsardescriptors`
--

LOCK TABLES `qsardescriptors` WRITE;
/*!40000 ALTER TABLE `qsardescriptors` DISABLE KEYS */;
/*!40000 ALTER TABLE `qsardescriptors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qsars`
--

DROP TABLE IF EXISTS `qsars`;
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

--
-- Dumping data for table `qsars`
--

LOCK TABLES `qsars` WRITE;
/*!40000 ALTER TABLE `qsars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qsars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reaction`
--

DROP TABLE IF EXISTS `reaction`;
CREATE TABLE `reaction` (
  `idreaction` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate latin1_bin NOT NULL,
  `idref` int(10) unsigned NOT NULL,
  `reaction` blob NOT NULL,
  PRIMARY KEY  (`idreaction`),
  KEY `reaction_name` (`name`),
  KEY `idref` (`idref`),
  CONSTRAINT `reaction_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

--
-- Dumping data for table `reaction`
--

LOCK TABLES `reaction` WRITE;
/*!40000 ALTER TABLE `reaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `reaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ref_authors`
--

DROP TABLE IF EXISTS `ref_authors`;
CREATE TABLE `ref_authors` (
  `idref` int(10) unsigned NOT NULL default '0',
  `idauthor` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idref`,`idauthor`),
  KEY `idauthor` (`idauthor`),
  CONSTRAINT `ref_authors_ibfk_1` FOREIGN KEY (`idref`) REFERENCES `literature` (`idref`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ref_authors_ibfk_2` FOREIGN KEY (`idauthor`) REFERENCES `author` (`idauthor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ref_authors`
--

LOCK TABLES `ref_authors` WRITE;
/*!40000 ALTER TABLE `ref_authors` DISABLE KEYS */;
/*!40000 ALTER TABLE `ref_authors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `src_dataset`
--

DROP TABLE IF EXISTS `src_dataset`;
CREATE TABLE `src_dataset` (
  `id_srcdataset` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default 'default',
  `idref` int(10) unsigned default NULL,
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `src_dataset`
--

LOCK TABLES `src_dataset` WRITE;
/*!40000 ALTER TABLE `src_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `src_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `struc_dataset`
--

DROP TABLE IF EXISTS `struc_dataset`;
CREATE TABLE `struc_dataset` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `id_srcdataset` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `struc_dataset`
--

LOCK TABLES `struc_dataset` WRITE;
/*!40000 ALTER TABLE `struc_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `struc_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `struc_user`
--

DROP TABLE IF EXISTS `struc_user`;
CREATE TABLE `struc_user` (
  `idstructure` int(10) unsigned NOT NULL default '0',
  `idambituser` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idstructure`,`idambituser`),
  KEY `idambituser` (`idambituser`),
  CONSTRAINT `struc_user_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_user_ibfk_2` FOREIGN KEY (`idambituser`) REFERENCES `ambituser` (`idambituser`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `struc_user`
--

LOCK TABLES `struc_user` WRITE;
/*!40000 ALTER TABLE `struc_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `struc_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `structure`
--

DROP TABLE IF EXISTS `structure`;
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

--
-- Dumping data for table `structure`
--

LOCK TABLES `structure` WRITE;
/*!40000 ALTER TABLE `structure` DISABLE KEYS */;
/*!40000 ALTER TABLE `structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
CREATE TABLE `study` (
  `idstudy` int(10) unsigned NOT NULL auto_increment,
  `idtemplate` int(10) unsigned NOT NULL,
  `name` varchar(64) default NULL,
  PRIMARY KEY  (`idstudy`),
  KEY `study_index4290` (`name`),
  KEY `idtemplate` (`idtemplate`),
  CONSTRAINT `study_ibfk_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `study`
--

LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_conditions`
--

DROP TABLE IF EXISTS `study_conditions`;
CREATE TABLE `study_conditions` (
  `idstudy` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  `value` varchar(64) default NULL,
  PRIMARY KEY  (`idstudy`,`id_fieldname`),
  KEY `study_conditions_index4299` (`value`),
  KEY `study_conditions_index4353` (`id_fieldname`),
  CONSTRAINT `study_conditions_ibfk_1` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `study_conditions_ibfk_2` FOREIGN KEY (`idstudy`) REFERENCES `study` (`idstudy`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `study_conditions`
--

LOCK TABLES `study_conditions` WRITE;
/*!40000 ALTER TABLE `study_conditions` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_conditions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_fieldnames`
--

DROP TABLE IF EXISTS `study_fieldnames`;
CREATE TABLE `study_fieldnames` (
  `id_fieldname` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) default NULL,
  `units` varchar(16) default NULL,
  `fieldtype` enum('string','numeric') default 'string',
  `fieldmode` enum('result','condition') default 'condition',
  PRIMARY KEY  (`id_fieldname`),
  UNIQUE KEY `study_fieldnames_index4255` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `study_fieldnames`
--

LOCK TABLES `study_fieldnames` WRITE;
/*!40000 ALTER TABLE `study_fieldnames` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_fieldnames` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_results`
--

DROP TABLE IF EXISTS `study_results`;
CREATE TABLE `study_results` (
  `idexperiment` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  `value` varchar(64) default NULL,
  `value_num` float(10,4) default NULL,
  PRIMARY KEY  (`idexperiment`,`id_fieldname`),
  KEY `study_results_index4205` (`value`),
  KEY `study_results_index4206` (`value_num`),
  KEY `study_results_index4355` (`id_fieldname`),
  CONSTRAINT `study_results_ibfk_1` FOREIGN KEY (`idexperiment`) REFERENCES `experiment` (`idexperiment`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `study_results_ibfk_2` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `study_results`
--

LOCK TABLES `study_results` WRITE;
/*!40000 ALTER TABLE `study_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stype`
--

DROP TABLE IF EXISTS `stype`;
CREATE TABLE `stype` (
  `stype_id` int(5) unsigned NOT NULL auto_increment,
  `substance_type` varchar(16) NOT NULL default '',
  PRIMARY KEY  (`stype_id`),
  UNIQUE KEY `stype_subst` (`substance_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `stype`
--

LOCK TABLES `stype` WRITE;
/*!40000 ALTER TABLE `stype` DISABLE KEYS */;
INSERT INTO `stype` VALUES (2,'inorganic'),(4,'mixture/unknown'),(1,'organic'),(3,'organometalic');
/*!40000 ALTER TABLE `stype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `substance`
--

DROP TABLE IF EXISTS `substance`;
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

--
-- Dumping data for table `substance`
--

LOCK TABLES `substance` WRITE;
/*!40000 ALTER TABLE `substance` DISABLE KEYS */;
/*!40000 ALTER TABLE `substance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) default NULL,
  `template` blob NOT NULL,
  PRIMARY KEY  (`idtemplate`),
  KEY `template_list_index4157` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_def`
--

DROP TABLE IF EXISTS `template_def`;
CREATE TABLE `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `id_fieldname` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idtemplate`,`id_fieldname`),
  KEY `template_def_index4168` (`id_fieldname`),
  CONSTRAINT `template_def_ibfk_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `template_def_ibfk_2` FOREIGN KEY (`id_fieldname`) REFERENCES `study_fieldnames` (`id_fieldname`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `template_def`
--

LOCK TABLES `template_def` WRITE;
/*!40000 ALTER TABLE `template_def` DISABLE KEYS */;
/*!40000 ALTER TABLE `template_def` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timings`
--

DROP TABLE IF EXISTS `timings`;
CREATE TABLE `timings` (
  `idsubstance` int(10) unsigned NOT NULL default '0',
  `time` int(11) unsigned default NULL,
  PRIMARY KEY  (`idsubstance`),
  KEY `time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timings`
--

LOCK TABLES `timings` WRITE;
/*!40000 ALTER TABLE `timings` DISABLE KEYS */;
/*!40000 ALTER TABLE `timings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `id` int(5) unsigned NOT NULL auto_increment,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `version`
--

LOCK TABLES `version` WRITE;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
INSERT INTO `version` VALUES (6,'2007-10-17 15:43:59');
/*!40000 ALTER TABLE `version` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-10-17 15:45:03
