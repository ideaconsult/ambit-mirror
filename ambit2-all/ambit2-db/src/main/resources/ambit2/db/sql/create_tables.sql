SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- -----------------------------------------------------
-- Table `ausers` Users
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ausers`;
CREATE TABLE `ausers` (
  `user_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `email` varchar(45) NOT NULL,
  `title` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `firstname` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `lastname` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `address` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `country` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `homepage` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `institute` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `keywords` varchar(128) DEFAULT '""',
  `reviewer` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `references`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `catalog_references`;
CREATE TABLE  `catalog_references` (
  `idreference` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` enum('Unknown','Dataset','Algorithm','Model','BibtexEntry','BibtexArticle','BibtexBook','Feature') NOT NULL DEFAULT 'Dataset',
  PRIMARY KEY (`idreference`),
  UNIQUE KEY `Index_2` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- -----------------------------------------------------
-- Table `chemicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chemicals`;
CREATE TABLE  `chemicals` (
  `idchemical` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `inchi` text CHARACTER SET latin1 COLLATE latin1_bin,
  `smiles` text CHARACTER SET latin1 COLLATE latin1_bin,
  `formula` varchar(64) DEFAULT NULL,
  `inchikey` varchar(27) DEFAULT NULL,
  `label` enum('OK','UNKNOWN','ERROR') NOT NULL DEFAULT 'UNKNOWN',
  `lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idchemical`),
  KEY `index_smiles` (`smiles`(760)) USING BTREE,
  KEY `index_idchemical` (`idchemical`) USING BTREE,
  KEY `index_inchi` (`inchi`(767)) USING BTREE,
  KEY `index_formula` (`formula`) USING BTREE,
  KEY `index_inchikey` (`inchikey`) USING BTREE,
  KEY `index_label` (`label`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `chem_relation` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chem_relation`;
CREATE TABLE  `chem_relation` (
  `idchemical1` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idchemical2` int(10) unsigned NOT NULL,
  `relation` varchar(64) NOT NULL,
  `metric` double DEFAULT NULL,
  PRIMARY KEY (`idchemical1`,`idchemical2`,`relation`),
  KEY `FK_chem_relation_2` (`idchemical2`),
  CONSTRAINT `FK_chem_relation_1` FOREIGN KEY (`idchemical1`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chem_relation_2` FOREIGN KEY (`idchemical2`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `substance_owner` (company submitted the substance dossier)
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `substance_owner`;
-- CREATE TABLE `substance_owner` (
--   `owner_prefix` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
--   `owner_uuid` varbinary(16) NOT NULL DEFAULT '',
--   `name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'Human readable name of the entry',
--   PRIMARY KEY (`owner_prefix`,`owner_uuid`),
--   KEY `name-x` (`name`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Substance owner (company)';

-- -----------------------------------------------------
-- Table `substance` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance`;
CREATE TABLE `substance` (
  `idsubstance` int(11) NOT NULL AUTO_INCREMENT,
  `prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ECB5 in I5 UUIDs like ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3',
  `uuid` varbinary(16) DEFAULT NULL COMMENT 'The UUID part of  I5 UUIDs in binary format',
  `documentType` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'documentTypeType from I5 XSD schema ',
  `format` varchar(6) COLLATE utf8_bin DEFAULT 'i5._4.',
  `name` text COLLATE utf8_bin COMMENT 'Human readable name of the entry',
  `publicname` text COLLATE utf8_bin,
  `content` blob,
  `substanceType` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `rs_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ReferenceSubstance UUID (prefix)',
  `rs_uuid` varbinary(16) DEFAULT NULL COMMENT 'ReferenceSubstance UUID',
  `owner_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL,
  `owner_uuid` varbinary(16) DEFAULT NULL,
  `owner_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idsubstance`),
  UNIQUE KEY `uuid-x` (`prefix`,`uuid`),
  KEY `doxType-x` (`documentType`),
  KEY `format-x` (`format`),
  KEY `stype-x` (`substanceType`),
  KEY `rs-uuid-x` (`rs_uuid`,`rs_prefix`),
  KEY `owner-uuid-x` (`owner_prefix`,`owner_uuid`),
  KEY `owner-name` (`owner_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Substance dossier (mainly to support IUCLID5)';

-- -----------------------------------------------------
-- Table `substance_ids` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_ids`;
CREATE TABLE `substance_ids` (
  `prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `uuid` varbinary(16) DEFAULT NULL,
  `type` varchar(64) NOT NULL,
  `id` varchar(64) NOT NULL,
  PRIMARY KEY (`prefix`,`uuid`,`type`,`id`),
  KEY `systemid` (`type`,`id`),
  CONSTRAINT `substanceids-x` FOREIGN KEY (`prefix`, `uuid`) REFERENCES `substance` (`prefix`, `uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `substance_relation` 
-- -----------------------------------------------------

DROP TABLE IF EXISTS `substance_relation`;
CREATE TABLE `substance_relation` (
  `cmp_prefix` varchar(6) COLLATE utf8_bin NOT NULL COMMENT 'Composition UUID prefix',
  `cmp_uuid` varbinary(16) NOT NULL COMMENT 'Composition UUID',
  `idsubstance` int(11) NOT NULL,
  `idchemical` int(11) unsigned NOT NULL,
  `relation` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower_value` double DEFAULT NULL,
  `proportion_real_upper` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_upper_value` double DEFAULT NULL,
  `proportion_real_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical_value` double DEFAULT NULL,
  `proportion_typical_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `rs_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ReferenceSubstance UUID (prefix)',
  `rs_uuid` varbinary(16) DEFAULT NULL COMMENT 'ReferenceSubstance UUID',
  `name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'composition name',
  PRIMARY KEY (`cmp_prefix`,`cmp_uuid`,`idsubstance`,`idchemical`,`relation`) USING BTREE,
  KEY `chemicalkey` (`idchemical`),
  KEY `relation-x` (`relation`),
  KEY `crs-uuid-x` (`rs_uuid`,`rs_prefix`),
  KEY `cmp-uuid-x` (`cmp_prefix`,`cmp_uuid`),
  KEY `idsubstance` (`idsubstance`),
  CONSTRAINT `chemicalkey` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idsubstance` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Dossier to chemicals relation';


-- -----------------------------------------------------
-- Table `substance_protocolapplication` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_protocolapplication`;
CREATE TABLE `substance_protocolapplication` (
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `guidance` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `params` text NOT NULL,
  `interpretation_result` varchar(128) DEFAULT NULL,
  `interpretation_criteria` text,
  `reference` text CHARACTER SET utf8 COLLATE utf8_bin,
  `reference_year` smallint(6) DEFAULT NULL,
  `reference_owner` varchar(128) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reliability` varchar(45) DEFAULT NULL COMMENT 'Klimish code (text) \n1 (reliable without restriction)\n2 (reliable with restrictions)\n3 (not reliable)\n4 (not assignable)\nother:\nempty (not specified)',
  `isRobustStudy` tinyint(1) DEFAULT NULL,
  `isUsedforClassification` tinyint(1) DEFAULT NULL,
  `isUsedforMSDS` tinyint(1) DEFAULT NULL,
  `purposeFlag` varchar(32) DEFAULT NULL,
  `studyResultType` varchar(128) DEFAULT NULL COMMENT 'experimental result\nestimated by calculation\nread-across\n(Q)SAR',
  PRIMARY KEY (`document_prefix`,`document_uuid`),
  KEY `substance` (`substance_prefix`,`substance_uuid`),
  KEY `endpoint` (`endpoint`),
  KEY `category` (`endpointcategory`),
  KEY `reference_owner` (`reference_owner`),
  KEY `reference-x` (`reference`(255)),
  KEY `topcategory` (`topcategory`,`endpointcategory`,`interpretation_result`),
  KEY `xse` (`substance_prefix`,`substance_uuid`,`topcategory`,`endpointcategory`),
  CONSTRAINT `substance-x` FOREIGN KEY (`substance_prefix`, `substance_uuid`) REFERENCES `substance` (`prefix`, `uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `substance_experiment`
-- this is intentionally denormalized table
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_experiment`;
CREATE TABLE `substance_experiment` (
  `idresult` int(11) NOT NULL AUTO_INCREMENT,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpointhash` varbinary(20) DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions',
  `endpoint` varchar(64) DEFAULT NULL,
  `conditions` text,
  `unit` varchar(45) DEFAULT NULL,
  `loQualifier` varchar(6) DEFAULT NULL,
  `loValue` double DEFAULT NULL,
  `upQualifier` varchar(6) DEFAULT NULL,
  `upValue` double DEFAULT NULL,
  `textValue` text,
  `errQualifier` varchar(6) DEFAULT NULL,
  `err` double DEFAULT NULL,
  `substance_prefix` varchar(6) DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  PRIMARY KEY (`idresult`),
  KEY `document_id` (`document_uuid`,`document_prefix`),
  KEY `endpoint` (`endpoint`),
  KEY `document-x` (`document_prefix`,`document_uuid`),
  KEY `hash-x` (`endpointhash`),
  KEY `category-x` (`topcategory`,`endpointcategory`,`endpoint`,`endpointhash`),
  KEY `substance-x` (`substance_prefix`,`substance_uuid`),
  CONSTRAINT `document-x` FOREIGN KEY (`document_prefix`, `document_uuid`) REFERENCES `substance_protocolapplication` (`document_prefix`, `document_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- A collection of substances and endpoints 
-- metadata
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bundle`;
CREATE TABLE `bundle` (
  `idbundle` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT 'default',
  `user_name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `idreference` int(11) unsigned NOT NULL,  
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `licenseURI` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `rightsHolder` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `maintainer` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `stars` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`idbundle`),
  UNIQUE KEY `assessment_name` (`name`),
  KEY `FK_assessment_1` (`user_name`),
  KEY `Index_6` (`maintainer`),
  KEY `Index_7` (`stars`),
  CONSTRAINT `FK_investigation_ref` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- A collection of substances 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bundle_substance`;
CREATE TABLE `bundle_substance` (
  `idsubstance` int(11) NOT NULL,
  `idbundle` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `substance_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  PRIMARY KEY (`idsubstance`,`idbundle`),
  UNIQUE KEY `u_sunstance_idx` (`substance_prefix`,`substance_uuid`,`idbundle`),
  KEY `s_bundle` (`idbundle`),
  KEY `a_substance_idx` (`idsubstance`),
  CONSTRAINT `a_metadata` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `a_substance` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- A collection of endpoints 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bundle_endpoints`;
CREATE TABLE `bundle_endpoints` (
  `idbundle` int(10) unsigned NOT NULL,
  `topcategory` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `endpointcategory` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT '',
  `endpointhash` varbinary(20) DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idbundle`,`topcategory`,`endpointcategory`,`endpointhash`),
  KEY `btopcategory` (`topcategory`,`endpointcategory`,`endpointhash`) USING BTREE,
  CONSTRAINT `b_metadata` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- A collection of chemicals 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bundle_chemicals`;
CREATE TABLE `bundle_chemicals` (
  `idbundle` int(10) unsigned NOT NULL,
  `idchemical` int(11) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tag` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `remarks` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idchemical`,`idbundle`),
  KEY `k_bundle` (`idbundle`),
  KEY `k_substance_idx` (`idchemical`),
  CONSTRAINT `c_chemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `c_metadata` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `bundle_substance_protocolapplication` 
-- -----------------------------------------------------
drop table if exists `bundle_substance_protocolapplication`;
CREATE TABLE `bundle_substance_protocolapplication` (
  `idbundle` int(11) unsigned NOT NULL,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `guidance` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `params` text NOT NULL,
  `interpretation_result` varchar(128) DEFAULT NULL,
  `interpretation_criteria` text,
  `reference` text CHARACTER SET utf8 COLLATE utf8_bin,
  `reference_year` smallint(6) DEFAULT NULL,
  `reference_owner` varchar(128) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reliability` varchar(45) DEFAULT NULL COMMENT 'Klimish code (text) \n1 (reliable without restriction)\n2 (reliable with restrictions)\n3 (not reliable)\n4 (not assignable)\nother:\nempty (not specified)',
  `isRobustStudy` tinyint(1) DEFAULT NULL,
  `isUsedforClassification` tinyint(1) DEFAULT NULL,
  `isUsedforMSDS` tinyint(1) DEFAULT NULL,
  `purposeFlag` varchar(32) DEFAULT NULL,
  `studyResultType` varchar(128) DEFAULT NULL COMMENT 'experimental result\nestimated by calculation\nread-across\n(Q)SAR',
  `copied` tinyint(4) DEFAULT '0',
  `deleted` tinyint(4) DEFAULT '0',
  `remarks` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idbundle`,`document_prefix`,`document_uuid`),
  KEY `bsubstance` (`substance_prefix`,`substance_uuid`),
  KEY `bendpoint` (`endpoint`),
  KEY `bcategory` (`endpointcategory`),
  KEY `breference_owner` (`reference_owner`),
  KEY `breference-x` (`reference`(255)),
  KEY `btopcategory` (`topcategory`,`endpointcategory`,`interpretation_result`),
  KEY `bxse` (`substance_prefix`,`substance_uuid`,`topcategory`,`endpointcategory`),
  KEY `idbundle_idx` (`idbundle`),
  CONSTRAINT `idbundle` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bsubstance-p` FOREIGN KEY (`substance_prefix`, `substance_uuid`) REFERENCES `substance` (`prefix`, `uuid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `bundle_substance_experiment`
-- -----------------------------------------------------
drop table if exists `bundle_substance_experiment`;
CREATE TABLE `bundle_substance_experiment` (
  `idbundle` int(11) unsigned NOT NULL,
  `idresult` int(11) NOT NULL AUTO_INCREMENT,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpointhash` varbinary(20) DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions',
  `endpoint` varchar(64) DEFAULT NULL,
  `conditions` text,
  `unit` varchar(45) DEFAULT NULL,
  `loQualifier` varchar(6) DEFAULT NULL,
  `loValue` double DEFAULT NULL,
  `upQualifier` varchar(6) DEFAULT NULL,
  `upValue` double DEFAULT NULL,
  `textValue` text,
  `errQualifier` varchar(6) DEFAULT NULL,
  `err` double DEFAULT NULL,
  `substance_prefix` varchar(6) DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `copied` tinyint(4) DEFAULT '0',
  `deleted` tinyint(4) DEFAULT '0',
  `remarks` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idresult`,`idbundle`),
  KEY `bdocument_id` (`idbundle`,`document_uuid`,`document_prefix`),
  KEY `bendpoint` (`endpoint`),
  KEY `bdocument-x` (`idbundle`,`document_prefix`,`document_uuid`),
  KEY `bhash-x` (`endpointhash`),
  KEY `bcategory-x` (`topcategory`,`endpointcategory`,`endpoint`,`endpointhash`),
  KEY `bsubstance-x` (`substance_prefix`,`substance_uuid`),
  KEY `idb_idx` (`idbundle`),
  CONSTRAINT `idb` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bdocument-x` FOREIGN KEY (`idbundle`, `document_prefix`, `document_uuid`) REFERENCES `bundle_substance_protocolapplication` (`idbundle`, `document_prefix`, `document_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- -----------------------------------------------------
-- Final matrix bundle_final_protocolapplication
-- -----------------------------------------------------
drop table if exists `bundle_final_protocolapplication`;
CREATE TABLE `bundle_final_protocolapplication` (
  `idbundle` int(11) unsigned NOT NULL,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `guidance` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `params` text NOT NULL,
  `interpretation_result` varchar(128) DEFAULT NULL,
  `interpretation_criteria` text,
  `reference` text CHARACTER SET utf8 COLLATE utf8_bin,
  `reference_year` smallint(6) DEFAULT NULL,
  `reference_owner` varchar(128) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reliability` varchar(45) DEFAULT NULL COMMENT 'Klimish code (text) \n1 (reliable without restriction)\n2 (reliable with restrictions)\n3 (not reliable)\n4 (not assignable)\nother:\nempty (not specified)',
  `isRobustStudy` tinyint(1) DEFAULT NULL,
  `isUsedforClassification` tinyint(1) DEFAULT NULL,
  `isUsedforMSDS` tinyint(1) DEFAULT NULL,
  `purposeFlag` varchar(32) DEFAULT NULL,
  `studyResultType` varchar(128) DEFAULT NULL COMMENT 'experimental result\nestimated by calculation\nread-across\n(Q)SAR',
  `copied` tinyint(4) DEFAULT '0',
  `deleted` tinyint(4) DEFAULT '0',
  `remarks` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idbundle`,`document_prefix`,`document_uuid`),
  KEY `fsubstance` (`substance_prefix`,`substance_uuid`),
  KEY `fendpoint` (`endpoint`),
  KEY `fcategory` (`endpointcategory`),
  KEY `freference_owner` (`reference_owner`),
  KEY `freference-x` (`reference`(255)),
  KEY `ftopcategory` (`topcategory`,`endpointcategory`,`interpretation_result`),
  KEY `fxse` (`substance_prefix`,`substance_uuid`,`topcategory`,`endpointcategory`),
  KEY `fidbundle_idx` (`idbundle`),
  CONSTRAINT `fidbundle` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fsubstance-p` FOREIGN KEY (`substance_prefix`, `substance_uuid`) REFERENCES `substance` (`prefix`, `uuid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Final matrix bundle_final_experiment
-- -----------------------------------------------------
drop table if exists `bundle_final_experiment`;
CREATE TABLE `bundle_final_experiment` (
  `idbundle` int(11) unsigned NOT NULL,
  `idresult` int(11) NOT NULL AUTO_INCREMENT,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(45) DEFAULT NULL,
  `endpointhash` varbinary(20) DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions',
  `endpoint` varchar(64) DEFAULT NULL,
  `conditions` text,
  `unit` varchar(45) DEFAULT NULL,
  `loQualifier` varchar(6) DEFAULT NULL,
  `loValue` double DEFAULT NULL,
  `upQualifier` varchar(6) DEFAULT NULL,
  `upValue` double DEFAULT NULL,
  `textValue` text,
  `errQualifier` varchar(6) DEFAULT NULL,
  `err` double DEFAULT NULL,
  `substance_prefix` varchar(6) DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `copied` tinyint(4) DEFAULT '0',
  `deleted` tinyint(4) DEFAULT '0',
  `remarks` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idresult`,`idbundle`),
  KEY `fdocument_id` (`idbundle`,`document_uuid`,`document_prefix`),
  KEY `fendpoint` (`endpoint`),
  KEY `fdocument-x` (`idbundle`,`document_prefix`,`document_uuid`),
  KEY `fhash-x` (`endpointhash`),
  KEY `fcategory-x` (`topcategory`,`endpointcategory`,`endpoint`,`endpointhash`),
  KEY `fsubstance-x` (`substance_prefix`,`substance_uuid`),
  KEY `fidb_idx` (`idbundle`),
  CONSTRAINT `fidb` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fdocument-x` FOREIGN KEY (`idbundle`, `document_prefix`, `document_uuid`) REFERENCES `bundle_final_protocolapplication` (`idbundle`, `document_prefix`, `document_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `structure` ;
CREATE TABLE `structure` (
  `idstructure` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `idchemical` int(11) unsigned NOT NULL,
  `structure` blob NOT NULL,
  `format` enum('SDF','CML','MOL','INC','NANO') COLLATE utf8_bin NOT NULL DEFAULT 'SDF',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `type_structure` enum('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental','NANO') COLLATE utf8_bin NOT NULL DEFAULT 'NA',
  `label` enum('OK','UNKNOWN','ERROR') COLLATE utf8_bin NOT NULL DEFAULT 'UNKNOWN' COMMENT 'quality label',
  `atomproperties` blob,
  `preference` int(10) unsigned NOT NULL DEFAULT '9999',
  `hash` varbinary(20) DEFAULT NULL,
  PRIMARY KEY (`idstructure`),
  KEY `FK_structure_2` (`user_name`),
  KEY `idchemical` (`idchemical`) USING BTREE,
  KEY `Index_4` (`label`),
  KEY `Index_5` (`idstructure`,`user_name`),
  KEY `Index_6` (`idchemical`,`preference`,`idstructure`) USING BTREE,
  KEY `Index_pref` (`preference`,`idchemical`) USING BTREE,
  KEY `Index_hash` (`hash`),
  CONSTRAINT `fk_idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- DELIMITER $
-- CREATE TRIGGER copy_history BEFORE UPDATE ON structure
-- FOR EACH ROW BEGIN
--   INSERT INTO history (idstructure,structure,format,updated,user_name,type_structure,label)
--        SELECT idstructure,structure,format,updated,user_name,type_structure,label FROM structure
--        WHERE structure.idstructure = OLD.idstructure;
--  END $
-- DELIMITER ;

-- -----------------------------------------------------
-- Procedure to move structures from one chemical to another
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `moveStructure`;
DELIMITER $
CREATE PROCEDURE moveStructure(

     IN chemical_from INTEGER,
     IN chemical_to INTEGER
)
BEGIN
--  update summary_property_chemicals set idchemical=chemical_to where idchemical=chemical_from;
  insert ignore into summary_property_chemicals select chemical_to,id_ci,idproperty from summary_property_chemicals where idchemical=chemical_from;
  delete from summary_property_chemicals where idchemical=chemical_from;
  update property_values set idchemical=chemical_to where idchemical=chemical_from;
  update structure set idchemical=chemical_to where idchemical=chemical_from;
  update query_results set idchemical=chemical_to where idchemical=chemical_from;
  delete from chemicals where idchemical=chemical_from;
 
END $

DELIMITER ;

-- -----------------------------------------------------
-- Table `properties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `properties`;
CREATE TABLE  `properties` (
  `idproperty` int(10) unsigned NOT NULL auto_increment,
  `idreference` int(11) unsigned NOT NULL default '0',
  `name` varchar(255) collate utf8_bin NOT NULL default '',
  `units` varchar(16) collate utf8_bin NOT NULL default '',
  `comments` varchar(255) collate utf8_bin NOT NULL default '',
  `islocal` tinyint(1) NOT NULL default '0',
  `ptype` set('STRING','NUMERIC') COLLATE utf8_bin DEFAULT null,
  PRIMARY KEY  USING BTREE (`idproperty`),
  UNIQUE KEY `ddictionary_name` USING BTREE (`name`,`idreference`),
  KEY `ddictionary_idref` (`idreference`),
  CONSTRAINT `FK_properties_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_annotation` minimal ontology annotation for properties
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_annotation`;
CREATE TABLE `property_annotation` (
  `idproperty` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'subject',
  `rdf_type` varchar(45) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Feature',
  `predicate` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'property',
  `object` varchar(180) CHARACTER SET utf8 NOT NULL COMMENT 'object',
  PRIMARY KEY (`idproperty`,`rdf_type`,`predicate`,`object`) USING BTREE,
  KEY `Index_2` (`predicate`,`object`) USING BTREE,
  CONSTRAINT `FK_property_annotation_1` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `property_string` string values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_string`;
CREATE TABLE  `property_string` (
  `idvalue_string` int(10) unsigned NOT NULL auto_increment,
  `value` varchar(255) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idvalue_string`),
  UNIQUE KEY `Index_3` (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `template` defines templates
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE  `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) collate utf8_bin default NULL,
  PRIMARY KEY  (`idtemplate`),
  UNIQUE KEY `template_list_index4157` USING BTREE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (84,NULL),(7,'Acute dermal toxicity'),(8,'Acute inhalation toxicity'),(9,'Acute oral toxicity'),(10,'Acute photoirritation'),(11,'Acute toxicity to fish (lethality)'),(12,'Adsorption/Desorption in sediment'),(13,'Adsorption/Desorption in soil '),(14,'Air- water partition coefficient (Henry`s law constant, H)'),(15,'BAF fish'),(16,'BAF other organisms '),(17,'BCF fish'),(18,'BCF other organisms '),(78,'Bioaccumulation'),(79,'Bioconcentration '),(19,'Biodegradation time frame (primary, ultimate degradation)'),(20,'Blood-brain barrier penetration'),(21,'Blood-lung barrier penetration'),(22,'Blood-testis barrier penetration'),(23,'Boiling point'),(89,'CasRN'),(24,'Carcinogenicity'),(91,'Names'),(27,'DNA-binding'),(86,'Descriptors'),(25,'Direct photolysis'),(26,'Dissociation constant (pKa)'),(1,'Ecotoxic effects'),(83,'Endocrine Activity'),(85,'Endpoints'),(2,'Environmental fate parameters '),(28,'Eye irritation/corrosion'),(29,'Gastrointestinal absorption'),(3,'Human health effects'),(30,'Hydrolysis '),(90,'IUPAC name'),(88,'Identifiers'),(31,'In vitro reproductive toxicity (e.g. embryotoxic effects in cell culture such as embryo stem cells)  '),(32,'In vivo pre-, peri-, post natal development and / or fertility (1 or 2 gen. Study or enhanced 1 gen study) '),(33,'In vivo pre-natal-developmental toxicity'),(34,'Indirect photolysis (OH-radical reaction, ozone-radical reaction, other)'),(35,'Long-term toxicity (survival, growth, reproduction)'),(36,'Long-term toxicity to Daphnia (lethality, inhibition of reproduction)'),(37,'Long-term toxicity to fish (egg/sac fry, growth inhibition of juvenile fish, early life stage, full life cycle)'),(38,'Melting point'),(39,'Metabolism (including metabolic clearance)'),(40,'Microbial inhibition (activated sludge respiration inhibition, inhibition of nitrification, other)'),(41,'Mutagenicity '),(42,'Octanol-air partition coefficient (Koa)'),(43,'Octanol-water distribution coefficient (D)'),(44,'Octanol-water partition coefficient (Kow)'),(45,'Ocular membrane penetration'),(46,'Organic carbon-sorption partition coefficient (organic carbon; Koc)'),(4,'Other'),(47,'Other (e.g. inhibition of specific enzymes involved in hormone synthesis or regulation, specify enzyme(s) and hormone)'),(48,'Oxidation '),(80,'Persistence: Abiotic degradation in air (Phototransformation)'),(81,'Persistence: Abiotic degradation in water'),(82,'Persistence: Biodegradation'),(49,'Photocarcinogenicity'),(50,'Photomutagenicity'),(51,'Photosensitisation'),(5,'Physicochemical effects '),(52,'Placental barrier penetration'),(53,'Protein-binding'),(54,'Ready/not ready biodegradability'),(55,'Receptor binding and gene expression (specify receptor)'),(56,'Receptor-binding (specify receptor)'),(57,'Repeated dose toxicity '),(58,'Respiratory sensitisation'),(59,'Short term toxicity (feeding, gavage, other)'),(61,'Short-term toxicity to Daphnia (immobilisation)'),(60,'Short-term toxicity to algae (inhibition of the exponential growth rate)'),(62,'Skin irritation /corrosion'),(63,'Skin penetration'),(64,'Skin sensitisation'),(65,'Surface tension'),(77,'Toxicity to birds'),(66,'Toxicity to earthworms (survival, growth, reproduction)'),(67,'Toxicity to plants (leaves, seed germination, root elongation)'),(68,'Toxicity to sediment organisms (survival, growth, reproduction)'),(69,'Toxicity to soil invertebrates (survival, growth, reproduction)'),(70,'Toxicity to soil microorganisms (inhibition of C-mineralisation, inhibition of N-mineralisation, other)'),(6,'Toxicokinetics '),(71,'Vapour pressure'),(72,'Vegetation-air partition coefficient'),(73,'Vegetation-soil partition coefficient'),(74,'Vegetation-water partition coefficient'),(75,'Water solubility'),(92,'Dataset');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `template_def` template definitions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template_def`;
CREATE TABLE  `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `order` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`idtemplate`,`idproperty`) USING BTREE,
  KEY `FK_template_def_2` (`idproperty`),
  CONSTRAINT `FK_template_def_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_template_def_2` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
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
INSERT INTO `dictionary` VALUES (11,'is_a',1),(35,'is_a',1),(36,'is_a',1),(37,'is_a',1),(40,'is_a',1),(59,'is_a',1),(60,'is_a',1),(61,'is_a',1),(66,'is_a',1),(67,'is_a',1),(68,'is_a',1),(69,'is_a',1),(70,'is_a',1),(77,'is_a',1),(4,'is_a',2),(12,'is_a',2),(13,'is_a',2),(19,'is_a',2),(25,'is_a',2),(30,'is_a',2),(34,'is_a',2),(46,'is_a',2),(48,'is_a',2),(54,'is_a',2),(72,'is_a',2),(73,'is_a',2),(74,'is_a',2),(78,'is_a',2),(79,'is_a',2),(80,'is_a',2),(81,'is_a',2),(82,'is_a',2),(7,'is_a',3),(8,'is_a',3),(9,'is_a',3),(10,'is_a',3),(24,'is_a',3),(28,'is_a',3),(31,'is_a',3),(32,'is_a',3),(33,'is_a',3),(41,'is_a',3),(47,'is_a',3),(49,'is_a',3),(50,'is_a',3),(51,'is_a',3),(55,'is_a',3),(56,'is_a',3),(57,'is_a',3),(58,'is_a',3),(62,'is_a',3),(64,'is_a',3),(4,'is_a',4),(14,'is_a',5),(23,'is_a',5),(26,'is_a',5),(38,'is_a',5),(42,'is_a',5),(43,'is_a',5),(44,'is_a',5),(65,'is_a',5),(71,'is_a',5),(75,'is_a',5),(20,'is_a',6),(21,'is_a',6),(22,'is_a',6),(27,'is_a',6),(29,'is_a',6),(39,'is_a',6),(45,'is_a',6),(52,'is_a',6),(53,'is_a',6),(63,'is_a',6),(15,'is_a',78),(16,'is_a',78),(17,'is_a',79),(18,'is_a',79),(85,'is_part_of',84),(86,'is_part_of',84),(88,'is_part_of',84),(1,'is_a',85),(2,'is_a',85),(3,'is_a',85),(4,'is_a',85),(5,'is_a',85),(6,'is_a',85),(89,'is_a',88),(90,'is_a',88),(91,'is_a',88),(92,'is_part_of',84);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

insert into template values (null,"Models");
insert into dictionary (idsubject,relationship,idobject)
SELECT t1.idtemplate,"is_a",t2.idtemplate FROM template t1
join template t2
where t1.name = "Models" and t2.name is null;

-- -----------------------------------------------------
DROP TABLE IF EXISTS `models`;
CREATE TABLE  `models` (
  `idmodel` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `idquery` int(10) unsigned DEFAULT NULL COMMENT 'dataset',
  `predictors` int(10) unsigned NOT NULL COMMENT 'template for predictors',
  `dependent` int(10) unsigned NOT NULL COMMENT 'template for dependent variables',
  `content` longblob NOT NULL,
  `algorithm` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT 'N/A' COMMENT 'URI of the algorithm',
  `mediatype` varchar(48) COLLATE utf8_bin NOT NULL DEFAULT 'application/java' COMMENT 'Content formats: JAVA_CLASS, WEKA_BASE64, PMML',
  `parameters` text COLLATE utf8_bin COMMENT 'Model parameters',
  `predicted` int(10) unsigned NOT NULL COMMENT 'template for predicted variables',
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `creator` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT 'guest',
  `dataset` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'dataset uri',
  `user_name` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT 'guest',
  `stars` int(10) unsigned NOT NULL DEFAULT '5' COMMENT 'stars rating',
  PRIMARY KEY (`idmodel`),
  UNIQUE KEY `Index_5` (`name`) USING BTREE,
  KEY `FK_models_predictors` (`predictors`),
  KEY `FK_models_dataset` (`idquery`),
  KEY `FK_models_dependent` (`dependent`),
  KEY `Index_6` (`algorithm`),
  KEY `Index_7` (`parameters`(255)),
  KEY `FK_models_predicted` (`predicted`),
  KEY `Index_creator` (`creator`),
  KEY `Index_10` (`dataset`),
  KEY `FK_models_users` (`user_name`),
  KEY `Index_12` (`stars`),
  CONSTRAINT `FK_models_dataset` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_dependent` FOREIGN KEY (`dependent`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_predicted` FOREIGN KEY (`predicted`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_predictors` FOREIGN KEY (`predictors`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE
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
-- v 3.2 value_num changed to double instead of double(14,4)
-- v 4.2 added idchemical field (regression - double back to double 14,4!)  
-- v 4.3 added index on idchemical and idproperty
-- v 5.1 value_num changed to double instead of double(14,4)  
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_values`;
CREATE TABLE `property_values` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idproperty` int(10) unsigned NOT NULL,
  `idstructure` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL,
  `user_name` varchar(16) COLLATE utf8_bin NOT NULL,
  `status` enum('OK','UNKNOWN','ERROR','TRUNCATED') COLLATE utf8_bin NOT NULL DEFAULT 'UNKNOWN',
  `text` text COLLATE utf8_bin,
  `idvalue_string` int(10) unsigned DEFAULT NULL,
  `value_num` double DEFAULT NULL,
  `idtype` enum('STRING','NUMERIC') COLLATE utf8_bin NOT NULL DEFAULT 'STRING',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_1` (`idproperty`,`idstructure`) USING BTREE,
  KEY `FK_property_values_1` (`user_name`),
  KEY `FK_property_values_2` (`idstructure`),
  KEY `Index_2` (`value_num`),
  KEY `FK_property_values_5` (`idvalue_string`),
  KEY `Index_3` (`idproperty`,`idtype`) USING BTREE,
  KEY `Index_8` (`idproperty`,`idvalue_string`),
  KEY `Index_12` (`idchemical`,`idproperty`),
  CONSTRAINT `FK_property_values_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_5` FOREIGN KEY (`idvalue_string`) REFERENCES `property_string` (`idvalue_string`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_6` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_pairstruc` values, assigned for pair of structures
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_pairstruc`;
CREATE TABLE  `property_pairstruc` (
  `idstructure1` int(10) unsigned NOT NULL auto_increment COMMENT 'First structure id',
  `idstructure2` int(10) unsigned NOT NULL COMMENT 'Second structure id',
  `idproperty` int(10) unsigned NOT NULL COMMENT 'Property id',
  `user_name` varchar(16) collate utf8_bin NOT NULL COMMENT 'User',
  `status` enum('OK','UNKNOWN','ERROR','TRUNCATED') collate utf8_bin NOT NULL,
  `text` text collate utf8_bin COMMENT 'Text value, if longer than allowed by property_string',
  `idvalue_string` int(10) unsigned NOT NULL COMMENT 'link to property_string',
  `value_num` double NOT NULL COMMENT 'numeric value',
  `idtype` enum('STRING','NUMERIC') collate utf8_bin NOT NULL,
  PRIMARY KEY  USING BTREE (`idstructure1`,`idstructure2`,`idproperty`),
  KEY `FK_relationship_struc_2` (`idstructure2`),
  KEY `FK_relationship_struc_3` (`idproperty`),
  KEY `FK_relationship_struc_4` (`user_name`),
  KEY `FK_relationship_struc_5` (`idvalue_string`),
  CONSTRAINT `FK_relationship_struc_1` FOREIGN KEY (`idstructure1`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_2` FOREIGN KEY (`idstructure2`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`),
  CONSTRAINT `FK_relationship_struc_5` FOREIGN KEY (`idvalue_string`) REFERENCES `property_string` (`idvalue_string`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `quality_labels` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_labels`;
CREATE TABLE  `quality_labels` (
  `id` int(10) unsigned NOT NULL,
  `sameas` varchar(255) COLLATE utf8_bin NOT NULL,
  `label` enum('OK','ProbablyOK','Unknown','ProbablyERROR','ERROR') COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `text` text COLLATE utf8_bin,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`sameas`) USING BTREE,
  KEY `FK_quality_labels_2` (`sameas`),
  KEY `FK_quality_labels_3` (`label`),
  CONSTRAINT `FK_quality_labels_1` FOREIGN KEY (`id`) REFERENCES `property_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Assign quality label for a set of properties, sameas "property"
-- for chemicals given by "dataset"
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `qvalues`;
DELIMITER $$

CREATE PROCEDURE qvalues(IN dataset INTEGER,IN property VARCHAR(255))
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL

BEGIN

DECLARE no_more_rows BOOLEAN;
DECLARE chemical INTEGER;

DECLARE thedataset CURSOR FOR
SELECT idchemical FROM struc_dataset join structure using(idstructure) where id_srcdataset=dataset group by idchemical;

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;  
 
OPEN thedataset;

delete from quality_labels where sameas =property;

the_loop: LOOP

	FETCH thedataset into chemical;
	IF no_more_rows THEN
		CLOSE thedataset;
		LEAVE the_loop;
	END IF;

	insert into quality_labels
	select id,property,
	if(count(distinct(id))=k,"OK",IF(count(distinct(id))>(k-count(distinct(id))),"ProbablyOK","ProbablyERROR")),
	concat(count(distinct(id)),"/",k),now() from property_values
	join properties using (idproperty)
	join struc_dataset using (idstructure)
	left join property_string using(idvalue_string)
	join
	(
	select count(distinct(id)) k from property_values
	join properties using (idproperty)
	join struc_dataset using (idstructure)
	left join property_string using(idvalue_string)
	where comments regexp concat("^",property)
	and id_srcdataset=dataset
	and idchemical=chemical
	) a
	where comments regexp concat("^",property)
	and id_srcdataset=dataset
	and idchemical=chemical
	group by `value`;
	

END LOOP the_loop;   

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Table `quality_pair` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_pair`;
CREATE TABLE  `quality_pair` (
  `idchemical` int(10) unsigned NOT NULL auto_increment,
  `idstructure` int(10) unsigned NOT NULL,
  `rel` int(10) unsigned NOT NULL default '0' COMMENT 'number of same structures',
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `TEXT` text collate utf8_bin,
  PRIMARY KEY  (`idchemical`,`idstructure`),
  KEY `FK_qpair_1` (`user_name`),
  KEY `FK_qpair_3` (`idstructure`),
  KEY `Index_4` (`TEXT`(255)),
  KEY `Index_5` USING BTREE (`idchemical`,`rel`),
  CONSTRAINT `FK_qpair_2` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_qpair_3` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `quality_structure` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_chemicals`;
CREATE TABLE  `quality_chemicals` (
  `idchemical` int(10) unsigned NOT NULL auto_increment,
  `num_sources` int(10) unsigned NOT NULL,
  `label` enum('Consensus','Majority','Unconfirmed','Ambiguous','Unknown') collate utf8_bin NOT NULL default 'Unknown',
  `num_structures` varchar(45) collate utf8_bin NOT NULL default '0',
  `text` varchar(255) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idchemical`),
  KEY `Index_4` (`num_structures`),
  KEY `Index_2` USING BTREE (`label`,`text`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- -----------------------------------------------------
-- Table `quality_structure` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_structure`;
CREATE TABLE  `quality_structure` (
  `idstructure` int(10) unsigned NOT NULL,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `label` enum('OK','ProbablyOK','Unknown','ProbablyERROR','ERROR') collate utf8_bin NOT NULL default 'Unknown',
  `text` text collate utf8_bin,
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  USING BTREE (`idstructure`,`user_name`),
  KEY `FK_quality_struc_2` (`user_name`),
  KEY `FK_quality_struc_3` (`label`),
  CONSTRAINT `FK_quality_struc_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
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
-- Table `src_dataset` datasets
-- -----------------------------------------------------
DROP TABLE IF EXISTS `src_dataset`;
CREATE TABLE  `src_dataset` (
  `id_srcdataset` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT 'default',
  `user_name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `idreference` int(11) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idtemplate` int(10) unsigned DEFAULT NULL,
  `licenseURI` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `rightsHolder` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `maintainer` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `stars` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`),
  KEY `FK_src_dataset_1` (`user_name`),
  KEY `FK_src_dataset_2` (`idreference`),
  KEY `FK_src_dataset_3` (`idtemplate`),
  KEY `Index_6` (`maintainer`),
  KEY `Index_7` (`stars`),
  CONSTRAINT `FK_src_dataset_2` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE,
  CONSTRAINT `FK_src_dataset_3` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ------------------------------------------------------------------------------------------
-- Triggers to duplicate info of properties used in a dataset via template/template_def table
-- ------------------------------------------------------------------------------------------
DELIMITER $

-- -----------------------------------------------------
-- Trigger to create template entry for a dataset
-- -----------------------------------------------------
CREATE DEFINER = CURRENT_USER TRIGGER insert_dataset_template BEFORE INSERT ON src_dataset
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO template (name) values (NEW.name);
    SET NEW.idtemplate = (SELECT idtemplate FROM template where template.name=NEW.name);
END $

DELIMITER ;

-- -----------------------------------------------------
-- Trigger to add property entry to template_def 
-- -----------------------------------------------------
DELIMITER $

CREATE DEFINER = CURRENT_USER TRIGGER insert_property_tuple AFTER INSERT ON property_tuples
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO template_def (idtemplate,idproperty,`order`) (
    SELECT idtemplate,idproperty,idproperty FROM
      (SELECT idtemplate FROM src_dataset join tuples using(id_srcdataset) WHERE idtuple=NEW.idtuple) a
      JOIN
      (SELECT idproperty from property_values WHERE id=NEW.id) b
    ) ;
 END $
 
DELIMITER ;
 
-- -----------------------------------------------------
-- Table `struc_dataset` structures per dataset
-- -----------------------------------------------------
DROP TABLE IF EXISTS `struc_dataset`;
CREATE TABLE IF NOT EXISTS  `struc_dataset` (
  `idstructure` int(10) unsigned NOT NULL,
  `id_srcdataset` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1` 
  	FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2` 
  	FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `sessions` User sessions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE  `sessions` (
  `idsessions` int(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `started` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `completed` timestamp NOT NULL DEFAULT '2010-01-01 01:01:01',
  `title` varchar(45) collate utf8_bin NOT NULL default 'temp',
  PRIMARY KEY  (`idsessions`),
  UNIQUE KEY `Index_3` USING BTREE (`title`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `query` User queries per session, now used as /dataset/R*
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query`;
CREATE TABLE  `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `idsessions` int(10) unsigned NOT NULL,
  `name` text collate utf8_bin NOT NULL,
  `content` text collate utf8_bin NOT NULL,
  `idtemplate` int(10) unsigned default NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY  (`idquery`),
  UNIQUE KEY `Index_3` USING BTREE (`name`(255),`idsessions`),
  KEY `FK_query_1` (`idsessions`),
  KEY `FK_query_2` (`idtemplate`),
  CONSTRAINT `FK_query_2` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
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
  `metric` float(10,6) default NULL,
  `text` varchar(200) collate utf8_bin default NULL,
  PRIMARY KEY  (`idquery`,`idchemical`,`idstructure`),
  KEY `FK_query_results_2` (`idstructure`),
  KEY `FK_query_results_3` (`idchemical`),
  KEY `Index_4` USING BTREE (`idquery`,`metric`),
  KEY `Index_5` (`idquery`),
  KEY `Index_6` (`idquery`,`text`),
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
  KEY `FK_funcgroups_1` (`user_name`)
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
-- Table `pc1024` PUBCHEM fingerprints (and more)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `pc1024`;
CREATE TABLE `pc1024` (
  `idchemical` int(10) unsigned NOT NULL DEFAULT '0',
  `fp1` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp2` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp3` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp4` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp5` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp6` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp7` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp8` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp9` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp10` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp11` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp12` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp13` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp14` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp15` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp16` bigint(20) unsigned NOT NULL DEFAULT '0',
  `time` int(10) unsigned DEFAULT '0',
  `bc` int(6) NOT NULL DEFAULT '0',
  `status` enum('invalid','valid','error') COLLATE utf8_bin NOT NULL DEFAULT 'invalid',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  PRIMARY KEY (`idchemical`),
  KEY `pcall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `pc1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
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
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL default '0000000000',
  PRIMARY KEY  (`idchemical`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `fp1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `fp1024_struc` 1024 length hashed fingerprints
-- -----------------------------------------------------

DROP TABLE IF EXISTS `fp1024_struc`;
CREATE TABLE  `fp1024_struc` (
  `idchemical` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
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
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL default '0000000000',
  PRIMARY KEY  USING BTREE (`idchemical`,`idstructure`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  KEY `fp1024struc_ibfk_2` (`idstructure`),
  CONSTRAINT `fp1024struc_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fp1024struc_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `sk1024` 1024 structural keys
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sk1024`;
CREATE TABLE  `sk1024` (
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
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,  
  `bc` int(6) NOT NULL default '0',
  `status` enum('invalid','valid','error') collate utf8_bin NOT NULL default 'invalid',
  PRIMARY KEY  (`idchemical`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `sk1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
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
-- atom environment strings (single level)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fpae`;
CREATE TABLE  `fpae` (
  `idfpae` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ae` varchar(255) CHARACTER SET latin1 NOT NULL DEFAULT '',
  PRIMARY KEY (`idfpae`),
  UNIQUE KEY `ae` (`ae`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- -----------------------------------------------------
-- atom environment of a chemical. 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fpaechemicals`;
CREATE TABLE  `fpaechemicals` (
  `idchemical` int(10) unsigned NOT NULL DEFAULT '0',
  `idfpae1` int(10) unsigned NOT NULL,
  `idfpae2` int(10) unsigned NOT NULL,
  `idfpae3` int(10) unsigned NOT NULL,
  `idfpae4` int(10) unsigned NOT NULL,
  `idfpae5` int(10) unsigned NOT NULL,
  `idfpae6` int(10) unsigned NOT NULL,
  `status` enum('valid','invalid','error') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'valid',
  `freq` int(10) unsigned NOT NULL DEFAULT '1',
  `atom` varchar(6) COLLATE latin1_bin NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idchemical`,`atom`) USING BTREE,
  KEY `FK_fpaelevels_1` (`idfpae1`),
  KEY `FK_fpaelevels_2` (`idfpae2`),
  KEY `FK_fpaelevels_3` (`idfpae3`),
  KEY `FK_fpaelevels_4` (`idfpae4`),
  KEY `FK_fpaelevels_5` (`idfpae5`),
  KEY `FK_fpaelevels_6` (`idfpae6`),
  KEY `Index_8` (`status`),
  KEY `Index_9` (`atom`,`idfpae1`,`idfpae2`,`idfpae3`,`idfpae4`,`idfpae5`,`idfpae6`),
  CONSTRAINT `FK_fpaelevels_1` FOREIGN KEY (`idfpae1`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_fpaelevels_2` FOREIGN KEY (`idfpae2`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_fpaelevels_3` FOREIGN KEY (`idfpae3`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_fpaelevels_4` FOREIGN KEY (`idfpae4`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_fpaelevels_5` FOREIGN KEY (`idfpae5`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_fpaelevels_6` FOREIGN KEY (`idfpae6`) REFERENCES `fpae` (`idfpae`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- -----------------------------------------------------
-- Procedure to add atom environments
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `setAtomEnvironment`;
DELIMITER $
CREATE PROCEDURE setAtomEnvironment(

     IN chemical_id INTEGER,
     IN chemical_atom VARCHAR(6),
     IN ae_freq INT,
     IN ae_1 VARCHAR(255),
     IN ae_2 VARCHAR(255),
     IN ae_3 VARCHAR(255),
     IN ae_4 VARCHAR(255),
     IN ae_5 VARCHAR(255),
     IN ae_6 VARCHAR(255),
     IN ae_status VARCHAR(10)
)
BEGIN
  insert ignore into fpae (ae) values (ae_1),(ae_2),(ae_3),(ae_4),(ae_5),(ae_6);

  insert into fpaechemicals
      (idchemical,
      atom,
      freq,
      idfpae1,
      idfpae2,
      idfpae3,
      idfpae4,
      idfpae5,
      idfpae6,
      `status`
  )
  SELECT chemical_id,
      chemical_atom,
      ae_freq,
      a1.idfpae,
      a2.idfpae,
      a3.idfpae,
      a4.idfpae,
      a5.idfpae,
      a6.idfpae,
      ae_status
  FROM fpae a1
  join (SELECT idfpae FROM fpae where ae= ae_2) as a2
  join (SELECT idfpae FROM fpae where ae= ae_3) as a3
  join (SELECT idfpae FROM fpae where ae= ae_4) as a4
  join (SELECT idfpae FROM fpae where ae= ae_5) as a5
  join (SELECT idfpae FROM fpae where ae= ae_6) as a6
  where a1.ae=ae_1
  ON DUPLICATE KEY UPDATE
    freq =values(freq),
    idfpae1=values(idfpae1),
    idfpae2=values(idfpae2),
    idfpae3=values(idfpae3),
    idfpae4=values(idfpae4),
    idfpae5=values(idfpae5),
    idfpae6=values(idfpae6),
    `status`=values(`status`)
  ;
END $

DELIMITER ;

-- -----------------------------------------------------
-- Table `bookmarks` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE  `bookmark` (
  `idbookmark` int(10) unsigned NOT NULL auto_increment,
  `creator` varchar(45) collate utf8_bin NOT NULL COMMENT 'dc:creator',
  `recalls` text collate utf8_bin NOT NULL COMMENT 'b:recalls Relates the bookmark with the resource that has been bookmarked. ',
  `hasTopic` varchar(255) collate utf8_bin NOT NULL COMMENT 'b:hasTopic Associates the bookmark with a Topic ',
  `title` varchar(45) collate utf8_bin NOT NULL COMMENT 'dc:title',
  `description` text collate utf8_bin NOT NULL COMMENT 'dc:description',
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT 'a:created The date and time on which the bookmark was created. Format should be YYYY-MM-DDTHH:MM[:SS]TZD (see [DATETIME])',
  `date` timestamp NOT NULL DEFAULT '2010-01-01 01:01:01' COMMENT 'dc:date The date and time on which the bookmark was last modified. Format should be YYYY-MM-DDTHH:MM[:SS]TZD (see [DATETIME])',
  PRIMARY KEY (`idbookmark`),
  KEY `Index_4` (`creator`,`hasTopic`,`date`) USING BTREE,
  KEY `Index_3` (`hasTopic`) USING BTREE,
  KEY `Index_2` (`creator`,`hasTopic`,`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------------------------------------------------------------------------
-- Table `property_ci` Case insensitive properties, (almost) a duplicate of property_string table
-- ----------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `property_ci`;
CREATE TABLE  `property_ci` (
  `id_ci` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `value_ci` varchar(255) NOT NULL,
  PRIMARY KEY (`id_ci`),
  UNIQUE KEY `Index_3` (`value_ci`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------------------------------------------------------------
-- Table `summary_property_chemicals` 
-- Similar to property_values, but properties are assigned to idchemical, rather than idstructure
-- Speeds up 'select distinct idchemical' queries 
-- ----------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS `summary_property_chemicals`;
CREATE TABLE  `summary_property_chemicals` (
  `idchemical` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_ci` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  PRIMARY KEY (`idchemical`,`id_ci`,`idproperty`),
  KEY `FK_ppci_2` (`id_ci`),
  KEY `FK_ppci_3` (`idproperty`),
  KEY `Index_4` (`idchemical`,`idproperty`),
  CONSTRAINT `FK_ppci_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ppci_2` FOREIGN KEY (`id_ci`) REFERENCES `property_ci` (`id_ci`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ppci_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP trigger IF EXISTS insert_string_ci;
DROP trigger IF EXISTS update_string_ci;
DROP trigger IF EXISTS summary_chemical_prop_insert;
DROP trigger IF EXISTS summary_chemical_prop_update;

DELIMITER $

CREATE DEFINER = CURRENT_USER TRIGGER insert_string_ci AFTER INSERT ON property_string
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO property_ci (value_ci) values (NEW.value);
 END $

DELIMITER ;
 
DELIMITER $
 
CREATE DEFINER = CURRENT_USER TRIGGER update_string_ci AFTER UPDATE ON property_string
 FOR EACH ROW BEGIN
    UPDATE property_ci set value_ci=NEW.value where value_ci=OLD.value;
 END $
 
DELIMITER ;
 
DELIMITER $

 CREATE DEFINER = CURRENT_USER TRIGGER summary_chemical_prop_insert AFTER INSERT ON property_values
 FOR EACH ROW BEGIN
    UPDATE properties set ptype=CONCAT_WS(',',ptype,NEW.idtype)  where idproperty=NEW.idproperty;
    
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
 END $

DELIMITER ;
 
DELIMITER $ 

 CREATE DEFINER = CURRENT_USER TRIGGER summary_chemical_prop_update AFTER UPDATE ON property_values
 FOR EACH ROW BEGIN
 	UPDATE properties set ptype=CONCAT_WS(',',ptype,NEW.idtype)  where idproperty=NEW.idproperty;
 
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
 END $
DELIMITER ;

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
insert into version (idmajor,idminor,comment) values (8,7,"AMBIT2 schema");

-- -----------------------------------------------------
-- Sorts comma separated strings
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `sortstring`;

DELIMITER $
CREATE FUNCTION `sortstring`(inString TEXT) RETURNS TEXT DETERMINISTIC
BEGIN
  DECLARE delim CHAR(1) DEFAULT ',';
  DECLARE strings INT DEFAULT 0;     -- number of substrings
  DECLARE forward INT DEFAULT 1;     -- index for traverse forward thru substrings
  DECLARE backward INT;   -- index for traverse backward thru substrings, position in calc. substrings
  DECLARE remain TEXT;               -- work area for calc. no of substrings
  DECLARE swap1 TEXT;                 -- left substring to swap
  DECLARE swap2 TEXT;                 -- right substring to swap
  SET remain = inString;
  SET backward = LOCATE(delim, remain);
  WHILE backward != 0 DO
    SET strings = strings + 1;
    SET backward = LOCATE(delim, remain);
    SET remain = SUBSTRING(remain, backward+1);
  END WHILE;
  IF strings < 2 THEN RETURN inString; END IF;
  REPEAT
    SET backward = strings;
    REPEAT
      SET swap1 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward-1),delim,-1);
      SET swap2 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward),delim,-1);
      IF  swap1 > swap2 THEN
        SET inString = TRIM(BOTH delim FROM CONCAT_WS(delim
        ,SUBSTRING_INDEX(inString,delim,backward-2)
        ,swap2,swap1
        ,SUBSTRING_INDEX(inString,delim,(backward-strings))));
      END IF;
      SET backward = backward - 1;
    UNTIL backward < 2 END REPEAT;
    SET forward = forward +1;
  UNTIL forward + 1 > strings
  END REPEAT;
RETURN inString;
END $
DELIMITER ;

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------

DROP FUNCTION IF EXISTS `sql_xtab`;
DELIMITER $$

CREATE FUNCTION sql_xtab(property_num INT,property_nom INT, query INT,bins DOUBLE) RETURNS TEXT READS SQL 
DATA 
begin
   set @x="";
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') as \"bins',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',value,'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by value
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from query_results','\n'
,'		join property_values using(idstructure) \n'
,'		where idproperty = ',property_num,' and idquery=',query,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select value as b,idchemical from query_results','\n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) \n'
,'		where idproperty = ',property_nom,' and idquery=',query,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from query_results join property_values using(idstructure) left join property_string using(idvalue_string) 
    where idproperty = property_nom and idquery=query;

    return @x;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `sql_dataset_xtab`;
DELIMITER $$

CREATE FUNCTION sql_dataset_xtab(property_num INT,property_nom INT, dataset INT,bins DOUBLE) RETURNS TEXT 
READS SQL DATA 
begin
   DECLARE x TEXT;
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') as \"bins',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',ifnull(text,value),'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by ifnull(text,value)
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from struc_dataset \n'
,'		join property_values using(idstructure) \n'
,'		where idproperty = ',property_num,' and id_srcdataset=',dataset,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select ifnull(text,value) as b,idchemical from struc_dataset \n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) \n'
,'		where idproperty = ',property_nom,' and id_srcdataset=',dataset,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from struc_dataset join property_values using(idstructure) left join property_string using(idvalue_string)
    where idproperty = property_nom and id_srcdataset=dataset;

    return @x;
end $$

DELIMITER ;

DROP PROCEDURE IF EXISTS `p_dataset_xtab`;
DELIMITER $$

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
CREATE PROCEDURE p_dataset_xtab(IN property_num INT,property_nom INT,q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_dataset_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- Generates cross tab view  given numerical and nominal property
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `p_xtab`;
DELIMITER $$

CREATE PROCEDURE p_xtab(IN property_num INT,property_nom INT,q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;


-- ------------------------------------------------------------------------------------------------------
-- Procedure to create entries in template/template_def tables, storing features per dataset
-- This is redundant information, but necessary to speed up queries retrieving properties for a dataset
-- ------------------------------------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS `copy_dataset_features`;

DELIMITER $$
CREATE PROCEDURE `copy_dataset_features`()
    READS SQL DATA
 BEGIN  
DECLARE no_more_rows BOOLEAN;
DECLARE dataset_id INTEGER;
DECLARE dataset_name VARCHAR(255);
DECLARE template_id INTEGER;

DECLARE datasets CURSOR FOR
SELECT id_srcdataset,name,idtemplate FROM src_dataset ;

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;  

SELECT "open";

OPEN datasets;

SELECT "start loop";
the_loop: LOOP

	FETCH datasets into dataset_id,dataset_name,template_id;
	IF no_more_rows THEN
		CLOSE datasets;
		LEAVE the_loop;
	END IF;
	
	SELECT dataset_name,dataset_id,template_id;
	
	IF template_id IS NULL THEN
	  INSERT IGNORE INTO template (idtemplate,name) values (null,dataset_name);
	
	  UPDATE src_dataset, template SET src_dataset.idtemplate=template.idtemplate
	  WHERE id_srcdataset=dataset_id AND template.name=src_dataset.name;
	ELSE
	  DELETE FROM template_def WHERE idtemplate= template_id;
	END IF;
	
	INSERT IGNORE into template_def (idtemplate,idproperty,`order`)
	SELECT idtemplate,idproperty,idproperty
	FROM property_values
	JOIN property_tuples using(id)
	JOIN tuples using (idtuple)
	JOIN src_dataset using(id_srcdataset)
	WHERE id_srcdataset=dataset_id
	GROUP by idproperty;

END LOOP the_loop;   

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Find chemical by identifier or inchi. 
-- Search mode -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
-- Example call findByProperty(0,3,"http://www.opentox.org/api/1.1#CASRN","",0,"",0,"InChI=1/C9H14N2O3/c1-4-9(5-2)6(12)10-8(14)11(3)7(9)13/h4-5H2,1-3H3,(H,10,12,14)",0);
-- -----------------------------------------------------

DROP PROCEDURE IF EXISTS `findByProperty`;
DELIMITER $$
CREATE PROCEDURE `findByProperty`(
                IN chemical_id INT,
                IN search_mode INT,  -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
                IN property_name VARCHAR(255),
                IN property_alias VARCHAR(255),
                IN property_id INT,
                IN query_value TEXT,  -- string value or inchi
                IN query_value_num DOUBLE,
                IN query_inchi TEXT,
                IN maxrows INT)
    READS SQL DATA
BEGIN
    DECLARE property_id INT DEFAULT -1;
    SET @found = null;
    IF (chemical_id<=0) THEN
      SET chemical_id = 0;
      IF (!isnull(query_value)) THEN
      -- text
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci p using(id_ci) where name = property_name and value_ci = query_value limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci using(id_ci) where comments = property_alias and value_ci = query_value limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci) where idproperty = property_id and value_ci = query_value limit 1;
          WHEN 3 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci)  where value_ci = query_value limit 1;
          WHEN 5 THEN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchikey = query_inchi limit 1;
          ELSE BEGIN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
          END;
          END CASE;
      ELSE
      -- numeric
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where name = property_name and value_num = query_value_num limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where comments = property_alias and value_num = query_value_num limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values
            where idproperty = property_id and value_num = query_value_num limit 1;
          ELSE BEGIN
            select  idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values where value_num = query_value_num limit 1;
          END;
          END CASE;
      END IF;
    END IF;
    -- fallback
    if ((chemical_id<=0) and !isnull(query_inchi)) THEN
      select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
    END IF;
    -- get the preferred structure
    IF (maxrows<=0) THEN
  	  	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure;
    ELSE
      	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure limit 1;
    END IF;
END $$

DELIMITER ;


-- -----------------------------------------------------
-- Deletes a dataset and associated structures, if the structures are not in any other dataset
-- Delete is allowed only if the star field is <= maxstars!
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `deleteDataset`;
DELIMITER $$

CREATE PROCEDURE deleteDataset(IN dataset INTEGER, IN maxstars INTEGER)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL

BEGIN

DECLARE no_more_rows BOOLEAN;
DECLARE chemical INTEGER;
DECLARE propertyid INTEGER;

DECLARE thedataset CURSOR FOR
SELECT idstructure FROM struc_dataset join src_dataset using(id_srcdataset) where id_srcdataset=dataset and stars<=maxstars;

DECLARE theproperties CURSOR FOR
SELECT idproperty FROM src_dataset join template_def using(idtemplate) where id_srcdataset=dataset and stars<=maxstars;

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;
--  Error: 1451 SQLSTATE: 23000 Foreign key constraint  
DECLARE CONTINUE HANDLER FOR SQLSTATE '23000' SET @x = @x + 1;

	SET @x = 0;
	 
	OPEN thedataset;
	
	the_loop: LOOP
	
		FETCH thedataset into chemical;
		IF no_more_rows THEN
			CLOSE thedataset;
			LEAVE the_loop;
		END IF;
	
		DELETE from struc_dataset where id_srcdataset=dataset and idstructure=chemical;
	-- this may fail because of foreign key constraints	
		DELETE from structure where idstructure=chemical;
	
	
	END LOOP the_loop;   
	
	-- now delete properties
	SET no_more_rows = FALSE;
	OPEN theproperties;
	
	prop_loop: LOOP
	
		FETCH theproperties into propertyid;
		IF no_more_rows THEN
			CLOSE theproperties;
			LEAVE prop_loop;
		END IF;
	
	-- this may fail because of foreign key constraints	
		DELETE from properties where idproperty=propertyid;
	
	END LOOP prop_loop;   
	
	-- finally delete the template and the src_dataset entry itself
	-- struc_dataset and template_def enjoy cascading delete
	DELETE d,t FROM src_dataset d, template t 
			WHERE d.idtemplate=t.idtemplate and id_srcdataset=dataset and stars<=maxstars;

END $$

DELIMITER ;


-- -----------------------------------------------------
-- numeric property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_number`;
create view `values_number` as
SELECT id,idproperty,idstructure,value_num as value,status,user_name
FROM property_values where value_num is not null;

-- -----------------------------------------------------
-- string property values
-- -----------------------------------------------------

DROP VIEW IF EXISTS `values_string`;
create view `values_string` as
SELECT id,idproperty,idstructure,if(status="TRUNCATED",text,value) as value,status,user_name,name
FROM properties join property_values using(idproperty) join property_string using(idvalue_string)
where idvalue_string is not null;

-- -----------------------------------------------------
-- all property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_all`;
create view values_all as
SELECT idstructure,idproperty,name,null as value_string,value_num as value_number,idreference FROM properties join property_values using(idproperty) 
where value_num is not null 
union
SELECT idstructure,idproperty,name,value as value_string,null as value_number,idreference FROM properties join property_values using(idproperty) join property_string using(idvalue_string)
where idvalue_string is not null;

-- -----------------------------------------------------
-- ontology
-- -----------------------------------------------------
DROP VIEW IF EXISTS `ontology`;
create view ontology as
SELECT t1.idtemplate as subjectid,t2.idtemplate as objectid,t1.name as subject,relationship,t2.name as object FROM template as t1 join dictionary as d on t1.idtemplate=d.idsubject join template as t2 on d.idobject=t2.idtemplate;

-- -----------------------------------------------------
-- Template definitions
-- -----------------------------------------------------
DROP VIEW IF EXISTS `template_properties`;
create view template_properties as
SELECT idtemplate,template.name as template,idproperty,properties.name as property,ptype as property_type FROM template join template_def using(idtemplate) join properties using(idproperty);

-- -----------------------------------------------------
-- Study tables joined
-- -----------------------------------------------------

CREATE  OR REPLACE VIEW `substance_study_view` AS
select idsubstance,p.substance_prefix,p.substance_uuid,documentType,format,
name,publicname,content,substanceType,
rs_prefix,rs_uuid,
owner_prefix,owner_uuid,owner_name,
p.document_prefix,p.document_uuid,
p.topcategory,p.endpointcategory,p.endpoint,
guidance,
reliability,isRobustStudy,purposeFlag,studyResultType,
params,interpretation_result,interpretation_criteria,
reference,updated,idresult,
e.endpoint as effectendpoint,conditions,unit, 
loQualifier, loValue, upQualifier, upValue, textValue, err from substance s
join substance_protocolapplication p on
s.prefix=p.substance_prefix and s.uuid=p.substance_uuid
left join substance_experiment e on
p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid
;

-- -----------------------------------------------------
-- 50 bins
-- -----------------------------------------------------
create OR REPLACE view bins50 as
  select 0 as d
  union select 1 as d union select 2 as d union select 3 as d union select 4 as d union select 5 as d
  union select 6 as d union select 7 as d union select 8 as d union select 9 as d union select 10 as d
  union select 11 as d union select 12 as d union select 13 as d union select 14 as d union select 15 as d
  union select 16 as d union select 17 as d union select 18 as d union select 19 as d union select 20 as d
  union select 21 as d union select 22 as d union select 23 as d union select 24 as d union select 25 as d
  union select 26 as d union select 27 as d union select 28 as d union select 29 as d union select 30 as d
  union select 31 as d union select 32 as d union select 33 as d union select 34 as d union select 35 as d
  union select 36 as d union select 37 as d union select 38 as d union select 39 as d union select 40 as d
  union select 41 as d union select 42 as d union select 43 as d union select 44 as d union select 45 as d
  union select 46 as d union select 47 as d union select 48 as d union select 49 as d union select 50 as d;

-- -----------------------------------------------------
-- Returns Tanimoto similarity betweeh two compounds given by idchemical 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoChemicals`;

DELIMITER $$
CREATE FUNCTION `tanimotoChemicals`(chemical1_id INT, chemical2_id INT) RETURNS double
BEGIN

  DECLARE tanimoto DOUBLE DEFAULT 0;

  select Nab/(Na+Nb-Nab) into tanimoto from (
  select
	bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  as Nab, f1.bc as Na, f2.bc as Nb
  from fp1024 as f1
  join fp1024 as f2 
  where f1.idchemical=chemical1_id and f2.idchemical=chemical2_id) T;

  RETURN tanimoto;

END $$

DELIMITER ;


-- -----------------------------------------------------
-- Returns maximum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getPropertyMax`;
DELIMITER $$
CREATE FUNCTION `getPropertyMax`(property_id INT, dataset_id INT) RETURNS double
BEGIN

    DECLARE range_max REAL;

    select max(value_num) into range_max from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id;

    RETURN range_max;

END $$
DELIMITER ;

-- -----------------------------------------------------
-- Returns minimum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getPropertyMin`;
DELIMITER $$
CREATE FUNCTION `getPropertyMin`(property_id INT, dataset_id INT) RETURNS double
BEGIN

    DECLARE range_min REAL;

    select min(value_num) into range_min from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id;

    RETURN range_min;

END $$
DELIMITER ;

-- -----------------------------------------------------
-- Returns range with variable width bins
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getBinnedRange`;
DELIMITER $$
CREATE FUNCTION `getBinnedRange`(property_id INT, dataset_id INT, maxPoints INT) RETURNS text CHARSET utf8 COLLATE utf8_bin
BEGIN
    DECLARE no_more_rows BOOLEAN;
    DECLARE number DOUBLE;
    DECLARE c INT DEFAULT 0;
    -- sort and count
    DECLARE numbers CURSOR FOR
    select value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id and value_num is not null
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
          BEGIN
            return c;
          END;
    SET @left = NULL;
    SET @bin=0;
    SET @bins = '';
    OPEN numbers;
    the_loop: LOOP

	    FETCH numbers into number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      IF (@left IS NULL) THEN SET @left = number; END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN

        if (@bin>0) THEN SET @bins = concat(@bins," UNION "); END IF;
        SET @bins = concat(@bins,"SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c);
        SET c = 0;
        set @left = number;
        set @bin = @bin+1;
      END IF;

    END LOOP the_loop;

    IF (c > 0) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c);
    END IF;

    return @bins;
    /*
    prepare  sql_bins  from  @bins;
    execute  sql_bins;
    deallocate prepare sql_bins;
    */
END $$

DELIMITER ;

-- -----------------------------------------------------
-- Returns maximum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `recordsInPropertyRange`;
DELIMITER $$
CREATE FUNCTION `recordsInPropertyRange`(property_id INT,dataset_id INT,minProperty DOUBLE, maxProperty DOUBLE) RETURNS INT
BEGIN
    DECLARE entries REAL;
    select count(*) into entries from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id
    and value_num >= minProperty and value_num < maxProperty;
    RETURN entries;
END $$
DELIMITER ;

-- -----------------------------------------------------
-- Histogram
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `histogram`;
DELIMITER $$
CREATE PROCEDURE `histogram`(property_num INT, dataset INT, minProperty DOUBLE, maxProperty DOUBLE)
begin
  select idchemical,idstructure, value_num,d, minProperty+d*(maxProperty-minProperty)/49 as a, minProperty+(d+1)*(maxProperty-minProperty)/49 as b from
  bins50
  join struc_dataset
  join property_values using(idstructure)
  where id_srcdataset=dataset and idproperty=property_num
  and value_num >= minProperty+d*(maxProperty-minProperty)/49  and value_num < minProperty+(d+1)*(maxProperty-minProperty)/49;
end $$
DELIMITER ;

-- -----------------------------------------------------
-- Grid with variable length cells, equal number of points in a cell, avg tanimoto distance in a cell
-- Example: call tanimotoBinnedSpace(13,3,10);
-- property_id
-- dataset_id
-- maxPoints
-- method -  0 : max; 1 : G2 likelihood
-- threshold_sim DOUBLE
-- threshold_act DOUBLE
-- --------------------------------------------------------
DROP PROCEDURE IF EXISTS `tanimotoBinnedSpace`;
DELIMITER $$
CREATE PROCEDURE `tanimotoBinnedSpace`(
                property_id INT,dataset_id INT, maxPoints INT, method INT, threshold_sim DOUBLE, threshold_act DOUBLE)
BEGIN
    DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
    DECLARE number DOUBLE;
    DECLARE c INT DEFAULT 0;

    DECLARE notstarted BOOLEAN DEFAULT TRUE;
    DECLARE leftBin DOUBLE;
    DECLARE bin INT DEFAULT 0;
    DECLARE bins TEXT DEFAULT "";

    -- calculate bins - same as getBinnedRange
    -- sort and count
    DECLARE numbers CURSOR FOR
    select value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id and value_num is not null
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
    END;

    OPEN numbers;

    the_loop: LOOP

	    FETCH numbers into number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      IF (notstarted) THEN
          SET leftBin = number; SET notstarted = false;
      END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN

        if (number > leftBin) THEN

          if (bin>0) THEN SET bins = concat(bins," UNION "); END IF;

          SET bins = concat(bins,"SELECT ",bin," as bin,",leftBin," as min1,",number," as max1,",c , " as c");
          SET c = 0;
          set leftBin = number;
          set bin = bin+1;
        END IF;
      END IF;

    END LOOP the_loop;

    IF (c > 0) THEN
        IF (leftbin = number) THEN SET number = leftBin + 0.0001; END IF;
        SET bins = concat(bins," UNION SELECT ",bin," as bin,",leftBin," as min1,",number," as max1,",c, " as c");

        -- all so far for bins
        -- now the space itself
        -- if (method = 0) THEN
        --  SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoCell(",
        --         property_id,",",dataset_id,",a.min1,a.max1,b.min1,b.max1),a.c,b.c from (",
        --          bins,") a join (",bins,") b where a.bin<=b.bin");
        if ((method=4) || (method=5)) THEN
          SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoG2row(",
                  property_id,",",dataset_id,",a.min1,a.max1,",
                  method,",",threshold_sim,",b.max1),a.c,b.c from (",
                  bins,") a join (",bins,") b");
        else
          SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoG2(",
                  property_id,",",dataset_id,",a.min1,a.max1,b.min1,b.max1,",
                  method,",",threshold_sim,",",threshold_act,"),a.c,b.c from (",
                  bins,") a join (",bins,") b where a.bin<=b.bin");

        end if;

        prepare  sqlMatrix  from  @matrix;
        execute  sqlMatrix;
        deallocate prepare  sqlMatrix;

    END IF;

END $$

DELIMITER ;

-- -------------
-- Histogram, writes slices into query_resutls table 
-- -------------
DROP PROCEDURE IF EXISTS `histogramFlexDB`;
DELIMITER $$
CREATE PROCEDURE `histogramFlexDB`(property_id INT,dataset_id INT,
                minProperty DOUBLE, maxProperty DOUBLE, maxPoints INT)
BEGIN
    DECLARE no_more_rows BOOLEAN;
    DECLARE number DOUBLE;
    DECLARE chemical INT DEFAULT 0;
    DECLARE structure INT DEFAULT 0;
    DECLARE c INT DEFAULT 0;

    -- sort and count
    DECLARE numbers CURSOR FOR
    select idchemical,idstructure,value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id
    and value_num>=minProperty and value_num<maxProperty
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;


    INSERT IGNORE into sessions values (null,"admin",now(),now(),"chemspace");
    INSERT IGNORE into query (SELECT null,idsessions,"chemspace","",null from sessions where title="chemspace");
    DELETE r from query_results r,`query` q where q.idquery=r.idquery and q.name="chemspace";

    SET @left = minProperty;
    SET @bin=0;
    SET @bins = 'SELECT 0,0,0,0';
    SET @chemicals = '';
    OPEN numbers;
    the_loop: LOOP

	    FETCH numbers into chemical,structure,number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c,",'",@chemicals,"'");
        SET c = 0;
        set @left = number;
        set @bin = @bin+1;
        set @chemicals = chemical;
      ELSE
        SET @chemicals = concat(@chemicals,",",chemical);
      END IF;

      INSERT INTO query_results SELECT idquery,chemical,structure,1,@bin,number from query where name="chemspace";

    END LOOP the_loop;

    IF (c > 0) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c,",'",@chemicals,"'");
    END IF;

 SELECT @bins;

  --  prepare  sqlBins  from  @bins;
  --  execute  sqlBins;
  --  deallocate prepare  sqlBins;

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Tanimoto distance of group of chemicals, given by property range
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoCell`;
DELIMITER $$
CREATE FUNCTION `tanimotoCell`(property_id INT, dataset_id INT, min1 DOUBLE, max1 DOUBLE, min2 DOUBLE, max2 DOUBLE) RETURNS double
BEGIN
  DECLARE tanimoto DOUBLE DEFAULT 0;
  select
	  max(
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  )) into tanimoto
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and s2.value_num >= min2 and s2.value_num < max2
  and d1.idstructure < d2.idstructure
  ;
  return tanimoto;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- G2 likelihood of dactivity > threshold_act, given similarity > threshold_sim
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoG2`;
DELIMITER $$
CREATE FUNCTION `tanimotoG2`(property_id INT, dataset_id INT,
                              min1 DOUBLE, max1 DOUBLE,
                              min2 DOUBLE, max2 DOUBLE,
                              method INT,
                              threshold_sim DOUBLE,threshold_dact DOUBLE) RETURNS double
BEGIN

  DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
  DECLARE sim float;
  DECLARE act float;
  DECLARE num FLOAT;
  DECLARE sali FLOAT;
  DECLARE maxsali FLOAT default 0;
  DECLARE maxsim FLOAT default 0;

  DECLARE g2 FLOAT DEFAULT 0;
  DECLARE id INT;

  -- large_act_large_sim
  DECLARE a FLOAT DEFAULT 0;
  -- large_act_small_sim
  DECLARE b FLOAT DEFAULT 0;
  -- small_act_large_sim
  DECLARE c FLOAT DEFAULT 0;
  -- small_act_small_sim
  DECLARE d FLOAT DEFAULT 0;

  DECLARE stats CURSOR FOR
  select
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
        )
        )
			  ,
        abs(s1.value_num-s2.value_num)
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and s2.value_num >= min2 and s2.value_num < max2
  and s1.idchemical != s2.idchemical
  ;

  DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
  END;

  SET @k = 1E-7;

  OPEN stats;

  the_loop: LOOP

    FETCH stats into sim,act;

  	IF no_more_rows THEN
	    	CLOSE stats;
		    LEAVE the_loop;
    END IF;

    SET sali = act/(1 + @k - sim);

    if (sali > maxsali) THEN
      SET maxsali = sali;
    END IF;

    if (sim > maxsim) THEN
      SET maxsim = sim;
    END IF;

    IF (sim > threshold_sim) THEN
      IF (act >= threshold_dact) THEN
        SET a = a + 1;
      ELSE
        SET c = c + 1;
       END IF;
    ELSE
      IF (act >= threshold_dact) THEN
        SET b = b + 1;
      ELSE
        SET d = d + 1;
       END IF;
    END IF;


  END LOOP the_loop;



  IF (method=1) THEN
    -- G2 likelihood
    SET a = a + @k;
    SET b = b + @k;
    SET c = c + @k;
    SET d = d + @k;
    SET  g2 = IF(a=0,0,IF(a=0,0,a*LN(a*(c+d)/(c*(a+b))))+IF(b=0,0,b*LN(b*(c+d)/(d*(a+b)))));
  ELSEIF (method = 2) THEN
    -- Fisher
    SET  g2 = IF((a+b+c+d)=0,0,(a+c)/(a+b+c+d));
  ELSEIF (method = 6) THEN
    SET g2 = maxsali;
  ELSEIF (method = 0) THEN -- maxsim
    SET g2 = maxsim;
  ELSEIF (method = 3) THEN
    SET @all = a+b+c+d;
    if (@all = 0) THEN
      return 0;
    END IF;
    -- SET g2 = if (a>0,10 + a/@all,if(d>0, d/@all, if (b>0, 5 + b/@all, if (c>0, 3 + c/@all , 0)))); -- IF((a+c)=0,0,a/(a+c));
--     SET g2 = 256*(256*(256*a/@all + b/@all) + c/@all) ;
    SET g2 = a;
    return a;
  END IF;
  return g2;
end $$

DELIMITER ;

-- g2 for the entire row --

DROP FUNCTION IF EXISTS `tanimotoG2row`;
DELIMITER $$
CREATE FUNCTION `tanimotoG2row`(property_id INT, dataset_id INT,
                              min1 DOUBLE, max1 DOUBLE,
                              method INT,
                              threshold_sim DOUBLE,threshold_dact DOUBLE) RETURNS double
BEGIN

  DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
  DECLARE sim double;
  DECLARE act double;
  DECLARE num FLOAT;
  DECLARE g2 FLOAT DEFAULT 0;

  -- large_act_large_sim
  DECLARE a FLOAT DEFAULT 0;
  -- large_act_small_sim
  DECLARE b FLOAT DEFAULT 0;
  -- small_act_large_sim
  DECLARE c FLOAT DEFAULT 1E-7;
  -- small_act_small_sim
  DECLARE d FLOAT DEFAULT 1E-7;

  DECLARE stats CURSOR FOR
  select
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
        )
			  ,
        abs(s1.value_num-s2.value_num)
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and d1.idstructure != d2.idstructure
  ;

  DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
  END;

  OPEN stats;

  the_loop: LOOP

    FETCH stats into sim,act;

  	IF no_more_rows THEN
	    	CLOSE stats;
		    LEAVE the_loop;
    END IF;

    IF (sim > threshold_sim) THEN
      IF (act >= threshold_dact) THEN
        SET a = a + 1;
      ELSE
        SET c = c + 1;
       END IF;
    ELSE
      IF (act >= threshold_dact) THEN
        SET b = b + 1;
      ELSE
        SET d = d + 1;
       END IF;
    END IF;


  END LOOP the_loop;

  IF (method=5) THEN
    SET g2 = a;
  ELSE
    SET  g2 = IF(a=0,0,a*LN(a*(c+d)/(c*(a+b))))+IF(b=0,0,b*LN(b*(c+d)/(d*(a+b))));
  END IF;
  return g2;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Tanimoto distance of group of chemicals, given by idquery
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoSet`;
DELIMITER $$
CREATE FUNCTION `tanimotoSet`(query_id INT, metric1 INT, metric2 INT) RETURNS double
BEGIN

  DECLARE tanimoto DOUBLE DEFAULT 0;

  select avg(
  (
  bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  ) / ( f1.bc + f2.bc - (
  bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  ))) into tanimoto
  from fp1024 as f1
  join fp1024 as f2
  where f1.idchemical in (select idchemical from query_results where idquery=idquery and metric=metric1)
  and f2.idchemical in (select idchemical from query_results where idquery=idquery and metric=metric2);

  RETURN tanimoto;

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Binned 2D space, Tanimoto average distance in each cell
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `tanimotoSpace`;
DELIMITER $$
CREATE PROCEDURE `tanimotoSpace`(
    query_id INT)
BEGIN
  select b1.d,b2.d,tanimotoSet(query_id,b1.d,b2.d) from bins100 b1
  join bins100 b2 where b1.d<b2.d;
end $$
DELIMITER ;


-- -----------------------------------------------------
-- Pair order by SALI
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `saliorder`;
DELIMITER $$
CREATE PROCEDURE `saliorder`(IN property_id INT, IN dataset_id INT, IN maxpair INT, IN zero DOUBLE)
BEGIN

  set zero = ifnull(zero,0.000001);

    select
  f1.idchemical,f2.idchemical,s1.value_num,s2.value_num,
      abs(s1.value_num-s2.value_num)/(1 + zero -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     )) as sali,
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     ) as tanimoto
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty= property_id
  and s2.idproperty= property_id
  and d1.idstructure < d2.idstructure
  order by sali desc limit maxpair
  ;

END $$

DELIMITER ;

-- ----------------------------
-- Chemical space stats
-- ----------------------------
DROP TABLE IF EXISTS `qsasheader`;
CREATE TABLE `qsasheader` (
  `idsasmap` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `threshold_dact` double NOT NULL,
  `threshold_sim` double NOT NULL,
  `id_srcdataset` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idsasmap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `qsasmap4`;
CREATE TABLE `qsasmap4` (
  `idsasmap` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL,
  `a` int(10) unsigned NOT NULL,
  `b` int(10) unsigned NOT NULL,
  `c` int(10) unsigned NOT NULL,
  `d` int(10) unsigned NOT NULL,
  `fisher` double DEFAULT NULL,
  `g2` double unsigned NOT NULL,
  `g2rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`idsasmap`,`idchemical`),
  KEY `idsasmap_idx` (`idsasmap`),
  KEY `idchemical_idx` (`idchemical`),
  KEY `fisher_index` (`idsasmap`,`fisher`),
  KEY `g2_index` (`g2`,`idsasmap`),
  KEY `g2rank` (`g2rank`),
  CONSTRAINT `idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idsasmap` FOREIGN KEY (`idsasmap`) REFERENCES `qsasheader` (`idsasmap`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP PROCEDURE IF EXISTS `g2counts`;
DELIMITER $$

CREATE PROCEDURE `g2counts`(IN dataset INT, IN property INT, IN dactivity DOUBLE, IN simthreshold DOUBLE,IN laplacek DOUBLE)
BEGIN
  insert into qsasheader values(null,dactivity,simthreshold,dataset,property,now());  
  set @idsasmap = LAST_INSERT_ID();
  set @laplacek = ifnull(laplacek,1E-7);
  insert into qsasmap4
	select @idsasmap,idchemical,
	sum(IF ((act>=dactivity) and (sim>=simthreshold),1,0)) a,
	sum(IF ((act>=dactivity) and (sim<simthreshold),1,0)) b,
	sum(IF ((act<dactivity) and (sim>=simthreshold),1,0)) c,
	sum(IF ((act<dactivity) and (sim<simthreshold),1,0)) d,0,0,null
	from 
	(
	select null,@idsasmap,s1.idchemical,
		  abs(s1.value_num-s2.value_num)  act,
		  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
		  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
		  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
		  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
		  /(f1.bc + f2.bc -
		  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
		  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
		  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
		  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
		  ) sim
		  from   struc_dataset d1
		  join   struc_dataset d2
		  join property_values s2 on d2.idstructure=s2.idstructure
		  join property_values s1 on d1.idstructure=s1.idstructure
		  join fp1024 f1 on f1.idchemical=s1.idchemical
		  join fp1024 f2 on f2.idchemical=s2.idchemical
		  where s2.idproperty=property and d1.id_srcdataset=dataset
		  and d2.id_srcdataset= dataset and s1.idproperty=property
		  and s1.idchemical != s2.idchemical
	) pairwise
	group by idchemical;
	-- calculate g2
	update qsasmap4 set fisher= (a+c)/(a+c+d),
    	  g2 = 
    	  (a+@laplacek)* ln((a+@laplacek)*((c+@laplacek)+(d+@laplacek))/((c+@laplacek)*((a+@laplacek)+(b+@laplacek))))+
  		  (b+@laplacek)* ln((b+@laplacek)*((c+@laplacek)+(d+@laplacek))/((d+@laplacek)*((a+@laplacek)+(b+@laplacek))))
  		  where idsasmap=@idsasmap;
   -- assign rank		  
   SET @rownum := 0;
   insert into qsasmap4 
       (SELECT idsasmap,idchemical,a,b,c,d,fisher,g2,(@rownum := @rownum + 1) AS rank FROM qsasmap4 WHERE idsasmap=@idsasmap ORDER BY g2 DESC
        ) on duplicate key  update  g2rank=values(g2rank);
          		  
   -- return sasmap id
   SELECT idsasmap,threshold_dact,threshold_sim,id_srcdataset,idproperty,p.name,p.comments,p.units,d.name 
   from qsasheader join src_dataset d using(id_srcdataset) join properties p using(idproperty) where idsasmap=@idsasmap;   
   

END $$

DELIMITER ;

-- profile all properties in a template

DROP procedure if exists `g2profile`;

DELIMITER $$

CREATE PROCEDURE `g2profile`(IN dataset INT, IN template INT, IN simthreshold DOUBLE,IN laplacek DOUBLE)
BEGIN

    DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
    DECLARE property DOUBLE;
    DECLARE avgv DOUBLE;
    DECLARE stdev DOUBLE;
    
    DECLARE c INT DEFAULT 0;

    DECLARE notstarted BOOLEAN DEFAULT TRUE;
    DECLARE leftBin DOUBLE;
    DECLARE bin INT DEFAULT 0;
    DECLARE bins TEXT DEFAULT "";

    -- calculate bins - same as getBinnedRange
    -- sort and count
    DECLARE props CURSOR FOR
    select idproperty,avg(value_num) a,std(value_num) s from property_values 
	join template_def using(idproperty)
	where idtemplate=template group by idproperty order by s desc;

    DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
    END;

    OPEN props;

    the_loop: LOOP

	    FETCH props into property,avgv,stdev;
    	IF no_more_rows THEN
	    	CLOSE props;
		    LEAVE the_loop;
	    END IF;
		
		IF stdev>0 THEN
            delete from qsasheader where id_srcdataset=dataset and idproperty=property;
			call g2counts(dataset,property,stdev,simthreshold,laplacek);
		END IF;

    END LOOP the_loop;

	SELECT idsasmap,threshold_dact,threshold_sim,id_srcdataset,idproperty,p.name,p.comments,p.units,d.name 
	   from qsasheader join src_dataset d using(id_srcdataset) join properties p using(idproperty)
	join template_def using(idproperty) where template_def.idtemplate=template;
END $$

DELIMITER ;


-- -----------------------------------------------------
-- default dictionary entries
-- -----------------------------------------------------
insert into catalog_references (idreference,title,url) values (1,"CAS Registry Number","http://www.cas.org");
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"CAS",1);

insert into catalog_references (idreference,title,url) values (2,"IUPAC name","http://www.iupac.org");
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"IUPAC Name",2);


-- Grants for ambit2@localhost
-- REVOKE ALL PRIVILEGES ON ambit2.* FROM 'admin'@'localhost';
-- REVOKE ALL PRIVILEGES ON ambit2.* FROM 'guest'@'localhost';
-- GRANT USAGE ON ambit2.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
-- GRANT ALL PRIVILEGES ON ambit2.* TO 'admin'@'localhost' WITH GRANT OPTION;
-- GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON ambit2.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';
-- GRANT EXECUTE ON FUNCTION sortstring TO 'guest'@'localhost'
-- GRANT CREATE TEMPORARY TABLEs on ambit2.* to 'guest'@'%'

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


