-- MySQL dump 10.11
--
-- Host: localhost    Database: ambit-test
-- ------------------------------------------------------
-- Server version	5.0.67-community-nt

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
-- Current Database: `ambit-test`
--

/*!40000 DROP DATABASE IF EXISTS `ambit-skin`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ambit-skin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

USE `ambit-skin`;

--
-- Table structure for table `atom_distance`
--

DROP TABLE IF EXISTS `atom_distance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `atom_distance` (
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  `atom1` varchar(2) collate utf8_bin NOT NULL default 'C',
  `atom2` varchar(2) collate utf8_bin NOT NULL default 'C',
  `distance` int(10) NOT NULL default '0',
  PRIMARY KEY  (`iddistance`),
  UNIQUE KEY `atom1` (`atom1`,`atom2`,`distance`),
  KEY `distance` (`distance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

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
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `atom_structure` (
  `idstructure` int(11) unsigned NOT NULL default '0',
  `iddistance` int(11) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`iddistance`,`idstructure`),
  KEY `adistance` (`idstructure`),
  CONSTRAINT `atom_distance_fk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `atom_distance_fk_2` FOREIGN KEY (`iddistance`) REFERENCES `atom_distance` (`iddistance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `atom_structure`
--

LOCK TABLES `atom_structure` WRITE;
/*!40000 ALTER TABLE `atom_structure` DISABLE KEYS */;
/*!40000 ALTER TABLE `atom_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `catalog_references`
--

DROP TABLE IF EXISTS `catalog_references`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `catalog_references` (
  `idreference` int(11) unsigned NOT NULL auto_increment,
  `title` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `url` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idreference`),
  UNIQUE KEY `Index_2` (`title`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `catalog_references`
--

LOCK TABLES `catalog_references` WRITE;
/*!40000 ALTER TABLE `catalog_references` DISABLE KEYS */;
INSERT INTO `catalog_references` VALUES (1,'CAS Registry Number','http://www.cas.org'),(2,'IUPAC name','http://www.iupac.org'),(3,'File','file:input.sdf'),(4,'Structure properties','');
/*!40000 ALTER TABLE `catalog_references` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chemicals`
--

DROP TABLE IF EXISTS `chemicals`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `chemicals` (
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `chemicals`
--

LOCK TABLES `chemicals` WRITE;
/*!40000 ALTER TABLE `chemicals` DISABLE KEYS */;
INSERT INTO `chemicals` VALUES (1,NULL,'[Na+].CCCCCCCCCCCCCCOS(=O)(=O)[O-]',NULL,-7616313607282657360),(2,NULL,'CC=1C=CNC=1(C)',NULL,3873016332513385302),(3,NULL,'CC1=CCC(CC1)C(C)(C)O',NULL,20488018948056040);
/*!40000 ALTER TABLE `chemicals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `dictionary` (
  `iddictionary` int(11) unsigned NOT NULL auto_increment,
  `idparent` int(11) unsigned NOT NULL,
  `name` varchar(128) collate utf8_bin NOT NULL,
  `idreference` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`iddictionary`),
  KEY `Index_2` (`name`),
  KEY `Index_3` (`idparent`),
  KEY `FK_dictionary_1` (`idreference`),
  CONSTRAINT `FK_dictionary_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,0,'CAS',1),(2,0,'IUPAC Name',2);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fp1024`
--

DROP TABLE IF EXISTS `fp1024`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fp1024` (
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
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fp1024`
--

LOCK TABLES `fp1024` WRITE;
/*!40000 ALTER TABLE `fp1024` DISABLE KEYS */;
/*!40000 ALTER TABLE `fp1024` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `funcgroups`
--

DROP TABLE IF EXISTS `funcgroups`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `funcgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `name` varchar(45) collate utf8_bin NOT NULL,
  `smarts` blob NOT NULL,
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`idfuncgroup`),
  UNIQUE KEY `Index_2` (`name`),
  KEY `FK_funcgroups_1` (`user_name`),
  CONSTRAINT `FK_funcgroups_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `funcgroups`
--

LOCK TABLES `funcgroups` WRITE;
/*!40000 ALTER TABLE `funcgroups` DISABLE KEYS */;
/*!40000 ALTER TABLE `funcgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `history` (
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
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `properties`
--

DROP TABLE IF EXISTS `properties`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `properties` (
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
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `properties`
--

LOCK TABLES `properties` WRITE;
/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
INSERT INTO `properties` VALUES (1,4,'EC','','Structure',0),(2,4,'GENERATED_SMILES','','Structure',0),(3,4,'Inventories','','Structure',0),(4,4,'MF','','Structure',0),(5,4,'GENERATED_InChI','','Structure',0),(6,4,'GENERATED_SMILES_Quality','','Structure',0),(7,4,'GENERATED_MF','','Structure',0),(8,4,'GENERATED_MW','','Structure',0),(9,4,'GENERATED_IUPAC_EN','','Structure',0),(10,4,'CAS','','Structure',0),(11,4,'Title','','Structure',0),(12,4,'Name','','Structure',0),(13,4,'ID','','Structure',0),(14,4,'Erythema (rabbit 3)-2d','','Structure',0),(15,4,'Class','','Structure',0),(16,4,'Oedema (rabbit 1)-2d','','Structure',0),(17,4,'C/NC','','Structure',0),(18,4,'Oedema (rabbit 3)-2d','','Structure',0),(19,4,'Erythema (rabbit 3)-1d','','Structure',0),(20,4,'Formula','','Structure',0),(21,4,'Erythema (rabbit 3)-7d','','Structure',0),(22,4,'Mean oedema-2d','','Structure',0),(23,4,'Erythema (rabbit 1)-7d','','Structure',0),(24,4,'Mean oedema-1d','','Structure',0),(25,4,'Erythema (rabbit 2)-3d','','Structure',0),(26,4,'Mean erythema-7d','','Structure',0),(27,4,'Oedema (rabbit 2)-1d','','Structure',0),(28,4,'Erythema (rabbit 1)-1h','','Structure',0),(29,4,'PII','','Structure',0),(30,4,'Mean erythema-1h','','Structure',0),(31,4,'Oedema (rabbit 3)-1h','','Structure',0),(32,4,'Oedema (rabbit 2)-3d','','Structure',0),(33,4,'Erythema (rabbit 3)-3d','','Structure',0),(34,4,'Mean erythema-2d','','Structure',0),(35,4,'Erythema (rabbit 1)-3d','','Structure',0),(36,4,'Oedema (rabbit 1)-7d','','Structure',0),(37,4,'IUPAC_Name','','Structure',0),(38,4,'Erythema (rabbit 1)-2d','','Structure',0),(39,4,'Oedema (rabbit 2)-1h','','Structure',0),(40,4,'Erythema (rabbit 2)-1d','','Structure',0),(41,4,'Oedema (rabbit 1)-1h','','Structure',0),(42,4,'Oedema (rabbit 1)-3d','','Structure',0),(43,4,'Erythema (rabbit 2)-7d','','Structure',0),(44,4,'Oedema (rabbit 3)-1d','','Structure',0),(45,4,'Oedema (rabbit 2)-7d','','Structure',0),(46,4,'smiles','','Structure',0),(47,4,'Mean erythema-1d','','Structure',0),(48,4,'FW','','Structure',0),(49,4,'Mean oedema-3d','','Structure',0),(50,4,'Erythema (rabbit 2)-2d','','Structure',0),(51,4,'No','','Structure',0),(52,4,'Oedema (rabbit 3)-3d','','Structure',0),(53,4,'InChI','','Structure',0),(54,4,'Erythema (rabbit 3)-1h','','Structure',0),(55,4,'Mean oedema-7d','','Structure',0),(56,4,'Erythema (rabbit 2)-1h','','Structure',0),(57,4,'Oedema (rabbit 1)-1d','','Structure',0),(58,4,'Animals','','Structure',0),(59,4,'Oedema (rabbit 3)-7d','','Structure',0),(60,4,'Mean oedema-1h','','Structure',0),(61,4,'Oedema (rabbit 2)-2d','','Structure',0),(62,4,'Erythema (rabbit 1)-1d','','Structure',0),(63,4,'Mean erythema-3d','','Structure',0),(64,4,'Erythema (rabbit 4)7d','','Structure',0),(65,4,'Erythema (rabbit 4)-1h','','Structure',0),(66,4,'Oedema (rabbit 4)-1h','','Structure',0),(67,4,'Oedema (rabbit 4)-3d','','Structure',0),(68,4,'Erythema (rabbit 4)-3d','','Structure',0),(69,4,'Oedema (rabbit 4)-7d','','Structure',0),(70,4,'Oedema (rabbit 4)-2d','','Structure',0),(71,4,'Erythema (rabbit 4)-2d','','Structure',0),(72,4,'Oedema (rabbit 4)-1d','','Structure',0),(73,4,'Erythema (rabbit 4)-1d','','Structure',0);
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_number`
--

DROP TABLE IF EXISTS `property_number`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_number` (
  `idvalue` int(10) unsigned NOT NULL auto_increment,
  `idtype` int(10) unsigned NOT NULL default '1',
  `value` float(10,4) NOT NULL,
  PRIMARY KEY  (`idvalue`),
  UNIQUE KEY `Index_3` (`value`),
  KEY `Index_2` (`idvalue`,`idtype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_number`
--

LOCK TABLES `property_number` WRITE;
/*!40000 ALTER TABLE `property_number` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_number` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_string`
--

DROP TABLE IF EXISTS `property_string`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_string` (
  `idvalue` int(10) unsigned NOT NULL auto_increment,
  `idtype` int(10) unsigned NOT NULL default '0',
  `value` varchar(200) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idvalue`),
  UNIQUE KEY `Index_3` (`value`),
  KEY `Index_2` (`idvalue`,`idtype`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_string`
--

LOCK TABLES `property_string` WRITE;
/*!40000 ALTER TABLE `property_string` DISABLE KEYS */;
INSERT INTO `property_string` VALUES (1,0,'205-380-3'),(2,0,'[Na+].CCCCCCCCCCCCCCOS(=O)(=O)[O-]'),(3,0,'DSL,TSCA'),(4,0,'C14H30O4S.Na'),(5,0,'InChI=1/C14H30O4S.Na/c1-2-3-4-5-6-7-8-9-10-11-12-13-14-18-19(15,16)17;/h2-14H2,1H3,(H,15,16,17);/q;+1/p-1'),(6,0,'Low'),(7,0,'C14H29NaO4S'),(8,0,'316.4324 (22.9892+293.4432)'),(9,0,'sodium tetradecyl sulfate'),(10,0,'139-88-8'),(11,0,'4891'),(12,0,'209-991-6'),(13,0,'Cc1cc[nH]c1C'),(14,0,'.'),(15,0,'C6H9N'),(16,0,'InChI=1/C6H9N/c1-5-3-4-7-6(5)2/h3-4,7H,1-2H3'),(17,0,'VeryHigh'),(18,0,'95.1424'),(19,0,'2,3-dimethyl-1H-pyrrole'),(20,0,'600-28-2'),(21,0,'9083'),(22,0,'2'),(23,0,'Alcohols'),(24,0,'NC'),(25,0,'3'),(26,0,'C_{10}H_{18}O'),(27,0,'2.3'),(28,0,'1'),(29,0,'2.7'),(30,0,'2.0'),(31,0,'0'),(32,0,'4.4'),(33,0,'1.3'),(34,0,'2-(4-methylcyclohex-3-en-1-yl)propan-2-ol'),(35,0,'CC1=CCC(CC1)C(O)(C)C'),(36,0,'1.7'),(37,0,'154.2493'),(38,0,'24'),(39,0,'98-55-5'),(40,0,'InChI=1/C10H18O/c1-8-4-6-9(7-5-8)10(2,3)11/h4,9,11H,5-7H2,1-3H3'),(41,0,'a-Terpineol [1]'),(42,0,'2.5'),(43,0,'2.8'),(44,0,'4.7'),(45,0,'1.8'),(46,0,'a-Terpineol [2]'),(47,0,'4'),(48,0,'1.0'),(49,0,'4.0'),(50,0,'a-Terpineol [3]'),(51,0,'0.8');
/*!40000 ALTER TABLE `property_string` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_tuples`
--

DROP TABLE IF EXISTS `property_tuples`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_tuples` (
  `idtuple` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`idtuple`,`id`),
  KEY `FK_property_tuples_2` (`id`),
  CONSTRAINT `FK_property_tuples_2` FOREIGN KEY (`id`) REFERENCES `property_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_tuple_1` FOREIGN KEY (`idtuple`) REFERENCES `tuples` (`idtuple`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_tuples`
--

LOCK TABLES `property_tuples` WRITE;
/*!40000 ALTER TABLE `property_tuples` DISABLE KEYS */;
INSERT INTO `property_tuples` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(2,14),(2,15),(2,16),(2,17),(2,18),(2,19),(2,20),(2,21),(2,22),(2,23),(2,24),(2,25),(2,26),(3,27),(3,28),(3,29),(3,30),(3,31),(3,32),(3,33),(3,34),(3,35),(3,36),(3,37),(3,38),(3,39),(3,40),(3,41),(3,42),(3,43),(3,44),(3,45),(3,46),(3,47),(3,48),(3,49),(3,50),(3,51),(3,52),(3,53),(3,54),(3,55),(3,56),(3,57),(3,58),(3,59),(3,60),(3,61),(3,62),(3,63),(3,64),(3,65),(3,66),(3,67),(3,68),(3,69),(3,70),(3,71),(3,72),(3,73),(3,74),(3,75),(3,76),(3,77),(3,78),(4,81),(4,83),(4,86),(4,87),(4,88),(4,89),(4,90),(4,91),(4,92),(4,95),(4,96),(4,97),(4,98),(4,100),(4,101),(4,102),(4,103),(4,108),(4,110),(4,111),(4,114),(4,116),(4,118),(4,119),(4,120),(4,125),(4,126),(4,127),(4,130),(4,131),(4,132),(4,133),(4,135),(4,136),(4,137),(4,138),(4,139),(4,140),(5,145),(5,149),(5,151),(5,153),(5,154),(5,155),(5,156),(5,158),(5,159),(5,161),(5,163),(5,165),(5,167),(5,175),(5,176),(5,180),(5,181),(5,184),(5,187),(5,188),(5,189),(5,190),(5,193),(5,196),(5,197),(5,199),(5,201);
/*!40000 ALTER TABLE `property_tuples` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_values`
--

DROP TABLE IF EXISTS `property_values`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_values` (
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
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_values`
--

LOCK TABLES `property_values` WRITE;
/*!40000 ALTER TABLE `property_values` DISABLE KEYS */;
INSERT INTO `property_values` VALUES (1,1,1,1,0,'guest','OK'),(2,2,1,2,0,'guest','OK'),(3,3,1,3,0,'guest','OK'),(4,4,1,4,0,'guest','OK'),(5,5,1,5,0,'guest','OK'),(6,6,1,6,0,'guest','OK'),(7,7,1,7,0,'guest','OK'),(8,8,1,8,0,'guest','OK'),(9,9,1,9,0,'guest','OK'),(10,10,1,10,0,'guest','OK'),(11,11,1,9,0,'guest','OK'),(12,12,1,9,0,'guest','OK'),(13,13,1,11,0,'guest','OK'),(14,1,2,12,0,'guest','OK'),(15,2,2,13,0,'guest','OK'),(16,3,2,14,0,'guest','OK'),(17,4,2,15,0,'guest','OK'),(18,5,2,16,0,'guest','OK'),(19,6,2,17,0,'guest','OK'),(20,7,2,15,0,'guest','OK'),(21,8,2,18,0,'guest','OK'),(22,9,2,19,0,'guest','OK'),(23,10,2,20,0,'guest','OK'),(24,11,2,19,0,'guest','OK'),(25,12,2,19,0,'guest','OK'),(26,13,2,21,0,'guest','OK'),(27,14,3,22,0,'guest','OK'),(28,15,3,23,0,'guest','OK'),(29,16,3,22,0,'guest','OK'),(30,17,3,24,0,'guest','OK'),(31,18,3,25,0,'guest','OK'),(32,19,3,22,0,'guest','OK'),(33,20,3,26,0,'guest','OK'),(34,21,3,25,0,'guest','OK'),(35,22,3,27,0,'guest','OK'),(36,23,3,28,0,'guest','OK'),(37,24,3,29,0,'guest','OK'),(38,25,3,22,0,'guest','OK'),(39,26,3,30,0,'guest','OK'),(40,27,3,25,0,'guest','OK'),(41,28,3,31,0,'guest','OK'),(42,29,3,32,0,'guest','OK'),(43,30,3,33,0,'guest','OK'),(44,31,3,22,0,'guest','OK'),(45,32,3,22,0,'guest','OK'),(46,33,3,25,0,'guest','OK'),(47,34,3,30,0,'guest','OK'),(48,35,3,22,0,'guest','OK'),(49,36,3,28,0,'guest','OK'),(50,37,3,34,0,'guest','OK'),(51,38,3,22,0,'guest','OK'),(52,39,3,22,0,'guest','OK'),(53,40,3,22,0,'guest','OK'),(54,41,3,28,0,'guest','OK'),(55,42,3,22,0,'guest','OK'),(56,43,3,22,0,'guest','OK'),(57,44,3,25,0,'guest','OK'),(58,45,3,28,0,'guest','OK'),(59,46,3,35,0,'guest','OK'),(60,47,3,36,0,'guest','OK'),(61,48,3,37,0,'guest','OK'),(62,49,3,27,0,'guest','OK'),(63,50,3,22,0,'guest','OK'),(64,51,3,38,0,'guest','OK'),(65,52,3,25,0,'guest','OK'),(66,10,3,39,0,'guest','OK'),(67,53,3,40,0,'guest','OK'),(68,12,3,41,0,'guest','OK'),(69,54,3,22,0,'guest','OK'),(70,55,3,36,0,'guest','OK'),(71,56,3,22,0,'guest','OK'),(72,57,3,22,0,'guest','OK'),(73,58,3,25,0,'guest','OK'),(74,59,3,25,0,'guest','OK'),(75,60,3,36,0,'guest','OK'),(76,61,3,22,0,'guest','OK'),(77,62,3,28,0,'guest','OK'),(78,63,3,27,0,'guest','OK'),(81,16,3,25,0,'guest','OK'),(83,18,3,22,0,'guest','OK'),(86,22,3,42,0,'guest','OK'),(87,21,3,22,0,'guest','OK'),(88,23,3,22,0,'guest','OK'),(89,24,3,43,0,'guest','OK'),(90,25,3,25,0,'guest','OK'),(91,64,3,22,0,'guest','OK'),(92,65,3,28,0,'guest','OK'),(95,28,3,22,0,'guest','OK'),(96,29,3,44,0,'guest','OK'),(97,66,3,22,0,'guest','OK'),(98,30,3,45,0,'guest','OK'),(100,32,3,25,0,'guest','OK'),(101,33,3,22,0,'guest','OK'),(102,34,3,27,0,'guest','OK'),(103,67,3,28,0,'guest','OK'),(108,39,3,25,0,'guest','OK'),(110,41,3,22,0,'guest','OK'),(111,42,3,25,0,'guest','OK'),(114,45,3,25,0,'guest','OK'),(116,47,3,30,0,'guest','OK'),(118,49,3,42,0,'guest','OK'),(119,68,3,28,0,'guest','OK'),(120,50,3,25,0,'guest','OK'),(125,12,3,46,0,'guest','OK'),(126,69,3,31,0,'guest','OK'),(127,55,3,45,0,'guest','OK'),(130,57,3,25,0,'guest','OK'),(131,70,3,22,0,'guest','OK'),(132,58,3,47,0,'guest','OK'),(133,71,3,22,0,'guest','OK'),(135,60,3,27,0,'guest','OK'),(136,61,3,25,0,'guest','OK'),(137,72,3,22,0,'guest','OK'),(138,62,3,22,0,'guest','OK'),(139,63,3,30,0,'guest','OK'),(140,73,3,22,0,'guest','OK'),(145,18,3,28,0,'guest','OK'),(149,21,3,31,0,'guest','OK'),(151,24,3,27,0,'guest','OK'),(153,64,3,28,0,'guest','OK'),(154,65,3,22,0,'guest','OK'),(155,26,3,48,0,'guest','OK'),(156,27,3,22,0,'guest','OK'),(158,29,3,49,0,'guest','OK'),(159,66,3,28,0,'guest','OK'),(161,31,3,31,0,'guest','OK'),(163,33,3,28,0,'guest','OK'),(165,67,3,25,0,'guest','OK'),(167,36,3,31,0,'guest','OK'),(175,44,3,28,0,'guest','OK'),(176,45,3,22,0,'guest','OK'),(180,49,3,45,0,'guest','OK'),(181,68,3,22,0,'guest','OK'),(184,52,3,31,0,'guest','OK'),(187,12,3,50,0,'guest','OK'),(188,69,3,28,0,'guest','OK'),(189,55,3,51,0,'guest','OK'),(190,54,3,28,0,'guest','OK'),(193,70,3,25,0,'guest','OK'),(196,59,3,31,0,'guest','OK'),(197,60,3,33,0,'guest','OK'),(199,72,3,25,0,'guest','OK'),(201,63,3,45,0,'guest','OK');
/*!40000 ALTER TABLE `property_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `query`
--

DROP TABLE IF EXISTS `query`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `idsessions` int(10) unsigned NOT NULL,
  `name` varchar(45) collate utf8_bin NOT NULL,
  `content` blob NOT NULL,
  PRIMARY KEY  (`idquery`),
  KEY `FK_query_1` (`idsessions`),
  KEY `Index_3` (`name`),
  CONSTRAINT `FK_query_1` FOREIGN KEY (`idsessions`) REFERENCES `sessions` (`idsessions`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Structure queries';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `query`
--

LOCK TABLES `query` WRITE;
/*!40000 ALTER TABLE `query` DISABLE KEYS */;
/*!40000 ALTER TABLE `query` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `query_results`
--

DROP TABLE IF EXISTS `query_results`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `query_results` (
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
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `query_results`
--

LOCK TABLES `query_results` WRITE;
/*!40000 ALTER TABLE `query_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `query_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `roles` (
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('ambit_admin'),('ambit_guest');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sessions` (
  `idsessions` int(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `started` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `completed` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`idsessions`),
  KEY `FK_sessions_1` (`user_name`),
  CONSTRAINT `FK_sessions_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `skinirritation`
--

DROP TABLE IF EXISTS `skinirritation`;
/*!50001 DROP VIEW IF EXISTS `skinirritation`*/;
/*!50001 CREATE TABLE `skinirritation` (
  `idstructure` int(10) unsigned,
  `idtuple` int(10) unsigned,
  `idproperty` int(10) unsigned,
  `tname` varchar(64),
  `name` varchar(128),
  `value` varchar(200)
) */;

--
-- Table structure for table `src_dataset`
--

DROP TABLE IF EXISTS `src_dataset`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `src_dataset` (
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `src_dataset`
--

LOCK TABLES `src_dataset` WRITE;
/*!40000 ALTER TABLE `src_dataset` DISABLE KEYS */;
INSERT INTO `src_dataset` VALUES (1,'TEST INPUT','guest',3);
/*!40000 ALTER TABLE `src_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `struc_dataset`
--

DROP TABLE IF EXISTS `struc_dataset`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `struc_dataset` (
  `idstructure` int(10) unsigned NOT NULL,
  `id_srcdataset` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `struc_dataset`
--

LOCK TABLES `struc_dataset` WRITE;
/*!40000 ALTER TABLE `struc_dataset` DISABLE KEYS */;
INSERT INTO `struc_dataset` VALUES (1,1),(2,1),(3,1);
/*!40000 ALTER TABLE `struc_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `struc_fgroups`
--

DROP TABLE IF EXISTS `struc_fgroups`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `struc_fgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idfuncgroup`,`idchemical`),
  KEY `FK_struc_fgroups_1` (`idchemical`),
  CONSTRAINT `FK_struc_fgroups_2` FOREIGN KEY (`idfuncgroup`) REFERENCES `funcgroups` (`idfuncgroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_struc_fgroups_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `struc_fgroups`
--

LOCK TABLES `struc_fgroups` WRITE;
/*!40000 ALTER TABLE `struc_fgroups` DISABLE KEYS */;
/*!40000 ALTER TABLE `struc_fgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `structure`
--

DROP TABLE IF EXISTS `structure`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `structure` (
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `structure`
--

LOCK TABLES `structure` WRITE;
/*!40000 ALTER TABLE `structure` DISABLE KEYS */;
INSERT INTO `structure` VALUES (1,1,'f\0\0x��T]o�0}����@����-�H(d�\r�¶��B�V��B7���:/-e��(��{���d��^>��m��/����-�<�~/���X,gՏ�BhiD,���*K�h\\��s�~�B��Rq��q� ��Hp�\n��u�\"���D�C`�j�R����\\������ѡN�H<��)�Z�.[�Eܴ�y��M?�E���Wm�\0x��Or��O��m�g�i�q���r8�7m��O����h��F9���;�ܙ�����Ӊ5B�
�0�3����	Q�8D6D.�gbO���H\n�D:@�D:@�D�W q\r\"�\\P��������L��HB�OF	���$	嘵�֑����9���,M��)+��#��+1���:8Y��V�����$dT��fE:�3gY�}βѼ���g��e^�\\��٘��i�ߗSv�j���Cy��>&�|��bߓty�]��~K���@+�iG�wh�<�Nq���
����a:��V�Q�Jo&	���o.NL2�43,b1��1
��d�h����Aԅxп��Iƪ���D���kp��������8}','SDF','2009-01-15 08:00:16','guest','NA'),(2,2,'9\0\0x��Q�J�0��S��
���$i��0�j
����Bd�Z���I����4�;B��%\'����&���5�>�U�]g��iZ̲U���%����Ѻk&v��PJ�9GDR3f�R\n\r�[���\Zj�H}�l&��J��f��/�,΄l\\����Akv�����yI7w���~��ͷ&\Z��hd�&\Z����i�c2�%���P\Z&��uxD�H����GD�#�FI���#����D�k(��1���#��İ����ݶ*�O�XF�
��i<
��t߇����4}-�����F�_�u��{d�U��X�G��E;���OZ����??��E��n��/�<��Yn���
$���R^�a����
x������','SDF','2009-01-15 08:00:16','guest','NA'),(3,3,'\Z\n\0\0x��T]k�0}ׯ��l�]}X6���t$�&�}=�Q����$����>�V���Ց!+��{t�a<����t��S4��+6F�=�wF)E�\\<��I� 2P2�#F�R�q��iXBUDR�.Uy��!���

8��e*`��^$Dמ���tO^^V���Ah \"~�����س]�\"�X��=����bZ�z>C\r���-��{�F�F\r$ۨ��N4j�ag7�1�B�v�����v6F	�?U��w�&H?��41s�4G\r�#A E�D�D�n��Q����5�,-���k�ݬ�YU����}IP)����1Ѭw�Ȍ��p�����������m�p�4��n��}��)���b��c�	�	�����f�f�4w��$>�;���\'�6�\\��k��hw�+�no����Xi*y��;��;���Mo�O��CqW��5�K��t��k�Y_��tSǷ8P]T@@��:�nAժ�j�ք� ����X�����i����~nF�U�����h�\"�����H?4EF<A�l���aYTy��p��	�C�o��&]F��un׷j����%����SD���������@5a |bl|0�3�1_','SDF','2009-01-15 08:00:16','guest','NA');
/*!40000 ALTER TABLE `structure` ENABLE KEYS */;
UNLOCK TABLES;

/*!50003 SET @SAVE_SQL_MODE=@@SQL_MODE*/;

DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="STRICT_TRANS_TABLES,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,TRADITIONAL,NO_AUTO_CREATE_USER" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `copy_history` BEFORE UPDATE ON `structure` FOR EACH ROW BEGIN
   INSERT INTO HISTORY (idstructure,structure,format,updated,user_name,type_structure)
        SELECT idstructure,structure,format,updated,user_name,type_structure FROM structure
        WHERE structure.idstructure = OLD.idstructure;
END */;;

DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@SAVE_SQL_MODE*/;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_bin default NULL,
  PRIMARY KEY  (`idtemplate`),
  KEY `template_list_index4157` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (1,'ECETOC Skin irritation data'),(2,'EINECS QA results');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_def`
--

DROP TABLE IF EXISTS `template_def`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `order` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idtemplate`,`idproperty`),
  CONSTRAINT `FK_template_def_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `template_def`
--

LOCK TABLES `template_def` WRITE;
/*!40000 ALTER TABLE `template_def` DISABLE KEYS */;
INSERT INTO `template_def` VALUES (1,14,0),(1,16,0),(1,18,0),(1,19,0),(1,21,0),(1,22,0),(1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,47,0),(1,49,0),(1,50,0),(1,52,0),(1,54,0),(1,55,0),(1,56,0),(1,57,0),(1,58,0),(1,59,0),(1,60,0),(1,61,0),(1,62,0),(1,63,0),(1,64,0),(1,65,0),(1,66,0),(1,67,0),(1,68,0),(1,69,0),(1,70,0),(1,71,0),(1,72,0),(1,73,0),(2,2,0),(2,3,0),(2,5,0),(2,6,0),(2,7,0),(2,8,0),(2,9,0);
/*!40000 ALTER TABLE `template_def` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `template_view`
--

DROP TABLE IF EXISTS `template_view`;
/*!50001 DROP VIEW IF EXISTS `template_view`*/;
/*!50001 CREATE TABLE `template_view` (
  `idtemplate` int(10) unsigned,
  `tname` varchar(64),
  `idproperty` int(10) unsigned,
  `name` varchar(128),
  `idreference` int(11) unsigned,
  `units` varchar(16),
  `comments` varchar(128)
) */;

--
-- Table structure for table `tuples`
--

DROP TABLE IF EXISTS `tuples`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tuples` (
  `idtuple` int(10) unsigned NOT NULL auto_increment,
  `id_srcdataset` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`idtuple`),
  KEY `FK_tuples_1` (`id_srcdataset`),
  CONSTRAINT `FK_tuples_1` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tuples`
--

LOCK TABLES `tuples` WRITE;
/*!40000 ALTER TABLE `tuples` DISABLE KEYS */;
INSERT INTO `tuples` VALUES (1,1),(2,1),(3,1),(4,1),(5,1);
/*!40000 ALTER TABLE `tuples` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user_roles` (
  `user_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`user_name`,`role_name`),
  KEY `FK_user_roles_2` (`role_name`),
  CONSTRAINT `FK_user_roles_2` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_roles_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('guest','ambit_guest');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `users` (
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
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('guest','084e0343a0486ff05530df6c705c8bb4','guest','confirmed','2009-01-09 13:47:46',NULL,'\"\"','\"\"','Default guest user','\"\"','\"\"','http://ambit.acad.bg','\"\"','guest',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `values_number`
--

DROP TABLE IF EXISTS `values_number`;
/*!50001 DROP VIEW IF EXISTS `values_number`*/;
/*!50001 CREATE TABLE `values_number` (
  `id` int(10) unsigned,
  `idproperty` int(10) unsigned,
  `idstructure` int(10) unsigned,
  `value` float(10,4),
  `idvalue` int(10) unsigned,
  `status` enum('OK','ERROR'),
  `user_name` varchar(16),
  `idtype` int(10) unsigned
) */;

--
-- Temporary table structure for view `values_string`
--

DROP TABLE IF EXISTS `values_string`;
/*!50001 DROP VIEW IF EXISTS `values_string`*/;
/*!50001 CREATE TABLE `values_string` (
  `id` int(10) unsigned,
  `idproperty` int(10) unsigned,
  `idstructure` int(10) unsigned,
  `value` varchar(200),
  `idvalue` int(10) unsigned,
  `status` enum('OK','ERROR'),
  `user_name` varchar(16),
  `idtype` int(10) unsigned
) */;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `comment` varchar(45) collate utf8_bin default NULL,
  PRIMARY KEY  (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `version`
--

LOCK TABLES `version` WRITE;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
INSERT INTO `version` VALUES (2,1,'2009-01-15 08:00:15','AMBIT2 schema');
/*!40000 ALTER TABLE `version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `ambit-test`
--

USE `ambit-test`;

--
-- Final view structure for view `skinirritation`
--

/*!50001 DROP TABLE `skinirritation`*/;
/*!50001 DROP VIEW IF EXISTS `skinirritation`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `skinirritation` AS select `values_string`.`idstructure` AS `idstructure`,`property_tuples`.`idtuple` AS `idtuple`,`values_string`.`idproperty` AS `idproperty`,`template_view`.`tname` AS `tname`,`properties`.`name` AS `name`,`values_string`.`value` AS `value` from (((`property_tuples` join `values_string` on((`property_tuples`.`id` = `values_string`.`id`))) join `properties` on((`values_string`.`idproperty` = `properties`.`idproperty`))) join `template_view` on((`values_string`.`idproperty` = `template_view`.`idproperty`))) where (`template_view`.`tname` = _utf8'ECETOC Skin irritation data') order by `values_string`.`idstructure`,`property_tuples`.`idtuple`,`values_string`.`idproperty` */;

--
-- Final view structure for view `template_view`
--

/*!50001 DROP TABLE `template_view`*/;
/*!50001 DROP VIEW IF EXISTS `template_view`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `template_view` AS select `template`.`idtemplate` AS `idtemplate`,`template`.`name` AS `tname`,`template_def`.`idproperty` AS `idproperty`,`properties`.`name` AS `name`,`properties`.`idreference` AS `idreference`,`properties`.`units` AS `units`,`properties`.`comments` AS `comments` from ((`template` join `template_def` on((`template`.`idtemplate` = `template_def`.`idtemplate`))) join `properties` on((`template_def`.`idproperty` = `properties`.`idproperty`))) order by `template`.`idtemplate` */;

--
-- Final view structure for view `values_number`
--

/*!50001 DROP TABLE `values_number`*/;
/*!50001 DROP VIEW IF EXISTS `values_number`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `values_number` AS select `property_values`.`id` AS `id`,`property_values`.`idproperty` AS `idproperty`,`property_values`.`idstructure` AS `idstructure`,`property_number`.`value` AS `value`,`property_values`.`idvalue` AS `idvalue`,`property_values`.`status` AS `status`,`property_values`.`user_name` AS `user_name`,`property_values`.`idtype` AS `idtype` from (`property_values` join `property_number` on(((`property_values`.`idvalue` = `property_number`.`idvalue`) and (`property_values`.`idtype` = `property_number`.`idtype`)))) */;

--
-- Final view structure for view `values_string`
--

/*!50001 DROP TABLE `values_string`*/;
/*!50001 DROP VIEW IF EXISTS `values_string`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `values_string` AS select `property_values`.`id` AS `id`,`property_values`.`idproperty` AS `idproperty`,`property_values`.`idstructure` AS `idstructure`,`property_string`.`value` AS `value`,`property_values`.`idvalue` AS `idvalue`,`property_values`.`status` AS `status`,`property_values`.`user_name` AS `user_name`,`property_values`.`idtype` AS `idtype` from (`property_values` join `property_string` on(((`property_values`.`idvalue` = `property_string`.`idvalue`) and (`property_values`.`idtype` = `property_string`.`idtype`)))) */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-01-15  9:08:46