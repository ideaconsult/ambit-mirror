-- The hash will be used as a lookup when generating dataset on the fly  

ALTER TABLE `substance_experiment` ADD COLUMN `endpointhash` VARBINARY(20) NULL DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions'  AFTER `document_uuid` ;
ALTER TABLE `substance_experiment` ADD INDEX `hash-x` (`endpointhash` ASC) ;

update substance_experiment set endpointhash= unhex(sha1(concat(ifnull(endpoint,""),ifnull(unit,""),ifnull(conditions,""))));

insert into version (idmajor,idminor,comment) values (8,2,"AMBIT2 schema");
