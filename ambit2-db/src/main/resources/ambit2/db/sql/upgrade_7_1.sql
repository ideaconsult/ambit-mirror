-- -----------------------------------------------------
-- Table `substance_protocolapplication` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `substance_protocolapplication`;
CREATE TABLE `substance_protocolapplication` (
  `document_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `document_uuid` varbinary(16) NOT NULL,
  `endpoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `guidance` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `substance_uuid` varbinary(16) DEFAULT NULL,
  `params` text NOT NULL,
  `reference` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`document_prefix`,`document_uuid`),
  KEY `substance` (`substance_prefix`,`substance_uuid`),
  KEY `endpoint` (`endpoint`)
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
  `unit` varchar(6) DEFAULT NULL,
  `loQualifier` varchar(6) DEFAULT NULL,
  `loValue` double DEFAULT NULL,
  `upQualifier` varchar(6) DEFAULT NULL,
  `upvalue` double DEFAULT NULL,
  PRIMARY KEY (`idresult`),
  KEY `document_id` (`document_uuid`,`document_prefix`),
  KEY `endpoint` (`endpoint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into version (idmajor,idminor,comment) values (7,1,"AMBIT Schema: substances study records support");