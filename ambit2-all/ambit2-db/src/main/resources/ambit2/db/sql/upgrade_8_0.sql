-- User refactoring. From now on a separate database for users AA will be used (ambit_users)

DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `roles`;

-- This table is not used for AA at all - see ambit_users database instead 
ALTER TABLE `users` 
	DROP COLUMN `registration_id` , 
	DROP COLUMN `registration_date` , 
	DROP COLUMN `registration_status` , 
	DROP COLUMN `password` , 
	CHANGE COLUMN `title` `title` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '""'  , 
	CHANGE COLUMN `firstname` `firstname` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '""'  , 
	CHANGE COLUMN `lastname` `lastname` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '""'  , 
	CHANGE COLUMN `webpage` `homepage` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '""'  , 
	CHANGE COLUMN `affiliation` `institute` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '""'  ;

-- ALTER TABLE `users` ADD COLUMN `iduser` INT(11) NULL AUTO_INCREMENT  FIRST, ADD UNIQUE INDEX `iduser_UNIQUE` (`iduser` ASC) ;
-- extend units length	
ALTER TABLE `substance_experiment` CHANGE COLUMN `unit` `unit` VARCHAR(45) NULL DEFAULT NULL  ;
insert into version (idmajor,idminor,comment) values (8,0,"AMBIT2 schema");	