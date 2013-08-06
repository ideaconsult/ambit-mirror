-- -----------------------------------------------------
-- Table `substance` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance`;
CREATE TABLE  `substance` (
  `idsubstance` int(11) NOT NULL AUTO_INCREMENT,
  `prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ECB5 in UUIDS like ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3',
  `uuid` varbinary(16) DEFAULT NULL COMMENT 'The UUID part of  ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3 in binary format',
  `documentType` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'documentTypeType from I5 XSD schema ',
  `format` varchar(6) COLLATE utf8_bin DEFAULT 'I5D',
  `formatversion` varchar(6) COLLATE utf8_bin DEFAULT '5.5',
  `name` text COLLATE utf8_bin COMMENT 'Human readable name of the entry',
  `publicname` text COLLATE utf8_bin,
  `content` blob,
  PRIMARY KEY (`idsubstance`),
  UNIQUE KEY `uuid-x` (`prefix`,`uuid`) USING HASH,
  KEY `doxType-x` (`documentType`),
  KEY `format-x` (`format`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Substance dossier (mainly to support IUCLID5)';


-- -----------------------------------------------------
-- Table `substance_relation` 
-- -----------------------------------------------------

DROP TABLE IF EXISTS `substance_relation`;
CREATE TABLE  `substance_relation` (
  `idsubstance` int(11) NOT NULL,
  `idchemical` int(11) unsigned NOT NULL,
  `relation` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower_value` double DEFAULT NULL,
  `proportion_real_upper` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_upper_value` double DEFAULT NULL,
  `proportion_real_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical_value` double DEFAULT NULL,
  `proportion_typical_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idsubstance`,`idchemical`,`relation`) USING BTREE,
  KEY `chemicalkey` (`idchemical`),
  KEY `relation-x` (`relation`),
  CONSTRAINT `chemicalkey` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Dossier to chemicals relation';

insert into version (idmajor,idminor,comment) values (7,0,"AMBIT Schema: substances support");