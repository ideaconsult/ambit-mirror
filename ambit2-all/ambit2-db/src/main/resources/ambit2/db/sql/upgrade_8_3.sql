ALTER TABLE `substance_relation` ADD COLUMN `name` VARCHAR(128) NULL DEFAULT NULL COMMENT 'composition name'  AFTER `rs_uuid` ;
update substance_relation set name= concat('Composition name to be updated on import ',hex(cmp_uuid));

insert into version (idmajor,idminor,comment) values (8,3,"Composition name support");