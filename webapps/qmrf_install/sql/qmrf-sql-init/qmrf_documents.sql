CREATE DATABASE `qmrf_documents` /*!40100 DEFAULT CHARACTER SET utf8 */;

use qmrf_documents;

DROP TABLE IF EXISTS `qmrf_documents`.`attachments`;
CREATE TABLE  `qmrf_documents`.`attachments` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `name` varchar(64) NOT NULL,
  `description` text NOT NULL,
  `type` enum('data_training','data_validation','document') NOT NULL default 'document',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `idattachment` int(10) unsigned NOT NULL auto_increment,
  `format` varchar(32) NOT NULL default 'txt',
  `original_name` text,
  `imported` tinyint(1) NOT NULL default '0',
  `content` blob,
  PRIMARY KEY  USING BTREE (`idattachment`),
  UNIQUE KEY `Index_4` USING BTREE (`idqmrf`,`name`),
  KEY `name` (`name`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`attachments_description`;
CREATE TABLE  `qmrf_documents`.`attachments_description` (
  `idattachment` int(10) unsigned NOT NULL auto_increment,
  `fieldname` varchar(128) NOT NULL,
  `fieldtype` varchar(45) NOT NULL,
  `newname` varchar(128) NOT NULL,
  PRIMARY KEY  USING BTREE (`idattachment`,`fieldname`),
  CONSTRAINT `FK_attachments_description_1` FOREIGN KEY (`idattachment`) REFERENCES `attachments` (`idattachment`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`catalog_algorithms`;
CREATE TABLE  `qmrf_documents`.`catalog_algorithms` (
  `id_algorithm` int(10) unsigned NOT NULL auto_increment,
  `definition` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY  (`id_algorithm`),
  UNIQUE KEY `Index_2` (`definition`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`catalog_authors`;
CREATE TABLE  `qmrf_documents`.`catalog_authors` (
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

DROP TABLE IF EXISTS `qmrf_documents`.`catalog_endpoints`;
CREATE TABLE  `qmrf_documents`.`catalog_endpoints` (
  `id_endpoint` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) character set utf8 NOT NULL,
  `endpoint_group` varchar(128) character set utf8 NOT NULL,
  `id_group` varchar(6) collate utf8_bin NOT NULL default '""',
  `id_name` varchar(6) collate utf8_bin NOT NULL default '""',
  `subgroup` varchar(64) collate utf8_bin NOT NULL,
  `id_subgroup` varchar(6) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`id_endpoint`),
  UNIQUE KEY `Index_3` USING BTREE (`endpoint_group`,`subgroup`,`name`),
  KEY `Index_2` (`name`),
  KEY `Index_4` USING BTREE (`id_group`,`id_subgroup`,`id_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `qmrf_documents`.`catalog_software`;
CREATE TABLE  `qmrf_documents`.`catalog_software` (
  `id_software` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) character set utf8 collate utf8_bin NOT NULL,
  `description` text NOT NULL,
  `contact` text NOT NULL,
  `url` text NOT NULL,
  PRIMARY KEY  (`id_software`),
  UNIQUE KEY `Index_2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`catalog_references`;
CREATE TABLE  `qmrf_documents`.`catalog_references` (
  `id_reference` int(10) unsigned NOT NULL auto_increment,
  `title` varchar(255) NOT NULL,
  `url` varchar(45) NOT NULL,
  PRIMARY KEY  (`id_reference`),
  KEY `Index_2` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='References';

DROP TABLE IF EXISTS `qmrf_documents`.`documents`;
CREATE TABLE  `qmrf_documents`.`documents` (
  `idqmrf` int(10) unsigned NOT NULL auto_increment,
  `qmrf_number` varchar(16) default NULL,
  `idqmrf_origin` int(10) unsigned default NULL,
  `user_name` varchar(16) NOT NULL,
  `xml` text NOT NULL,
  `status` enum('draft','submitted','under review','returned for revision','review completed','published','archived') NOT NULL default 'draft',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(3) unsigned NOT NULL default '1',
  `reviewer` varchar(16) default NULL,
  `qmrf_title` varchar(128) default NULL,
  PRIMARY KEY  (`idqmrf`),
  UNIQUE KEY `qmrf_number` USING BTREE (`qmrf_number`),
  KEY `status` (`status`),
  KEY `qmrf_ibfk_3` (`user_name`),
  KEY `Index_6` (`qmrf_title`),
  FULLTEXT KEY `xml` (`xml`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`u_hits`;
CREATE TABLE  `qmrf_documents`.`u_hits` (
  `page` varchar(64) NOT NULL default '',
  `ip` varchar(30) NOT NULL default '',
  `ref` varchar(128) NOT NULL,
  `counter` int(10) unsigned default NULL,
  `last_access` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `first_access` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`page`,`ip`,`ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`users`;
CREATE TABLE  `qmrf_documents`.`users` (
  `user_name` varchar(16) NOT NULL,
  `password` varchar(45) character set utf8 collate utf8_bin NOT NULL,
  `email` varchar(45) NOT NULL,
  `registration_status` enum('commenced','verified','confirmed','deleted') NOT NULL default 'commenced',
  `registration_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `registration_id` blob,
  `title` varchar(6) character set utf8 collate utf8_bin NOT NULL default '""',
  `firstname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `lastname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `address` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `country` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `webpage` varchar(255) character set utf8 collate utf8_bin NOT NULL default '""',
  `affiliation` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `keywords` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `reviewer` tinyint(1) NOT NULL default '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='QMRF Users; status is commenced at registration, verified after user email is verified, confirmed upon approval by an authority';

DROP TABLE IF EXISTS `qmrf_documents`.`doc_algorithms`;
CREATE TABLE  `qmrf_documents`.`doc_algorithms` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_algorithm` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '4.2',
  PRIMARY KEY  (`idqmrf`,`id_algorithm`,`chapter`),
  KEY `alg` (`id_algorithm`),
  CONSTRAINT `FK_doc_algorithms_1` FOREIGN KEY (`id_algorithm`) REFERENCES `catalog_algorithms` (`id_algorithm`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qmrf_documents`.`doc_authors`;
CREATE TABLE  `qmrf_documents`.`doc_authors` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_author` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '2.5',
  PRIMARY KEY  (`idqmrf`,`id_author`,`chapter`),
  KEY `auth` (`id_author`),
  CONSTRAINT `doc_alg_ibfk_2` FOREIGN KEY (`id_author`) REFERENCES `catalog_authors` (`id_author`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qmrf_documents`.`doc_software`;
CREATE TABLE  `qmrf_documents`.`doc_software` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_software` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '1.3',
  PRIMARY KEY  (`idqmrf`,`id_software`,`chapter`),
  KEY `soft` (`id_software`),
  CONSTRAINT `FK_doc_software_1` FOREIGN KEY (`id_software`) REFERENCES `catalog_software` (`id_software`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qmrf_documents`.`doc_endpoint`;
CREATE TABLE  `qmrf_documents`.`doc_endpoint` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_endpoint` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '3.2',
  PRIMARY KEY  (`idqmrf`,`id_endpoint`,`chapter`),
  KEY `soft` (`id_endpoint`),
  CONSTRAINT `FK_doc_endpoint_1` FOREIGN KEY (`id_endpoint`) REFERENCES `catalog_endpoints` (`id_endpoint`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qmrf_documents`.`doc_references`;
CREATE TABLE  `qmrf_documents`.`doc_references` (
  `idqmrf` int(10) unsigned NOT NULL default '0',
  `id_reference` int(10) unsigned NOT NULL default '0',
  `chapter` varchar(6) NOT NULL default '9.2',
  PRIMARY KEY  (`idqmrf`,`id_reference`,`chapter`),
  KEY `alg` (`id_reference`),
  CONSTRAINT `FK_doc_references_1` FOREIGN KEY (`id_reference`) REFERENCES `catalog_references` (`id_reference`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

use qmrf_documents;

LOCK TABLES `catalog_endpoints` WRITE;
/*!40000 ALTER TABLE `catalog_endpoints` DISABLE KEYS */;
INSERT INTO `catalog_endpoints` VALUES (73,'Melting point','Physicochemical effects ','1','1','',''),(74,'Boiling point','Physicochemical effects ','1','2','',''),(75,'Water solubility','Physicochemical effects ','1','3','',''),(76,'Vapour pressure','Physicochemical effects ','1','4','',''),(77,'Surface tension','Physicochemical effects ','1','5','',''),(78,'Octanol-water partition coefficient (Kow)','Physicochemical effects ','1','6','',''),(79,'Octanol-water distribution coefficient (D)','Physicochemical effects ','1','7','',''),(80,'Octanol-air partition coefficient (Koa)','Physicochemical effects ','1','8','',''),(81,'Air- water partition coefficient (Henry`s law constant, H)','Physicochemical effects ','1','9','',''),(82,'Dissociation constant (pKa)','Physicochemical effects ','1','10','',''),(83,'Hydrolysis ','Environmental fate parameters','2','a','Persistence: Abiotic degradation in water','1'),(84,'Oxidation ','Environmental fate parameters','2','b','Persistence: Abiotic degradation in water','1'),(85,'Other ','Environmental fate parameters','2','c','Persistence: Abiotic degradation in water','1'),(86,'Direct photolysis','Environmental fate parameters','2','a','Persistence: Abiotic degradation in air (Phototransformation)','2'),(87,'Indirect photolysis (OH-radical reaction, ozone-radical reaction, other)','Environmental fate parameters','2','b','Persistence: Abiotic degradation in air (Phototransformation)','2'),(88,'Ready/not ready biodegradability','Environmental fate parameters','2','a','Persistence: Biodegradation','3'),(89,'Biodegradation time frame (primary, ultimate degradation)','Environmental fate parameters','2','b','Persistence: Biodegradation','3'),(90,'BCF fish','Environmental fate parameters','2','a','Bioconcentration ','4'),(91,'BCF other organisms ','Environmental fate parameters','2','b','Bioconcentration ','4'),(92,'BAF fish','Environmental fate parameters','2','a','Bioaccumulation','5'),(93,'BAF other organisms ','Environmental fate parameters','2','b','Bioaccumulation','5'),(94,'Organic carbon-sorption partition coefficient (organic carbon; Koc)','Environmental fate parameters ','2','6','',''),(95,'Adsorption/Desorption in soil ','Environmental fate parameters ','2','7','',''),(96,'Adsorption/Desorption in sediment','Environmental fate parameters ','2','8','',''),(97,'Vegetation-water partition coefficient','Environmental fate parameters ','2','9','',''),(98,'Vegetation-air partition coefficient','Environmental fate parameters ','2','10','',''),(99,'Vegetation-soil partition coefficient','Environmental fate parameters ','2','11','',''),(100,'Short-term toxicity to Daphnia (immobilisation)','Ecotoxic effects','3','1','',''),(101,'Short-term toxicity to algae (inhibition of the exponential growth rate)','Ecotoxic effects','3','2','',''),(102,'Acute toxicity to fish (lethality)','Ecotoxic effects','3','3','',''),(103,'Long-term toxicity to Daphnia (lethality, inhibition of reproduction)','Ecotoxic effects','3','4','',''),(104,'Long-term toxicity to fish (egg/sac fry, growth inhibition of juvenile fish, early life stage, full life cycle)','Ecotoxic effects','3','5','',''),(105,'Microbial inhibition (activated sludge respiration inhibition, inhibition of nitrification, other)','Ecotoxic effects','3','6','',''),(106,'Toxicity to soil microorganisms (inhibition of C-mineralisation, inhibition of N-mineralisation, other)','Ecotoxic effects','3','7','',''),(107,'Toxicity to earthworms (survival, growth, reproduction)','Ecotoxic effects','3','8','',''),(108,'Toxicity to plants (leaves, seed germination, root elongation)','Ecotoxic effects','3','9','',''),(109,'Toxicity to soil invertebrates (survival, growth, reproduction)','Ecotoxic effects','3','10','',''),(110,'Toxicity to sediment organisms (survival, growth, reproduction)','Ecotoxic effects','3','11','',''),(111,'Short term toxicity (feeding, gavage, other)','Ecotoxic effects','3','a','Toxicity to birds','12'),(112,'Long-term toxicity (survival, growth, reproduction)','Ecotoxic effects','3','b','Toxicity to birds','12'),(113,'Acute inhalation toicity','Human health effects','4','1','',''),(114,'Acute oral toxicity','Human health effects','4','2','',''),(115,'Acute dermal toxicity','Human health effects','4','3','',''),(116,'Skin irritation /corrosion','Human health effects','4','4','',''),(117,'Acute photoirritation','Human health effects','4','5','',''),(118,'Skin sensitisation','Human health effects','4','6','',''),(119,'Respiratory sensitisation','Human health effects','4','7','',''),(120,'Photosensitisation','Human health effects','4','8','',''),(121,'Eye irritation/corrosion','Human health effects','4','9','',''),(122,'Mutagenicity ','Human health effects','4','10','',''),(123,'Photomutagenicity','Human health effects','4','11','',''),(124,'Carcinogenicity','Human health effects','4','12','',''),(125,'Photocarcinogenicity','Human health effects','4','13','',''),(126,'Repeated dose toxicity ','Human health effects','4','14','',''),(127,'In vitro reproductive toxicity (e.g. embryotoxic effects in cell culture such as embryo stem cells)  ','Human health effects','4','15','',''),(128,'In vivo pre-natal-developmental toxicity','Human health effects','4','16','',''),(129,'In vivo pre-, peri-, post natal development and / or fertility (1 or 2 gen. Study or enhanced 1 gen study) ','Human health effects','4','17','',''),(130,'Receptor-binding (specify receptor)','Human health effects','4','a','Endocrine Activity','18'),(131,'Receptor binding and gene expression (specify receptor)','Human health effects','4','b','Endocrine Activity','18'),(132,'Other (e.g. inhibition of specific enzymes involved in hormone synthesis or regulation, specify enzyme(s) and hormone)','Human health effects','4','c','Endocrine Activity','18'),(133,'Skin penetration','Toxicokinetics ','5','1','',''),(134,'Ocular membrane penetration','Toxicokinetics ','5','2','',''),(135,'Gastrointestinal absorption','Toxicokinetics ','5','3','',''),(136,'Blood-brain barrier penetration','Toxicokinetics ','5','4','',''),(137,'Placental barrier penetration','Toxicokinetics ','5','5','',''),(138,'Blood-testis barrier penetration','Toxicokinetics ','5','6','',''),(139,'Blood-lung barrier penetration','Toxicokinetics ','5','7','',''),(140,'Metabolism (including metabolic clearance)','Toxicokinetics ','5','8','',''),(141,'Protein-binding','Toxicokinetics ','5','9','',''),(142,'DNA-binding','Toxicokinetics ','5','10','',''),(143,'Other','Other','6','6','','');
UPDATE catalog_endpoints set id_endpoint=id_endpoint-72;
/*!40000 ALTER TABLE `catalog_endpoints` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` values ('editor',"4f0d4a73e581a9e08f2ce26a59582428",'nina@acad.bg','confirmed',now(),'','','QMRF','Editor','','','','','',1);
INSERT INTO `users` values ('guest',"4f0d4a73e581a9e08f2ce26a59582428",'nina@acad.bg','confirmed',now(),'','','Guest','User','','','','','',0);
INSERT INTO `users` values ('admin',"90ccd6798c380a61df628a73895c5fb2",'nina@acad.bg','confirmed',now(),'','','Admin','User','','','','','',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

