ALTER TABLE `bundle_substance_protocolapplication` ADD COLUMN `investigation_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `remarks` ;
ALTER TABLE `substance_protocolapplication` ADD INDEX `xinvestication` (`investigation_uuid` ASC) ;

ALTER TABLE `bundle_final_protocolapplication` ADD COLUMN `investigation_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `remarks` ;
ALTER TABLE `bundle_final_protocolapplication` ADD INDEX `bxinvestication` (`investigation_uuid` ASC) ;

-- version 9.0
insert into version (idmajor,idminor,comment) values (9,1,"AMBIT2 schema - study groups");