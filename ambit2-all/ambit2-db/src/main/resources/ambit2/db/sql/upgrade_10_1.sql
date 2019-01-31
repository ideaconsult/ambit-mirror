ALTER TABLE `substance_protocolapplication` ADD COLUMN `assay_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `investigation_uuid` 
, ADD INDEX `xassay` (`topcategory` ASC, `endpointcategory` ASC, `assay_uuid` ASC) ;

ALTER TABLE `bundle_substance_protocolapplication` ADD COLUMN `assay_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `investigation_uuid` 
, ADD INDEX `bxassay` (`topcategory` ASC, `endpointcategory` ASC, `assay_uuid` ASC) ;

ALTER TABLE `bundle_substance_experiment` ADD COLUMN `resultgroup` INT NULL DEFAULT NULL  AFTER `resulttype` ;

insert into version (idmajor,idminor,comment) values (10,1,"AMBIT2 schema");