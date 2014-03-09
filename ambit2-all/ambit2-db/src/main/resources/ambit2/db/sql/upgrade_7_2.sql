-- added reference owner
ALTER TABLE `substance_protocolapplication` ADD COLUMN `reference_owner` VARCHAR(128) NULL DEFAULT NULL  AFTER `reference_year` 
, ADD INDEX `reference_owner` (`reference_owner` ASC) ;

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

insert into version (idmajor,idminor,comment) values (7,2,"AMBIT2 schema");