-- we don't use tuples anymore!
-- DROP TRIGGER IF EXISTS insert_property_tuple;

-- to be able to group protocol applications
ALTER TABLE `substance_protocolapplication` ADD COLUMN `investigation_uuid` VARBINARY(16) NULL DEFAULT NULL  AFTER `studyResultType` ;

insert into version (idmajor,idminor,comment) values (9,0,"AMBIT2 schema - study groups");
