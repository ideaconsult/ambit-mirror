
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
  PRIMARY KEY (`cmp_prefix`,`cmp_uuid`,`idsubstance`,`idchemical`,`relation`) USING BTREE,
  KEY `chemicalkey` (`idchemical`),
  KEY `relation-x` (`relation`),
  KEY `crs-uuid-x` (`rs_uuid`,`rs_prefix`),
  KEY `cmp-uuid-x` (`cmp_prefix`,`cmp_uuid`),
  KEY `idsubstance_idx` (`idsubstance`),
  CONSTRAINT `idsubstance` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `chemicalkey` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Dossier to chemicals relation';

insert into version (idmajor,idminor,comment) values (7,1,"AMBIT Schema: substances study records support");