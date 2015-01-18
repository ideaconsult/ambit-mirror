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
  PRIMARY KEY (idbundle,`document_prefix`,`document_uuid`),
  KEY `bsubstance` (`substance_prefix`,`substance_uuid`),
  KEY `bendpoint` (`endpoint`),
  KEY `bcategory` (`endpointcategory`),
  KEY `breference_owner` (`reference_owner`),
  KEY `breference-x` (`reference`(255)),
  KEY `btopcategory` (`topcategory`,`endpointcategory`,`interpretation_result`),
  KEY `bxse` (`substance_prefix`,`substance_uuid`,`topcategory`,`endpointcategory`),
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
  PRIMARY KEY (`idresult`,`idbundle`),
  KEY `bdocument_id` (`idbundle`,`document_uuid`,`document_prefix`),
  KEY `bendpoint` (`endpoint`),
  KEY `bdocument-x` (idbundle,`document_prefix`,`document_uuid`),
  KEY `bhash-x` (`endpointhash`),
  KEY `bcategory-x` (`topcategory`,`endpointcategory`,`endpoint`,`endpointhash`),
  KEY `bsubstance-x` (`substance_prefix`,`substance_uuid`),
  CONSTRAINT `bdocument-x` FOREIGN KEY (idbundle,`document_prefix`, `document_uuid`) REFERENCES `bundle_substance_protocolapplication` (idbundle,`document_prefix`, `document_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `bundle_substance_experiment` ADD COLUMN `copied` TINYINT NULL DEFAULT 0  AFTER `substance_uuid` , ADD COLUMN `deleted` TINYINT NULL DEFAULT 0  AFTER `copied` , ADD COLUMN `remarks` VARCHAR(45) NULL DEFAULT NULL  AFTER `deleted` ;
ALTER TABLE `bundle_substance_protocolapplication` ADD COLUMN `copied` TINYINT NULL DEFAULT 0  AFTER `studyResultType` , ADD COLUMN `deleted` TINYINT NULL DEFAULT 0  AFTER `copied` , ADD COLUMN `remarks` VARCHAR(45) NULL DEFAULT NULL  AFTER `deleted` ;

ALTER TABLE `bundle_substance_protocolapplication` 
  ADD CONSTRAINT `idbundle`
  FOREIGN KEY (`idbundle` )
  REFERENCES `bundle` (`idbundle` )
  ON DELETE CASCADE
  ON UPDATE CASCADE
, ADD INDEX `idbundle_idx` (`idbundle` ASC) ;

ALTER TABLE `bundle_substance_experiment` 
  ADD CONSTRAINT `idb`
  FOREIGN KEY (`idbundle` )
  REFERENCES `bundle` (`idbundle` )
  ON DELETE CASCADE
  ON UPDATE CASCADE
, ADD INDEX `idb_idx` (`idbundle` ASC) ;


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

insert into version (idmajor,idminor,comment) values (8,7,"AMBIT2 schema");
