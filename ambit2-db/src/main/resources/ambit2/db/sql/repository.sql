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

-- --------
-- templates
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE  `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) collate utf8_bin default NULL,
  PRIMARY KEY  (`idtemplate`),
  UNIQUE KEY `template_list_index4157` USING BTREE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (84,NULL),(7,'Acute dermal toxicity'),(8,'Acute inhalation toicity'),(9,'Acute oral toxicity'),(10,'Acute photoirritation'),(11,'Acute toxicity to fish (lethality)'),(12,'Adsorption/Desorption in sediment'),(13,'Adsorption/Desorption in soil '),(14,'Air- water partition coefficient (Henry`s law constant, H)'),(15,'BAF fish'),(16,'BAF other organisms '),(17,'BCF fish'),(18,'BCF other organisms '),(78,'Bioaccumulation'),(79,'Bioconcentration '),(19,'Biodegradation time frame (primary, ultimate degradation)'),(20,'Blood-brain barrier penetration'),(21,'Blood-lung barrier penetration'),(22,'Blood-testis barrier penetration'),(23,'Boiling point'),(89,'CAS Registry number'),(24,'Carcinogenicity'),(91,'Chemical name'),(27,'DNA-binding'),(86,'Descriptors'),(25,'Direct photolysis'),(26,'Dissociation constant (pKa)'),(1,'Ecotoxic effects'),(83,'Endocrine Activity'),(85,'Endpoints'),(2,'Environmental fate parameters '),(28,'Eye irritation/corrosion'),(29,'Gastrointestinal absorption'),(3,'Human health effects'),(30,'Hydrolysis '),(90,'IUPAC name'),(88,'Identifiers'),(31,'In vitro reproductive toxicity (e.g. embryotoxic effects in cell culture such as embryo stem cells)  '),(32,'In vivo pre-, peri-, post natal development and / or fertility (1 or 2 gen. Study or enhanced 1 gen study) '),(33,'In vivo pre-natal-developmental toxicity'),(34,'Indirect photolysis (OH-radical reaction, ozone-radical reaction, other)'),(35,'Long-term toxicity (survival, growth, reproduction)'),(36,'Long-term toxicity to Daphnia (lethality, inhibition of reproduction)'),(37,'Long-term toxicity to fish (egg/sac fry, growth inhibition of juvenile fish, early life stage, full life cycle)'),(38,'Melting point'),(39,'Metabolism (including metabolic clearance)'),(40,'Microbial inhibition (activated sludge respiration inhibition, inhibition of nitrification, other)'),(41,'Mutagenicity '),(42,'Octanol-air partition coefficient (Koa)'),(43,'Octanol-water distribution coefficient (D)'),(44,'Octanol-water partition coefficient (Kow)'),(45,'Ocular membrane penetration'),(46,'Organic carbon-sorption partition coefficient (organic carbon; Koc)'),(4,'Other'),(47,'Other (e.g. inhibition of specific enzymes involved in hormone synthesis or regulation, specify enzyme(s) and hormone)'),(48,'Oxidation '),(80,'Persistence: Abiotic degradation in air (Phototransformation)'),(81,'Persistence: Abiotic degradation in water'),(82,'Persistence: Biodegradation'),(49,'Photocarcinogenicity'),(50,'Photomutagenicity'),(51,'Photosensitisation'),(5,'Physicochemical effects '),(52,'Placental barrier penetration'),(53,'Protein-binding'),(54,'Ready/not ready biodegradability'),(55,'Receptor binding and gene expression (specify receptor)'),(56,'Receptor-binding (specify receptor)'),(57,'Repeated dose toxicity '),(58,'Respiratory sensitisation'),(59,'Short term toxicity (feeding, gavage, other)'),(61,'Short-term toxicity to Daphnia (immobilisation)'),(60,'Short-term toxicity to algae (inhibition of the exponential growth rate)'),(62,'Skin irritation /corrosion'),(63,'Skin penetration'),(64,'Skin sensitisation'),(65,'Surface tension'),(77,'Toxicity to birds'),(66,'Toxicity to earthworms (survival, growth, reproduction)'),(67,'Toxicity to plants (leaves, seed germination, root elongation)'),(68,'Toxicity to sediment organisms (survival, growth, reproduction)'),(69,'Toxicity to soil invertebrates (survival, growth, reproduction)'),(70,'Toxicity to soil microorganisms (inhibition of C-mineralisation, inhibition of N-mineralisation, other)'),(6,'Toxicokinetics '),(71,'Vapour pressure'),(72,'Vegetation-air partition coefficient'),(73,'Vegetation-soil partition coefficient'),(74,'Vegetation-water partition coefficient'),(75,'Water solubility');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `template_def` template definitions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template_def`;
CREATE TABLE  `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `order` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idtemplate`,`idproperty`),
  KEY `FK_template_def_2` (`idproperty`),
  CONSTRAINT `FK_template_def_2` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_template_def_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `dictionary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE  `dictionary` (
  `idsubject` int(10) unsigned NOT NULL,
  `relationship` enum('is_a','is_part_of') collate utf8_bin NOT NULL default 'is_a',
  `idobject` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idsubject`,`relationship`,`idobject`),
  KEY `FK_dictionary_2` (`idobject`),
  CONSTRAINT `FK_dictionary_2` FOREIGN KEY (`idobject`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_dictionary_1` FOREIGN KEY (`idsubject`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (11,'is_a',1),(35,'is_a',1),(36,'is_a',1),(37,'is_a',1),(40,'is_a',1),(59,'is_a',1),(60,'is_a',1),(61,'is_a',1),(66,'is_a',1),(67,'is_a',1),(68,'is_a',1),(69,'is_a',1),(70,'is_a',1),(77,'is_a',1),(4,'is_a',2),(12,'is_a',2),(13,'is_a',2),(19,'is_a',2),(25,'is_a',2),(30,'is_a',2),(34,'is_a',2),(46,'is_a',2),(48,'is_a',2),(54,'is_a',2),(72,'is_a',2),(73,'is_a',2),(74,'is_a',2),(78,'is_a',2),(79,'is_a',2),(80,'is_a',2),(81,'is_a',2),(82,'is_a',2),(7,'is_a',3),(8,'is_a',3),(9,'is_a',3),(10,'is_a',3),(24,'is_a',3),(28,'is_a',3),(31,'is_a',3),(32,'is_a',3),(33,'is_a',3),(41,'is_a',3),(47,'is_a',3),(49,'is_a',3),(50,'is_a',3),(51,'is_a',3),(55,'is_a',3),(56,'is_a',3),(57,'is_a',3),(58,'is_a',3),(62,'is_a',3),(64,'is_a',3),(4,'is_a',4),(14,'is_a',5),(23,'is_a',5),(26,'is_a',5),(38,'is_a',5),(42,'is_a',5),(43,'is_a',5),(44,'is_a',5),(65,'is_a',5),(71,'is_a',5),(75,'is_a',5),(20,'is_a',6),(21,'is_a',6),(22,'is_a',6),(27,'is_a',6),(29,'is_a',6),(39,'is_a',6),(45,'is_a',6),(52,'is_a',6),(53,'is_a',6),(63,'is_a',6),(15,'is_a',78),(16,'is_a',78),(17,'is_a',79),(18,'is_a',79),(85,'is_part_of',84),(86,'is_part_of',84),(88,'is_part_of',84),(1,'is_a',85),(2,'is_a',85),(3,'is_a',85),(4,'is_a',85),(5,'is_a',85),(6,'is_a',85),(89,'is_part_of',88),(90,'is_part_of',88),(91,'is_part_of',88);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

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
  UNIQUE KEY `Index_2` USING BTREE (`idproperty`,`idstructure`,`idtype`),
  KEY `FK_property_values_1` (`user_name`),
  KEY `FK_property_values_2` (`idstructure`),
  KEY `Index_5` USING BTREE (`idvalue`,`idtype`),
  CONSTRAINT `FK_property_values_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
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
insert into version (idmajor,idminor,comment) values (2,2,"AMBIT2 schema");

-- -----------------------------------------------------
-- integer property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_int`;
create view `values_int` as
SELECT id,idproperty,idstructure,value,idvalue,status,user_name,idtype
FROM property_values join property_int using(idvalue,idtype);

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
-- all property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_all`;
create view values_all as
SELECT idstructure,idproperty,name,null as value_string,value as value_number FROM properties join property_values using(idproperty) join property_number using(idvalue,idtype)
union
SELECT idstructure,idproperty,name,value as value_string,null  FROM properties join property_values using(idproperty) join property_string using(idvalue,idtype);

-- -----------------------------------------------------
-- numeric property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_int_float`;
create view values_int_float as
SELECT id,idproperty,idstructure,value,idvalue,status,user_name,idtype,idreference,name FROM properties join property_values using(idproperty) join property_number using(idvalue,idtype)
union
SELECT id,idproperty,idstructure,value,idvalue,status,user_name,idtype,idreference,name FROM properties join property_values using(idproperty) join property_int using(idvalue,idtype);

-- -----------------------------------------------------
-- templates
-- -----------------------------------------------------
create view `template_view` as
SELECT idtemplate,template.name as tname,idproperty,properties.name,idreference,units,comments 
FROM template join template_def using(idtemplate) join properties using(idproperty) 
order by template.idtemplate;

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
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"CAS",1);

insert into catalog_references (idreference,title,url) values (2,"IUPAC name","http://www.iupac.org");
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"IUPAC Name",2);


-- Grants for ambit-test@localhost
-- REVOKE ALL PRIVILEGES ON `ambit-test`.* FROM 'admin'@'localhost';
-- REVOKE ALL PRIVILEGES ON `ambit-test`.* FROM 'guest'@'localhost';
GRANT USAGE ON `ambit-test`.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
GRANT ALL PRIVILEGES ON `ambit-test`.* TO 'admin'@'localhost' WITH GRANT OPTION;
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `ambit-test`.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;