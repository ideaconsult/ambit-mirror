-- property annotation
ALTER TABLE `property_annotation` CHANGE COLUMN `predicate` `predicate` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL COMMENT 'property'  ;
ALTER TABLE `property_annotation` CHANGE COLUMN `object` `object` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL COMMENT 'object', DROP INDEX `Index_2`  , ADD INDEX `Index_2` USING BTREE (`predicate` ASC, `object` ASC) ;
ALTER TABLE `property_annotation` DROP PRIMARY KEY, ADD PRIMARY KEY USING BTREE (`idproperty`, `rdf_type`, `predicate`, `object`) ;
-- shorten the index length to avoid filesort 
ALTER TABLE `property_annotation` CHANGE COLUMN `object` `object` VARCHAR(180) CHARACTER SET 'utf8' NOT NULL COMMENT 'object'  ;
insert into version (idmajor,idminor,comment) values (8,5,"AMBIT2 schema");
		
