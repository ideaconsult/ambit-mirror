
ALTER TABLE `ausers` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;

ALTER TABLE `bundle` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `structure` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `models` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `property_values` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `property_pairstruc` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `quality_pair` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `quality_structure` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `src_dataset` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `sessions` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;
ALTER TABLE `funcgroups` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL;

insert into version (idmajor,idminor,comment) values (10,6,"user name length");