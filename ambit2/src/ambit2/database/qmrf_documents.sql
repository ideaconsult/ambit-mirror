-- MySQL dump 10.11
--
-- Host: localhost    Database: qmrf_documents
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
-- Table structure for table `attachments`
--

DROP TABLE IF EXISTS `attachments`;
CREATE TABLE `attachments` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `name` varchar(64) NOT NULL,
  `description` text NOT NULL,
  `type` enum('data_training','data_validation','document') NOT NULL default 'document',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `idattachment` int(10) unsigned NOT NULL auto_increment,
  `format` varchar(32) NOT NULL default 'txt',
  `original_name` text,
  `imported` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idattachment`),
  UNIQUE KEY `Index_4` USING BTREE (`idqmrf`,`name`),
  KEY `name` (`name`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `attachments_description`
--

DROP TABLE IF EXISTS `attachments_description`;
CREATE TABLE `attachments_description` (
  `idattachment` int(10) unsigned NOT NULL,
  `fieldname` varchar(128) NOT NULL,
  `fieldtype` varchar(45) NOT NULL,
  `newname` varchar(128) NOT NULL,
  PRIMARY KEY  (`idattachment`),
  KEY `Index_2` (`fieldname`),
  CONSTRAINT `FK_attachments_description_1` FOREIGN KEY (`idattachment`) REFERENCES `attachments` (`idattachment`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `catalog_algorithms`
--

DROP TABLE IF EXISTS `catalog_algorithms`;
CREATE TABLE `catalog_algorithms` (
  `id_algorithm` int(10) unsigned NOT NULL auto_increment,
  `definition` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY  (`id_algorithm`),
  UNIQUE KEY `Index_2` (`definition`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `catalog_authors`
--

DROP TABLE IF EXISTS `catalog_authors`;
CREATE TABLE `catalog_authors` (
  `id_author` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '""',
  `affiliation` varchar(128) NOT NULL default '""',
  `address` varchar(128) NOT NULL default '""',
  `webpage` varchar(255) NOT NULL default '""',
  `email` varchar(45) NOT NULL default '""',
  PRIMARY KEY  (`id_author`),
  UNIQUE KEY `Index_3` (`name`,`email`),
  KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `catalog_endpoints`
--

DROP TABLE IF EXISTS `catalog_endpoints`;
CREATE TABLE `catalog_endpoints` (
  `id_endpoint` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) NOT NULL,
  `endpoint_group` varchar(128) NOT NULL,
  `id_group` varchar(6) character set utf8 collate utf8_bin NOT NULL default '""',
  `id_name` varchar(6) character set utf8 collate utf8_bin NOT NULL default '""',
  `subgroup` varchar(64) character set utf8 collate utf8_bin NOT NULL,
  `id_subgroup` varchar(6) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`id_endpoint`),
  UNIQUE KEY `Index_3` USING BTREE (`endpoint_group`,`subgroup`,`name`),
  KEY `Index_2` (`name`),
  KEY `Index_4` USING BTREE (`id_group`,`id_subgroup`,`id_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `catalog_software`
--

DROP TABLE IF EXISTS `catalog_software`;
CREATE TABLE `catalog_software` (
  `id_software` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) character set utf8 collate utf8_bin NOT NULL,
  `description` text NOT NULL,
  `contact` text NOT NULL,
  `url` text NOT NULL,
  PRIMARY KEY  (`id_software`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doc_algorithms`
--

DROP TABLE IF EXISTS `doc_algorithms`;
CREATE TABLE `doc_algorithms` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_algorithm` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '4.2',
  PRIMARY KEY  (`idqmrf`,`id_algorithm`,`chapter`),
  KEY `alg` (`id_algorithm`),
  CONSTRAINT `FK_doc_algorithms_1` FOREIGN KEY (`id_algorithm`) REFERENCES `catalog_algorithms` (`id_algorithm`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doc_authors`
--

DROP TABLE IF EXISTS `doc_authors`;
CREATE TABLE `doc_authors` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_author` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '2.5',
  PRIMARY KEY  (`idqmrf`,`id_author`,`chapter`),
  KEY `auth` (`id_author`),
  CONSTRAINT `doc_alg_ibfk_2` FOREIGN KEY (`id_author`) REFERENCES `catalog_authors` (`id_author`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doc_endpoint`
--

DROP TABLE IF EXISTS `doc_endpoint`;
CREATE TABLE `doc_endpoint` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_endpoint` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '3.2',
  PRIMARY KEY  (`idqmrf`,`id_endpoint`,`chapter`),
  KEY `soft` (`id_endpoint`),
  CONSTRAINT `FK_doc_endpoint_1` FOREIGN KEY (`id_endpoint`) REFERENCES `catalog_endpoints` (`id_endpoint`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doc_software`
--

DROP TABLE IF EXISTS `doc_software`;
CREATE TABLE `doc_software` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_software` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '1.3',
  PRIMARY KEY  (`idqmrf`,`id_software`,`chapter`),
  KEY `soft` (`id_software`),
  CONSTRAINT `FK_doc_software_1` FOREIGN KEY (`id_software`) REFERENCES `catalog_software` (`id_software`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE `documents` (
  `idqmrf` int(10) unsigned NOT NULL auto_increment,
  `qmrf_number` varchar(16) default NULL,
  `idqmrf_origin` int(10) unsigned default NULL,
  `user_name` varchar(16) NOT NULL,
  `xml` text NOT NULL,
  `status` enum('draft','submitted','under review','returned for revision','published','archived') NOT NULL default 'draft',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(3) unsigned NOT NULL default '1',
  `reviewer` varchar(16) default NULL,
  `qmrf_title` varchar(128) NOT NULL,
  PRIMARY KEY  (`idqmrf`),
  UNIQUE KEY `qmrf_number` USING BTREE (`qmrf_number`),
  KEY `status` (`status`),
  KEY `qmrf_ibfk_3` (`user_name`),
  KEY `Index_6` (`qmrf_title`),
  FULLTEXT KEY `xml` (`xml`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `name` varchar(32) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `u_hits`
--

DROP TABLE IF EXISTS `u_hits`;
CREATE TABLE `u_hits` (
  `page` varchar(64) NOT NULL default '',
  `ip` varchar(30) NOT NULL default '',
  `ref` varchar(128) NOT NULL,
  `counter` int(10) unsigned default NULL,
  `last_access` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `first_access` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`page`,`ip`,`ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_name` varchar(16) NOT NULL,
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-03-13 14:55:30
