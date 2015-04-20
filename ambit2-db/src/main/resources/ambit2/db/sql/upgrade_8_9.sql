ALTER TABLE `structure` CHANGE COLUMN `format` `format` ENUM('SDF','CML','MOL','INC','NANO','PDB','CIF') CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT 'SDF'  ;

insert into version (idmajor,idminor,comment) values (8,9,"AMBIT2 schema");

