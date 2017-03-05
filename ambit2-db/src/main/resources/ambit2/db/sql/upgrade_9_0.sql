-- we don't use tuples anymore!
-- DROP TRIGGER IF EXISTS insert_property_tuple;

-- to be able to group protocol applications
ALTER TABLE `substance_protocolapplication` ADD COLUMN `investigation_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `studyResultType` ;

-- to be able to delineate between different type of results
ALTER TABLE `substance_experiment` ADD COLUMN `resulttype` ENUM('RAW','DOSERESPONSE','AGGREGATED','NOTSPECIFIED') NULL DEFAULT 'NOTSPECIFIED'  AFTER `substance_uuid` ;
ALTER TABLE `substance_experiment` ADD COLUMN `resultgroup` INT NULL DEFAULT NULL  AFTER `resulttype` ;

-- version 9.0
insert into version (idmajor,idminor,comment) values (9,0,"AMBIT2 schema - study groups");


