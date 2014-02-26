-- added reference owner
ALTER TABLE `substance_protocolapplication` ADD COLUMN `reference_owner` VARCHAR(128) NULL DEFAULT NULL  AFTER `reference_year` 
, ADD INDEX `reference_owner` (`reference_owner` ASC) ;

insert into version (idmajor,idminor,comment) values (7,2,"AMBIT2 schema");