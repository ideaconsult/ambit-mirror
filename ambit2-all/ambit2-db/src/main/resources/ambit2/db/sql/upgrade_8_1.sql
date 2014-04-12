-- Some I5 sections have long names
ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `endpointcategory` `endpointcategory` VARCHAR(45) NULL DEFAULT NULL  ;
insert into version (idmajor,idminor,comment) values (8,1,"AMBIT2 schema");