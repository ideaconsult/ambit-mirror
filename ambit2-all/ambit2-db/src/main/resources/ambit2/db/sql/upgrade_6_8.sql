ALTER TABLE `chem_relation` ADD COLUMN `metric` DOUBLE AFTER `relation`;
insert into version (idmajor,idminor,comment) values (6,8,"AMBIT Schema: structure relations table");
