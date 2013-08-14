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
  PRIMARY KEY (`idsubstance`),
  UNIQUE KEY `uuid-x` (`prefix`,`uuid`),
  KEY `doxType-x` (`documentType`),
  KEY `format-x` (`format`),
  KEY `stype-x` (`substanceType`),
  KEY `rs-uuid-x` (`rs_uuid`,`rs_prefix`),
  KEY `owner-uuid-x` (`owner_prefix`,`owner_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Substance dossier (mainly to support IUCLID5)';


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
  PRIMARY KEY (`cmp_prefix`,`cmp_uuid`,`idsubstance`,`idchemical`,`relation`) USING BTREE,
  KEY `chemicalkey` (`idchemical`),
  KEY `relation-x` (`relation`),
  KEY `crs-uuid-x` (`rs_uuid`,`rs_prefix`),
  KEY `cmp-uuid-x` (`cmp_prefix`,`cmp_uuid`),
  CONSTRAINT `chemicalkey` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Dossier to chemicals relation';

insert into version (idmajor,idminor,comment) values (7,0,"AMBIT Schema: substances support");