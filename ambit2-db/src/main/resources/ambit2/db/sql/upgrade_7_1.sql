
-- -----------------------------------------------------
-- Table `substance_protocolapplication` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_protocolapplication`;
CREATE TABLE `substance_protocolapplication` (
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `topcategory` varchar(32) DEFAULT NULL,
  `endpointcategory` varchar(32) DEFAULT NULL,
  `endpoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `guidance` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `params` text NOT NULL,
  `interpretation_result` varchar(32) DEFAULT NULL,
  `interpretation_criteria` varchar(255) DEFAULT NULL,
  `reference` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`document_prefix`,`document_uuid`),
  KEY `substance` (`substance_prefix`,`substance_uuid`),
  KEY `endpoint` (`endpoint`),
  KEY `category` (`endpointcategory`),
  KEY `topcategory` (`topcategory`,`endpointcategory`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `substance_experiment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_experiment`;
CREATE TABLE `substance_experiment` (
  `idresult` int(11) NOT NULL AUTO_INCREMENT,
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `endpoint` varchar(45) DEFAULT NULL,
  `conditions` text,
  `unit` varchar(16) DEFAULT NULL,
  `loQualifier` varchar(6) DEFAULT NULL,
  `loValue` double DEFAULT NULL,
  `upQualifier` varchar(6) DEFAULT NULL,
  `upvalue` double DEFAULT NULL,
  PRIMARY KEY (`idresult`),
  KEY `document_id` (`document_uuid`,`document_prefix`),
  KEY `endpoint` (`endpoint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `substance_relation` FK to substance
-- -----------------------------------------------------
ALTER TABLE `substance_relation` 
  ADD CONSTRAINT `idsubstance`
  FOREIGN KEY (`idsubstance` )
  REFERENCES `substance` (`idsubstance` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- -----------------------------------------------------
-- Table `substance_protocolapplication` FK to substance
-- -----------------------------------------------------
ALTER TABLE `substance_protocolapplication` 
  ADD CONSTRAINT `substance-x`
  FOREIGN KEY (`substance_prefix` , `substance_uuid` )
  REFERENCES `substance` (`prefix` , `uuid` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- -----------------------------------------------------
-- Table `substance_experiment` FK to document (protocolapplication)
-- -----------------------------------------------------
ALTER TABLE `substance_experiment` 
  ADD CONSTRAINT `document-x`
  FOREIGN KEY (`document_prefix` , `document_uuid` )
  REFERENCES `substance_protocolapplication` (`document_prefix` , `document_uuid` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;


-- -----------------------------------------------------
-- Table `substance_owner` (company submitted the substance dossier)
-- -----------------------------------------------------
-- DROP TABLE IF EXISTS `substance_owner`;
-- CREATE TABLE `substance_owner` (
-- `owner_prefix` varchar(6) COLLATE utf8_bin NOT NULL DEFAULT '',
--   `owner_uuid` varbinary(16) NOT NULL DEFAULT '',
--   `name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'Human readable name of the entry',
--   PRIMARY KEY (`owner_prefix`,`owner_uuid`),
--   KEY `name-x` (`name`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Substance owner (company)';

ALTER TABLE `substance` ADD COLUMN `owner_name` VARCHAR(255) NULL DEFAULT NULL  AFTER `owner_uuid` ;
ALTER TABLE `substance_protocolapplication` ADD COLUMN `reference_year` SMALLINT NULL DEFAULT NULL  AFTER `reference` ;
ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `reference` `reference` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL  ;
ALTER TABLE `substance_protocolapplication` ADD COLUMN `reliability` VARCHAR(45) NULL DEFAULT NULL COMMENT 'Klimish code (text) \n1 (reliable without restriction)\n2 (reliable with restrictions)\n3 (not reliable)\n4 (not assignable)\nother:\nempty (not specified)'  AFTER `updated` , ADD COLUMN `isRobustStudy` TINYINT NULL DEFAULT NULL  AFTER `reliability` , ADD COLUMN `isUsedforClassification` TINYINT NULL DEFAULT NULL  AFTER `isRobustStudy` , ADD COLUMN `isUsedforMSDS` TINYINT NULL DEFAULT NULL  AFTER `isUsedforClassification` ;
ALTER TABLE `substance_protocolapplication` ADD COLUMN `purposeFlag` VARCHAR(32) NULL DEFAULT NULL  AFTER `isUsedforMSDS` ;
ALTER TABLE `substance_protocolapplication` ADD COLUMN `studyResultType` VARCHAR(128) NULL DEFAULT NULL COMMENT 'experimental result\nestimated by calculation\nread-across\n(Q)SAR'  AFTER `purposeFlag` ;
ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `interpretation_criteria` `interpretation_criteria` TEXT NULL DEFAULT NULL  ;
ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `interpretation_result` `interpretation_result` VARCHAR(128) NULL DEFAULT NULL  ;


CREATE  OR REPLACE VIEW `substance_study_view` AS
select idsubstance,substance_prefix,substance_uuid,documentType,format,
name,publicname,content,substanceType,
rs_prefix,rs_uuid,
owner_prefix,owner_uuid,owner_name,
p.document_prefix,p.document_uuid,
topcategory,endpointcategory,p.endpoint,
guidance,params,interpretation_result,interpretation_criteria,
reference,reference_year,updated,idresult,
e.endpoint as effectendpoint,conditions,unit, 
loQualifier, loValue, upQualifier, upvalue from substance s
join substance_protocolapplication p on
s.prefix=p.substance_prefix and s.uuid=p.substance_uuid
join substance_experiment e on
p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid
;

insert into version (idmajor,idminor,comment) values (7,1,"AMBIT Schema: substances study");