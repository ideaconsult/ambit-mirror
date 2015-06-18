ALTER TABLE `substance_experiment` CHANGE COLUMN `loQualifier` `loQualifier` VARCHAR(8) NULL DEFAULT NULL  , CHANGE COLUMN `upQualifier` `upQualifier` VARCHAR(8) NULL DEFAULT NULL  ;

-- ontology freetext with query expsnsion support
DROP TABLE IF EXISTS `ontobucket` ;
CREATE TABLE `ontobucket` (
  `s_source` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL,
  `s_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `o_source` varchar(16) COLLATE utf8_unicode_ci DEFAULT NULL,
  `o_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `label` text COLLATE utf8_unicode_ci,
  `relation` enum('label','subclass','db','endpoint','endpointhash','hash','protocol') COLLATE utf8_unicode_ci DEFAULT 'subclass',
  `uuid` varbinary(20) DEFAULT NULL,
  KEY `s_id` (`s_id`),
  KEY `o_id` (`o_id`,`relation`),
  FULLTEXT KEY `fulltext` (`s_id`,`o_id`,`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `ontobucket` CHANGE COLUMN `relation` `relation` ENUM('label','subclass','db','endpoint','endpointhash','hash','protocol','target') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT 'subclass'  ;
ALTER TABLE `ontobucket` CHANGE COLUMN `relation` `relation` ENUM('label','subclass','db','endpoint','endpointhash','hash','protocol','target','reference') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT 'subclass'  ;

ALTER TABLE `substance_protocolapplication` DROP INDEX `xse` , ADD INDEX `xse` (`substance_prefix` ASC, `substance_uuid` ASC, `topcategory` ASC, `endpointcategory` ASC, `interpretation_result` ASC) ;

insert into version (idmajor,idminor,comment) values (8,11,"AMBIT2 schema");
