ALTER TABLE `substance_experiment` 
DROP INDEX `category-x` 
, ADD INDEX `category-x` (`topcategory` ASC, `endpointcategory` ASC, `endpoint` ASC, `unit` ASC, `resulttype` ASC, `endpointhash` ASC) ;

ALTER TABLE `substance_experiment` CHANGE COLUMN `resulttype` `resulttype` VARCHAR(32) NULL DEFAULT NULL  ;

insert into version (idmajor,idminor,comment) values (10,2,"AMBIT2 schema");