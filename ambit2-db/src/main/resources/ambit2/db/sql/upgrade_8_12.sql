ALTER TABLE `bundle_substance` CHANGE COLUMN `remarks` `remarks` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL  ;

ALTER TABLE `substance_protocolapplication` 
ADD INDEX `guidance` (`topcategory` ASC, `endpointcategory` ASC, `guidance` ASC, `interpretation_result` ASC) ;

-- missing updates
-- ALTER TABLE `substance` ADD INDEX `owner-name` (`owner_name` ASC) ;
-- ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `isRobustStudy` `isRobustStudy` TINYINT(1) NULL DEFAULT NULL  , CHANGE COLUMN `isUsedforClassification` `isUsedforClassification` TINYINT(1) NULL DEFAULT NULL  , CHANGE COLUMN `isUsedforMSDS` `isUsedforMSDS` TINYINT(1) NULL DEFAULT NULL  ;
-- DROP TABLE IF EXISTS `chemstats`;

ALTER TABLE `ontobucket` CHANGE COLUMN `relation` `relation` ENUM('label','subclass','db','endpoint','endpointhash','hash','protocol','substancetype') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT 'subclass'  ;

-- no need of this view anymore
DROP VIEW IF EXISTS `values_number`;

-- new optimized table for atomenvironments 
-- the old one will be removed
-- -----------------------------------------------------
-- atom environment of a chemical. 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fpatomenvironments`;
CREATE TABLE `fpatomenvironments` (
  `idchemical` int(10) unsigned NOT NULL DEFAULT '0',
  `inchi` varchar(27) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `tags` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `time` int(10) unsigned DEFAULT '0',
  `bc` int(6) NOT NULL DEFAULT '0',
  `status` enum('invalid','valid','error') COLLATE utf8_bin NOT NULL DEFAULT 'invalid',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `levels` int(6) NOT NULL DEFAULT '7',
  `factory` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idchemical`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  FULLTEXT KEY `fulltext` (`inchi`,`tags`),
  CONSTRAINT `fpatomenvironments_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into version (idmajor,idminor,comment) values (8,12,"AMBIT2 schema");