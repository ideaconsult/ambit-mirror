ALTER TABLE `assay_template` CHANGE COLUMN `hint` `hint` TEXT NULL DEFAULT NULL  ;
insert into version (idmajor,idminor,comment) values (10,4,"Assay templates");
